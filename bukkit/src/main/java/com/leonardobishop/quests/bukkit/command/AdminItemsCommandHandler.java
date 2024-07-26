package com.leonardobishop.quests.bukkit.command;

import com.leonardobishop.quests.bukkit.BukkitQuestsPlugin;
import com.leonardobishop.quests.bukkit.item.ParsedQuestItem;
import com.leonardobishop.quests.bukkit.item.QuestItem;
import com.leonardobishop.quests.bukkit.util.lang3.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
            sender.sendMessage(ChatColor.DARK_GRAY + "Give a quest item to a player using /q a items give <player> <id> <amount>.");
        } else if (args[2].equalsIgnoreCase("import") && sender instanceof Player) {
            Player player = (Player) sender;
            ItemStack held = plugin.getVersionSpecificHandler().getItemInMainHand(player).clone();
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
        } else if (args.length >= 5 && args[2].equalsIgnoreCase("give")) {
            Player targetPlayer = plugin.getServer().getPlayer(args[3]);

            if (targetPlayer == null) {
                sender.sendMessage(ChatColor.RED + "That player is not online or is invalid!");
                return;
            }

            ItemStack item = plugin.getQuestItemRegistry().getItem(args[4]).getItemStack();

            if (item == null) {
                sender.sendMessage(ChatColor.RED + "Item not found!");
                return;
            }

            int amount = 1;
            // If there's a sixth arg, it should be the amount of items to give
            // Check it's not a value below 0
            if (args.length == 6 && Integer.parseInt(args[5]) > 0) {
                amount = Integer.parseInt(args[5]);
            }
            item.setAmount(amount);

            // if we got this far, all was well.
            // just give the item to the player already ;)
            Map<Integer, ItemStack> itemsToDrop = targetPlayer.getInventory().addItem(item);

            // drop items that could not be stored
            for (ItemStack itemToDrop : itemsToDrop.values()) {
                targetPlayer.getWorld().dropItem(targetPlayer.getLocation(), itemToDrop);
            }
        }
    }


    @Override
    public List<String> tabComplete(CommandSender sender, String[] args) {
        if (args.length == 3) {
            ArrayList<String> suggestions = new ArrayList<>();
            suggestions.add("import");
            suggestions.add("give");
            return TabHelper.matchTabComplete(args[2], suggestions);
        }
        // if we have 4 args and the 3rd one is give,
        // show available playername suggestions
        else if (args.length == 4 && args[2].equalsIgnoreCase("give")) {
            ArrayList<String> onlinePlayerNames = new ArrayList<>();
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                onlinePlayerNames.add(player.getName());
            }
            return TabHelper.matchTabComplete(args[3], onlinePlayerNames);
        }
        // if we have 5 args and the 4th one is give,
        // show available quest item suggestions
        else if (args.length == 5 && args[2].equalsIgnoreCase("give")) {
            ArrayList<String> availableQuestItems = new ArrayList<>();
            for (QuestItem questItem : plugin.getQuestItemRegistry().getAllItems()) {
                availableQuestItems.add(questItem.getId());
            }
            return TabHelper.matchTabComplete(args[4], availableQuestItems);
        }
        return Collections.emptyList();
    }

    @Override
    public @Nullable String getPermission() {
        return "quests.admin";
    }
}
