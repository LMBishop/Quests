package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.ParsedQuestItem;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.util.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class AdminItemsCommandHandler implements CommandHandler {

    private final BukkitQuestsPlugin plugin;

    public AdminItemsCommandHandler(BukkitQuestsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void handle(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(ChatColor.GRAY + "Imported items");
            for (QuestItem questItem : plugin.getQuestItemRegistry().getAllItems()) {
                sender.sendMessage(ChatColor.DARK_GRAY + " * " + ChatColor.RED + questItem.getId() + " (" + questItem.getType() + ")");
            }
            sender.sendMessage(ChatColor.GRAY.toString() + plugin.getQuestItemRegistry().getAllItems().size() + " items imported.");
            sender.sendMessage(ChatColor.DARK_GRAY + "Import a new held item using /q a items import <id>.");
        } else if (args[2].equalsIgnoreCase("import") && sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack held = new ItemStack(player.getItemInHand());
            held.setAmount(1);

            if (held == null || held.getType() == Material.AIR) {
                sender.sendMessage(ChatColor.RED + "Can't import an empty item!");
                return;
            }

            String id = args[3];

            if (!StringUtils.isAlphanumeric(id)) {
                sender.sendMessage(ChatColor.RED + "ID must be alphanumeric!");
                return;
            }

            File file = new File(plugin.getDataFolder() + File.separator + "items" + File.separator + id + ".yml");
            if (file.exists()) {
                sender.sendMessage(ChatColor.YELLOW + "Warning: A file by the name '" + id + ".yml' already exists and will be overwritten!");
            }

            YamlConfiguration item = new YamlConfiguration();
            item.set("type", "raw");
            item.set("item", held);
            try {
                item.save(file);
                plugin.getQuestItemRegistry().registerItem(id, new ParsedQuestItem("raw", id, held));
                sender.sendMessage(ChatColor.GRAY + "Held item saved to 'items/" + id + ".yml'. This can be referenced within tasks by the id '" + id + "'.");
            } catch (IOException e) {
                e.printStackTrace();
                sender.sendMessage(ChatColor.RED + "Couldn't save item. See console for problem.");
            }
        }
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 3) {
            return TabHelper.matchTabComplete(args[2], Collections.singletonList("import"));
        }
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
