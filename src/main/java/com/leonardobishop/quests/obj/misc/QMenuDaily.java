package com.leonardobishop.quests.obj.misc;

import com.leonardobishop.quests.obj.Options;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.quests.Quest;
import org.bukkit.Bukkit;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QMenuDaily implements QMenu {

    private final HashMap<Integer, String> slotsToQuestIds = new HashMap<>();
    private int backButtonLocation = -1;
    private boolean backButtonEnabled = true;
    private final QMenuCategory superMenu;
    private String categoryName;
    private final int pageSize = 45;
    private final QPlayer owner;

    public QMenuDaily(QPlayer owner, QMenuCategory superMenu) {
        this.owner = owner;
        this.superMenu = superMenu;
    }

    public void populate(List<Quest> quests) {
        int slot = 11;
        for (Quest quest : quests) {
            slotsToQuestIds.put(slot, quest.getId());
            slot++;
            if (slot == 16) {
                break;
            }
        }
    }

    @Override
    public HashMap<Integer, String> getSlotsToMenu() {
        return slotsToQuestIds;
    }

    @Override
    public QPlayer getOwner() {
        return owner;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public Inventory toInventory(int page) {
        int pageMin = pageSize * (page - 1);
        int pageMax = pageSize * page;
        String title = Options.GUITITLE_DAILY_QUESTS.toString();

 //       Inventory inventory = Bukkit.createInventory(null, 27, title);

        //TODO daily quests

//        int invSlot = 11;
//        for (int pointer = pageMin; pointer < pageMax; pointer++) {
//            if (slotsToQuestIds.containsKey(pointer)) {
//                Quest quest = Quests.getQuestManager().getQuestById(slotsToQuestIds.get(pointer));
//                QuestProgress questProgress = owner.getQuestProgressFile().getQuestProgress(quest);
//                long cooldown = owner.getQuestProgressFile().getCooldownFor(quest);
//                if (!owner.getQuestProgressFile().hasMetRequirements(quest)) {
//                    List<String> quests = new ArrayList<>();
//                    for (String requirement : quest.getRequirements()) {
//                        quests.add(Quests.getQuestManager().getQuestById(requirement).getDisplayNameStripped());
//                    }
//                    Map<String, String> placeholders = new HashMap<>();
//                    placeholders.put("{quest}", quest.getDisplayNameStripped());
//                    placeholders.put("{requirements}", String.join(", ", quests));
//                    ItemStack is = replaceItemStack(Items.QUEST_LOCKED.getItem(), placeholders);
//                    inventory.setItem(invSlot, is);
//                } else if (!quest.isRepeatable() && questProgress.isCompletedBefore()) {
//                    Map<String, String> placeholders = new HashMap<>();
//                    placeholders.put("{quest}", quest.getDisplayNameStripped());
//                    ItemStack is = replaceItemStack(Items.QUEST_COMPLETED.getItem(), placeholders);
//                    inventory.setItem(invSlot, is);
//                } else if (cooldown > 0) {
//                    Map<String, String> placeholders = new HashMap<>();
//                    placeholders.put("{time}", Quests.convertToFormat(TimeUnit.MINUTES.convert(cooldown, TimeUnit.MILLISECONDS)));
//                    placeholders.put("{quest}", quest.getDisplayNameStripped());
//                    ItemStack is = replaceItemStack(Items.QUEST_COOLDOWN.getItem(), placeholders);
//                    inventory.setItem(invSlot, is);
//                } else {
//                    inventory.setItem(invSlot, Quests.getQuestManager().getQuestById(quest.getId()).getDisplayItem().toItemStack(questProgress));
//                }
//            }
//            invSlot++;
//        }
//      return inventory;
        return Bukkit.createInventory(null, 27, title);
    }

    //Implement too
    public QMenuCategory getSuperMenu() {
        return this.superMenu;
    }

    public int getPageSize() {
        return this.pageSize;
    }
}
