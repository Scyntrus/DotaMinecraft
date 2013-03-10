package com.gmail.scyntrus.dotaminecraft;
import org.bukkit.entity.Player;

public class RecallScheduler
implements Runnable
{
   String name;
   DotaMinecraft plugin;

   public RecallScheduler(DotaMinecraft plugin, String playername)
   {
     this.plugin = plugin;
     this.name = playername;
   }
 
   public void run()
   {
     Player player = this.plugin.getServer().getPlayer(this.name);
     Integer team = (Integer)this.plugin.playerlist.get(this.name);
     if (team == 1)
       player.teleport(this.plugin.RedPoint);
     else if (team == 2)
       player.teleport(this.plugin.BluePoint);
     else {
       player.sendMessage("How can you recall if you're not on a team?");
     }
     this.plugin.playerRecallID.remove(this.name);
   }
 }