/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Listener;

import de.TerraStormDE.SurvivalChallenge.Enums.TConfigPath;
import de.TerraStormDE.SurvivalChallenge.Enums.TGameStatus;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Util.TGamePlayer;
import de.TerraStormDE.SurvivalChallenge.Web.TNickUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.kitteh.tag.TagAPI;

/**
 *
 * @author Hannes
 */
public class PlayerDeathListener implements Listener
{
    private final SurvivalChallenge plugin;
    
    public PlayerDeathListener(SurvivalChallenge instance){
        this.plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void death(final PlayerDeathEvent e){
        TGameStatus status = plugin.getGameManager().getCurrentGameStatus();
        if(status == TGameStatus.DEATH_MATCH || status == TGameStatus.IN_GAME){
            if(e.getEntity().getKiller() instanceof Player){
                String msg = plugin.convertPath(TConfigPath.MESSAGE_PLAYER_DEATH).toString();
                msg = msg.replace("%entity%",e.getEntity().getDisplayName());
                if(e.getEntity().getKiller() instanceof Player){
                    msg = msg.replace("%killer%", e.getEntity().getKiller().getDisplayName());
                }
                msg = msg.replace("&", "§");

                e.setDeathMessage(plugin.getPrefix() + msg);
                
                TGamePlayer killer = TGamePlayer.getGamePlayer(e.getEntity().getKiller().getUniqueId());
                killer.addKill();
            } else {
                e.setDeathMessage(plugin.getPrefix() + "§e" + e.getEntity().getName() + " §7ist gestorben!");
            }
            TGamePlayer player = TGamePlayer.getGamePlayer(e.getEntity().getUniqueId());
            player.setSpectator(true);
            player.addDeath();
            if((boolean) SurvivalChallenge.getInstance().convertPath(TConfigPath.SETTINGS_ENABLE_MODULS)){
                player.insertModulStats(plugin.getVoteManager().getWinnedChallenge().getName(), 1, 0);
            }
        }
    }
    @EventHandler
    public void respawn(PlayerRespawnEvent e){
        TGameStatus status = plugin.getGameManager().getCurrentGameStatus();
        if(status == TGameStatus.DEATH_MATCH || status == TGameStatus.IN_GAME){
            e.getPlayer().getInventory().clear();
            e.getPlayer().setAllowFlight(true);
            e.getPlayer().setFlying(true);
            e.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
            
            String s;
            if((s = TNickUtil.getNick(e.getPlayer().getName())) != null){
                e.getPlayer().setPlayerListName(s);
                e.getPlayer().setDisplayName(s);
                TagAPI.refreshPlayer(e.getPlayer());
            }
            
            for(Player all : Bukkit.getOnlinePlayers()){
                TGamePlayer player = TGamePlayer.getGamePlayer(all.getUniqueId());
                if(player.isSpectator() || all.equals(e.getPlayer())){
                    continue;
                }
                all.hidePlayer(e.getPlayer());
            }
            
            
        }
    }
    
}
