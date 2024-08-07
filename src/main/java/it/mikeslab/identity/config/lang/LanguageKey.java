package it.mikeslab.identity.config.lang;

import it.mikeslab.commons.api.config.ConfigurableEnum;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * The keys of the language file
 */
@Getter
@RequiredArgsConstructor
public enum LanguageKey implements ConfigurableEnum {

   KICK_MESSAGE_RESET_IDENTITY("kick-message-reset-identity", "Your identity has been reset"),
   IDENTITY_SAVED("identity-saved", "Identity saved"),
   ERROR_WHILE_SAVING_IDENTITY("error-while-saving-identity", "Error while saving identity. Please, retry again later"),
   MISSING_IDENTITY_DATA("missing-identity-data", "Missing identity data"),
   IDENTITY_RESET_FOR("identity-reset-for", "Identity reset for <player>"),
   IDENTITY_NOT_FOUND("identity-not-found", "Identity not found for <player>"),
   INPUT_TOO_LONG("input-too-long", "Input too long. Max length: <max>"),
   INPUT_TOO_SHORT("input-too-short", "Input too short. Min length: <min>"),
   INPUT_TOO_MANY_WORDS("input-too-many-words", "Input too many words. Max words: <max>"),
   INPUT_TOO_FEW_WORDS("input-too-few-words", "Input too few words. Min words: <min>"),
   VALUE_OUT_OF_RANGE("value-out-of-range", "Value out of range. Min: <min>, Max: <max>"),
   INPUT_SPAM("input-spam", "Spam detected"),
   MANDATORY_CLOSE_ATTEMPT("mandatory-close-attempt", "These inventories are mandatory to be completed: <missing>"),
   IDENTITY_ALREADY_SET("identity-already-set", "Identity already set"),
   IDENTITY_SETUP_START("identity-setup-start", "Identity setup started"),
   UNSET_VALUE("unset-value", "Unset value"),
   RELOAD_SUCCESS("reload-success", "Reload successful"),
   RELOAD_KICK_CAUSE("reload-kick-cause", "You have been kicked because of the configuration changes, please rejoin"),
   PRESET_DOESNT_EXISTS("preset-doesnt-exist", "The file <file> does not exist in the presets folder."),
   PRESET_EXTRACT_ERROR("preset-extract-error", "An error occurred while extracting the preset, check the console for more details."),
   PRESET_NO_INVENTORY_SECTION("preset-no-inventory-section", "The preset config does not contain any inventory section."),
   PRESET_INVALID_ENTRIES("preset-invalid-entries", "The preset config contains invalid entries."),
   PRESET_LOADED("preset-loaded", "The preset <file> has been successfully loaded.");


   private final String path;
   private final Object defaultValue;

}
