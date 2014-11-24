/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.ChestManager;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.inventory.ItemStack;

public class TChestContainer 
{
    
    private List<ItemStack> container;
    
    public TChestContainer(){
        container = new ArrayList<>();
    }
    public TChestContainer(List<ItemStack> container){
        this.container = container;
    }
    
    public void addItemStack(ItemStack i){
        this.container.add(i);
    }
    public void removeItemStack(ItemStack i){
        if(container.contains(i)){
            container.remove(i);
        }
    }
    public List<ItemStack> getContainer(){
        return container;
    }
    
}
