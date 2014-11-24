/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Timer;

import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Enums.TConfigPath;
import de.TerraStormDE.SurvivalChallenge.Enums.TGameStatus;
import de.TerraStormDE.SurvivalChallenge.Util.TGamePlayer;
import de.TerraStormDE.SurvivalChallenge.Util.TGameTimer;
import static de.TerraStormDE.SurvivalChallenge.Util.TGameTimer.getGameTimer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 *
 * @author Hannes
 */
public class TInGameTimer extends TGameTimer
{
    private final SurvivalChallenge plugin;
    private int vielfaches = 0;
    private boolean start_dm = false;

    public TInGameTimer(SurvivalChallenge instance,String name, int count)
    {
        super(name, count);
        this.plugin = instance;
    }

    @Override
    public boolean performHeader()
    {
        vielfaches = 10;
        plugin.getGameManager().setCurrentGameStatus(TGameStatus.IN_GAME);
        
        return true;
    }

    @Override
    public void performBody()
    {
        int count = getCount();
        if(Bukkit.getOnlinePlayers().length - TGamePlayer.getSpectatorAmount() == 1){
            
            TGamePlayer win = TGamePlayer.getWinnedPlayer();
            win.addPoints(20);
            win.insertModulStats(plugin.getVoteManager().getWinnedChallenge().getName(), 1, 1);
            
            plugin.getBackupManager().allowGameReset(true);
            Bukkit.broadcastMessage(plugin.getPrefix() + "§e" + win.getName() + "§7 hat das Spiel gewonnen!");
            stop(false);
            getGameTimer("break_count").start(1, false);
            
            return;
        }
        if(count == 60*vielfaches && vielfaches > 0){
            Bukkit.broadcastMessage(plugin.getPrefix() + "Das Deathmatch startet in §e" + (count/60) + " §7Minute(n)!");
            vielfaches--;
        }
        if(Bukkit.getOnlinePlayers().length - TGamePlayer.getSpectatorAmount() <= (int) plugin.convertPath(TConfigPath.SETTINGS_DEATHMATCH_START_AMOUNT)){
            if(!start_dm){
                setCount(60);
                start_dm = true;
                Bukkit.broadcastMessage(plugin.getPrefix() + "Das Deathmatch startet in 60 Sekunden!");
            }
        }
        
        if(count <= 15 && count > 0){
            Bukkit.broadcastMessage(plugin.getPrefix() + plugin.convertPath(TConfigPath.MESSAGE_DEATHMATCH_COUNT).toString().replace("&", "§").replace("%count%", Integer.toString(count)));
        }
        if(count == 0){
            stop(false);
            getGameTimer("dm_grace").start(1, false);
        }
    }
    
}
