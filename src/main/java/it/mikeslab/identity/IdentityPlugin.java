package it.mikeslab.identity;

import co.aikar.commands.BukkitCommandManager;
import co.aikar.commands.CommandReplacements;
import it.mikeslab.commons.LabCommons;
import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.config.Configurable;
import it.mikeslab.commons.api.database.Database;
import it.mikeslab.commons.api.database.async.AsyncDatabase;
import it.mikeslab.commons.api.database.async.AsyncDatabaseImpl;
import it.mikeslab.commons.api.database.config.ConfigDatabaseUtil;
import it.mikeslab.commons.api.formatter.FormatUtil;
import it.mikeslab.commons.api.inventory.config.ConditionParser;
import it.mikeslab.commons.api.inventory.config.GuiConfig;
import it.mikeslab.commons.api.inventory.event.GuiListener;
import it.mikeslab.commons.api.inventory.factory.GuiFactory;
import it.mikeslab.commons.api.inventory.factory.GuiFactoryImpl;
import it.mikeslab.commons.api.inventory.util.action.ActionHandler;
import it.mikeslab.commons.api.inventory.util.action.ActionHandlerImpl;
import it.mikeslab.commons.api.inventory.util.action.ActionRegistrar;
import it.mikeslab.commons.api.logger.LogUtils;
import it.mikeslab.commons.api.various.message.MessageHelperImpl;
import it.mikeslab.identity.command.IdentityCommand;
import it.mikeslab.identity.config.ConfigKey;
import it.mikeslab.identity.config.lang.LanguageKey;
import it.mikeslab.identity.event.GuiCloseListener;
import it.mikeslab.identity.event.GuiOpenEvent;
import it.mikeslab.identity.event.PlayerListener;
import it.mikeslab.identity.event.auth.AuthMeListener;
import it.mikeslab.identity.event.auth.NLoginListener;
import it.mikeslab.identity.handler.AntiSpam;
import it.mikeslab.identity.handler.AntiSpamImpl;
import it.mikeslab.identity.handler.IdentityCacheHandler;
import it.mikeslab.identity.handler.SetupCacheHandler;
import it.mikeslab.identity.inventory.action.ActionRegistrarImpl;
import it.mikeslab.identity.inventory.config.GuiConfigRegistrar;
import it.mikeslab.identity.inventory.config.condition.ConditionParserImpl;
import it.mikeslab.identity.papi.IdentityExpansion;
import it.mikeslab.identity.platform.PlatformLoader;
import it.mikeslab.identity.platform.spigot.PlatformLoaderSpigotImpl;
import it.mikeslab.identity.pojo.Identity;
import it.mikeslab.identity.preset.PresetsHelper;
import it.mikeslab.identity.preset.PresetsManager;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Getter
public final class IdentityPlugin extends JavaPlugin {

    public static final Set<String> INVENTORY_IDENTIFIERS = new HashSet<>();

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

    private PresetsManager presetsManager;

    private PlatformLoader platformLoader;

    private MessageHelperImpl messageHelper;

