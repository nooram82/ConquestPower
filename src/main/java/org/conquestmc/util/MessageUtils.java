package org.conquestmc.util;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.conquestmc.ConquestPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MessageUtils {
    private static final FileConfiguration CONFIG = ConquestPlugin.getPlugin().getConfig();
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();

    @NotNull
    public static Component getMessage(String message) {
        String configuredMessage = CONFIG.getString("messages." + message);
        return MINI_MESSAGE.deserialize(Objects.requireNonNullElseGet(configuredMessage, () -> "<red>Message <yellow>" + message + "<red> could not be found in the config file!"));
    }

    public static Component getMessage(String message, TagResolver.Single... resolvers) {
        String configuredMessage = CONFIG.getString("messages." + message);
        if (configuredMessage == null) {
            return MINI_MESSAGE.deserialize("<red>Message <yellow>" + message + "<red> could not be found in the config file!");
        }
        return MINI_MESSAGE.deserialize(configuredMessage, resolvers);
    }

    public static Component getMessagePAPI(Player player, String message, TagResolver.Single... resolvers) {
        String configuredMessage = CONFIG.getString("messages." + message);
        configuredMessage = PlaceholderAPI.setPlaceholders(player, configuredMessage);
        if (configuredMessage == null) {
            return MINI_MESSAGE.deserialize("<red>Message <yellow>" + message + "<red> could not be found in the config file!");
        }
        return MINI_MESSAGE.deserialize(configuredMessage, resolvers);
    }

    public static String tinifyText(String text) {
        return PlaceholderAPI.setPlaceholders(null, "%tinify_" + text + "%");
    }

    public static List<Component> getMessageList(String key) {
        List<String> configuredMessages = CONFIG.getStringList("messages." + key);
        List<Component> parsedMessages = new ArrayList<>();
        configuredMessages.forEach(msg -> parsedMessages.add(MINI_MESSAGE.deserialize(msg)));
        return parsedMessages;
    }

    public static List<Component> getMessageList(String key, TagResolver.Single... resolvers) {
        List<String> configuredMessages = CONFIG.getStringList("messages." + key);
        List<Component> parsedMessages = new ArrayList<>();
        configuredMessages.forEach(msg -> parsedMessages.add(MINI_MESSAGE.deserialize(msg, resolvers)));
        return parsedMessages;
    }

    public static String getMessageRaw(String message) {
        return CONFIG.getString("messages." + message);
    }

    public static void sendMessageList(Player player, List<Component> list) {
        for (var component: list) {
            player.sendMessage(component);
        }
    }

    public static void sendMiniMessage(CommandSender p, String mm) {
        var component = MINI_MESSAGE.deserialize(mm);
        p.sendMessage(component);
    }
    public static void sendMiniMessage(Player p, String mm) {
        var component = MINI_MESSAGE.deserialize(mm);
        p.sendMessage(component);
    }

    public static String powerToColor(double level) {

        double percent = 100 - (( level * 100) / 5000);

        int r, g, b = 0;
        if (percent < 50) {
            r = 128;
            g = (int) Math.round(5.1 * (percent));
        }
        else {
            g = 255;
            r = (int) Math.round(510 - 5.10 * (percent));
        }
        int h = r * 0x10000 + g * 0x100 + b * 0x1;
        String hsToHex =  "000000" + Integer.toHexString(h);
        return '#' + hsToHex.substring(hsToHex.length() - 6);
    }
}
