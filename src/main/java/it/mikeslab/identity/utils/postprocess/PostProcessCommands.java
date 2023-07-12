package it.mikeslab.identity.utils.postprocess;

import it.mikeslab.identity.Identity;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class PostProcessCommands {
    private Identity main;
    @Getter
    private ArrayList<PostCommand> postCommands;


    public void init(final Identity main) {
        this.postCommands = new ArrayList<>();
        this.main = main;

        String commandPath = "post-process.commands";
        if(main.getConfig().getConfigurationSection(commandPath).getKeys(false).size() == 0) {
            return;
        }

        for(String commandName : main.getConfig().getConfigurationSection(commandPath).getKeys(false)) {
            ConfigurationSection commandSection = main.getConfig().getConfigurationSection(commandPath+"."+commandName);
            postCommands.add(new PostCommand(commandSection.getString("command").replace("/", ""), SenderType.valueOf(commandSection.getString("executor").toUpperCase())));
        }
    }

    public void start(Player player) {

        if(postCommands.isEmpty()) {
            return;
        }

        for(PostCommand postCommand : postCommands) {
            String finalCommand = parseRawCommandArgs(postCommand.getCommand(), player);
            execute(postCommand.getSenderType(), finalCommand, player);
        }
    }


    private void execute(final SenderType senderType, final String command, final Player player) {

        if(senderType.equals(SenderType.PLAYER)) {
            player.performCommand(command);
        } else {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
        }

    }



    private String parseRawCommandArgs(String command, Player player) {
        return command.replace("{PLAYER}", player.getName())
                .replace("{X}", String.valueOf(player.getLocation().getX()))
                .replace("{Y}", String.valueOf(player.getLocation().getY()))
                .replace("{Z}", String.valueOf(player.getLocation().getZ()));
    }






}
