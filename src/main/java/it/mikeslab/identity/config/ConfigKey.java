package it.mikeslab.identity.config;

import it.mikeslab.commons.api.config.ConfigurableEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConfigKey implements ConfigurableEnum {

    MAX_AGE("max-age", 100),
    MIN_AGE("min-age", 0),
    DEFAULT_AGE("default-age", 18),
    MAX_NAME_LENGTH("max-name-length", 16),
    MIN_NAME_LENGTH("min-name-length", 3),
    LASTNAME_ENABLED("lastname-enabled", true),

    // todo this needs to be structured better
    ANVIL_CONFIRM_ITEM_MATERIAL("anvil-confirm-item-material", "MAP"),
    ANVIL_CONFIRM_ITEM_MODEL_DATA("anvil-confirm-item-model-data", 0),

    CHAT_FORMAT("chat-format", "<symbol> <yellow><age> <gray><firstName> <lastName><blue>: <white><message>"),

    MALE_SYMBOL("male-symbol", "♂"),
    FEMALE_SYMBOL("female-symbol", "♀"),
    OTHER_SYMBOL("other-gender-symbol", "⚧"),

    COMMAND_ALIASES("command-aliases", "identity|id"),
    ENABLE_CHAT_FORMATTER("enable-chat-formatter", true),

    KICK_IF_SAVING_ERROR("kick-if-saving-error", true),
    FIRST_CHAR_UPPERCASE("first-char-uppercase", true),

    CHAT_DISTANCE("chat-distance", -1),

    MANDATORY_FIELDS_NAME("mandatory-fields.name", true),
    MANDATORY_FIELDS_AGE("mandatory-fields.age", true),
    MANDATORY_FIELDS_GENDER("mandatory-fields.gender", true);

    private final String path;
    private final Object defaultValue;

}
