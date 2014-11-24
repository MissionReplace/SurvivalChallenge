/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Util;

import de.TerraStormDE.SurvivalChallenge.Enums.TConfigPath;
import de.TerraStormDE.SurvivalChallenge.Enums.TModulStatsType;
import de.TerraStormDE.SurvivalChallenge.Enums.TStatsType;
import de.TerraStormDE.SurvivalChallenge.Manager.TDatabaseConnection;
import de.TerraStormDE.SurvivalChallenge.Manager.TStatsConnection;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;

/**
 *
 * @author Hannes
 */
public class TGamePlayer {
    
    private int kills,deaths,points;
    private UUID uuid;
    private String name;
    private boolean spectator;
    private List<TObjectRestore> modul_stats;
    
    private static List<TGamePlayer> players = new ArrayList<TGamePlayer>();
    
    public TGamePlayer(String name,UUID player,boolean spectator)
    {
        this.name = name;
        this.uuid = player;
        this.spectator = spectator;
        if(!this.spectator){
            fetchData();
        }
        
        players.add(this);
    }
    
    public int getKills()
    {
        return kills;
    }
    public int getDeaths()
    {
        return deaths;
    }
    public int getPoints()
    {
        return points;
    }
    public String getName()
    {
        return name;
    }
    public UUID getUUID()
    {
        return uuid;
    }
    public List<TObjectRestore> getModulStats(){
        return modul_stats;
    }
    public int calculateModulStats(TModulStatsType type){
        int i = 0;
        for(TObjectRestore r : getModulStats()){
            i+= (int)r.getObject(type.getRestoreName());
        }
        return i;
    }
    public boolean isSpectator(){
        return spectator;
    }
    public void setSpectator(boolean b){
        this.spectator = b;
    }
    
    public void addKill()
    {
        kills++;
    }
    public void insertModulStats(String modul_name,int games,int wins){
        if(modul_name == null){
            return;
        }
        if(this.modul_stats != null){
            for(TObjectRestore restore : modul_stats){
                if(restore.getObject("name").toString().equalsIgnoreCase(modul_name)){
                    
                    //Bukkit.broadcastMessage("Found and edit [" + modul_name + ";" + this.name + "]");
                    
                    restore.addObject("wins", (int)restore.getObject("wins") + wins);
                    restore.addObject("games", (int) restore.getObject("games") + games);
                    
                    Bukkit.broadcastMessage("size: " + restore.getObjects().size());
                    return;
                }
            }
            
            //Bukkit.broadcastMessage("Not found and create [" + modul_name + ";" + this.name + "]");
            TObjectRestore r = new TObjectRestore();
            r.addObject("name", modul_name);
            r.addObject("wins", wins);
            r.addObject("games", games);
            this.modul_stats.add(r);
            
        } else {
            this.modul_stats = new ArrayList<>();
            TObjectRestore r = new TObjectRestore();
            r.addObject("name", modul_name);
            r.addObject("wins", wins);
            r.addObject("games", games);
            
            this.modul_stats.add(r);
        }
    }
    public void addPoints(int points)
    {
        this.points+= points;
    }
    public void addDeath()
    {
        deaths++;
    }
    
    
    public void fetchData()
    {
        SurvivalChallenge c = SurvivalChallenge.getInstance();
        TStatsConnection stats = c.getStatsConnection();
        stats.createStatsSelection(uuid);
        if(c.getConnection().hasConnection()){
            this.kills = stats.getStats(uuid, TStatsType.KILL);
            this.deaths = stats.getStats(uuid, TStatsType.DEATH);
            this.points = stats.getStats(uuid, TStatsType.POINT);
            this.modul_stats = stats.getPlayerModuls(uuid);
        }
    }
    
    public void saveData()
    {
        TDatabaseConnection con = SurvivalChallenge.getInstance().getConnection();
        con.queryUpdate("UPDATE sc_stats SET kills=" + kills + ", deaths=" + deaths + ", points=" + points + " WHERE player='" + uuid.toString() + "'");
        
        
        if(!(boolean) SurvivalChallenge.getInstance().convertPath(TConfigPath.SETTINGS_ENABLE_MODULS)){
            Bukkit.broadcastMessage("Moduls offline!");
            return;
        }
        if(modul_stats == null){
            Bukkit.broadcastMessage("Modul stats null!");
            return;
        }
        for(TObjectRestore restore : modul_stats){
            
            //Bukkit.broadcastMessage("LOOP [" + restore.getObject("name").toString() + ";" + this.name + "]");
            
            int games = (int)restore.getObject("games");
            int wins = (int)restore.getObject("wins");
            String name = restore.getObject("name").toString();
            if(SurvivalChallenge.getInstance().getStatsConnection().hasPlayedModul(uuid, name)){
                con.queryUpdate("UPDATE modul_stats SET wins=" + wins + ", games=" + games + " WHERE player='" + uuid.toString() + "' AND modul='" + name + "'");
            } else {
                con.queryUpdate("INSERT INTO modul_stats(player,modul,games,wins) VALUES ('" + uuid.toString() + "','" + name + "'," + games + "," + wins + ")");
            }
        }
    }
    
    
    public static TGamePlayer getGamePlayer(String name){
        for(TGamePlayer p : players){
            if(p.getName().equalsIgnoreCase(name)){
                return p;
            }
        }
        return null;
    }
    public static TGamePlayer getGamePlayer(UUID uuid){
        for(TGamePlayer p : players){
            if(p.getUUID().equals(uuid)){
                return p;
            }
        }
        return null;
    }
    public static TGamePlayer[] getGamePlayers(){
        TGamePlayer[] list = new TGamePlayer[players.size()];
        return players.toArray(list);
    }
    public static int getSpectatorAmount(){
        int i = 0;
        for(TGamePlayer player : players){
            if(player.isSpectator()){
                i++;
            }
        }
        return i;
    }
    public static TGamePlayer getWinnedPlayer(){
        if(Bukkit.getOnlinePlayers().length - getSpectatorAmount() == 1){
            for(TGamePlayer p : players){
                if(!p.isSpectator()){
                    return p;
                }
            }
        }
        return null;
    }
    public static void removeGamePlayer(UUID player,boolean save_data){
        TGamePlayer gp = getGamePlayer(player);
        if(gp != null){
            if(save_data){
                gp.saveData();
            }
            players.remove(gp);
        }
    }
    
}
