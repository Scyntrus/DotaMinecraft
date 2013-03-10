package me.scyntrus.dotaminecraft;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.Listener;

import com.onarandombox.MultiverseCore.MultiverseCore;

public class PluginListener implements Listener {
	DotaMinecraft plugin = null;
	
	public PluginListener(DotaMinecraft plugin){
		this.plugin = plugin;
	}

	@EventHandler
    public void onPluginEnable(PluginEnableEvent event) {
		if (event.getPlugin() instanceof MultiverseCore){
			LoadPlugin(this.plugin);
		}
	}
	
	public static void setupWorld(DotaMinecraft plugin) {
		plugin.world = plugin.getServer().getWorld(plugin.WorldName);
		
		//define locations
		plugin.RedPoint = new Location(plugin.world,-1188.,49.,440.);
		plugin.BluePoint = new Location(plugin.world,-945.,49.,195.);
		plugin.RedBed = new Location(plugin.world,-1194.,47.,444.);
		plugin.BlueBed = new Location(plugin.world,-941.,47.,191.);
		plugin.RedPoint.getChunk().load();
		plugin.BluePoint.getChunk().load();
		plugin.FarAwayLocation = new Location(plugin.world,-1448.,4.,-78.);
		plugin.turretlocations.clear();
		plugin.turretlocations.put(new Location(plugin.world,-1158.,55.,411.), "Red Nexus");
		plugin.turretlocations.put(new Location(plugin.world,-975.,55.,224.), "Blue Nexus");
		plugin.turretlocations.put(new Location(plugin.world,-1093.,54.,343.), "Red Mid Tower");
		plugin.turretlocations.put(new Location(plugin.world,-1040.,54.,292.), "Blue Mid Tower");
		plugin.turretlocations.put(new Location(plugin.world,-1042.,53.,180.), "Blue Top Inner Tower");
		plugin.turretlocations.put(new Location(plugin.world,-1130.,53.,190.), "Blue Top Outer Tower");
		plugin.turretlocations.put(new Location(plugin.world,-1193.,53.,260.), "Red Top Outer Tower");
		plugin.turretlocations.put(new Location(plugin.world,-1192.,53.,350.), "Red Top Inner Tower");
		plugin.turretlocations.put(new Location(plugin.world,-1091.,53.,455.), "Red Bottom Inner Tower");
		plugin.turretlocations.put(new Location(plugin.world,-1003.,53.,455.), "Red Bottom Outer Tower");
		plugin.turretlocations.put(new Location(plugin.world,-940.,53.,375.), "Blue Bottom Outer Tower");
		plugin.turretlocations.put(new Location(plugin.world,-941.,53.,285.), "Blue Bottom Inner Tower");
		
		plugin.turretstates.clear();
		for (String value : plugin.turretlocations.values()){
			plugin.turretstates.put(value, false);
		}
		
		plugin.world.setAutoSave(false);
		plugin.world.setPVP(true);
	}
	
	public static void LoadPlugin(DotaMinecraft plugin) {
		if (plugin.getServer().getWorld(plugin.WorldName)==null){
			System.out.println("Specified world does not exist!");
			plugin.Enabled = false;
			return;
		}
		plugin.MVCorePlugin = (MultiverseCore) plugin.pm.getPlugin("Multiverse-Core");
		
		setupWorld(plugin);
		
	    plugin.pm.registerEvents(new EntityListener(plugin), plugin);
	    plugin.pm.registerEvents(new BlockListener(plugin), plugin);
	    plugin.getCommand("dota").setExecutor(new DotaCommand(plugin));
	    plugin.getCommand("t").setExecutor(new TCommand(plugin));
		System.out.println("Dota Minecraft by Scyntrus Loaded!");
		
	}
}
