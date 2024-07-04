package it.mikeslab.identity.event.auth;

import fr.xephi.authme.events.LoginEvent;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.ConfigKey;
import it.mikeslab.identity.helper.JoinEventHelper;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class AuthMeListener implements Listener {

    private final IdentityPlugin instance;

    @EventHandler
    public void onAuth(LoginEvent event) {

        boolean afterAuth = instance.getCustomConfig().getBoolean(ConfigKey.SETUP_AFTER_AUTH);

        if(!afterAuth) {
            return;
        }

        new JoinEventHelper(instance)
                .getJoinListener()
                .accept(event.getPlayer());

    }


}