    @Override
    public void onEnable() {

        // Get the API plug-in instance
        this.labCommons = (LabCommons) this.getServer()
                .getPluginManager()
                .getPlugin("LabCommons");

        this.audiences = BukkitAudiences.create(this);

        this.messageHelper = new MessageHelperImpl();

        this.initConfig();

        this.setMongoLoggingToInfo();

        FormatUtil.printStartupInfos(this, "9C00FF");

        this.initInventories();

        initDatabase().thenAccept(isConnected -> {
            if(isConnected) {
                LogUtils.info(
                        LogUtils.LogSource.DATABASE,
                        "Connected to database."
                );
            } else {
                LogUtils.warn(
                        LogUtils.LogSource.DATABASE,
                        "Failed to connect to database."
                );
            }
        });


        this.initCache();

        this.loadPlatform();

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
                        LogUtils.info(
                                LogUtils.LogSource.DATABASE,
                                "Disconnected from database."
                        );
                    } else {
                        LogUtils.warn(
                                LogUtils.LogSource.DATABASE,
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

        if(guiFactory == null) {
            this.guiFactory = new GuiFactoryImpl(this);
        }

        if(this.guiListener == null)
            this.guiListener = new GuiListener(guiFactory, this);

        // from config
        this.guiConfigRegistrar = new GuiConfigRegistrar(
                this,
                Section.GUIS.getFieldName()
        );

        this.guiConfigRegistrar.register();

        INVENTORY_IDENTIFIERS.addAll(
                guiConfigRegistrar.getInventoryKeys()
        );

        this.guiFactory.setActionHandler(actionHandler);
        this.guiFactory.setConditionParser(conditionParser);
        this.guiFactory.setInventoryMap(this.getGuiConfigRegistrar().getPlayerInventories());

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
        String languageConfigFileName = "language.yml";
        String configFileName = "config.yml";
        String antiSpamConfigFileName = "antispam.yml";

        save(languageConfigFileName, false);
        save(configFileName, false);
        save(antiSpamConfigFileName, false);

        this.language = LabCommons.registerConfigurable(
                this.getDataFolder(),
                languageConfigFileName,
                LanguageKey.class
        );

        this.customConfig = LabCommons.registerConfigurable(
                this.getDataFolder(),
                configFileName,
                ConfigKey.class
        );

        this.antiSpamConfig = Configurable.newInstance(this.getDataFolder(), antiSpamConfigFileName);

        this.checkDebugMode();

        this.presetsManager = new PresetsManager(this);

        this.presetsManager.extractDefaults();

    }

    private void save(String resource, boolean replace) {
        if(!new File(getDataFolder(), resource).exists() || replace) {
            saveResource(resource, replace);
        }
    }

    private void initListeners() {

        this.getServer().getPluginManager().registerEvents(
                guiListener,
                this
        );

        this.getServer().getPluginManager().registerEvents(
                new PlayerListener(this),
                this
        );

        this.handleAuthSystems();

        // GuiListener
        this.getServer().getPluginManager().registerEvents(
                new GuiCloseListener(this),
                this
        );

        this.getServer().getPluginManager().registerEvents(
                new GuiOpenEvent(this),
                this
        );

        this.platformLoader.initListeners();
    }

    private void registerCommands() {

        BukkitCommandManager manager = new BukkitCommandManager(this);

        String commandAliases = this.getCustomConfig().getString(ConfigKey.COMMAND_ALIASES);

        CommandReplacements replacements = manager.getCommandReplacements();
        replacements.addReplacement("%command-aliases%", commandAliases);

        manager.getCommandCompletions().registerAsyncCompletion("presets", c -> {
            return new PresetsHelper(this).listLoadablePresents();
        });

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

    /**
     * Set the mongo logging level to >WARN if the configuration is set to false
     */
    private void setMongoLoggingToInfo() {
        if(!this.getCustomConfig().getBoolean(ConfigKey.MONGO_LOGGING)) {
            LabCommons.disableMongoInfoLogging();
        }
    }

    public void checkDebugMode() {
        if(this.getCustomConfig().getBoolean(ConfigKey.DEBUG_MODE)) {
            LabCommons.enableDebuggingMode();
        } else {
            LabCommons.disableDebuggingMode();
        }
    }

    /**
     * Reloads the configuration files and inventories
     */
    public void reload() {
        // Reload configuration files

        this.checkDebugMode();

        this.language = this.getLanguage().reload();
        this.customConfig = this.getCustomConfig().reload();
        this.antiSpamConfig = this.getAntiSpamConfig().reload();

        // Kick in-setup-players
        this.getServer().getOnlinePlayers().forEach(
                player -> {

                    UUID uuid = player.getUniqueId();

                    if (this.getSetupCacheHandler().getIdentity(uuid) != null) {

                        this.getSetupCacheHandler().remove(uuid);

                        Bukkit.getScheduler().runTask(this, () -> {
                            player.kickPlayer(
                                    ComponentsUtil.serialize(
                                            this.getLanguage().getComponent(LanguageKey.RELOAD_KICK_CAUSE)
                                    )
                            );
                        });
                    }
                }
        );

        // Reload inventories
        this.initInventories();
    }


    public void loadPlatform() {

        /*
        if(PlatformUtil.isPaper()) {
            LogUtils.info(
                    LogUtils.LogSource.PLUGIN,
                    "Loading platform support for Paper."
            );
            this.platformLoader = new PlatformLoaderPaperImpl(this);
            return;
        }

        if(PlatformUtil.isSpigot() || PlatformUtil.isUnknown()) {
            LogUtils.info(
                    LogUtils.LogSource.PLUGIN,
                    "Loading platform support for Spigot."
            );
            this.platformLoader = new PlatformLoaderSpigotImpl(this);
        }
         */

        LogUtils.info(
                LogUtils.LogSource.PLUGIN,
                "Loading platform support for Spigot."
        );
        this.platformLoader = new PlatformLoaderSpigotImpl(this);

    }

    /**
     * Checks if AuthMeReloaded is enabled
     * @return true if AuthMeReloaded is enabled
     */
    private boolean hasAuthMeReloaded() {
        return Bukkit.getPluginManager().isPluginEnabled("AuthMe");
    }

    /**
     * Checks if nLogin is enabled
     * @return true if nLogin is enabled
     */
    private boolean hasNLoginEnabled() {
        return Bukkit.getPluginManager().isPluginEnabled("nLogin");
    }

    /**
     * Handles the optional authentication systems
     * @return
     */
    private void handleAuthSystems() {

        String authenticator = null;
        Listener listener = null;

        if(hasAuthMeReloaded()) {
            authenticator = "AuthMe";
            listener = new AuthMeListener(this);
        }

        if(hasNLoginEnabled() && listener == null) {
            authenticator = "nLogin";
            listener = new NLoginListener(this);
        }

        // if any of the handled auth system has been enabled, activates it
        if(listener != null) {

            if(customConfig.getBoolean(ConfigKey.SETUP_AFTER_AUTH)) {

                LogUtils.info(
                        LogUtils.LogSource.PLUGIN,
                        String.format("%s detected. Auth handling is enabled, hooking into it.", authenticator)
                );

                Bukkit.getPluginManager().registerEvents(
                        listener,
                        this);

                return;
            }

            LogUtils.warn(
                    LogUtils.LogSource.PLUGIN,
                    String.format("%s detected. If you want to enable the " +
                                    "setup after authentication, set the config option 'settings.setup-after-auth' to true.",
                            authenticator)
            );

        }
    }


    @Getter
    @RequiredArgsConstructor
    private enum Section {

        GUIS("guis");

        private final String fieldName;

    }



}
