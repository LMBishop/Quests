package me.fatpigsarefat.quests.obj.misc;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.obj.Items;
import me.fatpigsarefat.quests.obj.Options;
import me.fatpigsarefat.quests.player.QPlayer;
import me.fatpigsarefat.quests.player.questprogressfile.QuestProgress;
import me.fatpigsarefat.quests.quests.Quest;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QMenuQuest implements QMenu {

    private HashMap<Integer, String> slotsToQuestIds = new HashMap<>();
    private int backButtonLocation = -1;
    private boolean backButtonEnabled = true;
    private QMenuCategory superMenu;
    private String categoryName;
    private final int pageSize = 45;
    private QPlayer owner;

    public QMenuQuest(QPlayer owner, String categoryName, QMenuCategory superMenu) {
        this.owner = owner;
        this.categoryName = categoryName;
        this.superMenu = superMenu;
    }

    public void populate(List<Quest> quests) {
        int slot = 0;
        for (Quest quest : quests) {
            slotsToQuestIds.put(slot, quest.getId());
            slot++;
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
        String title = "Quests";

        ItemStack pageIs = new ItemStack(Material.DIRT);
        ItemStack back = Items.BACK_BUTTON.getItem();

        Inventory inventory = Bukkit.createInventory(null, 54, title); //TODO make configurable title

        int invSlot = 0;
        for (int pointer = pageMin; pointer < pageMax; pointer++) {
            if (slotsToQuestIds.containsKey(pointer)) {
                Quest quest = Quests.getQuestManager().getQuestById(slotsToQuestIds.get(pointer));
                QuestProgress questProgress = owner.getQuestProgressFile().getQuestProgress(quest);
                long cooldown = owner.getQuestProgressFile().getCooldownFor(quest);
                if (!owner.getQuestProgressFile().hasMetRequirements(quest)) {
                    List<String> quests = new ArrayList<>();
                    for (String requirement : quest.getRequirements()) {
                        quests.add(Quests.getQuestManager().getQuestById(requirement).getDisplayNameStripped());
                    }
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("{quest}", quest.getDisplayNameStripped());
                    placeholders.put("{requirements}", String.join(", ", quests));
                    ItemStack is = replaceItemStack(Items.QUEST_LOCKED.getItem(), placeholders);
                    inventory.setItem(invSlot, is);
                } else if (!quest.isRepeatable() && questProgress.isCompletedBefore()) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("{quest}", quest.getDisplayNameStripped());
                    ItemStack is = replaceItemStack(Items.QUEST_COMPLETED.getItem(), placeholders);
                    inventory.setItem(invSlot, is);
                } else if (cooldown > 0) {
                    Map<String, String> placeholders = new HashMap<>();
                    placeholders.put("{time}", Quests.convertToFormat(TimeUnit.MINUTES.convert(cooldown, TimeUnit.MILLISECONDS)));
                    placeholders.put("{quest}", quest.getDisplayNameStripped());
                    ItemStack is = replaceItemStack(Items.QUEST_COOLDOWN.getItem(), placeholders);
                    inventory.setItem(invSlot, is);
                } else {
                    inventory.setItem(invSlot, Quests.getQuestManager().getQuestById(quest.getId()).getDisplayItem().toItemStack(questProgress));
                }
            }
            invSlot++;
        }

        inventory.setItem(49, pageIs);

        if (Options.CATEGORIES_ENABLED.getBooleanValue() && backButtonEnabled) {
            inventory.setItem(45, back);
            backButtonLocation = 45;
        }

        if (Options.TRIM_GUI_SIZE.getBooleanValue() && page == 1) {
            int slotsUsed = 0;
            for (int pointer = 0; pointer < pageMax; pointer++) {
                if (inventory.getItem(pointer) != null) {
                    slotsUsed++;
                }
            }

            int inventorySize = (slotsUsed >= 54) ? 54 : slotsUsed + (9 - slotsUsed % 9) * Math.min(1, slotsUsed % 9);
            inventorySize = inventorySize <= 0 ? 9 : inventorySize;
            if (inventorySize == 54) {
                return inventory;
            } else if (Options.CATEGORIES_ENABLED.getBooleanValue() && backButtonEnabled) {
                inventorySize += 9;
            }

            Inventory trimmedInventory = Bukkit.createInventory(null, inventorySize, title);

            for (int slot = 0; slot < trimmedInventory.getSize(); slot++) {
                if (slot >= (trimmedInventory.getSize() - 9) && backButtonEnabled){
                    if (Options.CATEGORIES_ENABLED.getBooleanValue()) {
                        trimmedInventory.setItem(slot, back);
                        backButtonLocation = slot;
                    }
                    break;
                }
                trimmedInventory.setItem(slot, inventory.getItem(slot));
            }
            return trimmedInventory;
        } else {
            return inventory;
        }

        //TODO add page controls
    }

    public boolean isBackButtonEnabled() {
        return backButtonEnabled;
    }

    public void setBackButtonEnabled(boolean backButtonEnabled) {
        this.backButtonEnabled = backButtonEnabled;
    }

    public int getBackButtonLocation() {
        return backButtonLocation;
    }

    public QMenuCategory getSuperMenu() {
        return superMenu;
    }

    public ItemStack replaceItemStack(ItemStack is, Map<String, String> placeholders) {
        ItemStack newItemStack = is.clone();
        List<String> lore = newItemStack.getItemMeta().getLore();
        List<String> newLore = new ArrayList<>();
        for (String s : lore) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                s = s.replace(entry.getKey(), entry.getValue());
            }
            newLore.add(s);
        }
        ItemMeta ism = newItemStack.getItemMeta();
        ism.setLore(newLore);
        newItemStack.setItemMeta(ism);
        return newItemStack;
    }
}
