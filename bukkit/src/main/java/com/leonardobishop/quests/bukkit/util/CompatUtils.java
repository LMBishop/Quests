package com.leonardobishop.quests.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class CompatUtils {

    private static final String CRAFTBUKKIT_PACKAGE = Bukkit.getServer().getClass().getPackage().getName();

    public static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean classWithMethodExists(String className, String methodName, Class<?>... methodParameterTypes) {
        className = className.replace("{}", CRAFTBUKKIT_PACKAGE);

        try {
            Class.forName(className).getDeclaredMethod(methodName, methodParameterTypes);
            return true;
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            return false;
        }
    }

    public static boolean isPluginEnabled(String pluginName) {
        return Bukkit.getPluginManager().isPluginEnabled(pluginName);
    }

    @SuppressWarnings("deprecation")
    public static String getPluginVersion(String pluginName) {
        Plugin plugin = Bukkit.getPluginManager().getPlugin(pluginName);
        return plugin != null ? plugin.getDescription().getVersion() : null;
    }

    public static Class<?> getFirstClassAvailable(String... classNames) {
        for (String className : classNames) {
            try {
                return Class.forName(className);
            } catch (ClassNotFoundException ignored) {
            }
        }
        return null;
    }
}
