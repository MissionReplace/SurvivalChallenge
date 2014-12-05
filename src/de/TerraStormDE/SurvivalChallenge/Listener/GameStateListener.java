/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Listener;

import de.TerraStormDE.SurvivalChallenge.Enums.TGameStatus;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Util.TGamePlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.world.WorldSaveEvent;

/**
 *
 * @author Hannes
 */
public class GameStateListener implements Listener
{
    private final SurvivalChallenge plugin;
    
    public GameStateListener(SurvivalChallenge instance){
        this.plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void damage(EntityDamageEvent e){
        if(e.getEntity() instanceof Player){
            TGameStatus status = plugin.getGameManager().getCurrentGameStatus();
            if(status != TGameStatus.END && status != TGameStatus.DEATH_MATCH_GRACE_PERIOD && status != TGameStatus.BEFORE_LOBBY && status != TGameStatus.LOBBY && status != TGameStatus.GAME_GRACE_PERIOD && status != TGameStatus.DEATH_MATCH_ON_SPAWNS && status != TGameStatus.GAME_ON_SPAWNS){
                e.setCancelled(false);
            } else {
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void hunger(FoodLevelChangeEvent e){
        if(!(e.getEntity() instanceof Player)){
            return;
        }
        Player p = (Player) e.getEntity();
        TGameStatus status = plugin.getGameManager().getCurrentGameStatus();
        if(status == TGameStatus.BEFORE_LOBBY || status == TGameStatus.LOBBY || status == TGameStatus.DEATH_MATCH_GRACE_PERIOD || status == TGameStatus.DEATH_MATCH_ON_SPAWNS || status == TGameStatus.GAME_GRACE_PERIOD || status == TGameStatus.GAME_ON_SPAWNS){
            e.setCancelled(true);
        }
        TGamePlayer player = TGamePlayer.getGamePlayer(p.getUniqueId());
        if(player != null && player.isSpectator()){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void drop(PlayerDropItemEvent e){
        TGameStatus status = plugin.getGameManager().getCurrentGameStatus();
        if(status == TGameStatus.BEFORE_LOBBY || status == TGameStatus.LOBBY){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void move(PlayerMoveEvent e){
        TGameStatus status = plugin.getGameManager().getCurrentGameStatus();
        if(status == TGameStatus.DEATH_MATCH_ON_SPAWNS || status == TGameStatus.GAME_ON_SPAWNS){
            if(e.getFrom().getX() > e.getTo().getX() || e.getFrom().getZ() > e.getTo().getZ() || e.getFrom().getX() < e.getTo().getX() || e.getFrom().getZ() < e.getTo().getZ()){
                e.getPlayer().teleport(e.getFrom());
            }
        }
    }
    @EventHandler
    public void save(WorldSaveEvent e){
        Bukkit.broadcastMessage("Save World: " + e.getWorld().getName());
        System.out.println("Save World: " + e.getWorld().getName());
    }
   
}
