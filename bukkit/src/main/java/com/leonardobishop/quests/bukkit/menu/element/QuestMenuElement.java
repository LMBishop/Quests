package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.itemstack.QItemStack;
import com.leonardobishop.quests.bukkit.util.Format;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class QuestMenuElement extends MenuElement {

    private final BukkitQuestsPlugin plugin;
    private final BukkitQuestsConfig config;
    private final QPlayer owner;
    private final Quest quest;

    public QuestMenuElement(BukkitQuestsPlugin plugin, QPlayer owner, Quest quest) {
        this.plugin = plugin;
        this.config = (BukkitQuestsConfig) plugin.getQuestsConfig();
        this.owner = owner;
        this.quest = quest;
    }

    public QPlayer getOwner() {
        return owner;
    }

    public String getQuestId() {
        return quest.getId();
    }

    public Quest getQuest() {
        return quest;
    }

    @Override
    public ItemStack asItemStack() {
        QuestProgress questProgress = owner.getQuestProgressFile().getQuestProgress(quest);
        QuestStartResult status = owner.canStartQuest(quest);
        long cooldown = owner.getQuestProgressFile().getCooldownFor(quest);
        QItemStack qItemStack = plugin.getQItemStackRegistry().getQuestItemStack(quest);

        if (status == QuestStartResult.QUEST_LOCKED) {
            List<String> quests = new ArrayList<>();
            for (String requirement : quest.getRequirements()) {
                Quest requirementQuest = plugin.getQuestManager().getQuestById(requirement);
                if (requirementQuest == null) continue;
                if (!owner.getQuestProgressFile().hasQuestProgress(requirementQuest) ||
                        !owner.getQuestProgressFile().getQuestProgress(requirementQuest).isCompletedBefore()) {
                    quests.add(Chat.legacyStrip(plugin.getQItemStackRegistry().getQuestItemStack(requirementQuest).getName()));
                }
            }
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{quest}", Chat.legacyStrip(qItemStack.getName()));
            placeholders.put("{questid}", quest.getId());
            if (quests.size() > 1 && plugin.getConfig().getBoolean("options.gui-truncate-requirements", true)) {
                placeholders.put("{requirements}", quests.get(0) + Messages.UI_PLACEHOLDERS_TRUNCATED.getMessageLegacyColor().replace("{amount}", String.valueOf(quests.size() - 1)));
            } else {
                placeholders.put("{requirements}", String.join(", ", quests));
            }
            ItemStack display;
            if (plugin.getQItemStackRegistry().hasQuestLockedItemStack(quest)) {
                display = plugin.getQItemStackRegistry().getQuestLockedItemStack(quest);
            } else {
                display = config.getItem("gui.quest-locked-display");
            }
            return MenuUtils.applyPlaceholders(plugin, owner.getPlayerUUID(), display, placeholders);
        } else if (status == QuestStartResult.QUEST_ALREADY_COMPLETED) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{quest}", Chat.legacyStrip(qItemStack.getName()));
            placeholders.put("{questid}", quest.getId());
            ItemStack display;
            if (plugin.getQItemStackRegistry().hasQuestCompletedItemStack(quest)) {
                display = plugin.getQItemStackRegistry().getQuestCompletedItemStack(quest);
            } else {
                display = config.getItem("gui.quest-completed-display");
            }
            return MenuUtils.applyPlaceholders(plugin, owner.getPlayerUUID(), display, placeholders);
        } else if (status == QuestStartResult.QUEST_NO_PERMISSION) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{quest}", Chat.legacyStrip(qItemStack.getName()));
            placeholders.put("{questid}", quest.getId());
            ItemStack display;
            if (plugin.getQItemStackRegistry().hasQuestPermissionItemStack(quest)) {
                display = plugin.getQItemStackRegistry().getQuestPermissionItemStack(quest);
            } else {
                display = config.getItem("gui.quest-permission-display");
            }
            return MenuUtils.applyPlaceholders(plugin, owner.getPlayerUUID(), display, placeholders);
        } else if (cooldown > 0) {
            Map<String, String> placeholders = new HashMap<>();
            placeholders.put("{time}", Format.formatTime(TimeUnit.SECONDS.convert(cooldown, TimeUnit.MILLISECONDS)));
            placeholders.put("{quest}", Chat.legacyStrip(qItemStack.getName()));
            placeholders.put("{questid}", quest.getId());
            ItemStack display;
            if (plugin.getQItemStackRegistry().hasQuestCooldownItemStack(quest)) {
                display = plugin.getQItemStackRegistry().getQuestCooldownItemStack(quest);
            } else {
                display = config.getItem("gui.quest-cooldown-display");
            }
            return MenuUtils.applyPlaceholders(plugin, owner.getPlayerUUID(), display, placeholders);
        } else {
            return MenuUtils.applyPlaceholders(plugin, owner.getPlayerUUID(), qItemStack.toItemStack(quest, owner, questProgress));
        }
    }

}
