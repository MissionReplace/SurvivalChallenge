/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Timer;

import de.TerraStormDE.SurvivalChallenge.ChallengeModul.TBasicModul;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Enums.TConfigPath;
import de.TerraStormDE.SurvivalChallenge.Util.TGameMap;
import de.TerraStormDE.SurvivalChallenge.Enums.TGameStatus;
import de.TerraStormDE.SurvivalChallenge.Util.TGameTimer;
import org.bukkit.Bukkit;

/**
 *
 * @author Hannes
 */
public class TLobbyTimer extends TGameTimer{
    private final SurvivalChallenge plugin;

    public TLobbyTimer(SurvivalChallenge instance,String name, int count) {
        super(name, count);
        this.plugin = instance;
    }

    @Override
    public boolean performHeader() {
        for(TGameMap map : TGameMap.getGameMaps()){
            if(!map.isReady()){
                return false;
            }
        }
        if(TGameMap.getGameMaps().length == 0 || TBasicModul.getChallenges().length == 0){
            return false;
        }
        if(Bukkit.getOnlinePlayers().length >= (int)plugin.convertPath(TConfigPath.SETTINGS_MIN_PLAYER)){
            if((boolean) plugin.convertPath(TConfigPath.SETTINGS_ENABLE_MODULS)){
                if(TBasicModul.getChallenges().length == 0){
                    Bukkit.broadcastMessage(plugin.getPrefix() + "§eModulList is empty!");
                    return false;
                }
            }
            Bukkit.broadcastMessage(plugin.getPrefix() + plugin.convertPath(TConfigPath.MESSAGE_LOBBY_START).toString().replace("&", "§").replace("%n%", "\n" + plugin.getPrefix()));
            plugin.getGameManager().setCurrentGameStatus(TGameStatus.LOBBY);
            plugin.getVoteManager().setVote(true);
            plugin.getVoteManager().registerRandomMaps();
            plugin.getVoteManager().registerRandomModuls();
            return true;
        }
        
        return false;
    }
    @Override
    public void performBody() {
        int count = getCount();
        
        if(Bukkit.getOnlinePlayers().length < (int) plugin.convertPath(TConfigPath.SETTINGS_MIN_PLAYER)){
            Bukkit.broadcastMessage(plugin.getPrefix() + plugin.convertPath(TConfigPath.MESSAGE_NOT_ENOTH_PLAYER).toString().replace("&", "$").replace("%n%", "\n" + plugin.getPrefix()));
            plugin.getGameManager().setCurrentGameStatus(TGameStatus.BEFORE_LOBBY);
            TGameMap.resetAllVotes();
            TBasicModul.resetAllVotes();
            plugin.getVoteManager().setVote(false);
            stop(true);
            return;
        }
        
        
        if(count == (int) plugin.convertPath(TConfigPath.SETTINGS_VOTE_TO_COUNT)){
            String win = (String)plugin.convertPath(TConfigPath.MESSAGE_VOTE_WIN);
            win = win.replace("&", "§");
            win = win.replace("%n%", "\n" + plugin.getPrefix());
            win = win.replace("%map%", plugin.getVoteManager().getWinnedMap().getDisplayName().replace("&", "§"));
            Bukkit.broadcastMessage(plugin.getPrefix() + win);
            
            if((boolean) plugin.convertPath(TConfigPath.SETTINGS_ENABLE_MODULS)){
                String m = (String) plugin.convertPath(TConfigPath.MESSAGE_MODUL_WIN);
                m = m.replace("&", "§");
                m = m.replace("%modul%", plugin.getVoteManager().getWinnedChallenge().getDisplayName().replace("&", "§"));
                Bukkit.broadcastMessage(plugin.getPrefix() + m);
            }
            
            plugin.getVoteManager().setVote(false);
            plugin.getVoteManager().clearBoard();
        } else {
            if(plugin.getVoteManager().canVote()){
                plugin.getVoteManager().showBoard();
            }
        }
        
        if(Integer.toString(count).contains("0") && count > 0 && count != 10){
            String msg = (String) plugin.convertPath(TConfigPath.MESSAGE_LOBBY_COUNT);
            msg = replaceIdent(msg);
            Bukkit.broadcastMessage(plugin.getPrefix() + msg);
        }
        if(count <= 10 && count > 0){
            String msg = (String) plugin.convertPath(TConfigPath.MESSAGE_LOBBY_COUNT);
            msg = replaceIdent(msg);
            Bukkit.broadcastMessage(plugin.getPrefix() + msg);
        }
        if(count == 0){
            stop(false);
            TGameTimer.getGameTimer("game_spawns_timer").start(1, false);
        }
        
    }
    
    
    private String replaceIdent(String s){
        s = s.replace("%count%", Integer.toString(getCount()));
        s = s.replace("&","§");
        s = s.replace("%n%", "\n" + plugin.getPrefix());
        return s;
    }

    
    
}
