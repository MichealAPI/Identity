package it.mikeslab.identity.util.inventory.input;

import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.inventory.config.GuiConfigImpl;
import it.mikeslab.commons.api.inventory.pojo.GuiElement;
import it.mikeslab.identity.IdentityPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
public class InputMenuLoader {

    private final IdentityPlugin instance;

    private final Map<String, InputMenuContext> inputMenusCache = new HashMap<>();

    public InputMenuContext load(Path relativePath) {

        File configFile = new File(instance.getDataFolder(), relativePath.toString());
        String fileName = configFile.getName();

        if (inputMenusCache.containsKey(fileName)) {
            return inputMenusCache.get(fileName);
        }

        // No checks are needed. Already done in the GuiConfigRegistrar#createInventory method

        YamlConfiguration config = YamlConfiguration.loadConfiguration(configFile);


        String title = config.getString(
                ComponentsUtil.getSerializedComponent(InputMenuField.TITLE.getField()),
                " " // Empty text
        );

        String basePlaceholder = config.getString(
                InputMenuField.BASE_PLACEHOLDER.getField(),
                " " // Empty text
        );

        int minLength = config.getInt(
                InputMenuField.MIN_LENGTH.getField(),
                -1 // No limit
        );

        int maxLength = config.getInt(
                InputMenuField.MAX_LENGTH.getField(),
                -1 // No limit
        );

        int minWords = config.getInt(
                InputMenuField.MIN_WORDS.getField(),
                -1 // No limit
        );

        int maxWords = config.getInt(
                InputMenuField.MAX_WORDS.getField(),
                -1 // No limit
        );

        GuiElement clickableElement = new GuiConfigImpl(instance).loadElement(
                config.getConfigurationSection(InputMenuField.CLICKABLE_ELEMENT.getField())
        );

        if(clickableElement == null) {
            return null;
        }


        InputMenuContext inputMenuContext = new InputMenuContext(
                title,
                basePlaceholder,
                clickableElement,
                minLength,
                maxLength,
                minWords,
                maxWords
        );


        // Load the file

        inputMenusCache.put(fileName, inputMenuContext);

        return inputMenuContext;

    }


    @Getter
    @RequiredArgsConstructor
    private enum InputMenuField {

        TITLE("title"),
        BASE_PLACEHOLDER("basePlaceholder"),
        CLICKABLE_ELEMENT("element"),
        MIN_LENGTH("minLength"),
        MAX_LENGTH("maxLength"),
        MIN_WORDS("minWords"),
        MAX_WORDS("maxWords");

        private final String field;

    }


}
