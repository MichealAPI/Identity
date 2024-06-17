package it.mikeslab.identity.inventory.impl;

import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.inventory.CustomInventoryContext;
import it.mikeslab.identity.inventory.action.ActionListener;
import it.mikeslab.identity.pojo.InventorySettings;
import it.mikeslab.identity.util.inventory.input.InputMenuContext;
import it.mikeslab.identity.util.inventory.input.InputMenuLoader;
import lombok.Data;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Optional;

@Data
public class InputMenu implements ActionListener {

    private CustomInventoryContext customContext; // No context for this inventory
    private IdentityPlugin instance;

    public InputMenu(IdentityPlugin instance, InventorySettings settings) {
        this.instance = instance;

        this.customContext = new CustomInventoryContext(
                instance,
                settings
        );
    }

    @Override
    public void show(Player player) {

        if(customContext == null) return;

        InputMenuLoader inputMenuLoader = instance
                .getGuiConfigRegistrar()
                .getInputMenuLoader();

        InputMenuContext context = inputMenuLoader.load(
                customContext
                        .getSettings()
                        .getRelativePath()
        );

        boolean closeOnFail = customContext.getSettings().isCloseOnFail();

        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> {
                    this.openFallbackGui(player);
                })
                .onClick((slot, stateSnapshot) -> { // Either use sync or async variant, not both
                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }

                    String text = stateSnapshot.getText();

                    boolean isInputValid = isValid(text, context);
                    if(isInputValid) {

                        // Saves to identity
                        this.handleInput(
                                text,
                                player,
                                context
                        );

                        return Collections.emptyList();
                    }

                    if(!closeOnFail) {
                        return Collections.singletonList(
                                AnvilGUI.ResponseAction.replaceInputText(context.getErrorPlaceholder())
                        );
                    }

                    this.openFallbackGui(player);
                    return Collections.emptyList();
                })
                .text(context.getBasePlaceholder())
                .title(context.getTitle()) //set the title of the GUI (only works in 1.14+)
                .itemRight(context.getClickableElement().create())
                .plugin(instance)
                .open(player);


    }

    private void handleInput(String input, Player player, InputMenuContext context) {

        GuiInteractEvent event = new GuiInteractEvent(
                player,
                context.getClickableElement()
        );

        this.handleSelection(Optional.of(() -> input), true)
                .getAction()
                .accept(event, input);

    }


    boolean isValid(String value, InputMenuContext context) {

        boolean result = true;

        if(context.getMaxLength() != -1) {
            result = value.length() <= context.getMaxLength();
        }

        if(context.getMinLength() != -1) {
            result = value.length() >= context.getMinLength();
        }

        if(context.getMinWords() != -1) {
            result = value.split(" ").length >= context.getMinWords();
        }

        if(context.getMaxWords() != -1) {
            result = value.split(" ").length <= context.getMaxWords();
        }

        // todo spam flags

        return result;
    }


}
