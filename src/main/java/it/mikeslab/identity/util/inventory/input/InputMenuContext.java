package it.mikeslab.identity.util.inventory.input;

import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * Represents the context of an input menu.
 */
@Getter
@RequiredArgsConstructor
public class InputMenuContext {

    private final String title,
            basePlaceholder;

    private final GuiElement clickableElement;

    private final int minLength, // -1 means no limit
                maxLength;

    private final int minWords,
                maxWords;

}
