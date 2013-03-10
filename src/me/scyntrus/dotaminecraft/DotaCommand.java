package me.scyntrus.dotaminecraft;

import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.onarandombox.MultiverseCore.api.MVWorldManager;

public class DotaCommand implements CommandExecutor {
	private final DotaMinecraft plugin;
	
	public DotaCommand(DotaMinecraft plugin){
		this.plugin = plugin;
	}
	
	@Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		if (sender instanceof Player){
			if (!((Player) sender).getWorld().getName().equals(plugin.WorldName)){
				sender.sendMessage("You must be in the Dota world to do this.");
				return true;
			}
			if (split.length == 0){
				return false;
			}
			if (split[0].toLowerCase().equals("join")){
				if (!plugin.playerlist.containsKey(sender.getName())){
					Integer jointeam = 0;
					Player player = (Player) sender;
					player.setGameMode(GameMode.SURVIVAL);
					player.getInventory().clear();
					player.getInventory().setArmorContents(null);
					player.setHealth(20);
					player.setFoodLevel(20);
					if (split.length==1) {
						if (plugin.BlueCount < plugin.RedCount){
							jointeam = 2;
						} else if (plugin.BlueCount > plugin.RedCount){
							jointeam = 1;
						} else {
							Random randomGenerator = new Random();
							if (randomGenerator.nextInt(2)==0){
								jointeam = 2;
							} else {
								jointeam = 1;
							}
						}
					} else {
						if (player.hasPermission("dota.chooseteam")) {
							if (split[1].toLowerCase().equals("blue")) {
								jointeam = 2;
							} else if (split[1].toLowerCase().equals("red")) {
								jointeam = 1;
							} else {
								player.sendMessage("What team is that? Please choose either "+ChatColor.RED+"red"+ChatColor.RESET+" or "+ChatColor.BLUE+"blue.");
								return true;
							}
						} else {
							player.sendMessage("You do not have permission to choose a team.");
							return true;
						}
					}
					if (plugin.playerhasjoined.containsKey(player.getName())){
						player.sendMessage("You have previously joined this round before, so you do not get starting items.");
					} else {
						player.getInventory().addItem(new ItemStack(Material.STONE_SWORD, 1));
						player.getInventory().addItem(new ItemStack(Material.RAW_CHICKEN, 1));
						player.getInventory().addItem(new ItemStack(Material.MAP, 1));
					}
					plugin.playerkills.put(player.getName(),0);
					plugin.playerdeaths.put(player.getName(),0);
					plugin.playercs.put(player.getName(),0);
					if (jointeam == 2){
						plugin.BlueCount++;
						plugin.playerlist.put(player.getName(), 2);
						player.teleport(plugin.BluePoint);
						player.setBedSpawnLocation(plugin.BlueBed);
						plugin.broadcastMessage(player.getName() + " has joined team "+ChatColor.BLUE+"Blue.");
					} else if (jointeam == 1){
						plugin.RedCount++;
						plugin.playerlist.put(player.getName(), 1);
						player.teleport(plugin.RedPoint);
						player.setBedSpawnLocation(plugin.RedBed);
						plugin.broadcastMessage(player.getName() + " has joined team "+ChatColor.RED+"Red.");
					}
					plugin.playerhasjoined.put(player.getName(), true);
				} else {
					sender.sendMessage("You're already on a team!");
				}
			} else if (split[0].toLowerCase().equals("restart")){
				if (sender.hasPermission("dota.restart")){
					if (plugin.GameInProgress && !sender.hasPermission("dota.forcerestart")){
						sender.sendMessage(ChatColor.DARK_RED + "A game is aready in progress. There is no need to restart the game.");
					} else {
						if (plugin.GameInProgress) {
							sender.sendMessage(ChatColor.DARK_RED + "You have restarted the game while in progress. You really shouldn't do that too often.");
						}
						for (Player player : plugin.getServer().getWorld(plugin.WorldName).getPlayers()){
							player.teleport(plugin.getServer().getWorld(plugin.WorldName).getSpawnLocation());
							player.setBedSpawnLocation(plugin.getServer().getWorld(plugin.WorldName).getSpawnLocation());
							player.getInventory().clear();
							player.getInventory().setArmorContents(null);
							player.setHealth(20);
							player.setFoodLevel(20);
						}
						plugin.playerhasjoined.clear();
						plugin.playerlist.clear();
						plugin.playerkills.clear();
						plugin.playerdeaths.clear();
						plugin.playerdeathitems.clear();
						plugin.playerdeatharmor.clear();
						plugin.broadcastMessage("Dota world is being restarted!");
						MVWorldManager MVWM = plugin.MVCorePlugin.getMVWorldManager();
						MVWM.unloadWorld(plugin.WorldName);
						MVWM.loadWorld(plugin.WorldName);
						
						PluginListener.setupWorld(plugin);
					}
				} else {
					sender.sendMessage("You do not have permission to restart the game.");
				}
			} else if (split[0].toLowerCase().equals("score")){
				for (String name : plugin.playerlist.keySet()){
					String message = "";
					if (plugin.playerlist.get(name)==1){
						message = ChatColor.RED.toString();
					} else if (plugin.playerlist.get(name)==2){
						message = ChatColor.BLUE.toString();
					}
					message += name + "    Kills: ";
					message += plugin.playerkills.get(name).toString() + "    Deaths: ";
					message += plugin.playerdeaths.get(name).toString() + "    CS: ";
					message += plugin.playercs.get(name).toString();
					sender.sendMessage(message);
				}
			} else if (split[0].toLowerCase().equals("recall")){
				if (!plugin.RecallEnabled) {
					sender.sendMessage("Recall has been disabled.");
					return true;
				}
				if (!plugin.playerlist.containsKey(sender.getName())){
					sender.sendMessage("You cannot recall if you're not on a team.");
					return true;
				}
				Integer id = plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new RecallScheduler(plugin, sender.getName()), plugin.RecallDelay * 20L);
				plugin.playerRecallID.put(sender.getName(), id);
				plugin.broadcastMessage(sender.getName()+" is recalling.");
				sender.sendMessage("You will recall to base in "+ plugin.RecallDelay +" seconds.");
				return true;
			} else {
				return false;
			}
		}
		return true;
    }
}
