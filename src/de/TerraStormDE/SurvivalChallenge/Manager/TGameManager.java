/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Manager;

import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Enums.TGameStatus;
import de.TerraStormDE.SurvivalChallenge.Util.TGameTimer;



/**
 *
 * @author Hannes
 */
public class TGameManager {
    
    
    private final SurvivalChallenge plugin;
    private TGameStatus current;
    
    public TGameManager(SurvivalChallenge instance){
        this.plugin = instance;
        current = TGameStatus.BEFORE_LOBBY;
    }
    
    
    
    public void startGameTimer(TGameTimer timer,int between,boolean restart){
        if(!timer.isRunning()){
            timer.start(between, restart);
        }
    }
    public void stopGameTimer(TGameTimer timer,boolean reset_count){
        if(timer.isRunning()){
            timer.stop(reset_count);
        }
    }
    public void setCurrentGameStatus(TGameStatus status){
        this.current = status;
    }
    public TGameStatus getCurrentGameStatus(){
        return current;
    }
}
