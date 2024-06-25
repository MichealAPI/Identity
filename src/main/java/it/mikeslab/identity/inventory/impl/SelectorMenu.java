package it.mikeslab.identity.inventory.impl;

import it.mikeslab.commons.api.inventory.util.InventorySettings;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.inventory.action.ActionListener;
import it.mikeslab.identity.inventory.impl.template.GuiTemplate;

import java.util.Optional;

public class SelectorMenu extends GuiTemplate implements ActionListener {


    public SelectorMenu(final IdentityPlugin instance, InventorySettings settings) {
        super(instance, settings);

        this.injectAction(
                instance,
                "select",
                this.handleSelection(
                        instance,
                        Optional.empty(),
                        true,
                        Optional.empty()
                )
        );
    }

}
