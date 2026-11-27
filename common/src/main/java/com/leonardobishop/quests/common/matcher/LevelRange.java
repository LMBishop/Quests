package com.leonardobishop.quests.common.matcher;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An inclusive range of levels, where level {@code 0} means "absent".
 * <p>
 * Notation (the part after the last {@code :} of e.g. an enchantment):
 * <table>
 *     <tr><td><i>(none)</i></td><td>1 or higher (present, any level)</td></tr>
 *     <tr><td>{@code 3}</td><td>exactly 3</td></tr>
 *     <tr><td>{@code 3+}</td><td>3 or higher</td></tr>
 *     <tr><td>{@code 3-}</td><td>0 to 3 (absence counts)</td></tr>
 *     <tr><td>{@code 3-5}</td><td>3 to 5</td></tr>
 *     <tr><td>{@code 0}</td><td>absent</td></tr>
 * </table>
 *
 * @param min lowest accepted level (at least 0)
 * @param max highest accepted level, or null for no upper bound
 */
@NullMarked
public record LevelRange(int min, @Nullable Integer max) {

    /** Any level of at least 1, i.e. present. */
    public static final LevelRange PRESENT = new LevelRange(1, null);

    private static final Pattern NOTATION = Pattern.compile("(\\d+)(\\+|-|-(\\d+))?");

    public LevelRange {
        if (min < 0) {
            throw new IllegalArgumentException("min cannot be negative");
        }
        if (max != null && max < min) {
            throw new IllegalArgumentException("max cannot be lower than min");
        }
    }

    /**
     * @param notation level notation without the name, e.g. {@code 3+}
     * @return the range, or null if the notation is invalid (including a reversed range)
     */
    public static @Nullable LevelRange parse(final String notation) {
        final Matcher m = NOTATION.matcher(notation);
        if (!m.matches()) {
            return null;
        }

        try {
            final int first = Integer.parseInt(m.group(1));
            if (m.group(2) == null) {
                return new LevelRange(first, first);
            }
            if (m.group(2).equals("+")) {
                return new LevelRange(first, null);
            }
            if (m.group(2).equals("-")) {
                return new LevelRange(0, first);
            }

            final int second = Integer.parseInt(m.group(3));
            return second < first ? null : new LevelRange(first, second);
        } catch (final NumberFormatException e) {
            return null;
        }
    }

    /**
     * @param level the actual level, {@code 0} if absent
     */
    @Contract(pure = true)
    public boolean contains(final int level) {
        return level >= this.min && (this.max == null || level <= this.max);
    }

    /**
     * @return whether the range accepts every level (including absence)
     */
    @Contract(pure = true)
    public boolean isAlwaysTrue() {
        return this.min == 0 && this.max == null;
    }

    /**
     * @return whether the range describes exactly one level
     */
    @Contract(pure = true)
    public boolean isSingle() {
        return this.max != null && this.min == this.max;
    }

    @Override
    public String toString() {
        if (this.max == null) {
            return this.min == 1 ? "any" : this.min + "+";
        }
        if (this.min == this.max) {
            return String.valueOf(this.min);
        }
        return this.min == 0 ? this.max + "-" : this.min + "-" + this.max;
    }
}
