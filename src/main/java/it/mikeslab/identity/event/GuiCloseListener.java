package it.mikeslab.identity.event;

import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.event.GuiCloseEvent;
import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.inventory.CustomInventory;
import it.mikeslab.identity.inventory.InventoryType;
import it.mikeslab.identity.inventory.config.GuiConfigRegistrar;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

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

            // Id is an auto-generated value from the gui factory
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


            checkOpeningNewCustomGui(player, (unused) -> {

                // do this if it is not opening a custom gui within 2 (//todo 1?) milliseconds

                if (!registrar.isMandatory(guiKey)) return;

                if (registrar.isCompleted(guiKey, playerUUID)) return;

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

        for(String mandatoryGuiKey : registrar.getMandatoryInventories()) {

            CustomInventory inventory = registrar
                    .getPlayerInventories()
                    .get(playerUUID)
                    .get(mandatoryGuiKey);

            if(!inventory.isCompleted()) {
                isMandatoryFieldMissing = true;
                break;
            }
        }

        if(isMandatoryFieldMissing) {

            Player player = (Player) event.getEvent().getPlayer();

            checkOpeningNewCustomGui(player, (unused) -> {
                Bukkit.getScheduler().runTask(
                        instance,
                        () -> fallbackGui.show(player)
                );
            });

            // todo send message/sound to player notifying that at least one field is missing
            //      you may want also to add an identifier for the missing field that needs to be
            //      completed
        }
    }

    void checkOpeningNewCustomGui(Player player, Consumer<Void> notCustomGuiConsumer) {

        Bukkit.getScheduler().runTaskLater(
                instance,
                () -> {

                    if(!(player.getOpenInventory().getTopInventory().getHolder() instanceof CustomGui))
                        notCustomGuiConsumer.accept(null);


                }, 1L);


    }


}
