/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Listener;

import de.TerraStormDE.SurvivalChallenge.ChallengeModul.TBasicModul;
import de.TerraStormDE.SurvivalChallenge.Enums.TConfigPath;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Util.TGameMap;
import de.TerraStormDE.SurvivalChallenge.Util.TItemUtil;
import java.util.Arrays;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Hannes
 */
public class VoteListener implements Listener
{
    private final SurvivalChallenge plugin;
    private Inventory map = null;
    private Inventory modul = null;
    
    public VoteListener(SurvivalChallenge instance){
        this.plugin = instance;
        this.plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    
    @EventHandler
    public void click(InventoryClickEvent e){
        if(e.getInventory().getName().equalsIgnoreCase(plugin.convertPath(TConfigPath.SETTINGS_MAP_VOTE_INV_NAME).toString().replace("&", "§"))){
            e.setCancelled(true);
            if(!plugin.getVoteManager().canVote()){
                ((Player)e.getWhoClicked()).sendMessage(plugin.getPrefix() + "Momentan kann nicht gevotet werden!");
                e.getView().close();
                return;
            }
            
            if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                ItemStack i = e.getCurrentItem();
                ItemMeta im = i.getItemMeta();
                TGameMap map = TGameMap.getByDisplayName(im.getDisplayName(),false);
                Player p = (Player) e.getWhoClicked();
                if(map != null){
                    if(TGameMap.hasAlreadyVoted(p.getName())){
                        p.sendMessage(plugin.getPrefix() + "Du hast bereits für eine Map gevotet!");
                        return;
                    }
                    p.sendMessage(plugin.getPrefix() + "Du hast erfolgreich für die Map §e" + map.getDisplayName().replace("&", "§") + " §7gevotet!");
                    map.addVote(p.getName());
                    p.getInventory().remove(p.getItemInHand());
                    e.getView().close();
                } else {
                    p.sendMessage(plugin.getPrefix() + "Diese Map ist nicht vorhanden!");
                }
            }
        } else if(e.getInventory().getName().equalsIgnoreCase(plugin.convertPath(TConfigPath.SETTINGS_SC_VOTE_INV_NAME).toString().replace("&", "§"))){
            e.setCancelled(true);
            if(!plugin.getVoteManager().canVote()){
                ((Player)e.getWhoClicked()).sendMessage(plugin.getPrefix() + "Momentan kann nicht gevotet werden!");
                e.getView().close();
                return;
            }
            if(e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR){
                ItemStack i = e.getCurrentItem();
                ItemMeta im = i.getItemMeta();
                TBasicModul modul = TBasicModul.getByDisplayName(im.getDisplayName(), false);
                Player p = (Player) e.getWhoClicked();
                if(modul != null){
                    if(TBasicModul.hasAlreadyVoted(p.getName())){
                        p.sendMessage(plugin.getPrefix() + "Du hast bereits für ein Modul gevotet!");
                        return;
                    }
                    e.getView().close();
                    p.getInventory().remove(p.getItemInHand());
                    modul.addVote(p.getName());
                    p.sendMessage(plugin.getPrefix() + "Du hast erfolgreich für das Modul '" + modul.getDisplayName().replace("&", "§") + "§7' gevotet!");
                } else {
                    p.sendMessage(plugin.getPrefix() + "Dieses Modul ist nicht vorhanden!");
                }
            }
        }
    }
    @EventHandler
    public void interact(PlayerInteractEvent e){
        if(e.getAction().toString().contains("RIGHT")){
            Player p = e.getPlayer();
            if(p.getItemInHand() != null){
                ItemStack hand = p.getItemInHand();
                
                ItemStack m_vote = TItemUtil.toItemStack(plugin.convertPath(TConfigPath.SETTINGS_MAP_VOTE_ITEM).toString());
                ItemMeta im_vote = m_vote.getItemMeta();
                im_vote.setDisplayName(plugin.convertPath(TConfigPath.SETTINGS_MAP_VOTE_ITEM_NAME).toString().replace("&", "§"));
                m_vote.setItemMeta(im_vote);
                
                ItemStack mo_vote = TItemUtil.toItemStack(plugin.convertPath(TConfigPath.SETTINGS_SC_VOTE_ITEM).toString());
                ItemMeta im_mo = mo_vote.getItemMeta();
                im_mo.setDisplayName(plugin.convertPath(TConfigPath.SETTINGS_SC_VOTE_ITEM_NAME).toString().replace("&", "§"));
                mo_vote.setItemMeta(im_mo);
                
                if(hand.equals(m_vote)){
                    if(TGameMap.getGameMaps().length == 0){
                        p.sendMessage(plugin.getPrefix() + "Es wurden keine Maps gefunden!");
                        return;
                    }
                    if(this.map == null){
                        buildMapInventory();
                    }
                    p.openInventory(this.map);
                    e.setCancelled(true);
                }
                if(hand.equals(mo_vote)){
                    if(TBasicModul.getChallenges().length == 0){
                        p.sendMessage(plugin.getPrefix() + "Es wurden keine Module gefunden!");
                        return;
                    }
                    if(this.modul == null){
                        buildModulInventory();
                    }
                    p.openInventory(this.modul);
                    e.setCancelled(true);
                }
                
                
            }
        }
    }
    /*int lines = 0;
                    while(lines * 9 < TGameMap.getGameMaps().length){
                        lines++;
                    }
                    Inventory inv = Bukkit.createInventory(null, lines*9, "Voting - SC");
                    int slot = 0;
                    for(TBasicModul modul : TBasicModul.getChallenges()){
                        ItemStack i = modul.getVoteItem();
                        ItemMeta im = i.getItemMeta();
                        im.setDisplayName(modul.getDisplayName().replace("&", "§"));
                        i.setItemMeta(im);
                        
                        inv.setItem(slot, i);
                        slot++;
                    }
                    p.openInventory(inv);
                    e.setCancelled(true);*/
    
    private void buildMapInventory(){
        int lines = 0;
        while(lines * 9 < TGameMap.getGameMaps().length){
            lines++;
        }
        
        this.map = Bukkit.createInventory(null, lines*9,plugin.convertPath(TConfigPath.SETTINGS_MAP_VOTE_INV_NAME).toString().replace("&", "§"));
        int current = 0;
        for(TGameMap map : TGameMap.getGameMaps()){
            ItemStack i = map.getVoteItem();
            ItemMeta im = i.getItemMeta();
            im.setDisplayName(map.getDisplayName().replace("&", "§"));
            i.setItemMeta(im);
                        
            this.map.setItem(current, i);
            current++;
        }
        
    }
    private void buildModulInventory(){
        int lines = 0;
        while(lines * 9 < TBasicModul.getChallenges().length){
            lines++;
        }
        this.modul = Bukkit.createInventory(null, lines*9,plugin.convertPath(TConfigPath.SETTINGS_SC_VOTE_INV_NAME).toString().replace("&", "§"));
        
        int current = 0;
        for(TBasicModul modul : TBasicModul.getChallenges()){
            ItemStack i = modul.getVoteItem();
            ItemMeta im = i.getItemMeta();
            im.setLore(Arrays.asList("§7§o" + ChatColor.stripColor(modul.getDescription())));
            im.setDisplayName(modul.getDisplayName().replace("&", "§"));
            i.setItemMeta(im);
                        
            this.modul.setItem(current, i);
            current++;
        }
    }
}
