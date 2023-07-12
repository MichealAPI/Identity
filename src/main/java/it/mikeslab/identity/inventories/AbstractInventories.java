package it.mikeslab.identity.inventories;

import de.themoep.inventorygui.InventoryGui;
import it.mikeslab.identity.Identity;
import it.mikeslab.identity.disk.Settings;
import it.mikeslab.identity.utils.PersonUtil;
import it.mikeslab.identity.utils.config.CustomConfigsInit;
import it.mikeslab.identity.utils.furnace.FurnaceGui;
import it.mikeslab.identity.utils.postprocess.PostProcessCommands;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.entity.Player;

public abstract class AbstractInventories {

    public abstract InventoryGui getNameChestGUI(Identity plugin, CustomConfigsInit customConfigsInit, boolean setup);

    public abstract InventoryGui getGenderChestGUI(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup);

    public abstract InventoryGui getAgeChestGUI(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup);

    public abstract FurnaceGui getAgeFurnaceGUI(Player player, Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup);

    public abstract AnvilGUI.Builder getNameAnvilGUI(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup);

    public abstract void openGUI(InventoryType inventory, Settings.InventoryType type, Identity plugin, Player player, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup);



}
