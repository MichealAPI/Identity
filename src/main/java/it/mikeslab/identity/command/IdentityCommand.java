package it.mikeslab.identity.command;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.*;
import co.aikar.commands.bukkit.contexts.OnlinePlayer;
import it.mikeslab.commons.api.component.ComponentsUtil;
import it.mikeslab.commons.api.formatter.FormatUtil;
import it.mikeslab.commons.api.various.message.MessageHelper;
import it.mikeslab.commons.api.various.message.MessageHelperImpl;
import it.mikeslab.identity.IdentityPlugin;
import it.mikeslab.identity.Permission;
import it.mikeslab.identity.config.lang.LanguageKey;
import it.mikeslab.identity.pojo.Identity;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

@CommandAlias("%command-aliases")
public class IdentityCommand extends BaseCommand {

    private final IdentityPlugin instance;
    private final MessageHelperImpl helper;

    public IdentityCommand(final IdentityPlugin instance) {

        this.instance = instance;
        this.helper = instance.getMessageHelper();

    }

    @Default
    public void defaultCommand(CommandSender sender) {
        FormatUtil.sendRunningInfos(sender, instance, "9C00FF");
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
                            helper.sendMessage(sender, LanguageKey.IDENTITY_ALREADY_SET);
                        } else {
                            helper.sendMessage(sender, LanguageKey.IDENTITY_SETUP_START);

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

        Identity filterIdentity = new Identity(); // this format nulls values
                                                  // and prevents its empty content
                                                  // from being queries inside the database
        filterIdentity.setUniqueId(targetUUID);

        Component resetMessage = instance.getLanguage().getComponent(
                LanguageKey.IDENTITY_RESET_FOR,
                Placeholder.unparsed("player", targetPlayer.getName()));


        Component notFound = instance.getLanguage().getComponent(LanguageKey.IDENTITY_NOT_FOUND,
                Placeholder.unparsed("player", targetPlayer.getName()));

        // Identities from setup are saved after the player logs out;
        // therefore, there is a time interval in which players could be stored only in
        // the setup passage
        if(instance.getSetupCacheHandler().getIdentity(targetUUID) != null) {
            instance.getAudiences().sender(sender).sendMessage(resetMessage);

            instance.getSetupCacheHandler().remove(targetUUID);

            this.kickReset(targetPlayer);
            return;
        }

        // Remove from the database, if found
        instance.getIdentityCacheHandler().dropIdentity(targetUUID).thenAcceptAsync(
                deleted -> {
                    if(deleted) {

                        // Reset the identity of the player
                        instance.getAudiences().sender(sender).sendMessage(resetMessage);

                        // Kick out to init a new Identity setup session
                        this.kickReset(targetPlayer);

                    } else {

                        // Player isn't found in the database
                        instance.getAudiences().sender(sender).sendMessage(notFound);
                    }
                }
        );
    }


    @Subcommand("reload")
    @Description("Reloads the plugin configurations")
    @CommandPermission(Permission.IDENTITY_RELOAD)
    public void reload(CommandSender sender) {
        instance.reload();
        helper.sendMessage(sender, LanguageKey.RELOAD_SUCCESS);
    }

    @Subcommand("loadPreset")
    @Description("Loads a preset from the presets folder")
    @CommandPermission(Permission.IDENTITY_LOAD_PRESET)
    @CommandCompletion("@presets")
    public void loadPreset(CommandSender sender, String fileName) {
        instance.getPresetsManager().loadExternalPreset(sender, fileName);
    }


    /**
     * Kicks the player to reset the identity
     * @param targetPlayer The player to kick
     */
    private void kickReset(Player targetPlayer) {

        Component kickMessage = instance.getLanguage().getComponent(LanguageKey.KICK_MESSAGE_RESET_IDENTITY);

        Bukkit.getScheduler().runTask(instance, () -> {
            targetPlayer.kickPlayer(
                    ComponentsUtil.serialize(kickMessage) // Configurable#getSerializedComponent uses MiniMessage.deserialize instead,
                                                          // ComponentsUtil#serialize uses MiniMessage's Legacy Section
                                                          // that is the most suitable for in this case.
            );
        });
    }


}
