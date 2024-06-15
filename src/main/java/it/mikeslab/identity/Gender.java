package it.mikeslab.identity;

import it.mikeslab.commons.api.config.Configurable;
import it.mikeslab.identity.config.ConfigKey;
import it.mikeslab.identity.config.lang.LanguageKey;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;

@Getter
@RequiredArgsConstructor
public enum Gender {

    FEMALE,
    MALE,
    OTHER;

    public String getGenderName(final Configurable langConfig) {
        return switch (this) {
            case MALE:
                yield langConfig.getString(LanguageKey.MALE_GENDER);
            case FEMALE:
                yield langConfig.getString(LanguageKey.FEMALE_GENDER);
            case OTHER:
                yield langConfig.getString(LanguageKey.OTHER_GENDER);
        };
    }

    public Component getSymbol(final Configurable customConfig) {
        return switch (this) {
            case MALE:
                yield customConfig.getComponent(ConfigKey.MALE_SYMBOL);
            case FEMALE:
                yield customConfig.getComponent(ConfigKey.FEMALE_SYMBOL);
            case OTHER:
                yield customConfig.getComponent(ConfigKey.OTHER_SYMBOL);
        };

    }

}
