/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Manager;

import de.TerraStormDE.SurvivalChallenge.Ranking.TRankingHead;
import de.TerraStormDE.SurvivalChallenge.Ranking.TRankingObject;
import de.TerraStormDE.SurvivalChallenge.Ranking.TRankingSign;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

/**
 *
 * @author Hannes
 */
public class TRankingObjectManager
{
    
    
    
    public static enum TRankingObjectType {
        
        RANKING_SIGN,
        RANKING_HEAD;
        
    }

    
    private File file = null;
    private final SurvivalChallenge plugin;
    
    public TRankingObjectManager(SurvivalChallenge instance){
        this.plugin = instance;
        this.file = new File(plugin.getDataFolder(),"heads.yml");
        if(!file.exists()){
            try
            {
                file.createNewFile();
            } catch (IOException ex)
            {
                Logger.getLogger(TRankingObjectManager.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    public void loadAll(){
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        List<String> heads;
        
        if((heads = cfg.getStringList("objects")) == null){
            return;
        }
        for(String s : heads){
            Location loc = toLocation(s);
            TRankingObjectType type = toObjectType(s);
            int rank = toRankingPos(s);
            
            if(type == TRankingObjectType.RANKING_HEAD){
                new TRankingHead(loc, rank);
            } else {
                new TRankingSign(loc, rank);
            }
        }
        
        /*TRankingSign.updateAll(Arrays.asList((TRankingObject[])TRankingSign.getRankingSigns()));
        TRankingHead.updateAll(Arrays.asList((TRankingObject[]) TRankingHead.getRankingHeads()));*/
        List<TRankingObject> h = new ArrayList<>();
        List<TRankingObject> h2 = new ArrayList<>();
        
        for(TRankingHead trh : TRankingHead.getRankingHeads()){
            h.add(trh);
        }
        for(TRankingSign trs : TRankingSign.getRankingSigns()){
            h2.add(trs);
        }
        
        TRankingObject.updateAll(h);
        TRankingObject.updateAll(h2);
    }
    
    // World,x,y,z
    public String toRankingString(Location loc,int ranking,TRankingObjectType type){
        return loc.getWorld().getName() + ";" + loc.getBlockX() + ";" + loc.getBlockY() + ";" + loc.getBlockZ() + ";" + ranking + ";" + type.toString();
    }
    public Location toLocation(String input){
        String[] values = input.split(";");
        String world = values[0];
        double x = Double.valueOf(values[1]);
        double y = Double.valueOf(values[2]);
        double z = Double.valueOf(values[3]);
        return new Location(Bukkit.getWorld(world), x, y, z);
    }
    public int toRankingPos(String input){
        return Integer.valueOf(input.split(";")[4]);
    }
    public TRankingObjectType toObjectType(String input){
        return TRankingObjectType.valueOf(input.split(";")[5]);
    }
    /*public boolean existsRankingObject(int rank,TRankingObjectType type){
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        List<String> list = cfg.getStringList("objects");
        if(list == null){
            return false;
        }
        for(String s : list){
            if(toRankingPos(s) == rank && toObjectType(s) == type){
                return true;
            }
        }
        return false;
    }*/
    public void removeRankingObjects(Location loc){
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        List<String> list = cfg.getStringList("objects");
        if(list == null){
            return;
        }
        
        List<String> ordinal = new ArrayList<>();
        for(String s : list){
            if(!toLocation(s).equals(loc)){
                ordinal.add(s);
            }
        }
        cfg.set("objects", ordinal);
        try
        {
            cfg.save(file);
        } catch (IOException ex)
        {
            Logger.getLogger(TRankingObjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public void createRankingObject(Location loc,int ranking_pos,TRankingObjectType type){
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        List<String> list;
        if((list = cfg.getStringList("objects")) == null){
            list = new ArrayList<>();
        }
        removeRankingObjects(loc);
        list.add(toRankingString(loc, ranking_pos, type));
        if(type == TRankingObjectType.RANKING_HEAD){
            //TRankingHead head = TRankingHead.getbyRank(ranking_pos);
            new TRankingHead(loc, ranking_pos);
        } else if(type == TRankingObjectType.RANKING_SIGN){
            new TRankingSign(loc, ranking_pos);
        }
        
        cfg.set("objects", list);
        try
        {
            cfg.save(file);
        } catch (IOException ex)
        {
            Logger.getLogger(TRankingObjectManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
