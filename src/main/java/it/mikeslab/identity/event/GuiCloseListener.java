package it.mikeslab.identity.event;

import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.CustomInventory;
import it.mikeslab.commons.api.inventory.InventoryType;
import it.mikeslab.commons.api.inventory.event.GuiEvent;
import it.mikeslab.commons.api.inventory.util.action.ActionHandler;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.lang.LanguageKey;
import it.mikeslab.identity.inventory.config.GuiConfigRegistrar;
import it.mikeslab.identity.pojo.Identity;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class GuiCloseListener implements Listener {

    private final IdentityPlugin instance;

    @EventHandler
    public void onGuiClose(GuiEvent event) {

        if(event.getWhen() != ActionHandler.ActionEvent.CLOSE) return;

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        CustomGui customGui = event.getGui();
        int closedGuiId = customGui.getId();

        if(instance.getGuiFactory().getCustomGui(closedGuiId) == null) return;

        if(customGui.getGuiFactory() != instance.getGuiFactory()) return;

        GuiConfigRegistrar registrar = instance.getGuiConfigRegistrar();

        registrar.fromCustomGuiId(closedGuiId, playerUUID).ifPresent(guiKey -> {

            // An 'id' is an auto-generated value from the gui factory
            // GuiKey is a configuration key that is used to identify the gui

            CustomInventory targetInventory = registrar
                    .getPlayerInventories()
                    .getInventory(playerUUID, guiKey);

            CustomInventory fallbackGui = registrar
                    .getPlayerInventories()
                    .getInventory(playerUUID, registrar.getFallbackGuiIdentifier());

            if(fallbackGui == null) return;

            if(targetInventory.getInventoryType() == InventoryType.MAIN) {
                this.manageSaving(event, fallbackGui, registrar);
                return;
            }

            checkOpeningNewCustomGui(player, () -> {

                Bukkit.getScheduler().runTask(
                        instance,
                        () -> fallbackGui.show(player)
                );

            });

        });
    }


    /**
     * Manage the saving of the inventory
     * @param event The event
     * @param fallbackGui The fallback gui
     * @param registrar The registrar
     */
    private void manageSaving(GuiEvent event, CustomInventory fallbackGui, GuiConfigRegistrar registrar) {

        boolean isMandatoryFieldMissing = false;

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();
        CustomGui gui = event.getGui();

        List<String> missingInventoriesDisplayName = new ArrayList<>();

        Identity setupIdentity = instance.getSetupCacheHandler().getIdentity(playerUUID);

        for(Map.Entry<String, String> mandatoryGuiEntry : registrar.getMandatoryInventories().entrySet()) {

            String keyId = mandatoryGuiEntry.getKey();
            String displayName = mandatoryGuiEntry.getValue();

            if(!setupIdentity.getValues().containsKey(keyId)) {
                isMandatoryFieldMissing = true;
                missingInventoriesDisplayName.add(displayName);
            }
        }

        if(isMandatoryFieldMissing) {

            Runnable negativeRunnable = () -> {
                Bukkit.getScheduler().runTask(
                        instance,
                        () -> {

                            instance.getMessageHelper().sendMessage(player,
                                    LanguageKey.MANDATORY_CLOSE_ATTEMPT,
                                    Placeholder.unparsed(
                                            "missing",
                                            String.join(", ", missingInventoriesDisplayName)
                                    )
                            );

                            fallbackGui.show(player);
                        }
                );
            };

            this.checkOpeningNewCustomGui(player, negativeRunnable);

            return;
        }


        // Run closing actions
        instance.getGuiFactory()
                .getActionHandler()
                .handleActions(
                        gui.getId(),
                        ActionHandler.ActionEvent.CLOSE,
                        event
                );

    }

    void checkOpeningNewCustomGui(Player player, Runnable notCustomGuiRunnable) {

        Bukkit.getScheduler().runTaskLater(
                instance,
                () -> {

                    Inventory topInventory = player.getOpenInventory().getTopInventory();

                    boolean isCustomGui = this.instance.getGuiFactory().getCustomInventory(
                            player.getUniqueId(),
                            topInventory) != null;

                    boolean isAnvil = topInventory.getType() == org.bukkit.event.inventory.InventoryType.ANVIL;

                    if(!isAnvil && !isCustomGui)
                        notCustomGuiRunnable.run();


                }, 1L);

    }


}
