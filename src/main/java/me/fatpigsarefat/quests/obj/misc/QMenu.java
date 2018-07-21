package me.fatpigsarefat.quests.obj.misc;

import me.fatpigsarefat.quests.player.QPlayer;

import java.util.HashMap;

public interface QMenu {

    QPlayer getOwner();
    HashMap<?, ?> getSlotsToMenu();

}
