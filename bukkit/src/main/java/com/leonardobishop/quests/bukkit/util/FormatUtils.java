package com.leonardobishop.quests.bukkit.util;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.config.QuestsConfig;
import org.jetbrains.annotations.NotNull;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.logging.Level;

public final class FormatUtils {

    private static DecimalFormat floatingFormat = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
    private static DecimalFormat integralFormat = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));

    public static void setNumberFormats(final @NotNull BukkitQuestsPlugin plugin) {
        floatingFormat = parseFormat(plugin, "floating", floatingFormat);
        integralFormat = parseFormat(plugin, "integral", integralFormat);
    }

    private static @NotNull DecimalFormat parseFormat(final @NotNull BukkitQuestsPlugin plugin, final @NotNull String path, final @NotNull DecimalFormat def) {
        final QuestsConfig config = plugin.getQuestsConfig();

        final String formatString = config.getString("number-formats." + path + ".format", def.toPattern());
        final String localeString = config.getString("number-formats." + path + ".locale", def.getDecimalFormatSymbols().getLocale().toLanguageTag());

        try {
            final Locale locale = Locale.forLanguageTag(localeString);
            final DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance(locale);
            return new DecimalFormat(formatString, symbols);
        } catch (final IllegalArgumentException e) {
            plugin.getLogger().log(Level.SEVERE, "Could not parse number format: " + formatString, e);
            return def;
        }
    }

    public static @NotNull String floating(final @NotNull Number floating) {
        return floatingFormat.format(floating);
    }

    public static @NotNull String integral(final @NotNull Number integral) {
        return integralFormat.format(integral);
    }

    public static @NotNull String time(final long totalSeconds) {
        final long hours = totalSeconds / 3600;

        final long remainingSeconds = totalSeconds % 3600;
        final long minutes = remainingSeconds / 60;
        final long seconds = remainingSeconds % 60;

        // {hours}h {minutes}m {seconds}s
        return Messages.TIME_FORMAT.getMessageLegacyColor()
                .replace("{hours}", String.format("%02d", hours))
                .replace("{minutes}", String.format("%02d", minutes))
                .replace("{seconds}", String.format("%02d", seconds));
    }
}
