/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Timer;

import de.TerraStormDE.SurvivalChallenge.Enums.TConfigPath;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Util.TGamePlayer;
import de.TerraStormDE.SurvivalChallenge.Enums.TGameStatus;
import de.TerraStormDE.SurvivalChallenge.Util.TGameTimer;
import org.bukkit.Bukkit;

/**
 *
 * @author Hannes
 */
public class TBreakTimer extends TGameTimer
{
    private final SurvivalChallenge plugin;

    public TBreakTimer(SurvivalChallenge instance,String name, int count)
    {
        super(name, count);
        this.plugin = instance;
    }

    @Override
    public boolean performHeader()
    {
        plugin.getGameManager().setCurrentGameStatus(TGameStatus.END);
        Bukkit.broadcastMessage(plugin.getPrefix() + plugin.convertPath(TConfigPath.MESSAGE_SERVER_RESTART).toString().replace("&", "§").replace("%count%", Integer.toString(getCount())));
        return true;
    }

    @Override
    public void performBody()
    {
        for(TGameTimer timer : getGameTimers()){
            if(!timer.equals(this) && timer.isRunning()){
                timer.stop(false);
            }
        }
        if(getCount() <= 5 && getCount() > 0){
            Bukkit.broadcastMessage(plugin.getPrefix() + plugin.convertPath(TConfigPath.MESSAGE_SERVER_RESTART).toString().replace("&", "§").replace("%count%", Integer.toString(getCount())));
        }
        if(getCount() == 0){
            for(TGamePlayer player : TGamePlayer.getGamePlayers()){
                Bukkit.broadcastMessage("Start Saving.. " + player.getName() + " [" + TGamePlayer.getGamePlayers().length + "]");
                
                if(!player.getName().equalsIgnoreCase(TGamePlayer.getWinnedPlayer().getName())){
                    if(!player.isSpectator()){
                        player.insertModulStats(plugin.getVoteManager().getWinnedChallenge().getName(), 1, 0);
                        Bukkit.broadcastMessage("Break Timer: " + player.getName());
                    }
                }
                player.saveData();
            }
            
            /*for(Player p : Bukkit.getOnlinePlayers()){
                //TBungeeUtil.sendPlayer(p, plugin.convertPath(TConfigPath.SETTINGS_SERVER_FALLBACK).toString());
                p.kickPlayer(plugin.getPrefix() + "§eDas Spiel ist zuende!");
            }*/
            Bukkit.reload();
        }
    }
    
}
