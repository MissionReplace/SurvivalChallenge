/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Util;

import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;


/**
 *
 * @author Hannes
 */
public class TDeathmatchArena
{
    
    private List<Location> spawns;
    private TGameBackup backup;
    private World world;
    private final SurvivalChallenge plugin;
    
    public TDeathmatchArena(SurvivalChallenge instance,World world){
        spawns = new ArrayList<>();
        this.plugin = instance;
        this.world = world;
        
        if(new File(SurvivalChallenge.getInstance().getDataFolder() + "/backups/" + world.getName()).exists()){
            this.backup = new TGameBackup(this);
        } else {
            this.backup = null;
        }
    }
    
    public void addSpawn(Location loc){
        this.spawns.add(loc);
    }
    public List<Location> getSpanws(){
        return spawns;
    }
    public World getWorld(){
        return world;
    }
    public TGameBackup getBackup(){
        return backup;
    }
    public void setBackup(TGameBackup backup){
        this.backup = backup;
    }
    public void teleportAll(){
        int id = 0;
        for(TGamePlayer player : TGamePlayer.getGamePlayers()){
            if(player.isSpectator()){
                continue;
            }
            Bukkit.getPlayer(player.getUUID()).teleport(spawns.get(id));
            id++;
            
            if(id >= spawns.size()){
                for(TGameTimer timer : TGameTimer.getGameTimers()){
                    timer.stop(false);
                }
                Bukkit.broadcastMessage(plugin.getPrefix() + "Es wurde nicht genügend Spawns für das Deathmatch gesetzt!");
                TGameTimer.getGameTimer("break_count").start(1, false);
                break;
            }
        }
    }
    
}
