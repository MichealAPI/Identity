package it.myke.identity.cmds;

import it.myke.identity.Identity;
import it.myke.identity.disk.Lang;
import it.myke.identity.disk.Settings;
import it.myke.identity.inventories.Inventories;
import it.myke.identity.inventories.InventoryType;
import it.myke.identity.utils.FormatUtils;
import it.myke.identity.utils.Legacy;
import it.myke.identity.utils.PersonUtil;
import it.myke.identity.utils.config.CustomConfigsInit;
import it.myke.identity.utils.postprocess.PostProcessCommands;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import redempt.ordinate.parser.metadata.CommandHook;

import static it.myke.identity.inventories.Inventories.inventories;

public class CommandListener {
    private final Identity main;
    private final FileConfiguration data;
    private final CustomConfigsInit customConfigsInit;
    private final PersonUtil personUtil;
    private final PostProcessCommands postProcessCommands;
    private final Inventories inventoriesUtil;

    public CommandListener(final Identity main, final CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, Inventories inventoriesUtil) {
        this.main = main;
        this.customConfigsInit = customConfigsInit;
        this.data = customConfigsInit.getDataConfig();
        this.personUtil = personUtil;
        this.postProcessCommands = postProcessCommands;
        this.inventoriesUtil = inventoriesUtil;
    }

    @CommandHook("reload")
    public void reload(CommandSender sender) {
        sender.sendMessage(Legacy.translate(MiniMessage.miniMessage().deserialize("<color:#00FF42>Reloading Configuration files...")));
        customConfigsInit.reloadConfigs();

        new Settings(customConfigsInit.getSettingsConfig()).init();
        new Lang(customConfigsInit.getLangConfig()).init();
        sender.sendMessage(Legacy.translate(MiniMessage.miniMessage().deserialize("<color:#AEFF00>Configs reload process completed!")));
    }


    @CommandHook("removePlayer")
    public void removePlayer(CommandSender sender, OfflinePlayer offlinePlayer) {
        if(customConfigsInit.getDataConfig().isConfigurationSection("data."+ offlinePlayer.getUniqueId())) {
            customConfigsInit.getDataConfig().set("data." + offlinePlayer.getUniqueId(), null);
            customConfigsInit.reload("data.yml");

            sender.sendMessage(Legacy.translate(Lang.PLAYER_RESET.replaceText(TextReplacementConfig.builder()
                    .matchLiteral("%removed%")
                    .replacement(offlinePlayer.getName()).build())));
            if(offlinePlayer.isOnline()) {
                offlinePlayer.getPlayer().kickPlayer(LegacyComponentSerializer.legacySection().serialize(Lang.IDENTITY_RESET_KICKMESSAGE));
            }
        } else {
            sender.sendMessage(Legacy.translate(Lang.PLAYER_NOT_FOUND.replaceText(TextReplacementConfig.builder()
                    .matchLiteral("%removed%").replacement(offlinePlayer.getName()).build())));
        }
    }

    @CommandHook("papi")
    public void papi(CommandSender sender) {
        sender.sendMessage(Legacy.translate(MiniMessage.miniMessage().deserialize("<color:#00E230>Available Placeholders <gray><bold>» </bold><white>%identity_name%<color:#00E230>,<white> %identity_surname%<color:#00E230>,<white> %identity_fullname%<color:#00E230>,<white> %identity_age%<color:#00E230>,<white> %identity_gender%")));
    }

    @CommandHook("copyright")
    public void copyright(CommandSender sender) {
        sender.sendMessage(Legacy.translate(MiniMessage.miniMessage().deserialize("\n<color:#00E230>This Server is running on the " + main.getDescription().getVersion() + " version of Identity! <gray>(MikesLab creation | www.mikeslab.it)\n")));
    }


    @CommandHook("showupdates")
    public void showupdates(CommandSender sender) {
        Player player = (Player) sender;
        
        inventories.get("update-" + main.getDescription().getVersion().replace(".", "-")).show((HumanEntity) sender);
    }


