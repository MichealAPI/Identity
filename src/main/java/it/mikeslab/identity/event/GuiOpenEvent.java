package it.mikeslab.identity.event;

import it.mikeslab.commons.api.inventory.CustomGui;
import it.mikeslab.commons.api.inventory.event.GuiEvent;
import it.mikeslab.commons.api.inventory.util.action.ActionHandler;
import it.mikeslab.identity.IdentityPlugin;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class GuiOpenEvent implements Listener {

    private final IdentityPlugin instance;

    @EventHandler
    public void onGuiOpen(GuiEvent event) {

        if(event.getWhen() != ActionHandler.ActionEvent.OPEN) return;

        CustomGui gui = event.getGui();

        // Run closing actions
        instance.getGuiFactory()
                .getActionHandler()
                .handleActions(
                        gui.getId(),
                        ActionHandler.ActionEvent.CLOSE,
                        event
                );
    }

}
