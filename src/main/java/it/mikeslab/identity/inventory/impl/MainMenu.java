package it.mikeslab.identity.inventory.impl;

import it.mikeslab.commons.api.inventory.pojo.GuiContext;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.inventory.impl.template.GuiTemplate;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class MainMenu extends GuiTemplate {

    public MainMenu(IdentityPlugin instance, GuiContext guiContext) {
        super(instance, guiContext);
    }

}
