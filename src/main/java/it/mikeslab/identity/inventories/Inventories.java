package it.mikeslab.identity.inventories;

import com.cryptomorin.xseries.XMaterial;
import com.cryptomorin.xseries.XSound;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import de.themoep.inventorygui.DynamicGuiElement;
import de.themoep.inventorygui.InventoryGui;
import de.themoep.inventorygui.StaticGuiElement;
import it.mikeslab.identity.Identity;
import it.mikeslab.identity.disk.Lang;
import it.mikeslab.identity.disk.Settings;
import it.mikeslab.identity.obj.Person;
import it.mikeslab.identity.update.UpdateReader;
import it.mikeslab.identity.update.UpdateShower;
import it.mikeslab.identity.utils.FormatUtils;
import it.mikeslab.identity.utils.Legacy;
import it.mikeslab.identity.utils.PersonUtil;
import it.mikeslab.identity.utils.config.CustomConfigsInit;
import it.mikeslab.identity.utils.furnace.FurnaceElement;
import it.mikeslab.identity.utils.furnace.FurnaceGui;
import it.mikeslab.identity.utils.furnace.FurnaceManager;
import it.mikeslab.identity.utils.furnace.TaskObject;
import it.mikeslab.identity.utils.inventory.Action;
import it.mikeslab.identity.utils.inventory.ActionElement;
import it.mikeslab.identity.utils.inventory.InventoryBuilder;
import it.mikeslab.identity.utils.postprocess.PostProcessCommands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static it.mikeslab.identity.utils.furnace.FurnaceManager.*;

public class Inventories extends AbstractInventories {
    public static HashMap<String, InventoryGui> inventories;
    public static HashMap<UUID, Boolean> processStarted = new HashMap<>();
    private HashMap<UUID, Long> cooldown;


    public Inventories(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        inventories = new HashMap<>();
        cooldown = new HashMap<>();
        if(Settings.NAME_ENABLED) inventories.put("name", getNameInventory(plugin, customConfigsInit, setup));
        if(Settings.GENDER_ENABLED) inventories.put("gender", getGenderChestGUI(plugin, customConfigsInit, personUtil, postProcessCommands, setup));
        if(Settings.AGE_ENABLED) inventories.put("age", getAgeChestGUI(plugin, customConfigsInit, personUtil, postProcessCommands, setup));

        ArrayList<String> registeredVersions = new UpdateReader().getVersions(plugin);

        if(registeredVersions != null) {
            int i = 0;
            for(String version : registeredVersions) {
                inventories.put("update-" + version, new UpdateShower().getNewUpdatesMenu(plugin, registeredVersions, i));
                i++;
            }
        }




    }

    public InventoryGui getNameInventory(Identity plugin, CustomConfigsInit customConfigsInit, boolean setup) {
        String[] rows = {
                "abcdefghi",
                "jklmnopqr",
                "stuvwxyzA",
        };
        InventoryBuilder inventoryBuilder = new InventoryBuilder().getBuilder(customConfigsInit, "name");
        InventoryGui inventoryGui = new InventoryGui(plugin, plugin.getNameHolder(), LegacyComponentSerializer.legacySection().serialize(inventoryBuilder.getTitle()), rows);
        inventoryGui.setFiller(inventoryBuilder.getFiller());

        for(ActionElement actionElement : inventoryBuilder.getElements()) {
            inventoryGui.addElement(actionElement.getCharPos(), actionElement.getStack(), click -> {
                if(actionElement.getAction() == Action.ENTER_NAME)
                    getNameListener((Player) click.getWhoClicked(), inventoryGui, setup);
                return true;
            });
        }


        return inventoryGui;
    }





    public static ItemStack getCustomStack() {
        ItemStack stack = XMaterial.matchXMaterial(Settings.ANVIL_GUI__ITEM__MATERIAL).parseItem();
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(Legacy.translate(Settings.ANVIL_GUI__ITEM__DISPLAY_NAME.decoration(TextDecoration.ITALIC, false)));
        meta.setLore(Legacy.translate(Settings.ANVIL_GUI__ITEM__LORE));
        meta.setCustomModelData(Settings.ANVIL_GUI__ITEM__CUSTOM_MODEL_DATA);
        stack.setItemMeta(meta);
        return stack;
    }




