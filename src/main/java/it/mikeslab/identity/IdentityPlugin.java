package it.mikeslab.identity;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandReplacements;
import it.mikeslab.commons.LabCommons;
import it.mikeslab.commons.api.config.Configurable;
import it.mikeslab.commons.api.database.Database;
import it.mikeslab.commons.api.database.async.AsyncDatabase;
import it.mikeslab.commons.api.database.async.AsyncDatabaseImpl;
import it.mikeslab.commons.api.database.config.ConfigDatabaseUtil;
import it.mikeslab.commons.api.formatter.FormatUtil;
import it.mikeslab.commons.api.inventory.GuiFactory;
import it.mikeslab.commons.api.inventory.config.ConditionParser;
import it.mikeslab.commons.api.inventory.config.GuiConfig;
import it.mikeslab.commons.api.inventory.event.GuiListener;
import it.mikeslab.commons.api.inventory.factory.GuiFactoryImpl;
import it.mikeslab.commons.api.inventory.util.action.ActionHandler;
import it.mikeslab.commons.api.inventory.util.action.ActionHandlerImpl;
import it.mikeslab.commons.api.inventory.util.action.ActionRegistrar;
import it.mikeslab.commons.api.logger.LoggerUtil;
import it.mikeslab.identity.command.IdentityCommand;
import it.mikeslab.identity.config.ConfigKey;
import it.mikeslab.identity.event.ChatListener;
import it.mikeslab.identity.event.GuiCloseListener;
import it.mikeslab.identity.event.PlayerListener;
import it.mikeslab.identity.handler.AntiSpam;
import it.mikeslab.identity.handler.AntiSpamImpl;
import it.mikeslab.identity.handler.IdentityCacheHandler;
import it.mikeslab.identity.handler.SetupCacheHandler;
import it.mikeslab.identity.inventory.action.ActionRegistrarImpl;
import it.mikeslab.identity.inventory.config.ConditionParserImpl;
import it.mikeslab.identity.inventory.config.GuiConfigRegistrar;
import it.mikeslab.identity.papi.IdentityExpansion;
import it.mikeslab.identity.pojo.Identity;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Level;

@Getter
public final class IdentityPlugin extends JavaPlugin {

    public static final String PLUGIN_NAME = "Identity";

    private AsyncDatabase<Identity> identityDatabase;
    private GuiFactory guiFactory;
    private GuiListener guiListener;
    private SetupCacheHandler setupCacheHandler;

    private Configurable language, customConfig, antiSpamConfig;

    private IdentityCacheHandler identityCacheHandler;

    private AntiSpam antiSpamHandler;

    private boolean placeholderAPIEnabled;

    private LabCommons labCommons;

    private BukkitAudiences audiences;

    private GuiConfigRegistrar guiConfigRegistrar;

    private Map<String, GuiConfig> cachedGuiConfig;

    private ActionHandler actionHandler;

    private ConditionParser conditionParser;

    @Override
    public void onEnable() {

        // Get the API plug-in instance
        this.labCommons = (LabCommons) getServer()
                .getPluginManager()
                .getPlugin("LabCommons");

        this.audiences = BukkitAudiences.create(this);

        this.initConfig();

        FormatUtil.printStartupInfos(this, audiences, "9C00FF");

        initDatabase().thenAccept(isConnected -> {
            if(isConnected) {
                LoggerUtil.log(
                        PLUGIN_NAME,
                        Level.INFO,
                        LoggerUtil.LogSource.DATABASE,
                        "Connected to database."
                );
            } else {
                LoggerUtil.log(
                        PLUGIN_NAME,
                        Level.WARNING,
                        LoggerUtil.LogSource.DATABASE,
                        "Failed to connect to database."
                );
            }
        });

        this.initInventories();
        this.initCache();

        this.initListeners();

        this.registerCommands();

        this.antiSpamHandler = new AntiSpamImpl(antiSpamConfig);
        this.antiSpamHandler.loadSpamWords();

        this.initPlaceholders();


    }

