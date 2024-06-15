package it.mikeslab.identity.inventory;

import it.mikeslab.commons.api.inventory.event.GuiInteractEvent;
import it.mikeslab.commons.api.inventory.pojo.GuiDetails;
import it.mikeslab.identity.handler.SetupCacheHandler;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * The context of the setup inventory
 */
@Data
@NoArgsConstructor
public class InventoryContext {

    private Optional<Map<String, Consumer<GuiInteractEvent>>> consumers;
    private SetupCacheHandler setupCacheHandler;
    private GuiDetails defaultGuiDetails;

}