    private boolean getNameListener(Player player, InventoryGui inventoryGui, boolean setup) {
        processStarted.put(player.getUniqueId(), setup);
        inventoryGui.close();
        player.sendMessage(Legacy.translate(Lang.INSERT_NAME));
        if(Settings.NAME_TITLEBAR_ENABLED) {
            player.sendTitle(Legacy.translate(Lang.INSERT_NAME_TITLE), Legacy.translate(Lang.INSERT_NAME_SUBTITLE));
        }
        return true;
    }













    @Override
    public InventoryGui getNameChestGUI(Identity plugin, CustomConfigsInit customConfigsInit, boolean setup) {
        String[] rows = {
                "abcdefghi",
                "jklmnopqr",
                "stuvwxyzA",
        };
        InventoryBuilder inventoryBuilder = new InventoryBuilder().getBuilder(customConfigsInit, "name");
        InventoryGui inventoryGui = new InventoryGui(plugin, plugin.getNameHolder(), LegacyComponentSerializer.legacySection().serialize(inventoryBuilder.getTitle()), rows);
        inventoryGui.setFiller(inventoryBuilder.getFiller());

        for(ActionElement actionElement : inventoryBuilder.getElements()) {
            inventoryGui.addElement(actionElement.getCharPos(), actionElement.getStack(), click -> {
                if(actionElement.getAction() == Action.ENTER_NAME)
                    getNameListener((Player) click.getWhoClicked(), inventoryGui, setup);
                return true;
            });
        }


        return inventoryGui;
    }




