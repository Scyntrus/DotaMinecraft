package com.gmail.scyntrus.dotaminecraft;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_5_R1.entity.CraftEntity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class EntityListener implements Listener {
	DotaMinecraft plugin;
	
	public EntityListener(DotaMinecraft plugin){
		this.plugin = plugin;
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		plugin.playerlist.remove(player.getName());
		plugin.playerkills.remove(player.getName());
		plugin.playerdeaths.remove(player.getName());
		if (!player.getWorld().getName().equals(plugin.WorldName)){
			return;
		}
		player.teleport(plugin.getServer().getWorld(plugin.WorldName).getSpawnLocation());
		player.setBedSpawnLocation(plugin.getServer().getWorld(plugin.WorldName).getSpawnLocation());
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setHealth(20);
		player.setFoodLevel(20);
		if (plugin.playerlist.containsKey(player.getName())){
			if (plugin.playerlist.get(player.getName())==1){
				plugin.RedCount--;
			} else if (plugin.playerlist.get(player.getName())==2){
				plugin.BlueCount--;
			}
		}
    }
	
	@EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
		if (!event.getPlayer().getWorld().getName().equals(plugin.WorldName)){
			return;
		}
		Location loc = event.getPlayer().getLocation();
		Integer x = loc.getBlockX();
		Integer z = loc.getBlockZ();
		String name = event.getPlayer().getName();
		event.getPlayer().setFoodLevel(20);
		if (plugin.playerlist.containsKey(name)){
			if (plugin.playerlist.get(name)==1){
				if (plugin.BlueSpawn.contains(x,z)){
					event.getPlayer().setHealth(0);
				}
			} else if (plugin.playerlist.get(name)==2){
				if (plugin.RedSpawn.contains(x,z)){
					event.getPlayer().setHealth(0);
				}
			}
		} else {
			if (!plugin.WorldSpawn.contains(x,z)){
				event.getPlayer().teleport(plugin.getServer().getWorld(plugin.WorldName).getSpawnLocation());
			}
		}
		if (plugin.playerRecallID.containsKey(name) && event.getFrom().distance(event.getTo())>0){
			if (event.getFrom().distance(event.getTo())>0){
				plugin.getServer().getScheduler().cancelTask(plugin.playerRecallID.get(name));
				plugin.playerRecallID.remove(name);
				event.getPlayer().sendMessage("You have moved, so your recall has been canceled.");
			}
		}
    }
	
	@EventHandler
	public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
		if (event.getDamager().getWorld().getName().equals(plugin.WorldName) && event.getDamager() instanceof Player && event.getEntity() instanceof Player) {
			String p1 = ((Player) event.getEntity()).getName();
			String p2 = ((Player) event.getDamager()).getName();
			if (plugin.playerlist.containsKey(p1) && plugin.playerlist.containsKey(p2)){
				if (plugin.playerlist.get(p1) == plugin.playerlist.get(p2)){
					event.setCancelled(true);
				}
			} else {
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		plugin.playerlist.remove(player.getName());
		plugin.playerkills.remove(player.getName());
		plugin.playerdeaths.remove(player.getName());
		if (!player.getWorld().getName().equals(plugin.WorldName)){
			return;
		}
		player.teleport(plugin.getServer().getWorld(plugin.WorldName).getSpawnLocation());
		player.setBedSpawnLocation(plugin.getServer().getWorld(plugin.WorldName).getSpawnLocation());
		player.getInventory().clear();
		player.getInventory().setArmorContents(null);
		player.setHealth(20);
		player.setFoodLevel(20);
		if (plugin.playerlist.containsKey(player.getName())){
			if (plugin.playerlist.get(player.getName())==1){
				plugin.RedCount--;
			} else if (plugin.playerlist.get(player.getName())==2){
				plugin.BlueCount--;
			}
		}
    }
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent event) {
		if (!event.getEntity().getWorld().getName().equals(plugin.WorldName)){
			return;
		}
		if (plugin.PlayersKeepItems) {
			DamageCause cause = event.getEntity().getLastDamageCause().getCause();
			if (cause==DamageCause.DROWNING) {
				event.getEntity().sendMessage("You died in the pirate cave, so you lost your items!");
			} else if (cause==DamageCause.LAVA){
				event.getEntity().sendMessage("You died in the lava cave, so you lost your items!");
			} else {
				plugin.playerdeathitems.put(event.getEntity().getName(),event.getEntity().getInventory().getContents());
				plugin.playerdeatharmor.put(event.getEntity().getName(),event.getEntity().getInventory().getArmorContents());
//				event.getEntity().getInventory().clear();
//				for (ItemStack e : event.getDrops()) {
//					System.out.println(e.getType().name());
//				}
//				System.out.println(event.getDrops().size());
				event.getDrops().clear();
			}
		}
		event.setKeepLevel(plugin.PlayersKeepLevel);
		String name = event.getEntity().getName();
		if (plugin.playerdeaths.containsKey(name)){
			plugin.playerdeaths.put(name,plugin.playerdeaths.get(name)+1);
		}
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		if (!event.getPlayer().getWorld().getName().equals(plugin.WorldName)){
			return;
		}
		String name = event.getPlayer().getName();
		if (plugin.playerdeathitems.containsKey(name)){
			PlayerInventory inven = event.getPlayer().getInventory();
			Integer count = 0;
			for (ItemStack istack : plugin.playerdeathitems.get(name)){
				if (istack instanceof ItemStack){
					inven.setItem(count, istack);
				}
				count++;
			}
			//inven.setContents(plugin.playerdeathitems.get(name));
			inven.setArmorContents(plugin.playerdeatharmor.get(name));
			plugin.playerdeathitems.remove(name);
			plugin.playerdeatharmor.remove(name);
		}
	}
	
	@EventHandler
	public void onEntityDeath(EntityDeathEvent event){
		if (!event.getEntity().getWorld().getName().equals(plugin.WorldName)){
			return;
		}
		if (event.getEntityType() != EntityType.PLAYER) {
			((CraftEntity) event.getEntity()).getHandle().setEquipment(0, null);
			((CraftEntity) event.getEntity()).getHandle().setEquipment(1, null);
			((CraftEntity) event.getEntity()).getHandle().setEquipment(2, null);
			((CraftEntity) event.getEntity()).getHandle().setEquipment(3, null);
			((CraftEntity) event.getEntity()).getHandle().setEquipment(4, null);
			if (event.getEntity().getKiller() instanceof Player){
				Player player = event.getEntity().getKiller();
				for (ItemStack istack : event.getDrops()){
					player.getInventory().addItem(istack);
				}
				plugin.playercs.put(player.getName(), plugin.playercs.get(player.getName())+1);
			}
			event.getDrops().clear();
		} else {
			if (event.getEntity().getKiller() instanceof Player){
				String name = event.getEntity().getKiller().getName();
				if (plugin.playerkills.containsKey(name)){
					plugin.playerkills.put(name,plugin.playerkills.get(name)+1);
				}
			}
			
		}
	}
	
	@EventHandler
	public void onPlayerPickupItem(PlayerPickupItemEvent event) {
		if (!event.getPlayer().getWorld().getName().equals(plugin.WorldName)){
			return;
		}
		String name = event.getPlayer().getName();
		if (plugin.playerlist.containsKey(name)){
			if (plugin.playerlist.get(name)==1){
				if (event.getItem().getLocation().distance(plugin.BluePoint) < 70.0){
					event.setCancelled(true);
				}
			} else if (plugin.playerlist.get(name)==2){
				if (event.getItem().getLocation().distance(plugin.RedPoint) < 70.0){
					event.setCancelled(true);
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent event) {
		if (!event.getPlayer().getWorld().getName().equals(plugin.WorldName)){
			return;
		}
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK && event.hasBlock()){
			if (event.getClickedBlock().getType() == Material.DISPENSER){
				event.setCancelled(true);
			} else if (event.getClickedBlock().getType() == Material.LEVER){
				if (plugin.RedCount == 0 || plugin.BlueCount == 0){
					event.setCancelled(true);
					event.getPlayer().sendMessage("Waiting for opposing players to join.");
				}
			} else if (event.getClickedBlock().getType() == Material.CHEST){
				String name = event.getPlayer().getName();
				if (plugin.playerlist.containsKey(name)){
					if (plugin.playerlist.get(name)==1){
						if (plugin.playerlist.containsKey(name)){
							if (plugin.playerlist.get(name)==1){
								if (event.getClickedBlock().getLocation().distance(plugin.BluePoint) < 70.0){
									event.setCancelled(true);
								}
							} else if (plugin.playerlist.get(name)==2){
								if (event.getClickedBlock().getLocation().distance(plugin.BluePoint) < 70.0){
									event.setCancelled(true);
								}
							}
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onEntityExplode(EntityExplodeEvent event){
		if (plugin.GameInProgress && event.getLocation().getWorld().getName().equals(plugin.WorldName)){
			Location exloc = event.getLocation();
			for (Location tuloc : plugin.turretlocations.keySet()){
				if (tuloc.distance(exloc) < 10){
					String turretname = plugin.turretlocations.get(tuloc);
					if (plugin.turretstates.get(turretname) == false){
						plugin.broadcastMessage(turretname + " has been destroyed.");
						plugin.turretstates.put(turretname, true);
						if (turretname.equals("Red Nexus")){
							plugin.broadcastMessage(ChatColor.BLUE+"Blue Team has won!");
							plugin.GameInProgress = false;
						} else if (turretname.equals("Blue Nexus")){
							plugin.broadcastMessage(ChatColor.RED+"Red Team has won!");
							plugin.GameInProgress = false;
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if (!event.getTo().getWorld().getName().equals(plugin.WorldName)){
			String name = event.getPlayer().getName();
			if (plugin.playerlist.containsKey(name) && event.getFrom().getWorld().getName().equals(plugin.WorldName)) {
				plugin.playerlist.remove(name);
				plugin.playerdeaths.remove(name);
				plugin.playerkills.remove(name);
				plugin.playerdeathitems.remove(name);
				plugin.playerdeatharmor.remove(name);
				event.getPlayer().getInventory().clear();
				event.getPlayer().getInventory().setArmorContents(null);
				event.getPlayer().setBedSpawnLocation(event.getTo().getWorld().getSpawnLocation(),true);
				event.getPlayer().sendMessage(ChatColor.DARK_RED + "You have left the game.");
			}
		}
	}
}
