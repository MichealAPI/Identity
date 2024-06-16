package it.mikeslab.identity.event;

import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.handler.IdentityCacheHandler;
import it.mikeslab.identity.pojo.Identity;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

@Getter
public class PlayerListener implements Listener {

    private final IdentityPlugin instance;
    private final IdentityCacheHandler cacheHandler;

    public PlayerListener(IdentityPlugin instance) {
        this.instance = instance;
        this.cacheHandler = instance.getIdentityCacheHandler();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        this.cacheHandler.getCachedIdentity(
                playerUUID
        ).thenAccept((playerFoundIdentity) -> {

            boolean isFound = playerFoundIdentity.isPresent();

            if(!isFound) {

                Bukkit.getScheduler().runTaskLater(
                        instance,
                        () -> openIdentityInventory(player),
                        1L
                );

                instance.getSetupCacheHandler()
                        .initSetup(playerUUID);

            }

        });


    }


    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        Identity identity = instance
                .getSetupCacheHandler()
                .getIdentity(playerUUID);

        if(identity != null) {

            System.out.println(identity.toString());

            // save to db
            instance.getIdentityDatabase().upsert(identity);
            instance.getSetupCacheHandler().remove(playerUUID);

            // todo evaluate if this is necessary, removing on quit
            instance.getGuiConfigRegistrar()
                    .getPlayerInventories()
                    .remove(playerUUID);
        }

    }


    private void openIdentityInventory(Player target) {

        UUID targetUUID = target.getUniqueId();

        // todo implement delay
        instance.getGuiConfigRegistrar()
                .getPlayerInventories()
                .get(targetUUID)
                .get(instance.getGuiConfigRegistrar().getFallbackGuiIdentifier())
                .show(target);
    }





}
