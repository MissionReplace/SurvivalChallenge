/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Manager;

import de.TerraStormDE.SurvivalChallenge.Events.SCCreateBackupEvent;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Util.TGameBackup;
import de.TerraStormDE.SurvivalChallenge.Util.TGameMap;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;

/**
 *
 * @author Hannes
 */
public class TBackupManager
{
    private final SurvivalChallenge plugin;
    private boolean allow;
    
    public TBackupManager(SurvivalChallenge instance){
        this.plugin = instance;
        allow = false;
    }
    
    public boolean unloadWorld(World w){
        Bukkit.unloadWorld(w,true);
        if(Bukkit.getWorld(w.getName()) == null){
            return true;
        }
        return false;
    }
    public void loadWorld(String w){
        Bukkit.createWorld(new WorldCreator(w));
    }
    
    public void createBackup(String mapname){
        SCCreateBackupEvent event = new SCCreateBackupEvent(Bukkit.getWorld(mapname));
        Bukkit.getPluginManager().callEvent(event);
        
        File world = new File(plugin.getDataFolder() + "/backups/" + mapname);
        if(world.exists()){
            deleteDirectoryFiles(world);
        }
        
        /*if(!world.exists()){
            Bukkit.broadcastMessage("YES");
            world.mkdirs();
            return;
        }*/
        try
        {
            copyDir(new File(mapname), world);
        } catch (IOException ex)
        {
            Logger.getLogger(TBackupManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    private void deleteDirectoryFiles(File dir){
        if(!dir.isDirectory()){
            return;
        }
        for(File f : dir.listFiles()){
            if(f.isDirectory()){
                if(f.listFiles().length > 0){
                    deleteDirectoryFiles(f);
                } else {
                    f.delete();
                }
            } else {
                f.delete();
            }
        }
        dir.delete();
    }
    
    public boolean existsBackup(String w){
        return new File(plugin.getDataFolder() + "/backups/" + w).exists();
    }
    public boolean loadBackup(TGameBackup backup){
        try
        {
            if(!backup.getBackupFile().exists()){
                return false;
            }
            if(!unloadWorld(Bukkit.getWorld(backup.getWorldFolder().getName()))){
                Bukkit.broadcastMessage("Error while unloading World! [" + backup.getWorldFolder().getName() + " | Folder]");
                return false;
            }
            deleteDirectoryFiles(backup.getWorldFolder());
            
            copyDir(backup.getBackupFile(),backup.getWorldFolder());
            loadWorld(backup.getWorldFolder().getName());
            return true;
        } catch (IOException ex)
        {
            Logger.getLogger(TBackupManager.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
    public void copyDir(File quelle, File ziel) throws FileNotFoundException, IOException {
		
        if(!ziel.exists()){
            ziel.mkdirs();
        }
        
		File[] files = quelle.listFiles();
		File newFile = null; 
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
					newFile = new File(ziel.getAbsolutePath() + System.getProperty("file.separator") + files[i].getName());
				if (files[i].isDirectory()) {
					copyDir(files[i], newFile);
				}
				else {
					copyFile(files[i], newFile);
				}
			}
		}
	}
	private void copyFile(File file, File ziel) throws FileNotFoundException, IOException {
		
		BufferedInputStream in = new BufferedInputStream(new FileInputStream(file));
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(ziel, true));
		int bytes = 0;
		while ((bytes = in.read()) != -1) {
			out.write(bytes); 
		}
		in.close();
		out.close();
	}
        public void allowGameReset(boolean b){
            this.allow = b;
        }
        public boolean isGameResetAllowed(){
            return allow;
        }
}
