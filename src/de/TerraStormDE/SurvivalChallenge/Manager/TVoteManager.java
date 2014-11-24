/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Manager;

import de.TerraStormDE.SurvivalChallenge.ChallengeModul.TBasicModul;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Enums.TConfigPath;
import de.TerraStormDE.SurvivalChallenge.Util.TGameMap;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

/**
 *
 * @author Hannes
 */
public class TVoteManager {
    
    
    private boolean voting;
    private final SurvivalChallenge plugin;
    private int count;
    private TBasicModul random,win;
    
    public TVoteManager(SurvivalChallenge instance){
        this.voting = false;
        this.plugin = instance;
        count = 0;
    }
    
    public void registerRandomMaps(){
        String s = plugin.convertPath(TConfigPath.SETTINGS_VOTE_MAP_AMOUNT).toString();
        int n;
        if(s.equalsIgnoreCase("auto")){
            n = TGameMap.getGameMaps().length;
        } else {
            n = Integer.valueOf(s);
        }
        
        if(n > TGameMap.getGameMaps().length){
            n = TGameMap.getGameMaps().length;
        }
        
        boolean same = n == TGameMap.getGameMaps().length;
        
        while(n != 0){
            TGameMap map = TGameMap.getRandomMap();
            if(same){
                map.setVoteMap(true);
                n--;
            } else {
                if(!map.isVoteMap()){
                    map.setVoteMap(true);
                    n--;
                }
            }
        }
    }
    public void registerRandomModuls(){
        String s = plugin.convertPath(TConfigPath.SETTINGS_VOTE_MODUL_AMOUNT).toString();
        int n;
        if(s.equalsIgnoreCase("auto")){
            n = TBasicModul.getChallenges().length;
        } else {
            n = Integer.valueOf(s);
        }
        
        if(n > TBasicModul.getChallenges().length){
            n = TBasicModul.getChallenges().length;
        }
        while(n != 0){
            TBasicModul modul = TBasicModul.getRandomModul();
            if(!modul.isVoteModul()){
                modul.setVoteModul(true);
                n--;
            }
        }
    }
    
    public void showBoard(){
        Scoreboard sb = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective ob = sb.getObjective("vote");
        if(ob == null){
            ob = sb.registerNewObjective("vote", "dummy");
        }
        
        if(count <= (int) plugin.convertPath(TConfigPath.VOTE_BOARD_SWITCH_DELAY)/2){
            String name = plugin.convertPath(TConfigPath.MAP_VOTE_BOARD_NAME).toString().replace("&", "§");
            ob.setDisplayName(name);
            ob.setDisplaySlot(DisplaySlot.SIDEBAR);

            for(TGameMap map : TGameMap.getGameMaps()){
                if(!map.isVoteMap()){
                    continue;
                }
                String row = (String)plugin.convertPath(TConfigPath.MAP_VOTE_BOARD_ROW);
                row = row.replace("&", "§");
                row = row.replace("%id%", Integer.toString(map.getID()));
                row = row.replace("%map%", ChatColor.stripColor(map.getDisplayName().replace("&", "§")));

                ob.getScore(row).setScore(map.getVotes());
            }
        } else {
            if((boolean)plugin.convertPath(TConfigPath.SETTINGS_ENABLE_MODULS)){
                String name = plugin.convertPath(TConfigPath.SC_VOTE_BOARD_NAME).toString().replace("&", "§");
            
                ob.setDisplayName(name);
                ob.setDisplaySlot(DisplaySlot.SIDEBAR);

                for(TBasicModul challange : TBasicModul.getChallenges()){
                    if(!challange.isVoteModul()){
                        continue;
                    }
                    String row = (String)plugin.convertPath(TConfigPath.SC_VOTE_BOARD_ROW);
                    row = row.replace("&", "§");
                    row = row.replace("%id%", Integer.toString(challange.getID()));
                    row = row.replace("%challenge%", ChatColor.stripColor(challange.getDisplayName().replace("&", "§")));

                    ob.getScore(row).setScore(challange.getVotes());
                }
            } else {
                this.count = 0;
            }
        }
        this.count++;
        if(count == (int) plugin.convertPath(TConfigPath.VOTE_BOARD_SWITCH_DELAY)*2){
            count = 0;
        }
        
        for(Player all : Bukkit.getOnlinePlayers()){
            all.setScoreboard(sb);
        }
    }
    public void setVote(boolean can_vote){
        this.voting = can_vote;
    }
    public boolean canVote(){
        return voting;
    }
    public TGameMap getWinnedMap(){
        TGameMap win = null;
        for(TGameMap map : TGameMap.getGameMaps()){
            if(win == null){
                win = map;
                continue;
            }
            if(win.getVotes() < map.getVotes()){
                win = map;
            }
        }
        return win;
    }
    public TBasicModul getWinnedChallenge(){
        if((boolean)plugin.convertPath(TConfigPath.SETTINGS_VOTE_MODUL_RANDOM)){
            if(this.random != null){
                return this.random;
            } else {
                this.random = TBasicModul.getChallenges()[(int)Math.random() * TBasicModul.getChallenges().length];
                return this.random;
            }
        }
        if(!(boolean) plugin.convertPath(TConfigPath.SETTINGS_ENABLE_MODULS)){
            return null;
        }
        
        if(this.win != null){
            return this.win;
        }
        TBasicModul win = null;
        for(TBasicModul challenge : TBasicModul.getChallenges()){
            if(win == null){
                win = challenge;
                continue;
            }
            if(win.getVotes() < challenge.getVotes()){
                win = challenge;
            }
        }
        
        this.win = win;
        return win;
    }
    public void activateWinnedChallenge(){
        TBasicModul c = getWinnedChallenge();
        if(c == null){
            return;
        }
        
        for(TBasicModul challenge : TBasicModul.getChallenges()){
            if(challenge.equals(c)){
                c.activateListener();
                continue;
            }
            Bukkit.getPluginManager().disablePlugin(challenge.getPlugin());
        }
    }
    public void clearBoard(){
        for(Player all : Bukkit.getOnlinePlayers()){
            all.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
        }
    }
}
