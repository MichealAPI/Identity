package it.mikeslab.identity.inventory.impl.template;

import it.mikeslab.commons.api.inventory.config.GuiConfig;
import it.mikeslab.commons.api.inventory.config.GuiConfigImpl;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.inventory.CustomInventory;
import it.mikeslab.identity.inventory.CustomInventoryContext;
import it.mikeslab.identity.inventory.InventoryContext;
import it.mikeslab.identity.pojo.InventorySettings;
import lombok.Data;

import java.io.File;
import java.util.Optional;

@Data
public abstract class GuiTemplate implements CustomInventory {

    private CustomInventoryContext customContext;

    public GuiTemplate(final IdentityPlugin instance, InventorySettings settings) {
        this.autowire(instance, settings);

        this.generate();
    }

    /**
     * Initialize the gui
     */
    private void generate() {
        String fileName = "inventories" + File.separator + this.getFileName() + ".yml";
        GuiConfig guiConfig;

        // Check if the guiConfig is cached, this
        // allows to prevent loading the same config multiple times
        if (this.getInstance().getCachedGuiConfig().containsKey(fileName)) {

            guiConfig = this.getInstance().getCachedGuiConfig().get(fileName);

        } else {

            guiConfig = new GuiConfigImpl(this.getInstance());
            guiConfig.loadConfig(fileName, true);

            this.getInstance().getCachedGuiConfig().put(fileName, guiConfig);
        }

        // Set up the context
        // Get the default gui details
        this.getInventoryContext().setDefaultGuiDetails(guiConfig.getGuiDetails(
                Optional.empty(),
                this.getConsumers()
        ));

        this.setId(
                this.getInstance()
                        .getGuiFactory()
                        .create(
                                this.getInventoryContext().getDefaultGuiDetails()
                        )
        );

    }



    /**
     * Autowires constructor values
     * @param instance The instance of the plugin
     * @param settings The settings of the inventory
     */
    private void autowire(final IdentityPlugin instance, final InventorySettings settings) {

        this.setCustomContext(new CustomInventoryContext());

        this.setSettings(settings);

        this.setInstance(instance);

        this.setInventoryContext(new InventoryContext());

        this.getInventoryContext().setConsumers(
                this.getConsumers()
        );
    }

}
