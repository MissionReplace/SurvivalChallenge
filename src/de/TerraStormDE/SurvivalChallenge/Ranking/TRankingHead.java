/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Ranking;

import de.TerraStormDE.SurvivalChallenge.Util.TObjectRestore;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Skull;

/**
 *
 * @author Hannes
 */
public class TRankingHead extends TRankingObject
{

    private static List<TRankingHead> heads = new ArrayList<>();
    
    public TRankingHead(Location loc, int ranking_pos)
    {
        super(loc, ranking_pos);
        heads.add(this);
    }

    @Override
    public void update(TObjectRestore restore)
    {
        Location loc = getLocation();
        
        loc.getBlock().setType(Material.SKULL);
        Skull s = (Skull) loc.getBlock().getState();
        s.setOwner(restore.getObject("name").toString());
        s.update(true);
    }
    public void remove(){
        heads.remove(this);
        objects.remove(this);
    }
    
    public static TRankingHead[] getRankingHeads(){
        TRankingHead[] list = new TRankingHead[heads.size()];
        return heads.toArray(list);
    }
    public static TRankingHead getbyRank(int rank){
        for(TRankingHead head : heads){
            if(head.getRankingPos() == rank){
                return head;
            }
        }
        return null;
    }
}
