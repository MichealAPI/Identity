package it.mikeslab.identity.event.auth;

import com.nickuc.login.api.event.bukkit.auth.AuthenticateEvent;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.helper.AuthHelper;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

@RequiredArgsConstructor
public class NLoginListener implements Listener {

    private final IdentityPlugin instance;

    /**
     * nLogin auth handling
     * @param event nLogin auth proprietary event
     */
    @EventHandler
    public void onLogin(AuthenticateEvent event) {
        new AuthHelper(instance).postAuth(event.getPlayer());
    }

}
