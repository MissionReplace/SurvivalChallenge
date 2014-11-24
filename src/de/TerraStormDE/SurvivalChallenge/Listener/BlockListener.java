/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Listener;

import de.TerraStormDE.SurvivalChallenge.Enums.TGameStatus;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Util.TBlockList;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Hannes
 */
public class BlockListener implements Listener
{
    
    private final SurvivalChallenge plugin;
    private static List<String> mode = new ArrayList<>();
    
    public BlockListener(SurvivalChallenge instance){
        this.plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void break_block(BlockBreakEvent e){
        TGameStatus status = plugin.getGameManager().getCurrentGameStatus();
        if(status == TGameStatus.BEFORE_LOBBY || status == TGameStatus.LOBBY){
            e.setCancelled(true);
            return;
        }
        
        if(!e.getPlayer().hasPermission("sc.break")){
            if(plugin.getBlockWhiteList() != null && plugin.getBlockWhiteList().containsBlock(e.getBlock().getType())){
                e.setCancelled(false);
                return;
            }
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void place_block(BlockPlaceEvent e){
        if(!e.getPlayer().hasPermission("sc.place")){
            if(e.getBlock().getType() == Material.CAKE_BLOCK || e.getBlock().getType() == Material.MELON_BLOCK){
                e.setCancelled(false);
                return;
            }
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void add(PlayerInteractEvent e){
        if(!containsPlayer(e.getPlayer().getName())){
            return;
        }
        if(e.getAction() == Action.RIGHT_CLICK_BLOCK){
            Block b = e.getClickedBlock();
            if(plugin.getBlockWhiteList() == null){
                plugin.setBlockWhiteList(new TBlockList());
            }
            if(plugin.getBlockWhiteList().containsBlock(b.getType())){
                e.getPlayer().sendMessage(plugin.getPrefix() + "Dieser Block war bereits vorhanden, er wurde entfernt!");
                plugin.getBlockWhiteList().removeBlock(b.getType());
                return;
            }
            plugin.getBlockWhiteList().addBlock(b.getType());
            e.getPlayer().sendMessage(plugin.getPrefix() + "Block wurde hinzugefügt!");
        }
    }
    public static void addPlayerToMode(String name){
        if(!mode.contains(name)){
            mode.add(name);
        }
    }
    public static void removePlayerFromMode(String name){
        if(mode.contains(name)){
            mode.remove(name);
        }
    }
    public static boolean containsPlayer(String name){
        return mode.contains(name);
    }
}
