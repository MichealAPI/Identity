package it.mikeslab.identity.papi;

import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.lang.LanguageKey;
import it.mikeslab.identity.pojo.Identity;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

@RequiredArgsConstructor
public class IdentityExpansion extends PlaceholderExpansion {

    private final IdentityPlugin instance;

    @Override
    public @NotNull String getIdentifier() {
        return "identity";
    }

    @Override
    public @NotNull String getAuthor() {
        return "MikesLab";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.8.1";
    }

    public String onPlaceholderRequest(Player player, String identifier) {

        if (player == null) {
            return "";
        }

        Identity identity = loadFromDatabase(player);

        if(identity == null) {
            identity = loadFromSetupCache(player);
        }

        if(identity.getValues().containsKey(identifier)) {
            return String.valueOf(identity.getValues().get(identifier));
        }

        return instance.getLanguage().getSerializedString(LanguageKey.UNSET_VALUE);

    }

    /**
     * Load the identity from the database or the cache if already saved
     * @param player the player to load the identity for
     * @return the identity
     */
    private Identity loadFromDatabase(Player player) {
        return instance.getIdentityCacheHandler()
                .getCachedIdentity(player.getUniqueId())
                .join()
                .orElse(null);
    }

    private Identity loadFromSetupCache(Player player) {
        return instance.getSetupCacheHandler().getIdentity(player.getUniqueId());
    }

}
