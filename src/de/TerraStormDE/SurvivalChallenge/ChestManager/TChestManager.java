/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.ChestManager;

import de.TerraStormDE.SurvivalChallenge.Enums.TConfigPath;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Hannes
 */
public class TChestManager
{
    
    private List<ItemStack> content;
    private HashMap<Location,Inventory> chests;
    private Random r;
    private final SurvivalChallenge plugin;
    private int task;
    
    public TChestManager(SurvivalChallenge instance){
        this.chests = new HashMap<>();
        this.content = new ArrayList<>();
        r = new Random();
        this.plugin = instance;
    }
    
    public void addContainer(TChestContainer container){
        for(ItemStack i : container.getContainer()){
            content.add(i);
        }
    }
    public Inventory createChest(Location loc){
        if(chests.containsKey(loc)){
            return chests.get(loc);
        }
        Inventory inv = Bukkit.createInventory(null, (int) plugin.convertPath(TConfigPath.CHEST_ROWS)*9, (String) plugin.convertPath(TConfigPath.CHEST_NAME).toString().replace("&", "§"));
        int items = r.nextInt((int)plugin.convertPath(TConfigPath.CHEST_MAX_ITEMS))+1;
        while(items != 0){
            items--;
            int pos = r.nextInt(inv.getSize());
            
            inv.setItem(pos, content.get(r.nextInt(content.size())));
        }
        
        this.chests.put(loc, inv);
        return inv;
    }
    public Inventory getChest(Location loc){
        return chests.get(loc);
    }
    public boolean containsChest(Location loc){
        return chests.containsKey(loc);
    }
    public int getBlockID(){
        String s = plugin.convertPath(TConfigPath.CHEST_BLOCK).toString();
        if(s.contains(":")){
            String[] values = s.split(":");
            return Integer.valueOf(values[0]);
        }
        return Integer.valueOf(s);
    }
    public byte getBlockSubID(){
        String s = plugin.convertPath(TConfigPath.CHEST_BLOCK).toString();
        if(s.contains(":")){
            String[] values = s.split(":");
            return Byte.valueOf(values[1]);
        }
        return 0;
    }
    public void startRefill(final int refill_time){
        task = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable()
        {

            @Override
            public void run()
            {
                if(plugin.getVoteManager().getWinnedMap().getWorld().getTime() >= refill_time){
                    plugin.getVoteManager().getWinnedMap().getWorld().setTime(0);
                    Bukkit.broadcastMessage(plugin.getPrefix() + "Die Kisten wurden wieder aufgefüllt!");
                    chests.clear();
                }
            }
        },20,20);
    }
}
