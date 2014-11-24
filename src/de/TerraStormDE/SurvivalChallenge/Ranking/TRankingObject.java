/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Ranking;

import de.TerraStormDE.SurvivalChallenge.Enums.TConfigPath;
import de.TerraStormDE.SurvivalChallenge.Enums.TStatsType;
import de.TerraStormDE.SurvivalChallenge.Manager.TStatsConnection;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.UserDatabase.UserProfile;
import de.TerraStormDE.SurvivalChallenge.Util.TObjectRestore;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;

public abstract class TRankingObject
{
    
    private Location loc;
    private int ranking;
    
    protected static List<TRankingObject> objects = new ArrayList<>();
    
    public TRankingObject(Location loc,int ranking_pos){
        this.loc = loc;
        this.ranking = ranking_pos;
        objects.add(this);
    }
    
    public Location getLocation(){
        return loc;
    }
    public int getRankingPos(){
        return ranking;
    }
    public void setLocation(Location loc){
        this.loc.getBlock().setType(Material.AIR);
        this.loc = loc;
    }
    
    
    public abstract void update(TObjectRestore restore);
    
    
    public static void updateAll(List<TRankingObject> objects){
        TStatsConnection stats = SurvivalChallenge.getInstance().getStatsConnection();
        TRankingObject[] list = sortByRank(objects);
        if(list.length == 0){
            return;
        }
        
        String order = SurvivalChallenge.getInstance().convertPath(TConfigPath.RANKING_ORDER_TYPE).toString();
        TStatsType type = null;
                    if(order.equalsIgnoreCase("kills")){
                        type = TStatsType.KILL;
                    } else if(order.equalsIgnoreCase("deaths")){
                        type = TStatsType.DEATH;
                    } else if(order.equalsIgnoreCase("games")){
                        type = TStatsType.GAME;
                    } else if(order.equalsIgnoreCase("wins")){
                        type = TStatsType.WIN;
                    } else if(order.equalsIgnoreCase("points")){
                        type = TStatsType.POINT;
                    } else {
                        type = TStatsType.KILL;
                    }
        
        List<UserProfile> players = stats.getTopPlayer(type, list[0].getRankingPos());
        int current_head = 0;
        int current_sign = 0;
        int use = 0;
        
        for(TRankingObject ob : objects){
            use = (ob instanceof TRankingHead ? current_head : current_sign);
            
            TObjectRestore restore = new TObjectRestore();
            restore.addObject("uuid", players.get(use).getUUID());
            restore.addObject("name", players.get(use).getName());
            restore.addObject("kills", stats.getStats(players.get(use).getUUID(), TStatsType.KILL));
            restore.addObject("deaths", stats.getStats(players.get(use).getUUID(), TStatsType.DEATH));
            restore.addObject("points", stats.getStats(players.get(use).getUUID(), TStatsType.POINT));
            
            
            ob.update(restore);
            if(ob instanceof TRankingHead){
                current_head++;
            }
            if(ob instanceof TRankingSign){
                current_sign++;
            }
        }
    }
    /*public static void updateAll(){
        TStatsConnection stats = SurvivalChallenge.getInstance().getStatsConnection();
        List<UUID> players = stats.getTopPlayer(TStatsType.KILL, heads.size());
        TRankingObject[] sort_list = sortHeads();
        
        int rank_pos = 0;
        
        for(TRankingObject h : sort_list){
            if(h.getLocation().getBlock().getState() instanceof Skull){
                Skull sk = (Skull) h.getLocation().getBlock().getState();
                sk.setSkullType(SkullType.PLAYER);
                sk.setOwner(SurvivalChallenge.getInstance().getUserAPI().getUser(players.get(rank_pos)).getName());
                sk.update(true);
            }
            rank_pos++;
            if(rank_pos >= players.size()){
                break;
            }
        }
        
    }*/
    public static TRankingObject[] sortByRank(List<TRankingObject> objects){
        TRankingObject[] array = new TRankingObject[objects.size()];
        array = objects.toArray(array);
        //Bukkit.broadcastMessage("" + array.length);
        
        TRankingObject temp = null;
        for(int run = 0; run <= array.length;run++){
            for(int pos = 0; pos < array.length-1;pos++){
                if(array[pos].getRankingPos() < array[pos+1].getRankingPos()){
                    
                    Bukkit.broadcastMessage("Sort");
                    temp = array[pos];
                    array[pos] = array[pos+1];
                    array[pos+1] = temp;
                }
            }
        }
        return array;
    }
    public static TRankingObject getbyLocation(Location loc){
        for(TRankingObject object : objects){
            if(object.getLocation().equals(loc)){
                return object;
            }
        }
        return null;
    }
    
}
