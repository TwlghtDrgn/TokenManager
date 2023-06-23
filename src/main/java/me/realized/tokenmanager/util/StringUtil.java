package me.realized.tokenmanager.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;

public final class StringUtil {

    private StringUtil() {}

    public static String fromList(final List<?> list) {
        StringBuilder builder = new StringBuilder();

        if (list != null && !list.isEmpty()) {
            for (int i = 0; i < list.size(); i++) {
                builder.append(list.get(i).toString()).append(i + 1 != list.size() ? "\n" : "");
            }
        }

        return builder.toString();
    }

    public static Component color(final String input) {
        return MiniMessage.miniMessage().deserialize(input);
    }

    public static List<Component> color(final List<String> input) {
        return color(input, null);
    }

    public static List<Component> color(final List<String> input, final Function<String, String> extra) {
        List<Component> cs = new ArrayList<>();
        input.replaceAll(s -> extra != null ? extra.apply(s) : s);
        for (String in:input) {
            cs.add(MiniMessage.miniMessage().deserialize(in));
        }
        return cs;
    }

    public static String format(long seconds) {
        if (seconds <= 0) {
            return "updating...";
        }

        long years = seconds / 31556952;
        seconds -= years * 31556952;
        long months = seconds / 2592000;
        seconds -= months * 2592000;
        long weeks = seconds / 604800;
        seconds -= weeks * 604800;
        long days = seconds / 86400;
        seconds -= days * 86400;
        long hours = seconds / 3600;
        seconds -= hours * 3600;
        long minutes = seconds / 60;
        seconds -= minutes * 60;

        StringBuilder sb = new StringBuilder();

        if (years > 0) {
            sb.append(years).append("yr");
        }

        if (months > 0) {
            sb.append(months).append("mo");
        }

        if (weeks > 0) {
            sb.append(weeks).append("w");
        }

        if (days > 0) {
            sb.append(days).append("d");
        }

        if (hours > 0) {
            sb.append(hours).append("h");
        }

        if (minutes > 0) {
            sb.append(minutes).append("m");
        }

        if (seconds > 0) {
            sb.append(seconds).append("s");
        }

        return sb.toString();
    }
}
