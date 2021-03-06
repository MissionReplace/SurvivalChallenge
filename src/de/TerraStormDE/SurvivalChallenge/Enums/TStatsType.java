package de.TerraStormDE.SurvivalChallenge.Enums;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author Hannes
 */
public enum TStatsType
{
    
    KILL("kills"),
    DEATH("deaths"),
    POINT("points"),
    GAME("games"),
    WIN("wins");
    
    private String row;
    private TStatsType(String row){
        this.row = row;
    }
    
    public String getTableRow(){
        return row;
    }
    public static TStatsType getBy(String name){
        
        for(TStatsType type : values()){
            String t = type.toString().toLowerCase();
            if((t + "s").equalsIgnoreCase(name) || t.equalsIgnoreCase(name)){
                return type;
            }
        }
        return KILL;
    }
    
}
