package it.mikeslab.identity.helper;

import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.ConfigKey;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
public class AuthHelper {

    private final IdentityPlugin instance;

    public void postAuth(Player player) {

        boolean afterAuth = instance.getCustomConfig().getBoolean(ConfigKey.SETUP_AFTER_AUTH);

        if(!afterAuth) {
            return;
        }

        new JoinEventHelper(instance)
                .getJoinListener()
                .accept(player);
    }

}
