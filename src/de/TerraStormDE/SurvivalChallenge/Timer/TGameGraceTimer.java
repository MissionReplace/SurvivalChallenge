/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Timer;

import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Enums.TConfigPath;
import de.TerraStormDE.SurvivalChallenge.Enums.TGameStatus;
import de.TerraStormDE.SurvivalChallenge.Util.TGameTimer;
import org.bukkit.Bukkit;

/**
 *
 * @author Hannes
 */
public class TGameGraceTimer extends TGameTimer
{
    private final SurvivalChallenge plugin;

    public TGameGraceTimer(SurvivalChallenge instance,String name, int count)
    {
        super(name, count);
        this.plugin = instance;
    }

    @Override
    public boolean performHeader()
    {
        plugin.getGameManager().setCurrentGameStatus(TGameStatus.GAME_GRACE_PERIOD);
        return true;
    }

    @Override
    public void performBody()
    {
        
        if(Bukkit.getOnlinePlayers().length < (int) plugin.convertPath(TConfigPath.SETTINGS_MIN_PLAYER)){
            Bukkit.broadcastMessage(plugin.getPrefix() + "Es sind nicht mehr genügend Spieler online, das Spiel wird abgebrochen!");
            getGameTimer("break_count").start(0, false);
            plugin.getGameManager().setCurrentGameStatus(TGameStatus.END);
            stop(false);
        }
        if(getCount() == 0){
            Bukkit.broadcastMessage(plugin.getPrefix() + "Die Friedensphase ist zuende, jetzt kann gekämpft werden!");
            plugin.getGameManager().setCurrentGameStatus(TGameStatus.IN_GAME);
            stop(false);
            getGameTimer("game_count").start(1, false);
        }
        if(getCount() == 50 || getCount() == 40 || getCount() == 30 || getCount() == 20 || getCount() <= 10 && getCount() > 0){
            Bukkit.broadcastMessage(plugin.getPrefix() + "Die Friedensphase endet in §e" + getCount() + " §7Sekunden!");
        }
    }
    
}
