package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.common.versioning.Version;
import org.bukkit.Keyed;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Biome;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.CaveVinesPlant;
import org.bukkit.damage.DamageSource;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.Camel;
import org.bukkit.entity.CamelHusk;
import org.bukkit.entity.Donkey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Goat;
import org.bukkit.entity.HappyGhast;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Llama;
import org.bukkit.entity.Mule;
import org.bukkit.entity.Nautilus;
import org.bukkit.entity.Player;
import org.bukkit.entity.SkeletonHorse;
import org.bukkit.entity.Strider;
import org.bukkit.entity.ZombieHorse;
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
import org.bukkit.inventory.SmithingInventory;
import org.bukkit.inventory.SmithingTransformRecipe;
import org.bukkit.inventory.SmithingTrimRecipe;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.NavigableSet;
import java.util.Objects;
import java.util.TreeSet;

/**
 * Interface used for implementing version-specific features.
 * All information about changes in the API should be documented HERE in the method docs.
 * Every implementation version must be added to the constant in the interface class.
 */
@SuppressWarnings({"deprecation", "BooleanMethodIsAlwaysInverted"})
@NullMarked
public interface VersionSpecificHandler {

    NavigableSet<Version> IMPLEMENTATIONS = Collections.unmodifiableNavigableSet(new TreeSet<>() {{
        this.add(Version.V1_8);
        this.add(Version.V1_9);
        this.add(Version.V1_11);
        this.add(Version.V1_11_2);
        this.add(Version.V1_14);
        this.add(Version.V1_15_2);
        this.add(Version.V1_16);
        this.add(Version.V1_17);
        this.add(Version.V1_19_2);
        this.add(Version.V1_20);
        this.add(Version.V1_20_4);
        this.add(Version.V1_21_2);
        this.add(Version.V1_21_6);
        this.add(Version.V1_21_11);
    }});

    static VersionSpecificHandler getImplementation(final Version serverVersion) {
        final Version matchedVersion = Objects.requireNonNullElseGet(
                IMPLEMENTATIONS.floor(serverVersion),
                IMPLEMENTATIONS::first
        );

        final String clazzName = String.format("%s_%s",
                VersionSpecificHandler.class.getCanonicalName(),
                matchedVersion.toClassNameString()
        );

        try {
            return (VersionSpecificHandler) Class.forName(clazzName).getConstructor().newInstance();
        } catch (final ReflectiveOperationException e) {
            throw new IllegalStateException("Failed to construct version specific handler", e);
        }
    }

    @SuppressWarnings("unused")
    Version getMinecraftVersion();

    /**
     * Elytra were introduced in {@code 1.9}.
     *
     * @see Player#isGliding()
     */
    boolean isPlayerGliding(Player player);

    /**
     * Camels were introduced in {@code 1.20}.
     *
     * @see Camel
     */
    boolean isPlayerOnCamel(Player player);

    /**
     * Camel Husks were introduced in {@code 1.21.11}.
     *
     * @see CamelHusk
     */
    boolean isPlayerOnCamelHusk(Player player);

    /**
     * Donkeys were introduced in {@code 1.6.1}.
     *
     * <p>
     * Each horse variant got its own interface in {@code 1.11}. {@link Horse#getVariant()} and {@link Horse.Variant}
     * have been deprecated. {@link AbstractHorse} superseded {@link Horse} (interface used before to represent all
     * the horses) and the old one represents now an actual horse. {@link Horse.Variant#DONKEY} corresponding interface
     * in modern versions is {@link Donkey}.
     * </p>
     */
    @SuppressWarnings("removal")
    boolean isPlayerOnDonkey(Player player);

    /**
     * Happy Ghasts were introduced in {@code 1.21.6}.
     *
     * @see HappyGhast
     */
    boolean isPlayerOnHappyGhast(Player player);

    /**
     * Horses were introduced in {@code 1.6.1}.
     *
     * <p>
     * Each horse variant got its own interface in {@code 1.11}. {@link Horse#getVariant()} and {@link Horse.Variant}
     * have been deprecated. {@link AbstractHorse} superseded {@link Horse} (interface used before to represent all
     * the horses) and the old one represents now an actual horse. {@link Horse.Variant#HORSE} corresponding interface
     * in modern versions is {@link Horse}.
     * </p>
     */
    @SuppressWarnings("removal")
    boolean isPlayerOnHorse(Player player);

