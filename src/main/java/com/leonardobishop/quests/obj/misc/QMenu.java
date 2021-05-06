package com.leonardobishop.quests.obj.misc;

import com.leonardobishop.quests.player.QPlayer;

import java.util.HashMap;
import java.util.Map;

public interface QMenu {

    QPlayer getOwner();
    Map<?, ?> getSlotsToMenu();

}
