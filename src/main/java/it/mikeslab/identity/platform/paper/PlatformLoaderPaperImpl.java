package it.mikeslab.identity.platform.paper;

import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.ConfigKey;
import it.mikeslab.identity.platform.PlatformLoader;
import it.mikeslab.identity.platform.paper.event.ChatListener;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class PlatformLoaderPaperImpl implements PlatformLoader {

    private final IdentityPlugin instance;

    @Override
    public void initListeners() {

        // Chat formatter
        if(instance.getCustomConfig().getBoolean(ConfigKey.ENABLE_CHAT_FORMATTER)) {
            instance.getServer().getPluginManager().registerEvents(
                    new ChatListener(instance),
                    instance
            );
        }

    }

}
