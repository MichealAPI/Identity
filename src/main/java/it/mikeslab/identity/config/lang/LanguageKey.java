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

   MISSING_FIRST_NAME("missing.first-name", "Missing first name"),
   MISSING_AGE("missing.age", "Missing age"),
   MISSING_GENDER("missing.gender", "Missing Gender"),
   ENTER_YOUR_FIRST_NAME("enter-your-first-name", "Enter your first name"),
   ENTER_YOUR_FULL_NAME("enter-your-full-name", "Enter your full name"),
   ANVIL_INVALID_NAME("anvil.invalid-name", "Invalid name"),
   ANVIL_INVALID_LAST_NAME("anvil.invalid-last-name", "Invalid last name"),
   ANVIL_NAME_PLACEHOLDER("anvil.name-placeholder", "Name"),
   KICK_MESSAGE_RESET_IDENTITY("kick-message-reset-identity", "Your identity has been reset"),
   MALE_GENDER("male-gender", "Male"),
   OTHER_GENDER("other-gender", "Other"),
   FEMALE_GENDER("female-gender", "Female"),
   IDENTITY_SAVED("identity-saved", "Identity saved"),
   ERROR_WHILE_SAVING_IDENTITY("error-while-saving-identity", "Error while saving identity. Please, retry again later"),
   MISSING_IDENTITY_DATA("missing-identity-data", "Missing identity data"),
   IDENTITY_RESET_FOR("identity-reset-for", "Identity reset for <player>"),
   IDENTITY_NOT_FOUND("identity-not-found", "Identity not found for <player>"),


   INPUT_TOO_LONG("input-too-long", "Input too long. Max length: <max>"),
   INPUT_TOO_SHORT("input-too-short", "Input too short. Min length: <min>"),
   INPUT_TOO_MANY_WORDS("input-too-many-words", "Input too many words. Max words: <max>"),
   INPUT_TOO_FEW_WORDS("input-too-few-words", "Input too few words. Min words: <min>"),
   VALUE_OUT_OF_RANGE("value-out-of-range", "Value out of range. Min: <min>, Max: <max>"),;


   private final String path;
   private final Object defaultValue;

}
