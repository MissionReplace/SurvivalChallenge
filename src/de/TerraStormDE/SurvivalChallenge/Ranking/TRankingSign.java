/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Ranking;

import de.TerraStormDE.SurvivalChallenge.Enums.TConfigPath;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Util.TObjectRestore;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Sign;

/**
 *
 * @author Hannes
 */
public class TRankingSign extends TRankingObject
{
    
    private static List<TRankingSign> signs = new ArrayList<>();
    private static List<String> lines = new ArrayList<String>();
    
    public TRankingSign(Location loc, int ranking_pos)
    {
        super(loc, ranking_pos);
        signs.add(this);
        
        setLines((List<String>)SurvivalChallenge.getInstance().convertPath(TConfigPath.RANKING_SINGS_LINES));
    }

    @Override
    public void update(TObjectRestore restore)
    {
        Location loc = getLocation();
        if(loc.getBlock().getState() instanceof Sign){
            Sign s = (Sign) loc.getBlock().getState();
            s.setLine(0, SurvivalChallenge.getInstance().getUserAPI().getUser(UUID.fromString(restore.getObject("uuid").toString())).getName());
            for(int i = 0; i < lines.size();i++){
                String l = replaceLine(restore, lines.get(i));
                s.setLine(i, l);
                s.update(true);
            }
        } else {
            loc.getBlock().setType(Material.SIGN_POST);
            update(restore);
        }
    }
    
    private String replaceLine(TObjectRestore restore,String line){
        line = line.replace("%kills%",Integer.toString((int)restore.getObject("kills")));
        line = line.replace("%deaths%", Integer.toString((int) restore.getObject("deaths")));
        line = line.replace("%points%", Integer.toString((int) restore.getObject("points")));
        line = line.replace("&", "§");
        if(line.contains("%name%")){
            line = line.replace("%name%", restore.getObject("name").toString());
        }
        
        return line;
    }
    public void remove(){
        signs.remove(this);
        objects.remove(this);
    }
    
    public static void setLines(List<String> lines_){
        if(lines_.size() > 4){
            throw new ArrayIndexOutOfBoundsException("List not valid!");
        }
        lines = lines_;
    }
    public static List<String> getLines(){
        return lines;
    }
    public static TRankingSign[] getRankingSigns(){
        TRankingSign[] list = new TRankingSign[signs.size()];
        return signs.toArray(list);
    }
    
    public static TRankingSign getbyRank(int rank){
        for(TRankingSign sign : signs){
            if(sign.getRankingPos() == rank){
                return sign;
            }
        }
        return null;
    }
}
