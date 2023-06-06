package it.myke.identity.inventories;

import com.cryptomorin.xseries.XSound;
import it.myke.identity.Identity;
import it.myke.identity.disk.Lang;
import it.myke.identity.disk.Settings;
import it.myke.identity.obj.Person;
import it.myke.identity.utils.Legacy;
import it.myke.identity.utils.PersonUtil;
import it.myke.identity.utils.config.CustomConfigsInit;
import it.myke.identity.utils.inventory.GenderCommands;
import it.myke.identity.utils.postprocess.PostProcessCommands;
import org.bukkit.entity.Player;

public class InventoryManager {



    public String getNextInventory(Player player, PersonUtil personUtil) {
        if(personUtil.isPerson(player.getUniqueId())) {
            Person playerIdentity = personUtil.getPerson(player.getUniqueId());
            if(playerIdentity.getName() == null && Settings.NAME_ENABLED) {
                return "name";
            } else if(playerIdentity.getGender() == null && Settings.GENDER_ENABLED) {
                return "gender";
            } else if(playerIdentity.getAge() == -1 && Settings.AGE_ENABLED) {
                return "age";
            }
        }
        return null;
    }


    /**
     * This is needed because you can decide to disable a feature of this plugin, so that become necessary
     * to get the new step automatically.
     * @param player Player to execute the openInventory action
     */

    public void openNextInventory(Player player, Identity plugin, PersonUtil personUtil, Inventories inventories, PostProcessCommands postProcessCommands, CustomConfigsInit customConfigsInit, boolean setup) {
        if (setup) {
            String nxtInventory = getNextInventory(player, personUtil);
            if (nxtInventory == null) {
                player.sendMessage(Legacy.translate(Lang.SETUP_COMPLETED));
                customConfigsInit.saveInConfig(player.getUniqueId(), personUtil);
                String gender = personUtil.getPerson(player.getUniqueId()).getGender();
                personUtil.removePerson(player.getUniqueId());
                player.closeInventory();

                //Post setup commands
                new GenderCommands(plugin.getConfig(), gender, player);
                postProcessCommands.start(player);

                XSound.play(player, "ENTITY_EXPERIENCE_ORB_PICKUP");
            } else {
                player.closeInventory();
                switch (nxtInventory) {
                    case "name" -> inventories.openGUI(InventoryType.NAME, Settings.INVENTORY_NAME_TYPE, plugin, player, customConfigsInit, personUtil, postProcessCommands, setup);
                    case "gender" -> inventories.openGUI(InventoryType.GENDER, Settings.InventoryType.CHEST, plugin, player, customConfigsInit, personUtil, postProcessCommands, setup);
                    case "age" -> inventories.openGUI(InventoryType.AGE, Settings.INVENTORY_AGE_TYPE, plugin, player, customConfigsInit, personUtil, postProcessCommands, setup);
                }

                XSound.play(player, "BLOCK_NOTE_BLOCK_PLING");
            }
        }
    }


}
