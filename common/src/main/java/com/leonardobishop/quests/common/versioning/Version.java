package com.leonardobishop.quests.common.versioning;

import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

@SuppressWarnings("ClassCanBeRecord")
@NullMarked
public final class Version implements Comparable<Version> {

    public static final Version V1_8 = new Version(1, 8);
    public static final Version V1_9 = new Version(1, 9);
    public static final Version V1_11 = new Version(1, 11);
    public static final Version V1_11_2 = new Version(1, 11, 2);
    public static final Version V1_14 = new Version(1, 14);
    public static final Version V1_15_2 = new Version(1, 15, 2);
    public static final Version V1_16 = new Version(1, 16);
    public static final Version V1_17 = new Version(1, 17);
    public static final Version V1_19_2 = new Version(1, 19, 2);
    public static final Version V1_20 = new Version(1, 20);
    public static final Version V1_20_4 = new Version(1, 20, 4);
    public static final Version V1_21_2 = new Version(1, 21, 2);
    public static final Version V1_21_6 = new Version(1, 21, 6);
    public static final Version V1_21_11 = new Version(1, 21, 11);
    public static final Version UNKNOWN = new Version(Integer.MAX_VALUE);

    private final @Nullable String preRelease;
    private final @Nullable String buildMetaData;
    private final int[] versionParts;

    private Version(final @Nullable String preRelease, final @Nullable String buildMetaData, final int... versionParts) {
        this.preRelease = preRelease;
        this.buildMetaData = buildMetaData;
        this.versionParts = versionParts;
    }

    private Version(final int... versionParts) {
        this(null, null, versionParts);
    }

    public boolean isHigherThan(final Version other) {
        return this.compareTo(other) > 0;
    }

    public boolean isHigherOrEqualTo(final Version other) {
        return this.compareTo(other) >= 0;
    }

    public boolean isEqualTo(final Version other) {
        return this.compareTo(other) == 0;
    }

    public boolean isLowerOrEqualTo(final Version other) {
        return this.compareTo(other) <= 0;
    }

    public boolean isLowerThan(final Version other) {
        return this.compareTo(other) < 0;
    }

    @SuppressWarnings("PointlessArithmeticExpression")
    @Override
    public int compareTo(final Version other) {
        final int commonLength = Math.min(this.versionParts.length, other.versionParts.length);

        for (int i = 0; i < commonLength; i++) {
            final int diff = this.versionParts[i] - other.versionParts[i];

            if (diff != 0) {
                return diff;
            }
        }

        for (int i = commonLength; i < this.versionParts.length; i++) {
            final int diff = this.versionParts[i] - 0;

            if (diff != 0) {
                return diff;
            }
        }

        for (int i = commonLength; i < other.versionParts.length; i++) {
            final int diff = 0 - other.versionParts[i];

            if (diff != 0) {
                return diff;
            }
        }

        return 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.preRelease, this.buildMetaData, Arrays.hashCode(this.versionParts));
    }

    @Override
    public boolean equals(final Object other) {
        if (other == null || this.getClass() != other.getClass()) {
            return false;
        }

        final Version otherVersion = (Version) other;

        return Objects.equals(this.preRelease, otherVersion.preRelease)
                && Objects.equals(this.buildMetaData, otherVersion.buildMetaData)
                && Objects.deepEquals(this.versionParts, otherVersion.versionParts);
    }

    public String toClassNameString() {
        final StringBuilder builder = new StringBuilder();

        builder.append('V');

        for (int i = 0; i < this.versionParts.length; i++) {
            builder.append(this.versionParts[i]);

            if (i != this.versionParts.length - 1) {
                builder.append('_');
            }
        }

        return builder.toString();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder();

        for (int i = 0; i < this.versionParts.length; i++) {
            builder.append(this.versionParts[i]);

            if (i != this.versionParts.length - 1) {
                builder.append('.');
            }
        }

        if (this.preRelease != null) {
            builder.append('-');
            builder.append(this.preRelease);
        }

        if (this.buildMetaData != null) {
            builder.append('+');
            builder.append(this.buildMetaData);
        }

        return builder.toString();
    }

    public static Version fromString(final String str) throws IllegalArgumentException {
        final int minusIndex = str.indexOf('-');
        final int plusIndex = str.indexOf('+', minusIndex + 1);

        final String preRelease = minusIndex != -1
                ? str.substring(minusIndex + 1, plusIndex != -1 ? plusIndex : str.length())
                : null;

        final String buildMetaData = plusIndex != -1
                ? str.substring(plusIndex + 1)
                : null;

        final int optionalIndex = minusIndex != -1 ? minusIndex : plusIndex;
        final String versionCore = optionalIndex != -1 ? str.substring(0, optionalIndex) : str;
        final String[] stringVersionParts = versionCore.split("\\.");
        final int[] versionParts = new int[stringVersionParts.length];

        for (int i = 0; i < stringVersionParts.length; i++) {
            final String stringVersionPart = stringVersionParts[i];
            final int versionPart;

            try {
                versionPart = Integer.parseUnsignedInt(stringVersionPart);
            } catch (final NumberFormatException e) {
                throw new IllegalArgumentException("Unparsable version part: '" + stringVersionPart + "'");
            }

            versionParts[i] = versionPart;
        }

        return new Version(preRelease, buildMetaData, versionParts);
    }
}
