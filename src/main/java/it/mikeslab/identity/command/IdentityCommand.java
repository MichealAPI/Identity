package it.mikeslab.identity.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import it.mikeslab.commons.api.formatter.FormatUtil;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.Permission;
import it.mikeslab.identity.config.lang.LanguageKey;
import it.mikeslab.identity.pojo.Identity;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("%command-aliases")
public class IdentityCommand extends BaseCommand {

    private final IdentityPlugin instance;
    private final BukkitAudiences audiences;

    public IdentityCommand(final IdentityPlugin instance) {

        this.instance = instance;
        this.audiences = instance.getAudiences();

    }

    @Default
    public void defaultCommand(CommandSender sender) {
        FormatUtil.sendRunningInfos(audiences.sender(sender), instance, "9C00FF");
    }

    @Subcommand("setup")
    @Description("Starts the identity setup process")
    @CommandPermission(Permission.IDENTITY_SETUP)
    @Syntax("(target)")
    public void startSetup(Player sender) {

            UUID playerUUID = sender.getUniqueId();

            instance.getIdentityCacheHandler().getCachedIdentity(playerUUID).thenAcceptAsync(
                    identityOptional -> {

                        if(identityOptional.isPresent()) {
                            audiences.sender(sender).sendMessage(
                                    instance.getLanguage().getComponent(LanguageKey.IDENTITY_ALREADY_SET)
                            );
                        } else {
                            audiences.sender(sender).sendMessage(
                                    instance.getLanguage().getComponent(LanguageKey.IDENTITY_SETUP_START)
                            );

                            instance.getSetupCacheHandler().initSetup(instance, sender);
                        }

                    }
            );
    }


    @Subcommand("reset")
    @Description("Reset the identity of a player")
    @CommandPermission(Permission.IDENTITY_RESET)
    @Syntax("(target)")
    public void reset(CommandSender sender, OnlinePlayer target) {

        Player targetPlayer = target.getPlayer();
        UUID targetUUID = targetPlayer.getUniqueId();

        Identity filterIdentity = new Identity(); //todo new Identity(targetUUID);
        filterIdentity.setUuid(targetUUID);

        instance.getIdentityCacheHandler().dropIdentity(targetUUID).thenAcceptAsync(
                deleted -> {
                    if(deleted) {

                        // Reset the identity of the player
                        audiences.sender(sender).sendMessage(
                                instance.getLanguage().getComponent(
                                        LanguageKey.IDENTITY_RESET_FOR,
                                        Placeholder.unparsed("player", targetPlayer.getName()))
                        );

                        // Kick out to init a new Identity setup session
                        Bukkit.getScheduler().runTask(instance, () -> {
                            targetPlayer.kickPlayer(
                                    instance.getLanguage().getSerializedString(LanguageKey.KICK_MESSAGE_RESET_IDENTITY)
                            );
                        });

                    } else {

                        // Player isn't found in the database
                        audiences.sender(sender).sendMessage(instance.getLanguage().getComponent(LanguageKey.IDENTITY_NOT_FOUND,
                                Placeholder.unparsed("player", targetPlayer.getName())));
                    }
                }
        );
    }


}