    @Override
    public InventoryGui getGenderChestGUI(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        String[] rows = {
                "abcdefghi",
                "jklmnopqr",
                "stuvwxyzA",
        };
        InventoryManager inventoryManager = new InventoryManager();
        InventoryBuilder inventoryBuilder = new InventoryBuilder().getBuilder(customConfigsInit, "gender");
        InventoryGui inventoryGui = new InventoryGui(plugin, plugin.getGenderHolder(), LegacyComponentSerializer.legacySection().serialize(inventoryBuilder.getTitle()), rows);
        inventoryGui.setFiller(inventoryBuilder.getFiller());

        HashBasedTable<Action, String, Component> table = HashBasedTable.create();

        table.put(Action.FEMALE, Lang.FEMALE_GENDER, Lang.GENDER_FEMALE_SELECTED);
        table.put(Action.NONBINARY, Lang.NON_BINARY_GENDER, Lang.GENDER_NON_BINARY_SELECTED);
        table.put(Action.MALE, Lang.MALE_GENDER, Lang.GENDER_MALE_SELECTED);
        for(ActionElement actionElement : inventoryBuilder.getElements()) {
            inventoryGui.addElement(actionElement.getCharPos(), actionElement.getStack(), click -> {
                if(table.containsRow(actionElement.getAction())) {
                    Table.Cell<Action, String, Component> genderCell = table.cellSet().stream().filter(cell -> cell.getRowKey() == actionElement.getAction()).findFirst().get();
                    personUtil.getPerson(click.getWhoClicked().getUniqueId()).setGender(genderCell.getColumnKey());
                    ((Player) click.getWhoClicked()).sendMessage(Legacy.translate(genderCell.getValue()));
                }
                if (!setup) {
                    customConfigsInit.saveInConfig(click.getWhoClicked().getUniqueId(), personUtil);
                    ((Player) click.getWhoClicked()).sendMessage(Legacy.translate(Lang.GENDER_EDITED));
                    click.getWhoClicked().closeInventory();
                    return true;
                }

                inventoryManager.openNextInventory((Player) click.getWhoClicked(), plugin, personUtil, this, postProcessCommands, customConfigsInit, setup);
                return true;
            });
        }

        inventoryGui.setCloseAction(close -> {
            if(personUtil.isPerson(close.getPlayer().getUniqueId())) {
                Person person = personUtil.getPerson(close.getPlayer().getUniqueId());
                if(person.getGender() == null) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> inventoryGui.show(close.getPlayer()), 1);
                    return false;
                }
            }
            return false;
        });


        return inventoryGui;
    }



    @Override
    public InventoryGui getAgeChestGUI(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        String[] rows = {
                "abcdefghi",
                "jklmnopqr",
                "stuvwxyzA",
        };
        InventoryBuilder inventoryBuilder = new InventoryBuilder().getBuilder(customConfigsInit, "age");
        InventoryGui inventoryGui = new InventoryGui(plugin, plugin.getAgeHolder(), LegacyComponentSerializer.legacySection().serialize(inventoryBuilder.getTitle()), rows);
        inventoryGui.setFiller(inventoryBuilder.getFiller());
        int minAge = Settings.MIN_AGE;
        int maxAge = Settings.MAX_AGE;
        AtomicInteger actualAge = new AtomicInteger(minAge);

        for(ActionElement actionElement : inventoryBuilder.getElements()) {

            if (actionElement.getAction() == Action.CONFIRM_AGE) {
                inventoryGui.addElement(new DynamicGuiElement(actionElement.getCharPos(), (viewer) -> new StaticGuiElement(actionElement.getCharPos(), actionElement.getStack(), click -> {
                    personUtil.getPerson(click.getWhoClicked().getUniqueId()).setAge(actualAge.get());
                    ((Player) click.getWhoClicked()).sendMessage(Legacy.translate(Lang.AGE_CONFIRMED));
                    new InventoryManager().openNextInventory((Player) click.getWhoClicked(), plugin, personUtil, this, postProcessCommands, customConfigsInit, setup);
                    if(!setup) {
                        customConfigsInit.saveInConfig(click.getWhoClicked().getUniqueId(), personUtil);
                        ((Player) click.getWhoClicked()).sendMessage(Legacy.translate(Lang.AGE_EDITED));
                        click.getWhoClicked().closeInventory();
                    }
                    return true;
                },LegacyComponentSerializer.legacySection().serialize(actionElement.getDisplayname()).replace("%actualage%", String.valueOf(actualAge.get())),
                        FormatUtils.loreListToSingleString(Settings.translateComponent(actionElement.getLore())).replace("%actualage%", String.valueOf(actualAge.get())))));
            } else {

                inventoryGui.addElement(actionElement.getCharPos(), actionElement.getStack(), click -> {

                    switch (actionElement.getAction()) {
                        case REMOVE_AGE: {
                            if (!(actualAge.get() - 1 < minAge)) {
                                actualAge.getAndDecrement();
                            } else {
                                ((Player) click.getWhoClicked()).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Legacy.translate(Lang.MIN_AGE_REACHED)));
                                XSound.play((Player) click.getWhoClicked(), "BLOCK_ANVIL_USE");
                            }
                            click.getGui().draw();
                        }
                        case ADD_AGE: {
                            if (!(actualAge.get() + 1 > maxAge)) {
                                actualAge.getAndIncrement();
                            } else {
                                ((Player) click.getWhoClicked()).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Legacy.translate((Lang.MAX_AGE_REACHED))));
                                XSound.play((Player) click.getWhoClicked(), "BLOCK_ANVIL_USE");
                            }
                            click.getGui().draw();
                        }
                    }

                    return true;
                });
            }
        }

        inventoryGui.setCloseAction(close -> {
            if(personUtil.isPerson(close.getPlayer().getUniqueId())) {
                Person person = personUtil.getPerson(close.getPlayer().getUniqueId());
                if(person.getAge() == -1) {
                    Bukkit.getScheduler().runTaskLater(plugin, () -> inventoryGui.show(close.getPlayer()), 1);
                    return false;
                }
            }
            return false;
        });

        return inventoryGui;
    }





    @Override
    public FurnaceGui getAgeFurnaceGUI(Player player, Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        int minAge = Settings.MIN_AGE;
        int maxAge = Settings.MAX_AGE;
        AtomicInteger actualAge = new AtomicInteger(minAge);

        InventoryBuilder inventoryBuilder = new InventoryBuilder().getBuilder(customConfigsInit, "furnace.age");
        FurnaceGui furnaceGui = new FurnaceGui(inventoryBuilder.getTitle(), plugin);
        FurnaceManager.personUtil = personUtil;

        FurnaceManager.addFurnace(player.getUniqueId(), furnaceGui); //!important

        for(ActionElement element : inventoryBuilder.getElements()) {
            switch (element.getAction()) {
                case REMOVE_AGE: {
                    furnaceGui.addElement(new FurnaceElement(element.getDisplayname(), element.getLore(), element.getIntPos(), click -> {
                        long now = System.currentTimeMillis();
                        long coolDownEnd = cooldown.getOrDefault(player.getUniqueId(), now);
                        if (now >= coolDownEnd) {
                            if (actualAge.get() - 1 >= minAge) {
                                actualAge.getAndDecrement();

                                TaskObject taskObject = FurnaceManager.taskMap.getOrDefault(furnaceGui, TaskObject.defaultObject());
                                taskObject.setAge(actualAge.get());
                                FurnaceManager.taskMap.put(furnaceGui, taskObject);

                                now = System.currentTimeMillis();
                                long cooldownMs = 200;
                                cooldown.put(player.getUniqueId(), now + cooldownMs);
                            } else {
                                ((Player) click.getWhoClicked()).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Legacy.translate(Lang.MIN_AGE_REACHED)));
                                XSound.play((Player) click.getWhoClicked(), "BLOCK_ANVIL_USE");
                            }
                        }
                    }, element.getStack(), false, -1));
                }

                case ADD_AGE: {
                    furnaceGui.addElement(new FurnaceElement(element.getDisplayname(), element.getLore(), element.getIntPos(), click -> {
                        long now = System.currentTimeMillis();
                        long coolDownEnd = cooldown.getOrDefault(player.getUniqueId(), now);
                        if (now >= coolDownEnd) {
                            if (actualAge.get() + 1 <= maxAge) {
                                actualAge.getAndIncrement();
                                FurnaceManager.taskMap.get(furnaceGui).setAge(actualAge.get());

                                now = System.currentTimeMillis();
                                long cooldownMs = 200;
                                cooldown.put(player.getUniqueId(), now + cooldownMs);
                            } else {
                                ((Player) click.getWhoClicked()).spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Legacy.translate(Lang.MAX_AGE_REACHED)));
                                XSound.play((Player) click.getWhoClicked(), "BLOCK_ANVIL_USE");
                            }
                        }
                    }, element.getStack(), false, -1));
                }

                case CONFIRM_AGE: {
                    FurnaceGui furnace = furnaceGuis.get(player.getUniqueId());
                    taskMap.get(furnace).setAge(actualAge.get());

                    ItemStack ageStack = furnace.getCustomStack(actualAge.get()) != null ? furnace.getCustomStack(actualAge.get()) : element.getStack();

                    furnaceGui.addElement(new FurnaceElement(element.getDisplayname(), element.getLore(), element.getIntPos(), click -> {
                        personUtil.getPerson(player.getUniqueId()).setAge(actualAge.get());

                        taskMap.get(furnace).setCompleted(true);
                        removeFurnace(player.getUniqueId(), furnaceGui);

                        if (!setup) {
                            customConfigsInit.saveInConfig(player.getUniqueId(), personUtil);
                            player.sendMessage(Legacy.translate(Lang.AGE_EDITED));
                            player.closeInventory();
                            return;
                        }
                        InventoryManager inventoryManager = new InventoryManager();
                        inventoryManager.openNextInventory(player, plugin, personUtil, this, postProcessCommands, customConfigsInit, true);
                    }, ageStack, true, -1));

                }
            }

        }
        return furnaceGui.build();
    }






    @Override
    public AnvilGUI.Builder getNameAnvilGUI(Identity plugin, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        return new AnvilGUI.Builder()
                .onClick((integer, stateSnapshot) -> {                                    //called when the inventory output slot is clicked
                    if(Settings.LASTNAME_REQUIRED) {
                        if(!stateSnapshot.getText().contains(" ")) {
                            stateSnapshot.getPlayer().sendMessage(Legacy.translate(Lang.LASTNAME_REQUIRED));
                            return AnvilGUI.Response.text(LegacyComponentSerializer.legacySection().serialize(Lang.LASTNAME_REQUIRED));
                        } else {
                            String[] textSplit = stateSnapshot.getText().trim().replaceAll(" +", " ").split(" ");
                            personUtil.getPerson(stateSnapshot.getPlayer().getUniqueId()).setName(FormatUtils.firstUppercase(textSplit[0]) + " " + FormatUtils.firstUppercase(textSplit[1]));
                            stateSnapshot.getPlayer().sendMessage(Legacy.translate(Lang.NAME_CONFIRMED.replaceText(TextReplacementConfig.builder()
                                    .matchLiteral("%name%").replacement(FormatUtils.firstUppercase(textSplit[0]) + " " + FormatUtils.firstUppercase(textSplit[1])).build())));
                            return AnvilGUI.Response.close();
                        }
                    } else {
                        String finalText;

                        if(stateSnapshot.getText().contains(" ")) {
                            String[] textSplit = stateSnapshot.getText().split(" ");
                            finalText = FormatUtils.firstUppercase(textSplit[0]);
                        } else {
                            finalText = FormatUtils.firstUppercase(stateSnapshot.getText());
                        }


                        personUtil.getPerson(stateSnapshot.getPlayer().getUniqueId()).setName(FormatUtils.firstUppercase(finalText));
                        stateSnapshot.getPlayer().sendMessage(Legacy.translate(Lang.NAME_CONFIRMED.replaceText(TextReplacementConfig.builder()
                                .matchLiteral("%name%").replacement(FormatUtils.firstUppercase(finalText)).build())));

                        return AnvilGUI.Response.close();
                    }
                })
                .onClose(stateSnapshot -> {
                    if (personUtil.getPerson(stateSnapshot.getPlayer().getUniqueId()).getName() != null) {
                        InventoryManager inventoryManager = new InventoryManager();
                        if(!setup) {
                            customConfigsInit.saveInConfig(stateSnapshot.getPlayer().getUniqueId(), personUtil);
                            stateSnapshot.getPlayer().sendMessage(Legacy.translate(Lang.NAME_EDITED));
                            stateSnapshot.getPlayer().closeInventory();
                            return;
                        }
                        inventoryManager.openNextInventory(stateSnapshot.getPlayer(), plugin, personUtil, this, postProcessCommands, customConfigsInit, setup);
                    }
                })
                .itemLeft(getCustomStack())
                .preventClose()
                .title(Settings.ANVIL_GUI__TITLE)
                .plugin(plugin);
    }






    @Override
    public void openGUI(InventoryType inventory, Settings.InventoryType type, Identity plugin, Player player, CustomConfigsInit customConfigsInit, PersonUtil personUtil, PostProcessCommands postProcessCommands, boolean setup) {
        switch (inventory) {
            case NAME:
                if(type == Settings.InventoryType.ANVIL) {
                    this.getNameAnvilGUI(plugin, customConfigsInit, personUtil, postProcessCommands, setup).open(player);
                }

                if (type == Settings.InventoryType.CHEST) {
                    this.getNameChestGUI(plugin, customConfigsInit, setup).show(player);
                }
                break;

            case GENDER:
                if (type == Settings.InventoryType.CHEST) {
                    this.getGenderChestGUI(plugin, customConfigsInit, personUtil, postProcessCommands, setup).show(player);
                }
                break;

            case AGE:
                if (type == Settings.InventoryType.FURNACE) {
                    this.getAgeFurnaceGUI(player, plugin, customConfigsInit, personUtil, postProcessCommands, setup).show(player);
                }

                if(type == Settings.InventoryType.CHEST) {
                    this.getAgeChestGUI(plugin, customConfigsInit, personUtil, postProcessCommands, setup).show(player);
                }
                break;
        }

    }
}