    /**
     * Llamas were introduced in {@code 1.11}.
     *
     * @see Llama
     */
    boolean isPlayerOnLlama(Player player);

    /**
     * Mules were introduced in {@code 1.6.1}.
     *
     * <p>
     * Each horse variant got its own interface in {@code 1.11}. {@link Horse#getVariant()} and {@link Horse.Variant}
     * have been deprecated. {@link AbstractHorse} superseded {@link Horse} (interface used before to represent all
     * the horses) and the old one represents now an actual horse. {@link Horse.Variant#MULE} corresponding interface
     * in modern versions is {@link Mule}.
     * </p>
     */
    @SuppressWarnings("removal")
    boolean isPlayerOnMule(Player player);

    /**
     * Nautiluses were introduced in {@code 1.21.11}.
     *
     * @see Nautilus
     */
    boolean isPlayerOnNautilus(Player player);

    /**
     * Skeleton horses were introduced in {@code 1.6.1}.
     *
     * <p>
     * Each horse variant got its own interface in {@code 1.11}. {@link Horse#getVariant()} and {@link Horse.Variant}
     * have been deprecated. {@link AbstractHorse} superseded {@link Horse} (interface used before to represent all
     * the horses) and the old one represents now an actual horse. {@link Horse.Variant#SKELETON_HORSE} corresponding
     * interface in modern versions is {@link SkeletonHorse}.
     * </p>
     */
    @SuppressWarnings("removal")
    boolean isPlayerOnSkeletonHorse(Player player);

    /**
     * Striders were introduced in {@code 1.16}.
     *
     * @see Strider
     */
    boolean isPlayerOnStrider(Player player);

    /**
     * Zombie (undead) horses were introduced in {@code 1.6.1}.
     *
     * <p>
     * Each horse variant got its own interface in {@code 1.11}. {@link Horse#getVariant()} and {@link Horse.Variant}
     * have been deprecated. {@link AbstractHorse} superseded {@link Horse} (interface used before to represent all
     * the horses) and the old one represents now an actual horse. {@link Horse.Variant#UNDEAD_HORSE} corresponding
     * interface in modern versions is {@link ZombieHorse}.
     * </p>
     */
    @SuppressWarnings("removal")
    boolean isPlayerOnZombieHorse(Player player);

    /**
     * Ability to swap item in hand while hovering over an item was introduced in {@code 1.16}.
     *
     * @see ClickType#SWAP_OFFHAND
     */
    boolean isOffHandSwap(ClickType clickType);

    /**
     * Dual-wielding system was introduced in {@code 1.9}.
     */
    boolean isOffHandEmpty(Player player);

    /**
     * Initially, the proper method to get an inventory contents except armor and other extra slots
     * (not allowing player to store results of crafting actions) was {@link PlayerInventory#getContents()}.
     * In {@code 1.9} {@link PlayerInventory#getStorageContents()} method was introduced superseding the old
     * one. In newer versions {@link PlayerInventory#getContents()} method returns all the items including
     * the extra slots of player inventories.
     *
     * @apiNote This method is intended to be used as a check for item crafting related task types.
     * @see VersionSpecificHandler#getStorageContents(PlayerInventory)
     */
    default int getAvailableSpace(Player player, ItemStack item) {
        PlayerInventory inventory = player.getInventory();
        HashMap<Integer, ? extends ItemStack> itemsOfType = inventory.all(item.getType());
        int availableSpace = 0;

        for (ItemStack existingItem : itemsOfType.values()) {
            if (item.isSimilar(existingItem)) {
                availableSpace += (item.getMaxStackSize() - existingItem.getAmount());
            }
        }

        for (ItemStack existingItem : this.getStorageContents(inventory)) {
            if (existingItem == null) {
                availableSpace += item.getMaxStackSize();
            }
        }

        return availableSpace;
    }

    /**
     * Initially, the proper method to get an inventory contents except armor and other extra slots
     * (not allowing player to store results of crafting actions) was {@link PlayerInventory#getContents()}.
     * In {@code 1.9} {@link PlayerInventory#getStorageContents()} method was introduced superseding the old
     * one. In newer versions {@link PlayerInventory#getContents()} method returns all the items including
     * the extra slots of player inventories.
     *
     * @apiNote This method is intended to be used as a check for item crafting related task types.
     */
    @Nullable ItemStack[] getStorageContents(PlayerInventory inventory);

