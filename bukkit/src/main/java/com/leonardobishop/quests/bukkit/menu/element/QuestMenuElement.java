package com.leonardobishop.quests.bukkit.menu.element;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.config.BukkitQuestsConfig;
import com.leonardobishop.quests.bukkit.menu.CancelQMenu;
import com.leonardobishop.quests.bukkit.menu.ClickResult;
import com.leonardobishop.quests.bukkit.menu.QMenu;
import com.leonardobishop.quests.bukkit.menu.itemstack.QItemStack;
import com.leonardobishop.quests.bukkit.util.FormatUtils;
import com.leonardobishop.quests.bukkit.util.MenuUtils;
import com.leonardobishop.quests.bukkit.util.Messages;
import com.leonardobishop.quests.bukkit.util.chat.Chat;
import com.leonardobishop.quests.common.enums.QuestStartResult;
import com.leonardobishop.quests.common.player.QPlayer;
import com.leonardobishop.quests.common.player.questprogressfile.QuestProgress;
import com.leonardobishop.quests.common.quest.Quest;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
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
    private final QMenu menu;
    private final boolean dummy;

    private final ClickType startClickType;
    private final ClickType trackClickType;
    private final ClickType cancelClickType;

    public QuestMenuElement(BukkitQuestsPlugin plugin, Quest quest, QMenu menu) {
        this(plugin, quest, menu, false);
    }

    public QuestMenuElement(BukkitQuestsPlugin plugin, Quest quest, QMenu menu, boolean dummy) {
        this.plugin = plugin;
        this.config = (BukkitQuestsConfig) plugin.getQuestsConfig();
        this.menu = menu;
        this.owner = menu.getOwner();
        this.quest = quest;
        this.dummy = dummy;

        this.startClickType = MenuUtils.getClickType(config, "options.gui-actions.start-quest", "LEFT");
        this.trackClickType = MenuUtils.getClickType(config, "options.gui-actions.track-quest", "DROP");
        this.cancelClickType = MenuUtils.getClickType(config, "options.gui-actions.cancel-quest", "RIGHT");
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

        Map<String, String> placeholders = new HashMap<>();
        ItemStack display;
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
            placeholders.put("{quest}", Chat.legacyStrip(qItemStack.getName()));
            placeholders.put("{questcolored}", qItemStack.getName());
            placeholders.put("{questid}", quest.getId());
            if (quests.size() > 1 && plugin.getConfig().getBoolean("options.gui-truncate-requirements", true)) {
                placeholders.put("{requirements}", quests.get(0) + Messages.UI_PLACEHOLDERS_TRUNCATED.getMessageLegacyColor().replace("{amount}", String.valueOf(quests.size() - 1)));
            } else {
                placeholders.put("{requirements}", String.join(", ", quests));
            }
            if (plugin.getQItemStackRegistry().hasQuestLockedItemStack(quest)) {
                display = plugin.getQItemStackRegistry().getQuestLockedItemStack(quest);
            } else {
                display = config.getItem("gui.quest-locked-display");
            }
        } else if (status == QuestStartResult.QUEST_ALREADY_COMPLETED) {
            placeholders.put("{quest}", Chat.legacyStrip(qItemStack.getName()));
            placeholders.put("{questcolored}", qItemStack.getName());
            placeholders.put("{questid}", quest.getId());
            if (plugin.getQItemStackRegistry().hasQuestCompletedItemStack(quest)) {
                display = plugin.getQItemStackRegistry().getQuestCompletedItemStack(quest);
            } else {
                display = config.getItem("gui.quest-completed-display");
            }
        } else if (status == QuestStartResult.QUEST_NO_PERMISSION) {
            placeholders.put("{quest}", Chat.legacyStrip(qItemStack.getName()));
            placeholders.put("{questcolored}", qItemStack.getName());
            placeholders.put("{questid}", quest.getId());
            if (plugin.getQItemStackRegistry().hasQuestPermissionItemStack(quest)) {
                display = plugin.getQItemStackRegistry().getQuestPermissionItemStack(quest);
            } else {
                display = config.getItem("gui.quest-permission-display");
            }
        } else if (cooldown > 0) {
            placeholders.put("{time}", FormatUtils.time(TimeUnit.SECONDS.convert(cooldown, TimeUnit.MILLISECONDS)));
            placeholders.put("{quest}", Chat.legacyStrip(qItemStack.getName()));
            placeholders.put("{questcolored}", qItemStack.getName());
            placeholders.put("{questid}", quest.getId());
            if (plugin.getQItemStackRegistry().hasQuestCooldownItemStack(quest)) {
                display = plugin.getQItemStackRegistry().getQuestCooldownItemStack(quest);
            } else {
                display = config.getItem("gui.quest-cooldown-display");
            }
        } else {
            return MenuUtils.applyPlaceholders(plugin, owner.getPlayerUUID(), qItemStack.toItemStack(quest, owner, questProgress));
        }
        return MenuUtils.applyPlaceholders(plugin, owner.getPlayerUUID(), display, placeholders);
    }

    @Override
    public ClickResult handleClick(Player whoClicked, ClickType clickType) {
        if (dummy) {
            return ClickResult.DO_NOTHING;
        }

        boolean close = config.getBoolean("options.gui-close-after-accept", true);
        if (!owner.hasStartedQuest(quest) && clickType == startClickType) {
            if (owner.startQuest(quest) == QuestStartResult.QUEST_SUCCESS) {
                return close ? ClickResult.CLOSE_MENU : ClickResult.REFRESH_PANE;
            }

        } else if (clickType == trackClickType) {
            if (owner.hasStartedQuest(quest)) {
                if (!plugin.getQuestsConfig().getBoolean("options.allow-quest-track")) {
                    return ClickResult.DO_NOTHING;
                }

                String tracked = owner.getPlayerPreferences().getTrackedQuestId();

                if (quest.getId().equals(tracked)) {
                    owner.trackQuest(null);
                } else {
                    owner.trackQuest(quest);
                }
                return close ? ClickResult.CLOSE_MENU : ClickResult.REFRESH_PANE;
            }

        } else if (clickType == cancelClickType) {
            if (owner.hasStartedQuest(quest)) {
                if (!plugin.getQuestsConfig().getBoolean("options.allow-quest-cancel")
                        || plugin.getConfig().getBoolean("options.quest-autostart")
                        || quest.isAutoStartEnabled()
                        || !quest.isCancellable()) {
                    return ClickResult.DO_NOTHING;
                }

                if (plugin.getQuestsConfig().getBoolean("options.gui-confirm-cancel", true)) {
                     CancelQMenu cancelQMenu = new CancelQMenu(plugin, menu, owner, quest);
                     plugin.getMenuController().openMenu(owner.getPlayerUUID(), cancelQMenu);
                } else {
                    if (menu.getOwner().cancelQuest(quest)) {
                        return close ? ClickResult.CLOSE_MENU : ClickResult.REFRESH_PANE;
                    }
                }
            }
        }

        return ClickResult.DO_NOTHING;
    }
}
