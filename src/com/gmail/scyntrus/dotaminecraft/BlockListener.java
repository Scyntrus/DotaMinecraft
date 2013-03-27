package com.gmail.scyntrus.dotaminecraft;

import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDispenseEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
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
		if (event.getBlock().getWorld().getName().equals(plugin.WorldName)){
			if (event.getItem().getType()==Material.MONSTER_EGG) {
				Dispenser disp = (Dispenser) event.getBlock().getState().getData();
				SpawnEgg e = (SpawnEgg) event.getItem().getData();
				BlockFace face = disp.getFacing();
				if (face == BlockFace.EAST) {
					face = BlockFace.NORTH;
				} else if (face == BlockFace.SOUTH) {
					face = BlockFace.EAST;
				} else if (face == BlockFace.WEST) {
					face = BlockFace.SOUTH;
				} else if (face == BlockFace.NORTH) {
					face = BlockFace.WEST;
				}
				LivingEntity newEntity = (LivingEntity) event.getBlock().getWorld().spawnEntity(
						event.getBlock().getRelative(face).getLocation().add(.5,0.,.5), 
						e.getSpawnedType());
				if (plugin.removeMobArmor) {
					newEntity.getEquipment().setHelmet(null);
					newEntity.getEquipment().setChestplate(null);
					newEntity.getEquipment().setLeggings(null);
					newEntity.getEquipment().setBoots(null);
				}
				if (plugin.giveMobsHelmet) {
					if (newEntity instanceof Monster && (newEntity.getEquipment().getHelmet() == null || newEntity.getEquipment().getHelmet().getType() == Material.AIR)) {
						newEntity.getEquipment().setHelmet(new ItemStack(Material.LEATHER_HELMET));
					}
				}
				event.setCancelled(true);
			}
		}
	}
}
