/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Manager;

import de.TerraStormDE.SurvivalChallenge.ChallengeModul.TBasicModul;
import de.TerraStormDE.SurvivalChallenge.Enums.TModulStatsType;
import de.TerraStormDE.SurvivalChallenge.Enums.TStatsType;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.UserDatabase.UserAPI;
import de.TerraStormDE.SurvivalChallenge.UserDatabase.UserProfile;
import de.TerraStormDE.SurvivalChallenge.Util.TGamePlayer;
import de.TerraStormDE.SurvivalChallenge.Util.TObjectRestore;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;

/**
 *
 * @author Hannes
 */
public class TStatsConnection
{
    private final SurvivalChallenge plugin;
    
    public TStatsConnection(SurvivalChallenge instance){
        this.plugin = instance;
        if(plugin.getConnection().hasConnection()){
            plugin.getConnection().queryUpdate("CREATE TABLE IF NOT EXISTS sc_stats(player VARCHAR(70),kills INT(5),deaths INT(5),points INT(10))");
            plugin.getConnection().queryUpdate("CREATE TABLE IF NOT EXISTS modul_stats(player VARCHAR(70),modul VARCHAR(20),games INT(10),wins INT(10))");
        }
    }
    
    public int getStats(UUID player,TStatsType type){
        int i = 0;
        if(plugin.getConnection().hasConnection()){
            try {
                Connection con = plugin.getConnection().getConnection();
                PreparedStatement st = null;
                ResultSet rs = null;
                
                st = con.prepareStatement("SELECT * FROM sc_stats WHERE player='" + player.toString() + "'");
                rs = st.executeQuery();
                rs.last();
                if(rs.getRow() != 0){
                    rs.first();
                    i = rs.getInt(type.getTableRow());
                }
            } catch (SQLException ex) {
                Logger.getLogger(TStatsConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return i;
    }
    public boolean hasStatsSelection(UUID player){
        if(plugin.getConnection().hasConnection()){
            try {
                Connection con = plugin.getConnection().getConnection();
                PreparedStatement st = null;
                ResultSet rs = null;
                
                st = con.prepareStatement("SELECT * FROM sc_stats WHERE player='" + player.toString() + "'");
                rs = st.executeQuery();
                rs.last();
                if(rs.getRow() != 0){
                    return true;
                }
            } catch (SQLException ex) {
                Logger.getLogger(TStatsConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return false;
    }
    public void createStatsSelection(UUID player){
        if(!hasStatsSelection(player)){
            plugin.getConnection().queryUpdate("INSERT INTO sc_stats(player,kills,deaths,points) VALUES ('" + player.toString() + "',0,0,0)");
        }
    }
    public boolean hasPlayedModul(UUID player,String m_name){
        if(plugin.getConnection().hasConnection()){
            try {
                Connection con = plugin.getConnection().getConnection();
                PreparedStatement st = null;
                ResultSet rs = null;
                
                st = con.prepareStatement("SELECT * FROM modul_stats WHERE player='" + player.toString() + "' AND modul='" + m_name + "'");
                rs = st.executeQuery();
                rs.last();
                if(rs.getRow() != 0){
                    return true;
                }
            } catch (SQLException ex) {
                return false;
            }
        }
        return false;
    }
    
    public List<TObjectRestore> getPlayerModuls(UUID player){
        List<TObjectRestore> restore = new ArrayList<>();
        
        if(plugin.getConnection().hasConnection()){
            try {
                Connection con = plugin.getConnection().getConnection();
                PreparedStatement st = null;
                ResultSet rs = null;
                
                st = con.prepareStatement("SELECT * FROM modul_stats WHERE player='" + player.toString() + "'");
                rs = st.executeQuery();
                while(rs.next()){
                    TObjectRestore ob = new TObjectRestore();
                    
                    ob.addObject("name", rs.getString("modul"));
                    ob.addObject("games", rs.getInt("games"));
                    ob.addObject("wins", rs.getInt("wins"));
                    restore.add(ob);
                    
                    //Bukkit.broadcastMessage("Fetch Data [" + rs.getString("modul") + ";" + plugin.getUserAPI().getUser(player).getName() + "]");
                }
            } catch (SQLException ex) {
                Logger.getLogger(TStatsConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return restore;
    }
    
    public List<UserProfile> getTopPlayer(TStatsType type,int amount){
        List<UserProfile> list = new ArrayList<>();
        if(plugin.getConnection().hasConnection()){
            try {
                Connection con = plugin.getConnection().getConnection();
                PreparedStatement st = null;
                ResultSet rs = null;
                
                if(type == TStatsType.GAME || type == TStatsType.WIN){
                    st = con.prepareStatement("SELECT * FROM modul_stats ORDER BY " + type.getTableRow() + " DESC LIMIT " + amount);
                } else {
                    st = con.prepareStatement("SELECT * FROM sc_stats ORDER BY " + type.getTableRow() + " DESC LIMIT " + amount);
                }
                
                rs = st.executeQuery();
                while(rs.next()){
                    list.add(plugin.getUserAPI().getUser(UUID.fromString(rs.getString("player"))));
                }
            } catch (SQLException ex) {
                Logger.getLogger(TStatsConnection.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return list;
    }
    
    /*private int[] performTopSort(TStatsType type){
        int[] basic = new int[TGamePlayer.getGamePlayers().length];
        
        int b = 0;
        for(TGamePlayer player : TGamePlayer.getGamePlayers()){
            switch(type){
                case DEATH:
                    basic[b] = player.getDeaths();
                    break;
                case POINT:
                    basic[b] = player.getPoints();
                    break;
                case KILL:
                    basic[b] = player.getKills();
                    break;
                case GAME:
                    basic[b] = player.calculateModulStats(TModulStatsType.GAME);
                    break;
                case WIN:
                    basic[b] = player.calculateModulStats(TModulStatsType.WIN);
                    break;
                default:
                    basic[b] = 0;
                    break;
                  
            }
            b++;
        }
        
        int temp = 0;
        for(int run = 0; run < basic.length;run++){
            for(int pos = 0; pos < basic.length-1;pos++){
                if(basic[pos] > basic[pos+1]){
                    temp = basic[pos];
                    basic[pos] = basic[pos+1];
                    basic[pos+1] = temp;
                }
            }
        }
        return basic;
    }*/
    
}
