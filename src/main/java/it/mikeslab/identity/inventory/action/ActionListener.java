package it.mikeslab.identity.inventory.action;

import it.mikeslab.commons.api.inventory.pojo.action.GuiAction;
import it.mikeslab.commons.api.logger.LoggerUtil;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.inventory.CustomInventory;
import it.mikeslab.identity.pojo.Condition;
import it.mikeslab.identity.pojo.Identity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.AbstractMap;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.logging.Level;

public interface ActionListener extends CustomInventory {

    /**
     * Injects an action into the action handler
     * @param prefix the prefix of the action
     * @param action the action to inject
     */
     default void injectAction(String prefix, GuiAction action) {

        this.getInstance().getActionHandler().injectAction(
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
    default void injectAction(String prefix, GuiAction action, Supplier<Condition> condition) {

         GuiAction mergedAction = new GuiAction((event, args) -> {

             if(!condition.get().isValid()) {
                    condition.get().getErrorMessage().ifPresent(s -> event.getWhoClicked().sendMessage(s));
                    return;
             }

             action.getAction().accept(event, args);


         });

         this.injectAction(prefix, mergedAction);

    }

    /**
     * Handles the selection of a value, used my most implementations
     * @param value the value to set, if present. Defaults to args
     * @param openFallback whether to open the fallback gui after the selection
     */
    default GuiAction handleSelection(Optional<Supplier<String>> value, boolean openFallback, Optional<Supplier<Condition>> condition) {
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

            Identity identity = this
                    .getInstance()
                    .getSetupCacheHandler()
                    .getIdentity(playerUUID);

            if (identity == null) {
                LoggerUtil.log(
                        Level.WARNING,
                        LoggerUtil.LogSource.UTIL,
                        "Could not find identity for player with UUID: " + playerUUID + " during setup."
                );
                return;
            }

            // Update the identity
            this.getInstance().getSetupCacheHandler().updateIdentity(
                    playerUUID,
                    new AbstractMap.SimpleEntry<>(
                            this.getCustomContext().getSettings().getFieldIdentifier(),
                            selectedValue
                    )
            );

            // Set the completed flag
            this.setCompleted(true);

            if(openFallback) {
                this.openFallbackGui(player);
            }

        });
    }

    /**
     * Opens the fallback gui
     * @param player the player to open the gui for
     */
    default void openFallbackGui(Player player) {
        CustomInventory fallbackInventory = this.getInstance().getGuiConfigRegistrar()
                .getPlayerInventories()
                .get(player.getUniqueId())
                .get(this.getInstance().getGuiConfigRegistrar().getFallbackGuiIdentifier());


        Bukkit.getScheduler().runTask(this.getInstance(), () -> {
            fallbackInventory.show(player);
        });
    }
}
