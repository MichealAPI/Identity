package it.mikeslab.identity.utils.inventory;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import it.mikeslab.identity.utils.postprocess.SenderType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class GenderCommands {
    private final HashBasedTable<String, SenderType, String> commands;


    public GenderCommands(FileConfiguration config, String gender, Player player) {
        this.commands = HashBasedTable.create();
        build(config.getConfigurationSection("post-process.gender-commands"));
        for(Table.Cell<String, SenderType, String> commandsCell : commands.cellSet()) {
            if(gender.equalsIgnoreCase(commandsCell.getRowKey())) {
                execute(commandsCell.getColumnKey(), commandsCell.getValue(), player);
            }
        }

    }

    private void execute( SenderType senderType,  String command,  Player player) {
        if(senderType.equals(SenderType.PLAYER)) {
            player.performCommand(parseRawCommandArgs(command, player));
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parseRawCommandArgs(command, player));
        }

    }

    private void addCommand( String gender,  SenderType senderType,  String command) {
        commands.put(gender, senderType, command);
    }

    private void build( ConfigurationSection section) {
        for(String gender : section.getKeys(false)) {
            addCommand(gender, SenderType.valueOf(section.getString(gender + ".executor")), section.getString(gender + ".command"));
        }
    }


    private String parseRawCommandArgs(String command, Player player) {
        return command.replace("{PLAYER}", player.getName())
                .replace("{X}", String.valueOf(player.getLocation().getX()))
                .replace("{Y}", String.valueOf(player.getLocation().getY()))
                .replace("{Z}", String.valueOf(player.getLocation().getZ()))
                .replace("/", "");
    }


}
