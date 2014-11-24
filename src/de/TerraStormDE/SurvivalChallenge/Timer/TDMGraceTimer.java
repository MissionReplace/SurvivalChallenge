/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Timer;

import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Util.TGamePlayer;
import de.TerraStormDE.SurvivalChallenge.Enums.TGameStatus;
import de.TerraStormDE.SurvivalChallenge.Util.TGameTimer;
import org.bukkit.Bukkit;

/**
 *
 * @author Hannes
 */
public class TDMGraceTimer extends TGameTimer
{
    private final SurvivalChallenge plugin;

    public TDMGraceTimer(SurvivalChallenge instance,String name, int count)
    {
        super(name, count);
        this.plugin = instance;
    }

    @Override
    public boolean performHeader()
    {
        plugin.getGameManager().setCurrentGameStatus(TGameStatus.DEATH_MATCH_GRACE_PERIOD);
        plugin.getVoteManager().getWinnedMap().getDeathmatchArena().teleportAll();
        return true;
    }

    @Override
    public void performBody()
    {
        if(Bukkit.getOnlinePlayers().length - TGamePlayer.getSpectatorAmount() == 1){
            Bukkit.broadcastMessage(plugin.getPrefix() + "Es sind nicht mehr genügend Spieler online,um das Deathmatch zu beginnen.");
            stop(false);
            plugin.getGameManager().setCurrentGameStatus(TGameStatus.END);
            getGameTimer("break_count").start(1, false);
            plugin.getBackupManager().allowGameReset(true);
            return;
        }
        if(getCount() <= 10 && getCount() > 0){
            Bukkit.broadcastMessage(plugin.getPrefix() + "Die Friedensphase endet in §e" + getCount() + " §7Sekunden!");
        }
        if(getCount() == 0){
            Bukkit.broadcastMessage(plugin.getPrefix() + "Ihr könnt nun §ekämpfen!");
            plugin.getGameManager().setCurrentGameStatus(TGameStatus.DEATH_MATCH);
            stop(false);
            getGameTimer("dm_count").start(1, false);
        }
    }
    
}
