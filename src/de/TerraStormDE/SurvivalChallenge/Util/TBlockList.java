/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Util;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.Material;

/**
 *
 * @author Hannes
 */
public class TBlockList
{
    
    private List<Material> list;
    
    public TBlockList(){
        list = new ArrayList<>();
    }
    
    public void addBlock(Material m){
        if(m.isBlock()){
            list.add(m);
        }
    }
    public boolean containsBlock(Material m){
        return list.contains(m);
    }
    public void removeBlock(Material m){
        if(containsBlock(m)){
            list.remove(m);
        }
    }
    public Material[] getBlocks(){
        Material[] list = new Material[this.list.size()];
        return this.list.toArray(list);
    }
}
