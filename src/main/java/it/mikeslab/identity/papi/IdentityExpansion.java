package it.mikeslab.identity.papi;

import it.mikeslab.identity.IdentityPlugin;
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
        return "widenidentity";
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

        Identity identity = instance.getIdentityCacheHandler()
                .getCachedIdentity(player.getUniqueId())
                .join()
                .orElse(null);

        if (identity == null) {
            return "";
        }

        if(identity.getValues().containsKey(identifier)) {
            return String.valueOf(identity.getValues().get(identifier));
        }

        return ""; // todo unset placeholder

    }


}
