package com.leonardobishop.quests.bukkit.hook.versionspecific;

import com.leonardobishop.quests.common.versioning.Version;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;

import java.lang.reflect.Method;

@NullMarked
public class VersionSpecificHandler_V1_12_2 extends VersionSpecificHandler_V1_11_2 {

    static @Nullable Method paperGetStateMethod;

    static {
        try {
            paperGetStateMethod = Block.class.getMethod("getState", boolean.class);
        } catch (NoSuchMethodException e) {
            // supported only on Paper
        }
    }

    @Override
    public Version getMinecraftVersion() {
        return Version.V1_12_2;
    }

    @Override
    public BlockState getStateNoSnapshot(Block block) {
        if (paperGetStateMethod == null) {
            return super.getStateNoSnapshot(block);
        }

        try {
            return (BlockState) paperGetStateMethod.invoke(block, false);
        } catch (ReflectiveOperationException e) {
            return super.getStateNoSnapshot(block);
        }
    }
}
