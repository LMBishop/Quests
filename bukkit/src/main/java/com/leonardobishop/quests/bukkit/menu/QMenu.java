package com.leonardobishop.quests.bukkit.menu;

import com.leonardobishop.quests.bukkit.menu.element.MenuElement;
import com.leonardobishop.quests.common.player.QPlayer;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.Nullable;

public abstract class QMenu {

    protected final QPlayer owner;
    protected final Int2ObjectOpenHashMap<MenuElement> menuElements = new Int2ObjectOpenHashMap<>();

    public QMenu(QPlayer owner) {
        this.owner = owner;
    }

    public final QPlayer getOwner() {
        return owner;
    }

    public @Nullable MenuElement getMenuElementAt(int slot) {
        return menuElements.get(slot);
    }

    abstract Inventory draw();

}
