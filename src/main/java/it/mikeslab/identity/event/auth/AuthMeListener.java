package it.mikeslab.identity.event.auth;

import fr.xephi.authme.events.LoginEvent;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.helper.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class AuthMeListener implements Listener {

    private final IdentityPlugin instance;

    /**
     * AuthMeReloaded auth handling
     * @param event AuthMeReloaded auth proprietary event
     */
    @EventHandler
    public void onAuth(LoginEvent event) {
        new AuthHelper(instance).postAuth(event.getPlayer());
    }

}
