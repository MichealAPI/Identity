package it.mikeslab.identity.inventory;

import com.google.common.collect.Multimap;
import it.mikeslab.commons.api.inventory.pojo.action.GuiAction;
import it.mikeslab.commons.api.inventory.util.action.ActionRegistrar;
import it.mikeslab.identity.inventory.config.GuiConfigRegistrar;
import it.mikeslab.identity.util.InventoryMap;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.Map;

@RequiredArgsConstructor
public class ActionRegistrarImpl implements ActionRegistrar {

    private final GuiConfigRegistrar guiConfigRegistrar;

    @Override
    public Multimap<String, GuiAction<?>> loadActions() {







    }


    private GuiAction<Player> getOpenGuiAction() {

        GuiAction openGuiAction = GuiAction
                .builder()
                .requiredClass(Player.class)
                .action((object, args) -> {

                    Player player = (Player) object;

                    // Get the gui from the args
                    Map<String, CustomInventory> inventoryMap = this.guiConfigRegistrar
                            .getPlayerInventories()
                            .get(player.getUniqueId());

                    // If the gui is not present, return
                    if (!inventoryMap.containsKey(args)) {
                        return;
                    }

                    // Open the gui
                    CustomInventory gui = inventoryMap.get(args);
                    gui.show(player);

                })
                .build();


    }



}
