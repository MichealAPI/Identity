package it.mikeslab.identity.platform.paper;

import it.mikeslab.commons.api.logger.LogUtils;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.ConfigKey;
import it.mikeslab.identity.platform.PlatformLoader;
import it.mikeslab.identity.platform.paper.event.ChatListener_1_18_R1;
import it.mikeslab.identity.platform.spigot.event.ChatListener;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlatformLoaderPaperImpl implements PlatformLoader {

    private final IdentityPlugin instance;

    @Override
    public void initListeners() {

        // Chat formatter
        if(instance.getCustomConfig().getBoolean(ConfigKey.ENABLE_CHAT_FORMATTER)) {

            if (!isCompatible(18)) {

                LogUtils.warn(LogUtils.LogSource.PLUGIN,
                        "Chat renderer is not compatible with this server version. Please, consider" +
                                " updating the server version. Switching to the standard chat listener."
                );

                instance.getServer().getPluginManager().registerEvents(
                        new ChatListener(instance), // Spigot listener
                        instance
                );

                return;
            }

            instance.getServer().getPluginManager().registerEvents(
                    new ChatListener_1_18_R1(instance),
                    instance
            );
        }

    }

    /**
     * Check if the server is compatible with the platform loader
     * @param minExpectedMajorVersion the minimum major version
     * @return true if the server is compatible
     * @throws NumberFormatException if the version is not a number
     */
    public boolean isCompatible(int minExpectedMajorVersion) throws NumberFormatException {

        String version = instance.getServer()
                .getClass()
                .getPackage()
                .getName();

        String[] split = version.split("_");

        int majorVersion = Integer.parseInt(split[1]);

        return majorVersion >= minExpectedMajorVersion;

    }


}
