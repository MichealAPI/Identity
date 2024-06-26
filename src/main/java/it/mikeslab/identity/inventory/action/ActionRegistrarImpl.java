package it.mikeslab.identity.inventory.action;

// import com.cryptomorin.xseries.XSound;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.inventory.pojo.action.GuiAction;
import it.mikeslab.commons.api.inventory.util.CustomInventory;
import it.mikeslab.commons.api.inventory.util.action.ActionRegistrar;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.util.SetupMap;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class ActionRegistrarImpl implements ActionRegistrar {

    private final IdentityPlugin instance;

    @Override
    public Multimap<String, GuiAction> loadActions() {

        Multimap<String, GuiAction> actionsMap = ArrayListMultimap.create();
        ;

        // Register the open gui action
        actionsMap.put("open", getOpenGuiAction());
        actionsMap.put("message", sendMessageToPlayer());
        actionsMap.put("player", executeCommandForPlayer());
        actionsMap.put("console", executeCommandForConsole());
        actionsMap.put("title", sendTitleToPlayer());
        actionsMap.put("sound", playSoundForPlayer());
        actionsMap.put("close", closeInventory());

        return actionsMap;

    }

    private GuiAction sendMessageToPlayer() {
        return new GuiAction((event, args) -> {

            Player player = event.getWhoClicked();
            player.sendMessage(ComponentsUtil.getSerializedComponent(args));
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
            player.sendTitle(
                    ComponentsUtil.getSerializedComponent(split[0]),
                    ComponentsUtil.getSerializedComponent(split[1]),
                    10,
                    70,
                    20
            );

        });
    }


    private GuiAction getOpenGuiAction() {
        return new GuiAction((event, args) -> {

            Player player = event.getWhoClicked();

            SetupMap setupMap = instance
                    .getGuiConfigRegistrar()
                    .getPlayerInventories();

            UUID playerUUID = player.getUniqueId();

            // If the gui is not present, return
            if (!setupMap.containsInventory(playerUUID, args)) {
                return;
            }

            // Open the gui
            CustomInventory gui = setupMap.getInventory(playerUUID, args);

            instance.getServer().getScheduler().runTask(instance, () -> {
                gui.show(player);
            });

        });
    }

    private GuiAction playSoundForPlayer() {
        return new GuiAction((ev, args) -> {

            Player target = ev.getWhoClicked();
            Location location = target.getLocation();

            // TODO FIX
            // XSound xSound = XSound.matchXSound(args).orElse(null);

            //if(xSound == null) {
            //     return;
            //}

            //xSound.play(location, 1, 1);
        });
    }

    private GuiAction closeInventory() {
        return new GuiAction((ev, args) -> {

            Player target = ev.getWhoClicked();
            target.closeInventory();

        });
    }

}
