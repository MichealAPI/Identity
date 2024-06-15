package it.mikeslab.identity.inventory.action;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import it.mikeslab.commons.api.inventory.pojo.action.GuiAction;
import it.mikeslab.commons.api.inventory.util.action.ActionRegistrar;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.inventory.CustomInventory;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

@RequiredArgsConstructor
public class ActionRegistrarImpl implements ActionRegistrar {

    private final IdentityPlugin instance;

    @Override
    public Multimap<String, GuiAction> loadActions() {

        Multimap<String, GuiAction> actionsMap = ArrayListMultimap.create();;

        // Register the open gui action
        actionsMap.put("open", getOpenGuiAction());
        actionsMap.put("message", sendMessageToPlayer());
        actionsMap.put("player", executeCommandForPlayer());
        actionsMap.put("console", executeCommandForConsole());
        actionsMap.put("title", sendTitleToPlayer());

        return actionsMap;

    }

    private GuiAction sendMessageToPlayer() {
        return new GuiAction((event, args) -> {

            Player player = event.getWhoClicked();
            player.sendMessage(args);
        });
    }

    private GuiAction executeCommandForPlayer() {
        return new GuiAction((event, args) -> {

            Player player = event.getWhoClicked();
            player.performCommand(args);

        });
    }


    private GuiAction executeCommandForConsole() {
        return new GuiAction((event, args) -> {

            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            console.sendMessage(args);

        });
    }

    private GuiAction sendTitleToPlayer() {
        return new GuiAction((event, args) -> {

            Player player = event.getWhoClicked();
            String[] split = args.split(";");
            player.sendTitle(split[0], split[1], 10, 70, 20);

        });
    }


    private GuiAction getOpenGuiAction() {
        return new GuiAction((event, args) -> {

                    Player player = event.getWhoClicked();

                    // Get the gui from the args
                    Map<String, CustomInventory> inventoryMap = instance.getGuiConfigRegistrar()
                            .getPlayerInventories()
                            .get(player.getUniqueId());

                    // If the gui is not present, return
                    if (!inventoryMap.containsKey(args)) {
                        return;
                    }

                    // Open the gui
                    CustomInventory gui = inventoryMap.get(args);
                    gui.show(player);

                });
    }



}
