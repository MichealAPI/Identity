package it.mikeslab.identity.event;

import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.event.GuiCloseEvent;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.ConfigKey;
import it.mikeslab.identity.config.lang.LanguageKey;
import it.mikeslab.identity.inventory.CustomInventory;
import it.mikeslab.identity.inventory.InventoryType;
import it.mikeslab.identity.inventory.config.GuiConfigRegistrar;
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
import java.util.function.Consumer;

@RequiredArgsConstructor
public class GuiCloseListener implements Listener {

    private final IdentityPlugin instance;

    @EventHandler
    public void onGuiClose(GuiCloseEvent event) {

        Player player = (Player) event.getEvent().getPlayer();
        UUID playerUUID = player.getUniqueId();

        int closedGuiId = event.getClosedGui().getId();

        if(instance.getGuiFactory().getCustomGui(closedGuiId) == null) return;

        if(event.getClosedGui().getGuiFactory() != instance.getGuiFactory()) return;

        GuiConfigRegistrar registrar = instance.getGuiConfigRegistrar();

        registrar.fromCustomGuiId(closedGuiId, playerUUID).ifPresent(guiKey -> {

            // An 'id' is an auto-generated value from the gui factory
            // GuiKey is a configuration key that is used to identify the gui

            CustomInventory targetInventory = registrar
                    .getPlayerInventories()
                    .get(playerUUID)
                    .get(guiKey);

            CustomInventory fallbackGui = registrar
                    .getPlayerInventories()
                    .get(playerUUID)
                    .get(
                            registrar.getFallbackGuiIdentifier()
                    );

            if(fallbackGui == null) return;

            if(targetInventory.getInventoryType() == InventoryType.MAIN) {
                this.manageSaving(event, fallbackGui, registrar);
                return;
            }


            checkOpeningNewCustomGui(player, () -> {

                // do this if it is not opening a custom gui within 2 (//todo 1?) milliseconds

                // todo is mandatory and is completed return check? Is it still useful?

                Bukkit.getScheduler().runTask(
                        instance,
                        () -> fallbackGui.show(player)
                );

            });

        });
    }


    /**
     * Manage the saving of the inventory
     * @param event The {@link org.bukkit.event.inventory.InventoryCloseEvent} event
     * @param fallbackGui The fallback gui
     * @param registrar The registrar
     */
    private void manageSaving(GuiCloseEvent event, CustomInventory fallbackGui, GuiConfigRegistrar registrar) {

        boolean isMandatoryFieldMissing = false;

        UUID playerUUID = event.getEvent().getPlayer().getUniqueId();

        List<String> missingInventoriesDisplayName = new ArrayList<>();

        for(Map.Entry<String, String> mandatoryGuiEntry : registrar.getMandatoryInventories().entrySet()) {

            String keyId = mandatoryGuiEntry.getKey();
            String displayName = mandatoryGuiEntry.getValue();

            CustomInventory inventory = registrar
                    .getPlayerInventories()
                    .get(playerUUID)
                    .get(keyId);

            if(!inventory.isCompleted()) {
                isMandatoryFieldMissing = true;
                missingInventoriesDisplayName.add(displayName);
            }
        }

        if(isMandatoryFieldMissing) {

            Player player = (Player) event.getEvent().getPlayer();

            checkOpeningNewCustomGui(player, () -> {
                Bukkit.getScheduler().runTask(
                        instance,
                        () -> {

                            player.sendMessage(
                                    instance.getLanguage()
                                            .getSerializedString(
                                                    LanguageKey.MANDATORY_CLOSE_ATTEMPT,
                                                    Placeholder.unparsed(
                                                            "missing",
                                                            String.join(", ", missingInventoriesDisplayName)
                                                    )
                                            ));

                            fallbackGui.show(player);
                        }
                );
            });
        }
    }

    void checkOpeningNewCustomGui(Player player, Runnable notCustomGuiRunnable) {

        Bukkit.getScheduler().runTaskLater(
                instance,
                () -> {

                    Inventory topInventory = player.getOpenInventory().getTopInventory();

                    boolean isCustomGui = topInventory.getHolder() instanceof CustomGui;
                    boolean isAnvil = topInventory.getType() == org.bukkit.event.inventory.InventoryType.ANVIL;

                    if(!isAnvil && !isCustomGui)
                        notCustomGuiRunnable.run();


                }, 1L);


    }


}