    @CommandHook("setName")
    public void setName(CommandSender sender, OfflinePlayer target, String firstName, String lastName)  {

        if(!Settings.NAME_ENABLED) {
            sender.sendMessage(Legacy.translate(Lang.NAME_DISABLED));
            return;
        }

        if(!Settings.LASTNAME_REQUIRED) {
            if(lastName == null) {
                sender.sendMessage(Legacy.translate(Lang.LASTNAME_REQUIRED));
                return;
            }

            String finalName = FormatUtils.firstUppercase(firstName) + " " + FormatUtils.firstUppercase(lastName);
            personUtil.setName(target.getUniqueId(), customConfigsInit, finalName);



            sender.sendMessage(Legacy.translate(Lang.NAME_SET_OTHER
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("%name%").replacement(finalName)
                            .matchLiteral("%player%").replacement(target.getName()).build())));

        } else {
            String finalName = FormatUtils.firstUppercase(firstName);
            personUtil.setName(target.getUniqueId(), customConfigsInit, finalName);
            sender.sendMessage(Legacy.translate(Lang.NAME_SET_OTHER
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("%name%").replacement(finalName)
                            .matchLiteral("%player%").replacement(target.getName()).build())));
        }
    }

    @CommandHook("setAge")
    public void setAge(CommandSender sender, OfflinePlayer target, int age) {

        if(!Settings.AGE_ENABLED) {
            sender.sendMessage(Legacy.translate(Lang.AGE_DISABLED));
            return;
        }

        if(age < Settings.MIN_AGE) {
            sender.sendMessage(Legacy.translate(Lang.MIN_AGE_REACHED
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("%age%").replacement(String.valueOf(age)).build())));
            return;
        }

        if(age > Settings.MAX_AGE) {
            sender.sendMessage(Legacy.translate(Lang.MAX_AGE_REACHED
                    .replaceText(TextReplacementConfig.builder()
                            .matchLiteral("%age%").replacement(String.valueOf(age)).build())));
            return;
        }

        personUtil.setAge(target.getUniqueId(), customConfigsInit, age);
        sender.sendMessage(Legacy.translate(Lang.AGE_SET_OTHER
                .replaceText(TextReplacementConfig.builder()
                        .matchLiteral("%age%").replacement(String.valueOf(age))
                        .matchLiteral("%player%").replacement(target.getName()).build())));

    }

    @CommandHook("setGender")
    public void setGender(CommandSender sender, OfflinePlayer target, String gender) {
        
        if(!Settings.GENDER_ENABLED) {
            sender.sendMessage(Legacy.translate(Lang.GENDER_DISABLED));
            return;
        }

        final String male = Lang.MALE_GENDER;
        final String female = Lang.FEMALE_GENDER;
        final String nonbinary = Lang.NON_BINARY_GENDER;

        if(gender.equalsIgnoreCase(male)) {
            personUtil.setGender(((Player) sender).getUniqueId(), customConfigsInit, gender);
            sender.sendMessage(Legacy.translate(Lang.GENDER_SET_OTHER.replaceText(TextReplacementConfig.builder()
                    .matchLiteral("%player%").replacement(target.getName())
                    .matchLiteral("%gender%").replacement(male).build())));
        } else if(gender.equalsIgnoreCase(female)) {
            personUtil.setGender(((Player) sender).getUniqueId(), customConfigsInit, gender);
            sender.sendMessage(Legacy.translate(Lang.GENDER_SET_OTHER.replaceText(TextReplacementConfig.builder()
                    .matchLiteral("%player%").replacement(target.getName())
                    .matchLiteral("%gender%").replacement(female).build())));
        }else if(gender.equalsIgnoreCase(nonbinary)) {
            personUtil.setGender(((Player) sender).getUniqueId(), customConfigsInit, gender);
            sender.sendMessage(Legacy.translate(Lang.GENDER_SET_OTHER.replaceText(TextReplacementConfig.builder()
                    .matchLiteral("%player%").replacement(target.getName())
                    .matchLiteral("%gender%").replacement(nonbinary).build())));
        } else sender.sendMessage(Legacy.translate(Lang.GENDER_NOT_VALID.replaceText(TextReplacementConfig.builder()
                .matchLiteral("%gender%").replacement(gender).build())));
    }



    @CommandHook("editGender")
    public void editGender(CommandSender sender) {
        Player player = (Player) sender;
        
        if(!Settings.GENDER_ENABLED) {
            player.sendMessage(Legacy.translate(Lang.GENDER_DISABLED));
            return;
        }

        if(!customConfigsInit.getDataConfig().isConfigurationSection("data." + ((Player) sender).getUniqueId())) {
            player.sendMessage(Legacy.translate(Lang.IDENTITY_AINT_SET));
            return;
        }

        PersonUtil personUtil = new PersonUtil();
        personUtil.addPerson(((Player) sender).getUniqueId(), -1, null, null);
        inventoriesUtil.openGUI(InventoryType.GENDER, Settings.InventoryType.CHEST, main, ((Player) sender).getPlayer(), customConfigsInit, personUtil, postProcessCommands, false);

    }

    @CommandHook("editAge")
    public void editAge(CommandSender sender) {
        Player player = (Player) sender;
        if(!Settings.AGE_ENABLED) {
            player.sendMessage(Legacy.translate(Lang.AGE_DISABLED));
            return;
        }

        if(!customConfigsInit.getDataConfig().isConfigurationSection("data." + ((Player) sender).getUniqueId())) {
            player.sendMessage(Legacy.translate(Lang.IDENTITY_AINT_SET));
            return;
        }

        PersonUtil personUtil = new PersonUtil();
        personUtil.addPerson(((Player) sender).getUniqueId(), -1, null, null);
        inventoriesUtil.openGUI(InventoryType.AGE, Settings.INVENTORY_AGE_TYPE, main, ((Player) sender).getPlayer(), customConfigsInit, personUtil, postProcessCommands, false);

    }

    @CommandHook("editName")
    public void editName(CommandSender sender) {
        Player player = (Player) sender;
        if(!Settings.NAME_ENABLED) {
            player.sendMessage(Legacy.translate(Lang.NAME_DISABLED));
            return;
        }

        if(!customConfigsInit.getDataConfig().isConfigurationSection("data." + ((Player) sender).getUniqueId())) {
            player.sendMessage(Legacy.translate(Lang.IDENTITY_AINT_SET));
            return;
        }

        PersonUtil personUtil = new PersonUtil();
        personUtil.addPerson(((Player) sender).getUniqueId(), -1, null, null);
        inventoriesUtil.openGUI(InventoryType.NAME, Settings.INVENTORY_NAME_TYPE, main, ((Player) sender).getPlayer(), customConfigsInit, personUtil, postProcessCommands, false);
    }


    @CommandHook("setup")
    public void setup(CommandSender sender) {
        personUtil.setup((Player) sender, customConfigsInit, main, postProcessCommands, inventoriesUtil, true);
    }








}
