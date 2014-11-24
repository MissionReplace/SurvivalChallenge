/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.ChestManager;

import de.TerraStormDE.SurvivalChallenge.Enums.TGameStatus;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Util.TGamePlayer;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Hannes
 */
public class TChestListener implements Listener
{
    
    private final SurvivalChallenge plugin;
    
    public TChestListener(SurvivalChallenge instance){
        this.plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void interact(PlayerInteractEvent e){
        TGameStatus status = plugin.getGameManager().getCurrentGameStatus();
        if(status == TGameStatus.BEFORE_LOBBY || status == TGameStatus.END || status == TGameStatus.GAME_ON_SPAWNS || status == TGameStatus.LOBBY){
            return;
        }
        
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block b = e.getClickedBlock();
            if(b.getType() == Material.getMaterial(plugin.getChestManager().getBlockID())){
                if(plugin.getChestManager().getBlockSubID() != 0){
                    if(b.getData() != plugin.getChestManager().getBlockSubID()){
                        e.setCancelled(true);
                        return;
                    }
                }
                TGamePlayer player = TGamePlayer.getGamePlayer(e.getPlayer().getUniqueId());
                if(player.isSpectator()){
                    e.setCancelled(true);
                    return;
                }
                
                if(plugin.getChestManager().containsChest(b.getLocation())){
                    e.setCancelled(true);
                    e.getPlayer().openInventory(plugin.getChestManager().createChest(b.getLocation()));
                } else {
                    e.setCancelled(true);
                    e.getPlayer().openInventory(plugin.getChestManager().createChest(b.getLocation()));
                }
            }
        }
    }
    
}
