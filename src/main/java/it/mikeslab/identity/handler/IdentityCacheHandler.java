package it.mikeslab.identity.handler;

import it.mikeslab.commons.api.database.async.AsyncDatabase;
import it.mikeslab.identity.pojo.Identity;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * This class is responsible for handling the cache of identities.
 * Caches player identities to avoid unnecessary database queries.
 * on player join.
 */
public class IdentityCacheHandler {

    private final AsyncDatabase<Identity> identityDatabase;
    private final Map<UUID, Identity> identityMap;

    // This is a setup cache handler that contains the player's identity
    // after they have joined the server, and the setup is in progress or completed.
    // This is used to store the player's identity temporarily until they quit the server
    // and their data is saved to the database.
    private final SetupCacheHandler setupCacheHandler;

    public IdentityCacheHandler(AsyncDatabase<Identity> identityDatabase, SetupCacheHandler setupCacheHandler) {
        this.identityMap = new HashMap<>();
        this.identityDatabase = identityDatabase;

        this.setupCacheHandler = setupCacheHandler;
    }


    public void purgeCache() {
        this.identityMap.clear();
    }

    public CompletableFuture<Optional<Identity>> getCachedIdentity(UUID uuid) {

        // Checks setup cache
        Identity setupIdentity = setupCacheHandler.getIdentity(uuid);
        if(setupIdentity != null) {
            return CompletableFuture.completedFuture(
                    Optional.of(setupIdentity)
            );
        }

        // Checks identity map, a post-database lookup cache
        if (this.identityMap.containsKey(uuid)) {
            return CompletableFuture.completedFuture(
                    Optional.of(this.identityMap.get(uuid))
            );

        } else {

            // Return the identity if it is present in the database
            return this.loadIdentity(uuid).thenApply(identity -> {
                if (identity != null) {
                    this.identityMap.put(uuid, identity);
                    return Optional.of(identity);
                } else {
                    return Optional.empty();
                }
            });

        }
    }

    public CompletableFuture<Identity> loadIdentity(UUID uuid) {

        Identity identityFilter = new Identity(); // todo new Identity(targetUUID);
        identityFilter.setUuid(uuid);

        return this.identityDatabase.findOne(identityFilter);

    }

    public CompletableFuture<Boolean> deleteFromDatabase(Identity identity) {
        return this.identityDatabase.delete(identity); // if present, it will delete the target identity
    }


    public CompletableFuture<Boolean> dropIdentity(UUID uuid) {
        Identity databaseFilter = new Identity(uuid);
        boolean mapRemoveResult = this.identityMap.remove(uuid) != null;

        return deleteFromDatabase(databaseFilter)
                .thenApply(databaseDeleteResult -> mapRemoveResult && databaseDeleteResult);
    }



}
