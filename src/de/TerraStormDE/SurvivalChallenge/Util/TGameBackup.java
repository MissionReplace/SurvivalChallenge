/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Util;

import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import java.io.File;
import java.io.FileNotFoundException;

/**
 *
 * @author Hannes
 */
public class TGameBackup
{
    
    private File file;
    private File world;
    private TGameMap map;
    private TDeathmatchArena dm;
    
    public TGameBackup(TGameMap map) throws FileNotFoundException{
        File f = new File(SurvivalChallenge.getInstance().getDataFolder() + "/backups/" + map.getWorld().getName());
        if(!f.isDirectory() && f.exists()){
            throw new FileNotFoundException("GameBackup is not valid!");
        }
        this.file = f;
        this.world = new File(f.getName());
        if(!this.world.exists()){
            throw new FileNotFoundException("World Folder not found!");
        }
        this.map = map;
    }
    public TGameBackup(TDeathmatchArena dm){
        this.world = new File(dm.getWorld().getName());
        this.file = new File(SurvivalChallenge.getInstance().getDataFolder() + "/backups/" + dm.getWorld().getName());
        this.dm = dm;
    }
    
    public TGameMap getGameMap(){
        return map;
    }
    public TDeathmatchArena getDeathmatchArena(){
        return dm;
    }
    public File getBackupFile(){
        return file;
    }
    public File getWorldFolder(){
        return world;
    }
    
    @Override
    public boolean equals(Object ob){
        if(!(ob instanceof TGameBackup)){
            return false;
        }
        TGameBackup b = (TGameBackup) ob;
        if(b.getBackupFile().getPath().equalsIgnoreCase(this.file.getPath()) && b.getWorldFolder().getPath().equalsIgnoreCase(world.getPath())){
            return true;
        }
        
        return false;
    }
    
}
