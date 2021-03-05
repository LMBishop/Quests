package com.leonardobishop.quests.menu.object;

import com.leonardobishop.quests.Quests;
import com.leonardobishop.quests.player.QPlayer;
import com.leonardobishop.quests.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.quests.Quest;
import com.leonardobishop.quests.util.Items;
import com.leonardobishop.quests.util.Options;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QuestMenuElement extends MenuElement {

    private final Quests plugin;
    private final QPlayer owner;
    private final String questId;

    public QuestMenuElement(Quests plugin, QPlayer owner, String questId) {
        this.plugin = plugin;
        this.owner = owner;
        this.questId = questId;
    }

    public QPlayer getOwner() {
        return owner;
    }

    public String getQuestId() {
        return questId;
    }

    @Override
    public ItemStack asItemStack() {
        Quest quest = plugin.getQuestManager().getQuestById(questId);
        QuestProgress questProgress = owner.getQuestProgressFile().getQuestProgress(quest);
        long cooldown = owner.getQuestProgressFile().getCooldownFor(quest);
        if (!owner.getQuestProgressFile().hasMetRequirements(quest)) {
            List<String> quests = new ArrayList<>();
            for (String requirement : quest.getRequirements()) {
                quests.add(Quests.get().getQuestManager().getQuestById(requirement).getDisplayNameStripped());
            }
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{quest}", quest.getDisplayNameStripped());
            placeholders.put("{requirements}", String.join(", ", quests));
            ItemStack is = replaceItemStack(Items.QUEST_LOCKED.getItem(), placeholders);
            return is;
        } else if (!quest.isRepeatable() && questProgress.isCompletedBefore()) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{quest}", quest.getDisplayNameStripped());
            ItemStack is = replaceItemStack(Items.QUEST_COMPLETED.getItem(), placeholders);
            return is;
        } else if (quest.isPermissionRequired() && !Bukkit.getPlayer(owner.getUuid()).hasPermission("quests.quest." + quest.getId())) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{quest}", quest.getDisplayNameStripped());
            ItemStack is = replaceItemStack(Items.QUEST_PERMISSION.getItem(), placeholders);
            return is;
        } else if (cooldown > 0) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{time}", Quests.get().convertToFormat(TimeUnit.SECONDS.convert(cooldown, TimeUnit.MILLISECONDS)));
            placeholders.put("{quest}", quest.getDisplayNameStripped());
            ItemStack is = replaceItemStack(Items.QUEST_COOLDOWN.getItem(), placeholders);
            return is;
        } else {
            return replaceItemStack(quest.getDisplayItem().toItemStack(quest, owner.getQuestProgressFile(), questProgress));
        }
    }

    private ItemStack replaceItemStack(ItemStack is) {
        return replaceItemStack(is, Collections.emptyMap());
    }

    private ItemStack replaceItemStack(ItemStack is, Map<String, String> placeholders) {
        ItemStack newItemStack = is.clone();
        List<String> lore = newItemStack.getItemMeta().getLore();
        List<String> newLore = new ArrayList<>();
        ItemMeta ism = newItemStack.getItemMeta();
        Player player = Bukkit.getPlayer(owner.getUuid());
        if (lore != null) {
            for (String s : lore) {
                for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                    s = s.replace(entry.getKey(), entry.getValue());
                    if (plugin.getPlaceholderAPIHook() != null && Options.GUI_USE_PLACEHOLDERAPI.getBooleanValue()) {
                        s = plugin.getPlaceholderAPIHook().replacePlaceholders(player, s);
                    }
                }
                newLore.add(s);
            }
        }
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            ism.setDisplayName(ism.getDisplayName().replace(entry.getKey(), entry.getValue()));
            if (plugin.getPlaceholderAPIHook() != null && Options.GUI_USE_PLACEHOLDERAPI.getBooleanValue()) {
                ism.setDisplayName(plugin.getPlaceholderAPIHook().replacePlaceholders(player, ism.getDisplayName()));
            }
        }
        ism.setLore(newLore);
        newItemStack.setItemMeta(ism);
        return newItemStack;
    }
}
