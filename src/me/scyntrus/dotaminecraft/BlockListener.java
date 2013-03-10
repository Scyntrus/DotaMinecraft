package me.scyntrus.dotaminecraft;

import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.Listener;
import org.bukkit.material.Dispenser;
import org.bukkit.material.SpawnEgg;

public class BlockListener implements Listener {
	DotaMinecraft plugin = null;
	
	public BlockListener(DotaMinecraft plugin){
		this.plugin = plugin;
	}

	@EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
		if (event.getBlock().getWorld().getName().equals(plugin.WorldName)){
			event.setCancelled(true);
		}
	}

	@EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
		if (event.getBlock().getWorld().getName().equals(plugin.WorldName)){
			event.setCancelled(true);
		}
	}

	@EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
		if (event.getBlock().getWorld().getName().equals(plugin.WorldName)){
			event.setCancelled(true);
		}
	}
	
	@EventHandler
    public void onBlockDispenseEvent(BlockDispenseEvent event) {
		if (plugin.ForceMobSpawn && event.getItem().getType()==Material.MONSTER_EGG && !plugin.world.getAllowMonsters()) {
			Dispenser disp = (Dispenser) event.getBlock().getState().getData();
			SpawnEgg e = (SpawnEgg) event.getItem().getData();
			event.getBlock().getWorld().spawnEntity(event.getBlock().getRelative(disp.getFacing()).getLocation().add(.5,0.,.5), e.getSpawnedType());
		}
	}
}
