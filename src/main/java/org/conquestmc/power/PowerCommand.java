package org.conquestmc.power;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.conquestmc.util.MessageUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;

import static org.bukkit.Bukkit.getServer;
import static org.conquestmc.power.PowerController.*;
import static org.conquestmc.util.MessageUtils.*;

public class PowerCommand implements TabExecutor {
    private static final List<String> ADMIN_COMPLETES = List.of("set", "add", "remove");
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) {
            if (sender instanceof Player player) {
                double power = PowerController.getPower(player);
                var message = MessageUtils.getMessageList("power-info", Placeholder.component("power", Component.text("\uD83D\uDDE1 " + power).color(TextColor.fromHexString(powerToColor(power)))));
                sendMessageList(player, message);
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_BANJO, 1, 1);
                return true;
            }
        }
        if (!sender.hasPermission("conquestmc.power")) {
            sender.sendMessage("You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            sender.sendMessage("Usage: /poweradmin <add|remove|set> <player> [amount]");
            return true;
        }

        String subCommand = args[0].toLowerCase();

        if (subCommand.equals("add") || subCommand.equals("remove") || subCommand.equals("set")) {
            if (args.length < 3) {
                sendMiniMessage(sender, "<yellow>Usage: /poweradmin " + subCommand + " <player> <amount>");
                return true;
            }

            Player targetPlayer = getServer().getPlayer(args[1]);

            if (targetPlayer == null) {
                sendMiniMessage(sender, "<red>Player not found!");
                return true;
            }

            double amount;
            try {
                amount = Double.parseDouble(args[2]);
            } catch (NumberFormatException e) {
                sendMiniMessage(sender, "<red>Invalid amount! Please provide a correct number");
                return true;
            }

            switch (subCommand) {
                case "add" -> {
                    addPower(targetPlayer, amount);
                    sendMiniMessage(sender, "<green>Added <yellow>" + amount + "<green> power to <gold>" + targetPlayer.getName() + ".");
                }
                case "remove" -> {
                    removePower(targetPlayer, amount);
                    sendMiniMessage(sender, "<green>Removed <red>" + amount + "<green> power from <gold>" + targetPlayer.getName() + ".");
                }
                default -> {
                    setPower(targetPlayer, amount);
                    sendMiniMessage(sender, "<green>Set <gold>" + targetPlayer.getName() + "'s<green> power to <gold>" + amount);
                }
            }
        } else {
            Player targetPlayer = getServer().getPlayer(subCommand);

            if (targetPlayer == null) {
                sendMiniMessage(sender, "<red>Player not found!");
                return true;
            }

            double power = getPower(targetPlayer);
            var rawMessage = getMessageRaw("power-info-other").replace("{player_name}", tinifyText(targetPlayer.getName()))
                    .replace("<color>", "<" + powerToColor(power) + ">")
                    .replace("<power>", String.valueOf(power));

            sendMiniMessage(targetPlayer, rawMessage);
        }

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender.hasPermission("conquestmc.admin")) {
            return ADMIN_COMPLETES;
        }
        return Collections.emptyList();
    }
}
