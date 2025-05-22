package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.entity.Donkey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Mule;
import org.bukkit.entity.Player;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.ZombieHorse;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class VersionSpecificHandler11 extends VersionSpecificHandler9 implements VersionSpecificHandler {

    private static Method getPassengersMethod;

    static {
        try {
            getPassengersMethod = Entity.class.getMethod("getPassengers");
        } catch (final NoSuchMethodException e) {
            // server version cannot support the method (doesn't work on 1.11, 1.11.1)
        }
    }

    @Override
    public int getMinecraftVersion() {
        return 11;
    }

    @Override
    public boolean isPlayerOnDonkey(Player player) {
        return player.getVehicle() instanceof Donkey;
    }

    @Override
    public boolean isPlayerOnHorse(Player player) {
        return player.getVehicle() instanceof Horse;
    }

    @Override
    public boolean isPlayerOnLlama(Player player) {
        return player.getVehicle() instanceof Llama;
    }

    @Override
    public boolean isPlayerOnMule(Player player) {
        return player.getVehicle() instanceof Mule;
    }

    @Override
    public boolean isPlayerOnSkeletonHorse(Player player) {
        return player.getVehicle() instanceof SkeletonHorse;
    }

    @Override
    public boolean isPlayerOnZombieHorse(Player player) {
        return player.getVehicle() instanceof ZombieHorse;
    }

    @Override
    public int removeItem(Inventory inventory, int slot, int amountToRemove) {
        ItemStack item = inventory.getItem(slot);

        if (item == null) {
            return 0;
        }

        int amountInStack = item.getAmount();
        int newAmountInStack = Math.max(0, amountInStack - amountToRemove);
        item.setAmount(newAmountInStack);

        return amountInStack - newAmountInStack;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<Entity> getPassengers(Entity entity) {
        if (getPassengersMethod == null) {
            return super.getPassengers(entity);
        }

        try {
            return (List<Entity>) getPassengersMethod.invoke(entity);
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new IllegalStateException("Entity#getPassengers invocation failed", e);
        }
    }
}
