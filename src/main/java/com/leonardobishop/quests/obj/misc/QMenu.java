package com.leonardobishop.quests.obj.misc;

import com.leonardobishop.quests.player.QPlayer;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;

public interface QMenu {

    QPlayer getOwner();
    HashMap<?, ?> getSlotsToMenu();
    Inventory toInventory(int page);

}
