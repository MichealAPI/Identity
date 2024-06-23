package it.mikeslab.identity.handler;

import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.pojo.Identity;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetupCacheHandler {

    private final Map<UUID, Identity> identityMap = new HashMap<>();

    /**
     * Initialize the setup for the player
     * @param instance the IdentityPlugin instance
     * @param target the player to set up
     */
    public void initSetup(IdentityPlugin instance, Player target) {

        UUID targetUUID = target.getUniqueId();

        Bukkit.getScheduler().runTaskLater(instance, () -> {
                    instance.getGuiConfigRegistrar()
                            .getPlayerInventories()
                            .get(targetUUID)
                            .get(instance.getGuiConfigRegistrar().getFallbackGuiIdentifier())
                            .show(target);
                }, 1L);

        Identity identity = new Identity(targetUUID);

        identityMap.put(targetUUID, identity);

    }

    /**
     * Get the identity of the player
     * @param uuid the player's UUID
     * @return the player's identity
     */
    public Identity getIdentity(UUID uuid) {
        return identityMap.getOrDefault(uuid, null); // this has to return a default of
                                                                // null to confirm that an identity effectively doesn't exist
    }

    /**
     * Update the identity of the player
     * @param uuid the player's UUID
     * @param entryValue the entry value to put in the identity values
     */
    public void updateIdentity(UUID uuid, Map.Entry<String, Object> entryValue) {

        Identity identity = identityMap.computeIfAbsent(uuid, Identity::new);

        identity.getValues().put(
                entryValue.getKey(),
                entryValue.getValue()
        );

    }

    /**
     * Removes a player from the setup cache,
     * this happens when the player has completed the setup and
     * has quit the server. On player quit, player's data gets saved
     * @param uuid the player's UUID
     */
    public void remove(UUID uuid) {
        identityMap.remove(uuid);
    }


}
