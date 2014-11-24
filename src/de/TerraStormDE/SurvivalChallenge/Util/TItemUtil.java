/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Hannes
 */
public class TItemUtil
{
    
    public static ItemStack toItemStack(String s){
        ItemStack i = null;
        if(s.contains(":")){
            String[] values = s.split(":");
            int amount = 1;
            if(values[1].contains(",")){
                values[1] = values[1].replace(" ", "");
                amount = Integer.valueOf(values[1].split(",")[1]);
            }
            
            i = new ItemStack(Material.getMaterial(Integer.valueOf(values[0])),amount,Short.valueOf(values[1]));
        } else {
            int amount = 1;
            Material m = null;
            
            if(s.contains(",")){
                amount = Integer.valueOf(s.split(",")[1]);
                m = Material.getMaterial(Integer.valueOf(s.split(",")[0]));
            } else {
                m = Material.getMaterial(Integer.valueOf(s));
            }
            i = new ItemStack(m,amount);
            
        }
        return i;
    }
    
}
