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
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Hannes
 */
public class SpectatorListener implements Listener
{
    
    private final SurvivalChallenge plugin;
    
    public SpectatorListener(SurvivalChallenge instance){
        this.plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void compass(PlayerInteractEvent e){
        if(!e.getAction().toString().contains("RIGHT")){
            return;
        }
        if(e.getPlayer().getItemInHand() == null || e.getPlayer().getItemInHand().getType() != Material.COMPASS){
            return;
        }
        TGamePlayer player = TGamePlayer.getGamePlayer(e.getPlayer().getUniqueId());
        if(player == null){
            return;
        }
        if(player.isSpectator()){
            int lines = 0;
            while(lines * 9 < Bukkit.getOnlinePlayers().length){
                lines++;
            }
            if(lines > 6){
                lines = 6;
            }
            Inventory inv = Bukkit.createInventory(null, lines*9,"Spieler - Übersicht");
            int slot = 0;
            
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.equals(e.getPlayer())){
                    continue;
                }
                ItemStack skull = new ItemStack(Material.SKULL_ITEM,1,(short)3);
                ItemMeta im = skull.getItemMeta();
                im.setDisplayName("§6§l" + p.getName());
                skull.setItemMeta(im);
                
                inv.setItem(slot, skull);
                slot++;
            }
            e.getPlayer().openInventory(inv);
        }
    }
    @EventHandler
    public void inv(InventoryClickEvent e){
        if(e.getInventory().getName().equalsIgnoreCase("Spieler - Übersicht")){
            if(e.getCurrentItem()!= null && e.getCurrentItem().getType() != Material.AIR){
                Player p = (Player) e.getWhoClicked();
                String name = ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName());
                if(Bukkit.getPlayer(name) != null){
                    p.sendMessage(plugin.getPrefix() + "Du beobachtest jetzt '§e" + name + "§7'");
                    p.teleport(Bukkit.getPlayer(name).getLocation());
                } else {
                    p.sendMessage(plugin.getPrefix() + "Error");
                    e.getView().close();
                }
            }
        }
    }
    
    @EventHandler
    public void damage(EntityDamageByEntityEvent e){
        if(e.getDamager() instanceof Player){
            Player damager = (Player) e.getDamager();
            TGamePlayer player = TGamePlayer.getGamePlayer(damager.getUniqueId());
            if(player != null && player.isSpectator()){
                e.setCancelled(true);
            }
        }
        if(e.getEntity() instanceof Player){
            Player entity = (Player) e.getEntity();
            TGamePlayer player = TGamePlayer.getGamePlayer(entity.getUniqueId());
            if(player != null && player.isSpectator()){
                e.setCancelled(true);
            }
        }
    }
    
    @EventHandler
    public void flight(PlayerToggleFlightEvent e){
        TGamePlayer player = TGamePlayer.getGamePlayer(e.getPlayer().getUniqueId());
        if(player == null){
            return;
        }
        if(player.isSpectator()){
            e.setCancelled(false);
            e.getPlayer().setAllowFlight(true);
            e.getPlayer().setFlying(true);
        }
    }
    
    @EventHandler
    public void picup(PlayerPickupItemEvent e){
        TGamePlayer player = TGamePlayer.getGamePlayer(e.getPlayer().getUniqueId());
        if(player != null && player.isSpectator()){
            e.setCancelled(true);
        }
    }
    @EventHandler
    public void drop(PlayerDropItemEvent e){
        TGamePlayer player = TGamePlayer.getGamePlayer(e.getPlayer().getUniqueId());
        if(player != null && player.isSpectator()){
            e.setCancelled(true);
        }
    }
    
    
}
