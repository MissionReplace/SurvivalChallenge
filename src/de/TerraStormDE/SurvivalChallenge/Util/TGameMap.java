/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Util;

import de.TerraStormDE.SurvivalChallenge.Enums.TConfigPath;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 *
 * @author Hannes
 */
public class TGameMap {
    
    private static ArrayList<TGameMap> maps = new ArrayList<TGameMap>();
    private static List<String> vote_list = new ArrayList<String>();
    
    private int id;
    private World world;
    private String name,display,builder;
    private int votes;
    private List<Location> spawns;
    private TDeathmatchArena dm_arena;
    private ItemStack vote_item;
    private boolean isvotemap;
    private TGameBackup backup;
    
    public TGameMap(World w,String name,List<Location> spawns){
        this.world = w;
        this.name = name;
        this.votes = 0;
        this.id = maps.size()+1;
        if(spawns == null){
            this.spawns = new ArrayList<>();
        } else {
            this.spawns = spawns;
        }
        this.isvotemap = false;
        try
        {
            this.backup = new TGameBackup(this);
        } catch (FileNotFoundException ex)
        {
            ex.printStackTrace();
        }
        
        maps.add(this);
    }
    public void teleportAll(){
        int id = 0;
        for(Player p : Bukkit.getOnlinePlayers()){
            p.teleport(spawns.get(id));
            
            id++;
            if(id >= spawns.size()){
                break;
            }
        }
    }
    public void addSpawn(Location loc){
        if(!spawns.contains(loc)){
            spawns.add(loc);
        }
    }
    public Location[] getSpawns(){
        Location[] list = new Location[spawns.size()];
        return spawns.toArray(list);
    }
    
    public int getID(){
        return id;
    }
    public void setDisplayName(String name){
        this.display = name;
    }
    public void setVoteMap(boolean b){
        this.isvotemap = b;
    }
    public void setVoteItem(ItemStack i){
        this.vote_item = i;
    }
    public void setDeathmatchArena(TDeathmatchArena arena){
        this.dm_arena = arena;
    }
    public void setBuilder(String builder){
        this.builder = builder;
    }
    public void setWorld(World w){
        this.world = w;
    }
    public World getWorld(){
        return this.world;
    }
    
    public void sendReadyMessage(Player p){
        String prefix = SurvivalChallenge.getInstance().getPrefix();
        int min = (int) SurvivalChallenge.getInstance().convertPath(TConfigPath.SETTINGS_MIN_PLAYER);
        boolean ready = true;
        
        if(dm_arena == null || dm_arena.getSpanws().size() < min){
            p.sendMessage(prefix + "Es fehlen §e" + (dm_arena == null ? min : (min - dm_arena.getSpanws().size())) + " §7Deathmatch spawns!");
            ready = false;
        }
        if(spawns.size() < min){
            p.sendMessage(prefix + "Es fehlen §e" + (min - spawns.size() + " §7Arena spawns!"));
            ready = false;
        }
        if(backup == null || !backup.getBackupFile().exists()){
            p.sendMessage(prefix + "GameBackup ist §enicht vorhanden\n" + prefix + " - Benutze: §e/sc cbackup");
            ready = false;
        }
        if(ready){
            p.sendMessage(prefix + "§aDie Map ist vollständig konfiguriert.");
        }
    }
    public boolean isReady(){
        int min = (int) SurvivalChallenge.getInstance().convertPath(TConfigPath.SETTINGS_MIN_PLAYER);
        boolean ready = true;
        if(dm_arena == null || dm_arena.getSpanws().size() < min){
            ready = false;
        }
        if(spawns.size() < min){
            ready = false;
        }
        if(backup == null || !backup.getBackupFile().exists()){
            ready = false;
        }
        if(builder == null || display == null || vote_item == null || world == null){
            ready = false;
        }
        
        return ready;
    }
    
    public void addVote(String p_name){
        if(!vote_list.contains(p_name)){
            vote_list.add(p_name);
            this.votes++;
        }
    }
    public int getVotes(){
        return votes;
    }
    public String getName(){
        return name;
    }
    public boolean isVoteMap(){
        return isvotemap;
    }
    public String getDisplayName(){
        return display;
    }
    public String getBuilder(){
        return builder;
    }
    public TGameBackup getBackup(){
        return backup;
    }
    public TDeathmatchArena getDeathmatchArena(){
        return dm_arena;
    }
    public ItemStack getVoteItem(){
        return vote_item;
    }
    
    public static TGameMap getGameMap(String o_name){
        TGameMap m = null;
        for(TGameMap gm : maps){
            if(gm.getName().equalsIgnoreCase(o_name)){
                m = gm;
                break;
            }
            
        }
        return m;
    }
    public static TGameMap getByDisplayName(String dname,boolean use_color){
        for(TGameMap map : maps){
            String d = map.getDisplayName();
            if(use_color){
                d = d.replace("&", "§");
                dname = dname.replace("&", "§");
            } else {
                d = ChatColor.stripColor(d.replace("&", "§"));
                dname = ChatColor.stripColor(dname.replace("&", "§"));
            }
            if(d.equalsIgnoreCase(dname)){
                Bukkit.broadcastMessage(map.getName() + " | D: " + d + " | Dname: " + dname);
                return map;
            }
        }
        return null;
    }
    public static TGameMap getByWorld(World w){
        for(TGameMap map : maps){
            if(map.getWorld().equals(w)){
                return map;
            }
        }
        return null;
    }
    public void resetVotes(){
        this.votes = 0;
    }
    
    public static TGameMap[] getGameMaps(){
        TGameMap[] list = new TGameMap[maps.size()];
        return maps.toArray(list);
    }
    public static boolean hasAlreadyVoted(String name){
        if(vote_list.contains(name)){
            return true;
        }
        return false;
    }
    public static void resetAllVotes(){
        for(TGameMap map : maps){
            map.setVoteMap(false);
            map.resetVotes();
        }
        vote_list.clear();
    }
    public static void deleteMap(String name){
        if(getGameMap(name) != null){
            maps.remove(getGameMap(name));
        }
    }
    public static TGameMap getRandomMap(){
        return maps.get(new Random().nextInt(maps.size()));
    }
}
