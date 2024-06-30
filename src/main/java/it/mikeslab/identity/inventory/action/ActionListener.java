package it.mikeslab.identity.inventory.action;

import it.mikeslab.commons.api.inventory.CustomInventory;
import it.mikeslab.commons.api.inventory.pojo.action.GuiAction;
import it.mikeslab.commons.api.logger.LogUtils;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.pojo.Condition;
import it.mikeslab.identity.pojo.Identity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;

public interface ActionListener extends CustomInventory {

    /**
     * Injects an action into the action handler
     * @param prefix the prefix of the action
     * @param action the action to inject
     */
     default void injectAction(IdentityPlugin instance, String prefix, GuiAction action) {

        instance.getActionHandler().injectAction(
                this.getId(),
                prefix,
                action
        );

    }

    /**
     * Injects an action into the action handler with a condition
     * @param prefix the prefix of the action
     * @param action the action to inject
     * @param condition the condition to check before executing the action
     */
    default void injectAction(IdentityPlugin instance, String prefix, GuiAction action, Supplier<Condition> condition) {

         GuiAction mergedAction = new GuiAction((event, args) -> {

             if(!condition.get().isValid()) {
                    condition.get().getErrorMessage().ifPresent(s -> event.getWhoClicked().sendMessage(s));
                    return;
             }

             action.getAction().accept(event, args);


         });

         this.injectAction(instance, prefix, mergedAction);

    }

    /**
     * Handles the selection of a value, used my most implementations
     * @param value the value to set, if present. Defaults to args
     * @param openFallback whether to open the fallback gui after the selection
     */
    default GuiAction handleSelection(IdentityPlugin instance, Optional<Supplier<String>> value, boolean openFallback, Optional<Supplier<Condition>> condition) {
        // todo double condition check? Here and in the inject action method

        return new GuiAction((event, args) -> {

            // Condition check
            if(condition.isPresent()) {
                Condition conditionValue = condition.get().get();

                boolean isValid = conditionValue.isValid();
                Optional<String> errorMessage = conditionValue.getErrorMessage();

                if(!isValid) {
                    errorMessage.ifPresent(s -> event.getWhoClicked().sendMessage(s));
                    return;
                }
            }

            String selectedValue = args;

            if(value.isPresent()) {
                selectedValue = value
                        .get()
                        .get();
            }

            Player player = event.getWhoClicked();
            UUID playerUUID = player.getUniqueId();

            Identity identity = instance
                    .getSetupCacheHandler()
                    .getIdentity(playerUUID);

            if (identity == null) {
                LogUtils.warn(
                        LogUtils.LogSource.UTIL,
                        "Could not find identity for player with UUID: " + playerUUID + " during setup."
                );
                return;
            }

            // Update the identity
            instance.getSetupCacheHandler().updateIdentity(
                    playerUUID,
                    new AbstractMap.SimpleEntry<>(
                            this.getGuiContext().getFieldIdentifier(),
                            selectedValue
                    )
            );

            if(openFallback) {

                String fallbackIdentifier = instance.getGuiConfigRegistrar().getFallbackGuiIdentifier();

                // This force-regen the main inventory that may contain the
                // now updated placeholder value
                instance.getGuiConfigRegistrar()
                        .getPlayerInventories()
                        .forceExpiration(
                                playerUUID,
                                fallbackIdentifier
                        );

                this.openFallbackGui(
                        instance,
                        player
                );
            }

        });
    }

    /**
     * Opens the fallback gui
     * @param player the player to open the gui for
     */
    default void openFallbackGui(IdentityPlugin instance, Player player) {

        String fallbackIdentifier = instance.getGuiConfigRegistrar().getFallbackGuiIdentifier();

        CustomInventory fallbackInventory = instance.getGuiConfigRegistrar()
                .getPlayerInventories()
                .getInventory(
                        player.getUniqueId(),
                        fallbackIdentifier
                );

        Bukkit.getScheduler().runTask(instance, () -> {
            fallbackInventory.show(player);
        });
    }
}