    @Override
    public void onDisable() {

        // Prevent memory leaks

        // Disconnects from the database
        this.identityDatabase.disconnect().thenAccept(
                isDisconnected -> {
                    if(isDisconnected) {
                        LoggerUtil.log(
                                PLUGIN_NAME,
                                Level.INFO,
                                LoggerUtil.LogSource.DATABASE,
                                "Disconnected from database."
                        );
                    } else {
                        LoggerUtil.log(
                                PLUGIN_NAME,
                                Level.WARNING,
                                LoggerUtil.LogSource.DATABASE,
                                "Failed to disconnect from database."
                        );
                    }
                }
        );

        identityCacheHandler.purgeCache();

    }


    private void initInventories() {

        this.cachedGuiConfig = new HashMap<>();

        this.initActions();

        GuiFactoryImpl guiFactoryImpl = new GuiFactoryImpl(this);
        this.guiFactory = guiFactoryImpl;
        this.guiListener = new GuiListener(guiFactoryImpl, this);

        this.getServer().getPluginManager().registerEvents(
                guiListener,
                this
        );

        // from config
        this.guiConfigRegistrar = new GuiConfigRegistrar(
                this,
                this.getCustomConfig().getConfiguration().getConfigurationSection(Section.GUIS.getFieldName())
        );

        this.guiConfigRegistrar.register();

        this.guiFactory.setActionHandler(actionHandler);
        this.guiFactory.setConditionParser(conditionParser);

    }

    private void initActions() {

        ActionRegistrar actionRegistrar = new ActionRegistrarImpl(this);

        this.actionHandler = new ActionHandlerImpl(
                actionRegistrar.loadActions()
        );

        this.conditionParser = new ConditionParserImpl();

    }

    private CompletableFuture<Boolean> initDatabase() {

        ConfigurationSection section = this.getCustomConfig()
                .getConfiguration()
                .getConfigurationSection("database");

        ConfigDatabaseUtil<Identity> configDatabaseUtil = new ConfigDatabaseUtil<>(
                section,
                this.getDataFolder()
        );

        Database<Identity> database = configDatabaseUtil.getDatabaseInstance();

        AsyncDatabase<Identity> asyncDatabase = new AsyncDatabaseImpl<>(database);

        this.identityDatabase = asyncDatabase;

        return asyncDatabase.connect(new Identity());
    }


    private void initConfig() {

        // default config
        File configFile = new File(getDataFolder(), "config.yml");

        File languageConfigFile = new File(getDataFolder(), "language.yml");

        File antiSpamConfigFile = new File(getDataFolder(), "antispam.yml");

        saveResource(languageConfigFile.getName(), false);
        saveResource(configFile.getName(), false);
        saveResource(antiSpamConfigFile.getName(), false);

        this.language = Configurable
                .newInstance()
                .loadConfiguration(languageConfigFile);

        this.customConfig = Configurable
                .newInstance()
                .loadConfiguration(configFile);

        this.antiSpamConfig = Configurable
                .newInstance()
                .loadConfiguration(antiSpamConfigFile);
    }


    private void initListeners() {
        this.getServer().getPluginManager().registerEvents(
                new PlayerListener(this),
                this
        );

        // Chat formatter
        if(this.getCustomConfig().getBoolean(ConfigKey.ENABLE_CHAT_FORMATTER)) {
            this.getServer().getPluginManager().registerEvents(
                     new ChatListener(this),
                     this
            );
        }

        // GuiListener
        this.getServer().getPluginManager().registerEvents(
                new GuiCloseListener(this),
                this
        );


    }

    private void registerCommands() {
        BukkitCommandManager manager = new BukkitCommandManager(this);

        String commandAliases = this.getCustomConfig().getString(ConfigKey.COMMAND_ALIASES);

        CommandReplacements replacements = manager.getCommandReplacements();
        replacements.addReplacement("%command-aliases%", commandAliases);

        manager.registerCommand(new IdentityCommand(this));

    }

    /**
     * Initializes the cache handler
     */
    private void initCache() {

        this.setupCacheHandler = new SetupCacheHandler();
        this.identityCacheHandler = new IdentityCacheHandler(
                identityDatabase,
                setupCacheHandler
        );

    }


    private void initPlaceholders() {

        placeholderAPIEnabled = this.getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");

        if(placeholderAPIEnabled) {

            // Register the expansion
            new IdentityExpansion(this).register();

        }

    }


    @Getter
    @RequiredArgsConstructor
    private enum Section {

        GUIS("guis");

        private final String fieldName;

    }


}
