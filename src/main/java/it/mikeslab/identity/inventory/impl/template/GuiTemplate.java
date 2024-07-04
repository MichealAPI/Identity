package it.mikeslab.identity.inventory.impl.template;

import it.mikeslab.commons.api.inventory.CustomInventory;
import it.mikeslab.commons.api.inventory.config.GuiConfig;
import it.mikeslab.commons.api.inventory.config.GuiConfigImpl;
import it.mikeslab.commons.api.inventory.pojo.GuiContext;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.ConfigKey;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Optional;

@Data
public abstract class GuiTemplate implements CustomInventory {

    @Setter
    private GuiContext guiContext;
    private IdentityPlugin instance;

    public GuiTemplate(final IdentityPlugin instance, GuiContext context) {
        this.instance = instance;
        this.guiContext = context;

        this.generate();

        // Setting up for animations
        context
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
        this.getGuiContext().setDefaultGuiDetails(guiConfig.getGuiDetails(
                Optional.empty(),
                this.getConsumers()
        ));

        this.setId(
                this.getInstance()
                        .getGuiFactory()
                        .create(
                                this.getGuiContext().getDefaultGuiDetails()
                        )
        );

        // Register the open and close actions
        guiConfig.registerOpenCloseActions(
                this.getId(),
                getGuiContext().getGuiFactory()
        );

    }

}
