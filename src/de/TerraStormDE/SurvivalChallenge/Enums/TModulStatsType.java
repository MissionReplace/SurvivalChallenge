/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Enums;

/**
 *
 * @author Hannes
 */
public enum TModulStatsType
{
    
    GAME("games"),
    WIN("wins");
    
    private String row;
    private TModulStatsType(String row){
        this.row = row;
    }
    
    public String getRestoreName(){
        return row;
    }
    
}
