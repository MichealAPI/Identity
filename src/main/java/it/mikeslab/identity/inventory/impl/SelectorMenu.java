package it.mikeslab.identity.inventory.impl;

import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.inventory.action.ActionListener;
import it.mikeslab.identity.inventory.impl.template.GuiTemplate;
import it.mikeslab.identity.pojo.InventorySettings;

import java.util.Optional;

public class SelectorMenu extends GuiTemplate implements ActionListener {


    public SelectorMenu(final IdentityPlugin instance, InventorySettings settings) {
        super(instance, settings);

        this.injectAction(
                "select",
                this.handleSelection(Optional.empty(), true)
        );
    }

}
