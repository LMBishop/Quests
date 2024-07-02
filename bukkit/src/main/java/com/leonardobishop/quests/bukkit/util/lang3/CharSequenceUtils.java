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
public final class CharSequenceUtils {

    // https://github.com/apache/commons-lang/blob/98a67224956a680958568986747160d20cfbbe25/src/main/java/org/apache/commons/lang3/CharSequenceUtils.java#L294-L338
    public static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart, // Quests - package -> public
                                        final CharSequence substring, final int start, final int length)    {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        }
        int index1 = thisStart;
        int index2 = start;
        int tmpLen = length;

        // Extract these first so we detect NPEs the same as the java.lang.String version
        final int srcLen = cs.length() - thisStart;
        final int otherLen = substring.length() - start;

        // Check for invalid parameters
        if (thisStart < 0 || start < 0 || length < 0) {
            return false;
        }

        // Check that the regions are long enough
        if (srcLen < length || otherLen < length) {
            return false;
        }

        while (tmpLen-- > 0) {
            final char c1 = cs.charAt(index1++);
            final char c2 = substring.charAt(index2++);

            if (c1 == c2) {
                continue;
            }

            if (!ignoreCase) {
                return false;
            }

            // The real same check as in String.regionMatches():
            final char u1 = Character.toUpperCase(c1);
            final char u2 = Character.toUpperCase(c2);
            if (u1 != u2 && Character.toLowerCase(u1) != Character.toLowerCase(u2)) {
                return false;
            }
        }

        return true;
    }
}
