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
import org.bukkit.entity.Player;

/**
 *
 * @author Hannes
 */
public class TDeathmatchTimer extends TGameTimer
{
    private final SurvivalChallenge plugin;

    public TDeathmatchTimer(SurvivalChallenge instance,String name, int count)
    {
        super(name, count);
        this.plugin = instance;
    }

    @Override
    public boolean performHeader()
    {
        plugin.getGameManager().setCurrentGameStatus(TGameStatus.DEATH_MATCH);
        return true;
    }

    @Override
    public void performBody()
    {
        if(Bukkit.getOnlinePlayers().length - TGamePlayer.getSpectatorAmount() == 1){
            
            TGamePlayer win = TGamePlayer.getWinnedPlayer();
            win.addPoints(20);
            
            if((boolean) plugin.convertPath(TConfigPath.SETTINGS_ENABLE_MODULS)){
                win.insertModulStats(plugin.getVoteManager().getWinnedChallenge().getName(), 1, 1);
            }
            
            
            plugin.getBackupManager().allowGameReset(true);
            Bukkit.broadcastMessage(plugin.getPrefix() + "§e" + win.getName() + "§7 hat das Spiel gewonnen!");
            stop(false);
            getGameTimer("break_count").start(1, false);  
            return;
            
        }
        if(getCount() <= 10 && getCount() > 0){
            Bukkit.broadcastMessage(plugin.getPrefix() + "Das Deathmatch endet in §e" + getCount() + " §7Sekunden!");
        }
        if(getCount() == 0){
            Bukkit.broadcastMessage(plugin.getPrefix() + "Das Deathmatch ist zuende, es konnte kein Gewinner festgestellt werden!");
            stop(false);
            plugin.getBackupManager().allowGameReset(true);
            getGameTimer("break_count").start(1, false);
        }
    }
    
}
