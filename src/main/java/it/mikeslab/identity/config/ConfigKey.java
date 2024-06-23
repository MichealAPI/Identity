package it.mikeslab.identity.config;

import it.mikeslab.commons.api.config.ConfigurableEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ConfigKey implements ConfigurableEnum {

    CHAT_FORMAT("chat-format", "<symbol> <yellow><age> <gray><firstName> <lastName><blue>: <white><message>"),

    COMMAND_ALIASES("command-aliases", "identity|id"),
    ENABLE_CHAT_FORMATTER("enable-chat-formatter", true),

    KICK_IF_SAVING_ERROR("kick-if-saving-error", true),

    CHAT_DISTANCE("chat-distance", -1),

    MONGO_LOGGING("settings.mongo-info-logging", false),

    EXTRACT_DEFAULTS("settings.extract-defaults", true),

    ANIMATION_INTERVAL("settings.animation-interval", 2),

    ON_JOIN_SETUP("settings.on-join-setup", true),;

    private final String path;
    private final Object defaultValue;

}
