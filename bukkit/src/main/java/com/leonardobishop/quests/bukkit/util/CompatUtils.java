package com.leonardobishop.quests.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class CompatUtils {

    public static boolean classExists(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static boolean classWithMethodExists(String className, String methodName, Class<?>... methodParameterTypes) {
        try {
            Class.forName(className).getMethod(methodName, methodParameterTypes);
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
}
