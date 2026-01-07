package com.leonardobishop.quests.bukkit.hook.versionspecific;

import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.SmithItemEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.projectiles.ProjectileSource;
import org.jspecify.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class VersionSpecificHandler8 implements VersionSpecificHandler {

    @Override
    public int getMinecraftVersion() {
        return 8;
    }

    @Override
    public boolean isPlayerGliding(Player player) {
        return false;
    }

    @Override
    public boolean isPlayerOnCamel(Player player) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPlayerOnDonkey(Player player) {
        return player.getVehicle() instanceof Horse horse && horse.getVariant() == Horse.Variant.DONKEY;
    }

    @Override
    public boolean isPlayerOnHappyGhast(Player player) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPlayerOnHorse(Player player) {
        return player.getVehicle() instanceof Horse horse && horse.getVariant() == Horse.Variant.HORSE;
    }

    @Override
    public boolean isPlayerOnLlama(Player player) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPlayerOnMule(Player player) {
        return player.getVehicle() instanceof Horse horse && horse.getVariant() == Horse.Variant.MULE;
    }

    @Override
    public boolean isPlayerOnNautilus(Player player) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPlayerOnSkeletonHorse(Player player) {
        return player.getVehicle() instanceof Horse horse && horse.getVariant() == Horse.Variant.SKELETON_HORSE;
    }

    @Override
    public boolean isPlayerOnStrider(Player player) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isPlayerOnZombieHorse(Player player) {
        return player.getVehicle() instanceof Horse horse && horse.getVariant() == Horse.Variant.UNDEAD_HORSE;
    }

    @Override
    public boolean isOffHandSwap(ClickType clickType) {
        return false;
    }

    @Override
    public boolean isOffHandEmpty(Player player) {
        return false;
    }

    @Override
    public int getAvailableSpace(Player player, ItemStack newItemStack) {
        int availableSpace = 0;
        PlayerInventory inventory = player.getInventory();
        HashMap<Integer, ? extends ItemStack> itemStacksWithSameMaterial = inventory.all(newItemStack.getType());
        for (ItemStack existingItemStack : itemStacksWithSameMaterial.values()) {
            if (newItemStack.isSimilar(existingItemStack)) {
                availableSpace += (newItemStack.getMaxStackSize() - existingItemStack.getAmount());
            }
        }

        for (ItemStack existingItemStack : inventory.getContents()) {
            if (existingItemStack == null) {
                availableSpace += newItemStack.getMaxStackSize();
            }
        }

        return availableSpace;
    }

    @Override
    public boolean isHotbarMoveAndReaddSupported() {
        return true;
    }

    @Override
    public boolean isCaveVinesPlantWithBerries(BlockData blockData) {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getItemInMainHand(Player player) {
        return player.getItemInHand();
    }

    @SuppressWarnings("deprecation")
    @Override
    public ItemStack getItemInEquipmentSlot(PlayerInventory inventory, EquipmentSlot slot) {
        return switch (slot) {
            case CHEST -> inventory.getChestplate();
            case FEET -> inventory.getBoots();
            case HAND -> inventory.getItemInHand();
            case HEAD -> inventory.getHelmet();
            case LEGS -> inventory.getLeggings();

            // there are 5 equipment slots on 1.8
            default -> null;
        };
    }

    @Override
    public ItemStack getItem(PlayerBucketEmptyEvent event) {
        return new ItemStack(event.getBucket(), 1);
    }

    @Override
    public EquipmentSlot getHand(PlayerInteractEvent event) {
        return EquipmentSlot.HAND;
    }

    @Override
    public EquipmentSlot getHand(PlayerInteractEntityEvent event) {
        return EquipmentSlot.HAND;
    }

    @Override
    public ItemStack[] getSmithItems(SmithItemEvent event) {
        return new ItemStack[0];
    }

    @Override
    public String getSmithMode(SmithItemEvent event) {
        return null;
    }

    @Override
    public boolean isGoat(Entity entity) {
        return false;
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

        // It's needed in older versions
        // https://github.com/LMBishop/Quests/issues/787
        inventory.setItem(slot, item);

        return amountInStack - newAmountInStack;
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<Entity> getPassengers(Entity entity) {
        Entity passenger = entity.getPassenger();
        return passenger != null ? List.of(passenger) : List.of();
    }

    @Override
    public @Nullable Player getDamager(@Nullable EntityDamageEvent event) {
        if (!(event instanceof EntityDamageByEntityEvent byEntityEvent)) {
            return null;
        }

        Entity damager = byEntityEvent.getDamager();

        if (damager instanceof Player) {
            return (Player) damager;
        }

        if (damager instanceof Projectile projectile) {
            ProjectileSource shooter = projectile.getShooter();

            if (shooter instanceof Player) {
                return (Player) shooter;
            }
        }

        return null;
    }

    @Override
    public @Nullable Entity getDirectSource(@Nullable EntityDamageEvent lastDamageCause) {
        return null;
    }

    @Override
    public boolean isCake(Material type) {
        return type == Material.CAKE;
    }

    @SuppressWarnings({"removal", "UnstableApiUsage"})
    @Override
    public String getBiomeKey(Biome biome) {
        return biome.name();
    }
}
