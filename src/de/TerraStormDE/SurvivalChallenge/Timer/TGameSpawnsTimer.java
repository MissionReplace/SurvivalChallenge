/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Timer;

import de.TerraStormDE.SurvivalChallenge.ChallengeModul.TBasicModul;
import de.TerraStormDE.SurvivalChallenge.Enums.TConfigPath;
import de.TerraStormDE.SurvivalChallenge.Enums.TGameStatus;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Util.TGameMap;
import de.TerraStormDE.SurvivalChallenge.Util.TGamePlayer;
import de.TerraStormDE.SurvivalChallenge.Util.TGameTimer;
import static de.TerraStormDE.SurvivalChallenge.Util.TGameTimer.getGameTimer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Hannes
 */
public class TGameSpawnsTimer extends TGameTimer
{
    private final SurvivalChallenge plugin;

    public TGameSpawnsTimer(SurvivalChallenge instance ,String name, int count)
    {
        super(name, count);
        this.plugin = instance;
    }

    @Override
    public boolean performHeader()
    {
        plugin.getGameManager().setCurrentGameStatus(TGameStatus.GAME_ON_SPAWNS);
        TGameMap map = plugin.getVoteManager().getWinnedMap();
        if(Bukkit.getOnlinePlayers().length > map.getSpawns().length){
            Bukkit.broadcastMessage(plugin.getPrefix() + "Es wurde nicht $egenügend §7Spawns gesetzt!");
            getGameTimer("break_count").start(1, false);
            return false;
        }
        
        TBasicModul c = null;
        if((boolean)plugin.convertPath(TConfigPath.SETTINGS_ENABLE_MODULS)){
            if((c = plugin.getVoteManager().getWinnedChallenge()) != null){
                plugin.getVoteManager().getWinnedChallenge().activateListener();
            }
        }
        
        for(Player p : Bukkit.getOnlinePlayers()){
            if(TGamePlayer.getGamePlayer(p.getUniqueId()).isSpectator()){
                continue;
            }
            p.setFoodLevel(20);
            p.setHealth(20.0);
            p.getInventory().clear();
        }
        map.teleportAll();
        map.getWorld().setTime(0);
        
        if((boolean)plugin.convertPath(TConfigPath.CHEST_REFILL)){
            plugin.getChestManager().startRefill((int)plugin.convertPath(TConfigPath.CHEST_REFILL_TIME));
        }
        
        TGameMap win = plugin.getVoteManager().getWinnedMap();
        String info = plugin.convertPath(TConfigPath.MESSAGE_INFO_START).toString();
        info = info.replace("&", "§");
        info = info.replace("%map%", win.getDisplayName().replace("&", "§"));
        info = info.replace("%builder%", win.getBuilder());
        info = info.replace("%n%", "\n" + plugin.getPrefix());
        
        if((boolean) plugin.convertPath(TConfigPath.SETTINGS_ENABLE_MODULS) && c != null){
            for(int i = 0; i < 100;i++){
                for(Player p : Bukkit.getOnlinePlayers()){
                    p.sendMessage(" ");
                }
            }
            Bukkit.broadcastMessage(plugin.getPrefix() + info);
            Bukkit.broadcastMessage(plugin.getPrefix() + "Es wird mit dem Modul '" + c.getDisplayName().replace("&", "§") + "§7' gespielt.");
            Bukkit.broadcastMessage(plugin.getPrefix() + "Die Beschreibung des Moduls lautet: " + c.getDescription());
        }
        
        return true;
    }

    @Override
    public void performBody()
    {
        if(Bukkit.getOnlinePlayers().length < (int) plugin.convertPath(TConfigPath.SETTINGS_MIN_PLAYER)){
            Bukkit.broadcastMessage(plugin.getPrefix() + plugin.convertPath(TConfigPath.MESSAGE_NOT_ENOTH_PLAYER).toString().replace("&", "§"));
            stop(false);
            getGameTimer("break_count").start(1, false);
            return;
        }
        if(getCount() < (int) plugin.convertPath(TConfigPath.SETTINGS_GAME_MOVE_SEND) && getCount() > 0){
            String s = plugin.convertPath(TConfigPath.MESSAGE_GAME_MOVE_COUNT).toString();
            s = s.replace("&", "§");
            s = s.replace("%count%", Integer.toString(getCount()));
            
            Bukkit.broadcastMessage(plugin.getPrefix() + s);
        } else if(getCount() == 0){
            stop(false);
            Bukkit.broadcastMessage(plugin.getPrefix() + "Ihr könnt euch nun bewegen!");
            plugin.getGameManager().setCurrentGameStatus(TGameStatus.GAME_GRACE_PERIOD);
            getGameTimer("game_grace").start(1, false);
        }
    }
    
}
