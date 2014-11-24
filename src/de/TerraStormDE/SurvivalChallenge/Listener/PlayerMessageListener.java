/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Listener;

import de.TerraStormDE.SurvivalChallenge.Enums.TConfigPath;
import de.TerraStormDE.SurvivalChallenge.Enums.TGameStatus;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Util.TGameMap;
import de.TerraStormDE.SurvivalChallenge.Util.TGamePlayer;
import de.TerraStormDE.SurvivalChallenge.Util.TGameTimer;
import de.TerraStormDE.SurvivalChallenge.Util.TItemUtil;
import de.TerraStormDE.SurvivalChallenge.Web.TNickUtil;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

/**
 *
 * @author Hannes
 */
public class PlayerMessageListener implements Listener
{
    private final SurvivalChallenge plugin;
    
    public PlayerMessageListener(SurvivalChallenge instance){
        this.plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void join(final PlayerJoinEvent e){
        TNickUtil.addNick(e.getPlayer().getName());
        
        e.getPlayer().getInventory().clear();
        e.getPlayer().getInventory().setArmorContents(null);
        e.getPlayer().setHealth(20.0);
        e.getPlayer().setFoodLevel(20);
        
        plugin.getUserAPI().createUser(e.getPlayer().getName(), e.getPlayer().getUniqueId());
        if(plugin.getGameManager().getCurrentGameStatus() != TGameStatus.BEFORE_LOBBY && plugin.getGameManager().getCurrentGameStatus() != TGameStatus.LOBBY){
            if(TGamePlayer.getGamePlayer(e.getPlayer().getUniqueId()) == null){
                new TGamePlayer(e.getPlayer().getName(), e.getPlayer().getUniqueId(), true);
            }
            e.setJoinMessage(null);
            e.getPlayer().getInventory().addItem(new ItemStack(Material.COMPASS));
            e.getPlayer().teleport(SurvivalChallenge.getInstance().getVoteManager().getWinnedMap().getWorld().getSpawnLocation());
            e.getPlayer().setAllowFlight(true);
            e.getPlayer().setFlying(true);
            for(Player all : Bukkit.getOnlinePlayers()){
                if(all.equals(e.getPlayer()) || TGamePlayer.getGamePlayer(all.getUniqueId()).isSpectator()){
                    continue;
                }
                all.hidePlayer(e.getPlayer());
            }
        } else {
            String name = e.getPlayer().getName();
            if(e.getPlayer().hasPermission(plugin.convertPath(TConfigPath.NICK_ITEM_PERMISSIONS).toString())){
                if((boolean) plugin.convertPath(TConfigPath.NICK_SET_AUTO)){
                    name = TNickUtil.getRandomNick();
                }
            }
            if(TGamePlayer.getGamePlayer(e.getPlayer().getUniqueId()) == null){
                new TGamePlayer(e.getPlayer().getName(), e.getPlayer().getUniqueId(), false);
            }
            
            e.getPlayer().setPlayerListName(name);
            e.getPlayer().setDisplayName(name);
            e.setJoinMessage(plugin.getPrefix() + plugin.convertPath(TConfigPath.MESSAGE_JOIN).toString().replace("&", "§").replace("%p%",name));
            if(Bukkit.getOnlinePlayers().length >= (int) plugin.convertPath(TConfigPath.SETTINGS_MIN_PLAYER)){
                TGameTimer.getGameTimer("lobby_count").start(1, true);
            }
        }
        
        if(e.getPlayer().isOp()){
                for(TGameMap map : TGameMap.getGameMaps()){
                    if(!map.isReady()){
                        e.getPlayer().sendMessage("§8[§c§lSC-Warnung§8] §7Die Map '" + map.getName() + "' ist nicht vollständig!");
                    }
                }
            }
        
        int default_pos = 4;
        int diff = (int) plugin.convertPath(TConfigPath.SETTINGS_VOTE_ITEM_POS_DIFF);
        boolean enable_m = (boolean) plugin.convertPath(TConfigPath.SETTINGS_ENABLE_MODULS);
        
        if(!enable_m){
            diff = 0;
        }
        
        ItemStack i = TItemUtil.toItemStack(plugin.convertPath(TConfigPath.SETTINGS_MAP_VOTE_ITEM).toString());
        ItemMeta im = i.getItemMeta();
        im.setDisplayName(plugin.convertPath(TConfigPath.SETTINGS_MAP_VOTE_ITEM_NAME).toString().replace("&", "§"));
        i.setItemMeta(im);
        
        e.getPlayer().getInventory().setItem(default_pos-diff, i);
        
        if(enable_m){
            ItemStack i2 = TItemUtil.toItemStack(plugin.convertPath(TConfigPath.SETTINGS_SC_VOTE_ITEM).toString());
            ItemMeta im2 = i2.getItemMeta();
            im2.setDisplayName(plugin.convertPath(TConfigPath.SETTINGS_SC_VOTE_ITEM_NAME).toString().replace("&", "§"));
            i2.setItemMeta(im2);
            
            e.getPlayer().getInventory().setItem(default_pos+diff,i2);
        }
        
        /*if(e.getPlayer().hasPermission(plugin.convertPath(TConfigPath.NICK_ITEM_PERMISSIONS).toString())){
            ItemStack stack = TItemUtil.toItemStack(plugin.convertPath(TConfigPath.NICK_ITEM_ID).toString());
            ItemMeta stackim = stack.getItemMeta();
            stackim.setDisplayName(plugin.convertPath(TConfigPath.NICK_ITEM_NAME).toString().replace("&", "§"));
            stack.setItemMeta(im);
            
            e.getPlayer().getInventory().setItem((diff == 0 ? default_pos+2 : default_pos+diff), stack);
        }*/
        
        if(plugin.getLobby() == null){
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, new Runnable()
        {

            @Override
            public void run()
            {
                e.getPlayer().teleport(plugin.getLobby());
            }
        },5);
    }
    
    @EventHandler
    public void quit(PlayerQuitEvent e){
        TGamePlayer player = TGamePlayer.getGamePlayer(e.getPlayer().getUniqueId());
        if(player.isSpectator()){
            e.setQuitMessage(null);
            TGamePlayer.removeGamePlayer(e.getPlayer().getUniqueId(), true);
        } else {
            String quit = plugin.convertPath(TConfigPath.MESSAGE_QUIT).toString();
            quit = quit.replace("&", "§");
            quit = quit.replace("%p%", e.getPlayer().getDisplayName());
            e.setQuitMessage(plugin.getPrefix() + quit);
        }
    }
    
    @EventHandler
    public void kick(PlayerKickEvent e) {
        TGamePlayer.removeGamePlayer(e.getPlayer().getUniqueId(), true);
    }
    
    @EventHandler
    public void chat(AsyncPlayerChatEvent e){
        TGamePlayer player = TGamePlayer.getGamePlayer(e.getPlayer().getUniqueId());
        if(player.isSpectator()){
            e.setCancelled(true);
            for(TGamePlayer all : TGamePlayer.getGamePlayers()){
                if(all.isSpectator()){
                    Bukkit.getPlayer(all.getUUID()).sendMessage("§8[§4X§8] §7" + e.getPlayer().getDisplayName() + ": §f" + e.getMessage());
                }
            }
        } else {
            e.setFormat("§8[§a§lTribut§8] §7" + e.getPlayer().getDisplayName() + ": §f" + e.getMessage());
        }
    }
}