    /**
     * Initially, clicking with a number key on a crafting result made the item go to the selected slot.
     * Starting with {@code 1.9} clicking it is no longer effective.
     *
     * @apiNote This method is intended to be used as a check for item crafting related task types.
     */
    boolean isHotbarMoveAndReaddSupported();

    /**
     * Initially, drop key clicking with control pressed on a crafting result resulted in dropping
     * the recipe amount of an item. Starting with {@code 1.21.2} clicking it results in dropping
     * the max craftable amount possible - <a href="https://github.com/LMBishop/Quests/issues/317">
     * related issue</a>.
     *
     * @apiNote This method is intended to be used as a check for item crafting related task types.
     */
    boolean isCraftingControlDropAllSupported();

    /**
     * Cave vines plants were introduced in {@code 1.17}.
     *
     * @see CaveVinesPlant#isBerries()
     */
    boolean isCaveVinesPlantWithBerries(BlockData blockData);

    /**
     * Dual-wielding system was introduced in {@code 1.9}.
     */
    ItemStack getItemInMainHand(Player player);

    /**
     * Initially there was no {@link PlayerInventory#getItem(EquipmentSlot)} method. Possible enum values were:
     * {@link EquipmentSlot#CHEST}, {@link EquipmentSlot#FEET}, {@link EquipmentSlot#HAND}, {@link EquipmentSlot#HEAD}
     * and {@link EquipmentSlot#LEGS}. In {@code 1.9} {@link EquipmentSlot#OFF_HAND} was introduced, however a method
     * to get specified equipment slot item still hasn't existed. In {@code 1.15.2} the method was finally introduced
     * {@link PlayerInventory#getItem(EquipmentSlot)} making us able to no longer maintain this one.
     */
    @Nullable ItemStack getItemInEquipmentSlot(PlayerInventory inventory, EquipmentSlot slot);

    /**
     * Dual-wielding system was introduced in {@code 1.9}.
     * Unfortunately {@link PlayerBucketEmptyEvent#getHand()} method was added later in {@code 1.19.2}.
     * {@link PlayerBucketEmptyEvent#getItemStack()} returns the empty action result (empty bucket).
     */
    ItemStack getItem(PlayerBucketEmptyEvent event);

    /**
     * Dual-wielding system was introduced in {@code 1.9}.
     */
    @Nullable EquipmentSlot getHand(PlayerInteractEvent event);

    /**
     * Dual-wielding system was introduced in {@code 1.9}.
     */
    EquipmentSlot getHand(PlayerInteractEntityEvent event);

    /**
     * Items smithing system was introduced in {@code 1.16} with {@link SmithingInventory#getInputEquipment()}
     * and {@link SmithingInventory#getInputMineral()}. In {@code 1.20} the feature was extended to support templates.
     * Due to the following reason, new method was added: {@link SmithingInventory#getInputTemplate()}.
     */
    @Nullable ItemStack[] getSmithItems(SmithItemEvent event);

    /**
     * Items smithing system was introduced in {@code 1.16} with {@link SmithingTransformRecipe}.
     * In {@code 1.20} the feature was extended to support templates. Due to the following reason,
     * new class has been added {@link SmithingTrimRecipe}.
     */
    @Nullable String getSmithMode(SmithItemEvent event);

    /**
     * Goats were introduced in {@code 1.17}.
     *
     * @see Goat
     */
    boolean isGoat(Entity entity);

    /**
     * <a href="https://github.com/LMBishop/Quests/issues/787">Reason behind moving it to a version specific handler</a>
     */
    int removeItem(Inventory inventory, int slot, int amountToRemove);

    /**
     * {@link Entity#getPassengers()} method was introduced in {@code 1.11.2}.
     */
    List<Entity> getPassengers(Entity entity);

    /**
     * {@link DamageSource}s were introduced in {@code 1.20.4}.
     */
    @Nullable Player getDamager(@Nullable EntityDamageEvent lastDamageCause);

    /**
     * {@link DamageSource}s were introduced in {@code 1.20.4}.
     */
    @Nullable Entity getDirectSource(@Nullable EntityDamageEvent lastDamageCause);

    /**
     * {@link Tag#CANDLE_CAKES} was introduced in {@code 1.17}.
     */
    boolean isCake(Material type);

    /**
     * {@link Biome} implements {@link Keyed} from {@code 1.14}.
     */
    String getBiomeKey(Biome biome);
}
