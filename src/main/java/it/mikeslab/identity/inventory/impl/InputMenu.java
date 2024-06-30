package it.mikeslab.identity.inventory.impl;

import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.pojo.GuiContext;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.lang.LanguageKey;
import it.mikeslab.identity.inventory.action.ActionListener;
import it.mikeslab.identity.pojo.Condition;
import it.mikeslab.identity.util.inventory.input.InputMenuContext;
import it.mikeslab.identity.util.inventory.input.InputMenuLoader;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.Optional;
import java.util.function.Supplier;

@Getter @Setter
public class InputMenu implements ActionListener {

    private final IdentityPlugin instance;
    private GuiContext guiContext;

    public InputMenu(IdentityPlugin instance, GuiContext guiContext) {
        this.instance = instance;
        this.guiContext = guiContext;
    }

    @Override
    public void show(Player player) {

        if(guiContext == null) return;

        InputMenuLoader inputMenuLoader = instance
                .getGuiConfigRegistrar()
                .getInputMenuLoader();

        InputMenuContext context = inputMenuLoader.load(
                guiContext.getRelativePath()
        );

        boolean closeOnFail = guiContext.isCloseOnFail();

        new AnvilGUI.Builder()
                .onClose(stateSnapshot -> this.openFallbackGui(instance, player))
                .onClick((slot, stateSnapshot) -> { // Either use sync or async variant, not both

                    if(slot != AnvilGUI.Slot.OUTPUT) {
                        return Collections.emptyList();
                    }

                    String text = stateSnapshot.getText();

                    Supplier<Condition> conditionSupplier = () -> isValid(text, context);
                    boolean isInputValid = conditionSupplier.get().isValid();
                    String errorMessage = conditionSupplier.get().getErrorMessage().orElse(null);

                    if(isInputValid) {

                        // Saves to identity
                        this.handleInput(
                                text,
                                player,
                                context,
                                conditionSupplier
                        );

                        return Collections.emptyList();
                    }

                    if(!closeOnFail && errorMessage != null) {
                        return Collections.singletonList(
                                AnvilGUI.ResponseAction.replaceInputText(errorMessage)
                        );
                    }

                    this.openFallbackGui(instance, player);
                    return Collections.emptyList();

                })
                .text(context.getBasePlaceholder())
                .title(context.getTitle()) //set the title of the GUI (only works in 1.14+)
                .itemLeft(context.getClickableElement().create())
                .plugin(instance)
                .open(player);


    }

    private void handleInput(String input, Player player, InputMenuContext context, Supplier<Condition> condition) {

        GuiInteractEvent event = new GuiInteractEvent(
                player,
                Collections.singletonList(context.getClickableElement())
        );

        this.handleSelection(instance, Optional.of(() -> input), true, Optional.of(condition))
                .getAction()
                .accept(event, input);

    }


    private Condition isValid(String value, InputMenuContext context) {

        boolean result = true;
        String errorMessage = null;

        if(context.getMaxLength() != -1 && value.length() > context.getMaxLength()) {
            result = false;
            errorMessage = instance.getLanguage().getSerializedString(
                    LanguageKey.INPUT_TOO_LONG,
                    Placeholder.unparsed("max", context.getMaxLength() + "")
            );
        }

        if(context.getMinLength() != -1 && value.length() < context.getMinLength()) {
            result = false;
            errorMessage = instance.getLanguage().getSerializedString(
                    LanguageKey.INPUT_TOO_SHORT,
                    Placeholder.unparsed("min", context.getMinLength() + "")
            );
        }

        if(context.getMinWords() != -1 && value.split(" ").length < context.getMinWords()) {
            result = false;
            errorMessage = instance.getLanguage().getSerializedString(
                    LanguageKey.INPUT_TOO_FEW_WORDS,
                    Placeholder.unparsed("min", context.getMinWords() + "")
            );
        }

        if(context.getMaxWords() != -1 && value.split(" ").length > context.getMaxWords()) {
            result = false;
            errorMessage = instance.getLanguage().getSerializedString(
                    LanguageKey.INPUT_TOO_MANY_WORDS,
                    Placeholder.unparsed("max", context.getMaxWords() + "")
            );
        }

        if(instance.getAntiSpamHandler().isSpam(value)) {
            result = false;
            errorMessage = instance.getLanguage().getSerializedString(
                    LanguageKey.INPUT_SPAM
            );
        }

        return new Condition(result, Optional.ofNullable(errorMessage));
    }



}
