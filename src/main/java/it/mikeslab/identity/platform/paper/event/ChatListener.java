package it.mikeslab.identity.platform.paper.event;

import io.papermc.paper.chat.ChatRenderer;
import io.papermc.paper.event.player.AsyncChatEvent;
import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.various.platform.PlatformUtil;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.config.ConfigKey;
import it.mikeslab.identity.pojo.Identity;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Listen for chat events if enabled
 * from the configuration
 *
 * @see ConfigKey#ENABLE_CHAT_FORMATTER
 */
public class ChatListener implements Listener, ChatRenderer {

    private final IdentityPlugin instance;

    public ChatListener(final IdentityPlugin instance) {
        this.instance = instance;

        if (!PlatformUtil.isPaper()) {
            throw new UnsupportedOperationException("Unsupported platform for Paper ChatListener.");
        }

    }


    @EventHandler
    public void onChat(AsyncChatEvent event) {

        Player source = event.getPlayer();
        UUID sourceUUID = source.getUniqueId();

        Optional<Identity> identityOptional = instance
                .getIdentityCacheHandler()
                .getCachedIdentity(sourceUUID)
                .join();

        identityOptional.ifPresent(value -> {
            event.renderer(this);
        });

        int chatDistance = instance.getCustomConfig().getInt(ConfigKey.CHAT_DISTANCE);

        if (chatDistance == -1) return;

        // Remove viewers that are too far away
        event.viewers().removeIf(
                viewer -> {
                    if(viewer instanceof Entity) {

                        Entity entity = (Entity) viewer;

                        return entity
                                .getLocation()
                                .distanceSquared(source.getLocation()) > chatDistance * chatDistance;

                    } else {
                        return false;
                    }
                }
        );
    }


    @Override
    public @NotNull Component render(@NotNull Player player, @NotNull Component sourceDisplayName, @NotNull Component message, @NotNull Audience audience) {

        String chatFormat = instance.getCustomConfig().getString(ConfigKey.CHAT_FORMAT);

        // Check if PlaceholderAPI is enabled
        // and replace placeholders, if any
        if (instance.isPlaceholderAPIEnabled()) {

            chatFormat = PlaceholderAPI.setPlaceholders(player, chatFormat);

        }

        return Objects.requireNonNull(ComponentsUtil.getComponent(
                chatFormat,
                Placeholder.component("message", message)
        ));
    }
}
