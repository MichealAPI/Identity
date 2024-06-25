package it.mikeslab.identity.inventory.impl.template;

import it.mikeslab.commons.api.inventory.config.GuiConfig;
import it.mikeslab.commons.api.inventory.config.GuiConfigImpl;
import it.mikeslab.commons.api.inventory.util.CustomInventory;
import it.mikeslab.commons.api.inventory.util.CustomInventoryContext;
import it.mikeslab.commons.api.inventory.util.InventoryContext;
import it.mikeslab.commons.api.inventory.util.InventorySettings;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.ConfigKey;
import lombok.Data;

import java.io.File;
import java.util.Optional;

@Data
public abstract class GuiTemplate implements CustomInventory {

    private CustomInventoryContext customContext;
    private IdentityPlugin instance;

    public GuiTemplate(final IdentityPlugin instance, InventorySettings settings) {
        this.instance = instance;

        this.autowire(instance, settings);

        this.generate();

        // Setting up for animations
        customContext
                .getInventoryContext()
                .getDefaultGuiDetails()
                .setAnimationInterval(
                        instance.getCustomConfig().getInt(ConfigKey.ANIMATION_INTERVAL)
                );
    }

    /**
     * Initialize the gui
     */
    private void generate() {

        File configFile = new File(getInstance().getDataFolder(), this.getRelativePath().toString());
        String fileName = configFile.getName();

        GuiConfig guiConfig;

        // Check if the guiConfig is cached, this
        // allows preventing loading the same config multiple times
        if (this.getInstance().getCachedGuiConfig().containsKey(fileName)) {

            guiConfig = this.getInstance().getCachedGuiConfig().get(fileName);

        } else {

            guiConfig = new GuiConfigImpl(this.getInstance());
            guiConfig.loadConfig(this.getRelativePath(), true);

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

        CustomInventoryContext context = new CustomInventoryContext(instance, settings);
        context.setGuiFactory(instance.getGuiFactory());

        this.setCustomContext(context);

        this.setInstance(instance);

        this.setInventoryContext(new InventoryContext());

        this.getInventoryContext().setConsumers(
                this.getConsumers()
        );
    }

}
