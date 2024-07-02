package com.leonardobishop.quests.bukkit.util.lang3;

/**
 * Apache Commons Lang<br>
 * Copyright 2001-2024 The Apache Software Foundation<br>
 * <br>
 * This product includes software developed at<br>
 * The Apache Software Foundation (<a href="https://www.apache.org/">https://www.apache.org/</a>).<br>
 * <br>
 * License: <a href="https://github.com/apache/commons-lang/blob/98a67224956a680958568986747160d20cfbbe25/LICENSE.txt">LICENSE.txt</a>
 */
@SuppressWarnings({"BooleanMethodIsAlwaysInverted", "SizeReplaceableByIsEmpty"})
public final class StringUtils {

    // https://github.com/apache/commons-lang/blob/98a67224956a680958568986747160d20cfbbe25/src/main/java/org/apache/commons/lang3/StringUtils.java#L3655-L3657
    public static boolean isEmpty(final CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    // https://github.com/apache/commons-lang/blob/98a67224956a680958568986747160d20cfbbe25/src/main/java/org/apache/commons/lang3/StringUtils.java#L3414-L3425
    public static boolean isAlphanumeric(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isLetterOrDigit(cs.charAt(i))) {
                if (cs.charAt(i) == '-') continue; // Quests - allow minus sign usage in ids
                return false;
            }
        }
        return true;
    }

    // https://github.com/apache/commons-lang/blob/98a67224956a680958568986747160d20cfbbe25/src/main/java/org/apache/commons/lang3/StringUtils.java#L3827-L3838
    public static boolean isNumeric(final CharSequence cs) {
        if (isEmpty(cs)) {
            return false;
        }
        final int sz = cs.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isDigit(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // https://github.com/apache/commons-lang/blob/98a67224956a680958568986747160d20cfbbe25/src/main/java/org/apache/commons/lang3/StringUtils.java#L1952-L1963
    public static boolean equals(final CharSequence cs1, final CharSequence cs2, final boolean ignoreCase) { // Quests - make ignore case a parameter
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1.length() != cs2.length()) {
            return false;
        }
        return CharSequenceUtils.regionMatches(cs1, ignoreCase, 0, cs2, 0, cs1.length()); // Quests - make ignore case a parameter
    }

    // https://github.com/apache/commons-lang/blob/98a67224956a680958568986747160d20cfbbe25/src/main/java/org/apache/commons/lang3/StringUtils.java#L8037-L8047
    public static boolean startsWith(final CharSequence str, final CharSequence prefix, final boolean ignoreCase) { // Quests - private -> public
        if (str == null || prefix == null) {
            return str == prefix;
        }
        // Get length once instead of twice in the unlikely case that it changes.
        final int preLen = prefix.length();
        if (preLen > str.length()) {
            return false;
        }
        return CharSequenceUtils.regionMatches(str, ignoreCase, 0, prefix, 0, preLen);
    }

    // https://github.com/apache/commons-lang/blob/98a67224956a680958568986747160d20cfbbe25/src/main/java/org/apache/commons/lang3/StringUtils.java#L1755-L1764
    public static boolean endsWith(final CharSequence str, final CharSequence suffix, final boolean ignoreCase) { // Quests - private -> public
        if (str == null || suffix == null) {
            return str == suffix;
        }
        if (suffix.length() > str.length()) {
            return false;
        }
        final int strOffset = str.length() - suffix.length();
        return CharSequenceUtils.regionMatches(str, ignoreCase, strOffset, suffix, 0, suffix.length());
    }
}
