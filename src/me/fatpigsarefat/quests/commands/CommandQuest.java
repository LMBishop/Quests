package me.fatpigsarefat.quests.commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import me.fatpigsarefat.quests.Quests;
import me.fatpigsarefat.quests.utils.Messages;
import me.fatpigsarefat.quests.utils.Quest;
import me.fatpigsarefat.quests.utils.SelectorType;
import net.md_5.bungee.api.ChatColor;

public class CommandQuest implements CommandExecutor {

	private ArrayList<UUID> syncTime = new ArrayList<UUID>();

	@SuppressWarnings("unused")
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

		if (cmd.getLabel().equalsIgnoreCase("quest") && sender instanceof Player) {
			final Player player = (Player) sender;

			if (!player.hasPermission("quest.command")
					&& Quests.getInstance().getConfig().getBoolean("requires-permission")) {
				player.sendMessage(ChatColor.RED + "No permission.");
				return true;
			}

			if (player.hasPermission("quest.admin") && args.length >= 1 && args[0].equals("reload")) {
				Quests.getInstance().reloadConfig();
				Quests.getInstance().reloadQuests();
				player.sendMessage(ChatColor.GREEN + "Configuration reloaded.");
				return true;
			}

			if (player.hasPermission("quest.synctime") && args.length >= 1 && args[0].equalsIgnoreCase("synctime")) {
				if (syncTime.contains(player.getUniqueId())) {
					syncTime.remove(player.getUniqueId());
					Quests.getInstance().getQuestData().setTimePlayed(player.getUniqueId(),
							player.getStatistic(Statistic.PLAY_ONE_TICK) / 20 / 60);
					player.sendMessage(Messages.SYNC_SUCCESS.getMessage());
				} else {
					player.sendMessage(Messages.SYNC_PLAYTIME.getMessage().replace("%playtimebefore%", Quests.getInstance().getQuestData().timeConvert(
									Quests.getInstance().getQuestData().getTimePlayed(player.getUniqueId()))).replace("%playtimeafter%", Quests.getInstance().getQuestData()
											.timeConvert(player.getStatistic(Statistic.PLAY_ONE_TICK) / 20 / 60)).replace("%command%", "/" + cmd.getName()+ " " + args[0]));
					syncTime.add(player.getUniqueId());

					new BukkitRunnable() {
						@Override
						public void run() {
							if (syncTime.contains(player.getUniqueId())) {
								syncTime.remove(player.getUniqueId());
								player.sendMessage(Messages.SYNC_TIMEOUT.getMessage());
							}
						}
					}.runTaskLater(Quests.getInstance(), 300L);
				}
				return true;
			}

			if (player.hasPermission("quest.getDebug()") && args.length >= 1 && args[0].equals("getDebug()")) {
				player.sendMessage(ChatColor.GREEN + "Debug mode removed for now.");
				return true;
			}

			if (Quests.getInstance().getQuestManager().getBlacklistedWorlds().contains(player.getWorld().getName())) {
				player.sendMessage(ChatColor.RED + "Quests cannot be completed or used here.");
				return true;
			}

			File d = new File(Quests.getInstance().getDataFolder() + File.separator + "data.yml");
			YamlConfiguration data = YamlConfiguration.loadConfiguration((File) d);
			File questlayout = new File(Quests.getInstance().getDataFolder() + File.separator + "questlayout.yml");
			YamlConfiguration questlayoutdata = null;
			if (!questlayout.exists()) {
				try {
					player.sendMessage(ChatColor.GREEN + "Creating new file questlayout.yml...");
					questlayout.createNewFile();
				} catch (IOException e) {
					player.sendMessage(ChatColor.RED + "Failed. See console for error details.");
					e.printStackTrace();
					return true;
				}
			}
			if (questlayout.exists()) {
				questlayoutdata = YamlConfiguration.loadConfiguration((File) questlayout);
				if (!questlayoutdata.contains("active")) {
					questlayoutdata.set("active", false);
					try {
						questlayoutdata.save(questlayout);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			if (Quests.getInstance().getQuestManager().getSelectorMode() == SelectorType.ALL) {
				Inventory inv = Bukkit.createInventory(null, Quests.getInstance().getConfig().getInt("gui.slots"),
						ChatColor.translateAlternateColorCodes('&',
								Quests.getInstance().getConfig().getString("gui.title")));
				if (questlayout.exists() && questlayoutdata.getBoolean("active")) {
					if (questlayoutdata.contains("slot")) {
						for (String s : questlayoutdata.getConfigurationSection("slot").getKeys(false)) {
							if (s.startsWith("CUSTOMITEMSTACK")) {
								int slotId = questlayoutdata.getInt("slot." + s);
								ItemStack is = questlayoutdata.getItemStack("itemstack." + slotId);
								inv.setItem(slotId, is);
							}
						}
					}
				}
				int slot = 0;
				for (Quest quest : Quests.getInstance().getQuestManager().getQuests()) {
					String questId = quest.getNameId();
					if (questId.contains("CUSTOMITEMSTACK")) {
						player.sendMessage(ChatColor.RED + "There is an error with the configuration.");
						player.sendMessage(ChatColor.WHITE + "Details:");
						player.sendMessage("Problematic quest: " + questId);
						player.sendMessage("Error details: reserved word");
						player.sendMessage("Additional information: n/a");
						player.sendMessage(ChatColor.BOLD
								+ "Problem: CUSTOMITEMSTACK is a reserved word for the Quest GUI designer and therefore cannot be set as a quest identifier.");
						return true;
					}
					int customSlot = 0;
					if (questlayoutdata.contains("slot." + quest.getNameId()) && questlayoutdata.getBoolean("active")) {
						customSlot = questlayoutdata.getInt("slot." + quest.getNameId());
					} else {
						customSlot = slot;
					}
					if (!Quests.getInstance().getQuestData().hasMetRequirements(quest, player.getUniqueId())) {
						inv.setItem(customSlot, Quests.getInstance().getQuestData().getLockedItemStack());
						slot++;
						continue;
					} else if (!quest.isRedoable() && Quests.getInstance().getQuestData()
							.hasCompletedQuestBefore(quest.getNameId(), player.getUniqueId())) {
						inv.setItem(customSlot, Quests.getInstance().getQuestData().getCompleteItemStack());
						slot++;
						continue;
					} else if (quest.isCoodlownEnabled()
							&& Quests.getInstance().getQuestData().isOnCooldown(quest, player.getUniqueId())) {
						inv.setItem(customSlot,
								Quests.getInstance().getQuestData().getCooldownItemStack(quest, player.getUniqueId()));
						slot++;
						continue;
					} else {
						inv.setItem(customSlot, Quests.getInstance().getQuestData().getDisplayItemReplaced(quest,
								player.getUniqueId()));
						slot++;
					}
				}
				player.openInventory(inv);
				player.updateInventory();
				return true;
			} else if (Quests.getInstance().getQuestManager().getSelectorMode() == SelectorType.RANDOM) {
				Inventory inv = Bukkit.createInventory(null, Quests.getInstance().getConfig().getInt("gui.mini-slots"),
						ChatColor.translateAlternateColorCodes('&',
								Quests.getInstance().getConfig().getString("gui.title")));
				ItemStack is = new ItemStack(Material.STAINED_GLASS_PANE, 1, (byte) 15);
				ItemMeta ism = is.getItemMeta();
				ism.setDisplayName(" ");
				is.setItemMeta(ism);
				
				for (int i = 0; i < Quests.getInstance().getConfig().getInt("gui.mini-slots"); i++) {
					inv.setItem(i, is);
				}
				
				int i = 11;
				for (String s : Quests.getInstance().getQuestData().getRandomQuests(player.getUniqueId())) {
					if (Quests.getInstance().getQuestManager().getQuestById(s) == null) {
						i++;
						continue;
					}
					if (Quests.getInstance().getQuestData()
							.hasCompletedQuestBefore(Quests.getInstance().getQuestManager().getQuestById(s).getNameId(), player.getUniqueId())) {
						inv.setItem(i, Quests.getInstance().getQuestData().getCompleteItemStack());
						i++;
						continue;
					}
					Quest quest = Quests.getInstance().getQuestManager().getQuestById(s);
					inv.setItem(i, Quests.getInstance().getQuestData().getDisplayItemReplaced(quest, player.getUniqueId()));
					i++;
				}
				player.openInventory(inv);
				player.updateInventory();
				return true;
			}
		}

		if (cmd.getLabel().equalsIgnoreCase("quest") && !(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Sorry, players only!");
			return true;
		}

		return false;
	}

}
