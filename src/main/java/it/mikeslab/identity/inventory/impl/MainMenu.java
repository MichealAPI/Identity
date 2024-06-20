package it.mikeslab.identity.inventory.impl;

import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.inventory.impl.template.GuiTemplate;
import it.mikeslab.identity.inventory.pojo.InventorySettings;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MainMenu extends GuiTemplate {

    public MainMenu(IdentityPlugin instance, InventorySettings settings) {
        super(instance, settings);
    }

}
