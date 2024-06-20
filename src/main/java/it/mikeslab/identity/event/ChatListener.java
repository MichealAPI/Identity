package it.mikeslab.identity.event;

import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.ConfigKey;
import it.mikeslab.identity.pojo.Identity;
import lombok.RequiredArgsConstructor;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;

/**
 * Listen for chat events if enabled
 * from the configuration
 */
@RequiredArgsConstructor
public class ChatListener implements Listener {

    private final IdentityPlugin instance;

    private Identity identity; // internal identity cache

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {

        Player source = event.getPlayer();
        UUID sourceUUID = source.getUniqueId();

        Optional<Identity> identityOptional = instance
                .getIdentityCacheHandler()
                .getCachedIdentity(sourceUUID)
                .join();

        identityOptional.ifPresent(value -> {
            this.identity = value;

            event.setFormat(
                    this.getFormat(
                            source,
                            event.getMessage()
                    )
            );


        });

        int chatDistance = instance.getCustomConfig().getInt(ConfigKey.CHAT_DISTANCE);

        if(chatDistance == -1) return;

        // Remove viewers that are too far away
        event.getRecipients().removeIf(
                viewer -> viewer
                        .getLocation()
                        .distanceSquared(source.getLocation()) > chatDistance * chatDistance);

    }


    /**
     * Render the chat message
     * @param source The player sending the message
     * @param message The message being sent
     * @return The rendered message
     */
    private String getFormat(@NotNull Player source, @NotNull String message) {

        String chatFormat = instance.getCustomConfig().getString(ConfigKey.CHAT_FORMAT);

        // Check if PlaceholderAPI is enabled
        // and replace placeholders, if any
        if(instance.isPlaceholderAPIEnabled()) {

            chatFormat = PlaceholderAPI.setPlaceholders(source, chatFormat);

        }

        return ComponentsUtil.serialize(
                ComponentsUtil.getComponent(chatFormat, Placeholder.unparsed("message", message))
        );

    }

}
