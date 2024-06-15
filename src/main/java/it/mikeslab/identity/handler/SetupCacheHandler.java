package it.mikeslab.identity.handler;

import it.mikeslab.identity.pojo.Identity;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class SetupCacheHandler {

    private final Map<UUID, Identity> identityMap = new HashMap<>();

    /**
     * Initialize the setup for the player
     * @param uuid the player's UUID
     */
    public void initSetup(UUID uuid) {

        Identity identity = new Identity(uuid);

        identityMap.put(uuid, identity);

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

        System.out.println(identityMap.get(uuid).getValues().toString());

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
