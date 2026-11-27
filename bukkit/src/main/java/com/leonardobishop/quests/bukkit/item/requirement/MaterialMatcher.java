package com.leonardobishop.quests.bukkit.item.requirement;

import com.leonardobishop.quests.bukkit.util.CompatUtils;
import com.leonardobishop.quests.common.tasktype.key.TaskKeyParser;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 * A value of the {@code material} item option: either one material ({@code STONE}) or an item tag
 * ({@code #minecraft:logs}, {@code #logs} for the {@code minecraft} namespace).
 * <p>
 * Item tags are the vanilla and data pack item tags exposed by the Bukkit tag API, which exists
 * since Minecraft 1.13. On older servers tags are reported as a configuration error.
 */
@NullMarked
public sealed interface MaterialMatcher permits MaterialMatcher.Single, MaterialMatcher.ItemTag {

    /** Whether the server provides the Bukkit tag API (1.13+). */
    boolean TAGS_SUPPORTED = CompatUtils.classWithMethodExists("org.bukkit.Bukkit", "getTag", String.class, NamespacedKey.class, Class.class);

    boolean matches(Material material);

    /**
     * @return the single material for {@code defined-exact}, or null if this matcher covers several materials
     */
    @Nullable Material exact();

    record Single(Material material) implements MaterialMatcher {

        @Override
        public boolean matches(final Material material) {
            return this.material == material;
        }

        @Override
        public Material exact() {
            return this.material;
        }

        @Override
        public String toString() {
            return this.material.name();
        }
    }

    record ItemTag(Tag<Material> tag) implements MaterialMatcher {

        @Override
        public boolean matches(final Material material) {
            return this.tag.isTagged(material);
        }

        @Override
        public @Nullable Material exact() {
            return null;
        }

        @Override
        public String toString() {
            return "#" + this.tag.getKey();
        }
    }

    Pattern NAMESPACE = Pattern.compile("[a-z0-9._-]+");
    Pattern TAG_PATH = Pattern.compile("[a-z0-9._/-]+");

    static TaskKeyParser<MaterialMatcher> parser() {
        return (raw, context) -> {
            final String value = String.valueOf(raw).trim();

            if (!value.startsWith("#")) {
                final Material material = Material.matchMaterial(value);
                if (material == null || material == Material.AIR) {
                    context.error("Unknown material '" + value + "' in '" + context.getPath() + "'");
                    return null;
                }
                return new Single(material);
            }

            if (!TAGS_SUPPORTED) {
                context.error("Item tag '" + value + "' in '" + context.getPath() + "' cannot be used, item tags require Minecraft 1.13 or newer");
                return null;
            }

            final String id = value.substring(1).toLowerCase(Locale.ROOT);
            final int colon = id.indexOf(':');
            final String namespace = colon >= 0 ? id.substring(0, colon) : NamespacedKey.MINECRAFT;
            final String path = colon >= 0 ? id.substring(colon + 1) : id;

            if (!NAMESPACE.matcher(namespace).matches() || !TAG_PATH.matcher(path).matches()) {
                context.error("Invalid item tag '" + value + "' in '" + context.getPath() + "' (expected e.g. '#minecraft:logs')");
                return null;
            }

            final MaterialMatcher tag = TagSupport.itemTag(namespace, path);
            if (tag == null) {
                context.error("Unknown item tag '" + value + "' in '" + context.getPath() + "'");
                return null;
            }

            return tag;
        };
    }

    /**
     * Isolates every use of the tag API (1.13+), so the class is only loaded when tags are supported.
     */
    final class TagSupport {

        private TagSupport() {
        }

        @SuppressWarnings("deprecation")
        static @Nullable MaterialMatcher itemTag(final String namespace, final String path) {
            // NamespacedKey#fromString only exists since 1.16.5, the constructor works on every version with tags
            final NamespacedKey key = NamespacedKey.MINECRAFT.equals(namespace) ? NamespacedKey.minecraft(path) : new NamespacedKey(namespace, path);
            final Tag<Material> tag = Bukkit.getTag(Tag.REGISTRY_ITEMS, key, Material.class);
            return tag != null ? new ItemTag(tag) : null;
        }
    }
}
