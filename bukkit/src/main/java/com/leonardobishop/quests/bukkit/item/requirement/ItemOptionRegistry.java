package com.leonardobishop.quests.bukkit.item.requirement;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.UnmodifiableView;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Options available in item definitions on the running server, keyed by their configuration name.
 * <p>
 * Built-in options are registered by {@link DefaultItemOptions} when the plugin starts, depending on the
 * server version. Other plugins may register their own options before quests are loaded.
 */
@NullMarked
public final class ItemOptionRegistry {

    /** Reserved key selecting the kind of definition ({@code defined} / {@code defined-exact}). */
    public static final String TYPE_KEY = "type";

    private final Map<String, ItemOption<?>> options = new LinkedHashMap<>();

    /**
     * @param option the option to register
     * @throws IllegalArgumentException if an option with the same key is already registered
     */
    public void register(final ItemOption<?> option) {
        Objects.requireNonNull(option, "option cannot be null");

        if (TYPE_KEY.equals(option.getKey())) {
            throw new IllegalArgumentException("'" + TYPE_KEY + "' is reserved");
        }
        if (this.options.putIfAbsent(option.getKey(), option) != null) {
            throw new IllegalArgumentException("An item option '" + option.getKey() + "' is already registered");
        }
    }

    @Contract(pure = true)
    public @Nullable ItemOption<?> get(final String key) {
        return this.options.get(key);
    }

    /**
     * @return registered options in registration order
     */
    @Contract(pure = true)
    public @UnmodifiableView Collection<ItemOption<?>> getOptions() {
        return Collections.unmodifiableCollection(this.options.values());
    }

    @Contract(pure = true)
    public String getKeysAsString() {
        return String.join(", ", this.options.keySet());
    }
}
