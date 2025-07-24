package com.leonardobishop.quests.bukkit.util;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.common.config.QuestsConfig;
import org.jspecify.annotations.NullMarked;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.logging.Level;

@NullMarked
public final class FormatUtils {

    private static DecimalFormat floatingFormat = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
    private static DecimalFormat integralFormat = new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.US));

    public static void setNumberFormats(final BukkitQuestsPlugin plugin) {
        floatingFormat = parseFormat(plugin, "floating", floatingFormat);
        integralFormat = parseFormat(plugin, "integral", integralFormat);
    }

    private static DecimalFormat parseFormat(final BukkitQuestsPlugin plugin, final String path, final DecimalFormat def) {
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

    public static String floating(final Number floating) {
        return floatingFormat.format(floating);
    }

    public static String integral(final Number integral) {
        return integralFormat.format(integral);
    }

    // Probably can be somehow cleaned
    public static String time(long seconds) {
        String messageString = Messages.TIME_FORMAT.getMessageLegacyColor();

        final boolean useDays = messageString.contains("{days}");
        if (useDays) {
            final long days = seconds / 86400;
            seconds = seconds % 86400;
            messageString = messageString.replace("{days}", String.format("%02d", days));
        }

        final boolean useHours = messageString.contains("{hours}");
        if (useHours) {
            final long hours = seconds / 3600;
            seconds = seconds % 3600;
            messageString = messageString.replace("{hours}", String.format("%02d", hours));
        }

        final boolean useMinutes = messageString.contains("{minutes}");
        if (useMinutes) {
            final long minutes = seconds / 60;
            seconds = seconds % 60;
            messageString = messageString.replace("{minutes}", String.format("%02d", minutes));
        }

        final boolean useSeconds = messageString.contains("{seconds}");
        if (useSeconds) {
            messageString = messageString.replace("{seconds}", String.format("%02d", seconds));
        }

        // ({days}d) {hours}h {minutes}m {seconds}s
        return messageString;
    }
}
