package it.mikeslab.identity.util;

import it.mikeslab.commons.api.config.Configurable;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.identity.Gender;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.lang.LanguageKey;
import it.mikeslab.identity.handler.SetupCacheHandler;
import it.mikeslab.identity.pojo.Identity;
import lombok.experimental.UtilityClass;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@UtilityClass
public class InventoryUtil {


//    public void setPlaceholders(IdentityPlugin instance, Player player, GuiDetails guiDetails) {
//
//        SetupCacheHandler setupCacheHandler = instance.getSetupCacheHandler();
//        Configurable language = instance.getLanguage();
//
//        UUID uuid = player.getUniqueId();
//        Identity identity = setupCacheHandler.getIdentity(uuid);
//
//        String firstName = identity.getFirstName() == null ? language.getString(LanguageKey.MISSING_FIRST_NAME) : identity.getFirstName();
//        String lastName = identity.getLastName() == null ? "" : identity.getLastName(); // "" because it's optional
//        String age = identity.getAge() == -1 ? language.getString(LanguageKey.MISSING_AGE) : String.valueOf(identity.getAge());
//        String gender = identity.getGender() == null ? language.getString(LanguageKey.MISSING_GENDER) :
//                Gender.valueOf(identity.getGender())
//                        .getGenderName(language);
//
//        guiDetails.setPlaceholders(
//                Map.of(
//                        "{age}", age,
//                        "{firstName}", firstName,
//                        "{lastName}", lastName,
//                        "{player}", player.getName(),
//                        "{gender}", gender
//                )
//        );
//
//    }


}
