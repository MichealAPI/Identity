package it.mikeslab.identity.helper;

import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.ConfigKey;
import it.mikeslab.identity.handler.IdentityCacheHandler;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.function.Consumer;

public class JoinEventHelper {

    private final IdentityPlugin instance;
    private final IdentityCacheHandler cacheHandler;

    public JoinEventHelper(IdentityPlugin instance) {
        this.instance = instance;
        this.cacheHandler = instance.getIdentityCacheHandler();
    }


    public Consumer<Player> getJoinListener() {
        return (player) -> {

            UUID playerUUID = player.getUniqueId();

            this.cacheHandler.getCachedIdentity(
                    playerUUID
            ).thenAccept((playerFoundIdentity) -> {

                boolean isFound = playerFoundIdentity.isPresent();

                if (!isFound) {

                    if (!instance.getCustomConfig().getBoolean(ConfigKey.ON_JOIN_SETUP)) return;

                    instance.getSetupCacheHandler().initSetup(
                            instance,
                            player
                    );

                }

            });
        };
    }

}
