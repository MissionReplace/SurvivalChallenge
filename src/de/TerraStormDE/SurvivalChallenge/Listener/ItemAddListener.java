/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Listener;

import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Hannes
 */
public class ItemAddListener implements Listener
{
    
    private final SurvivalChallenge plugin;
    private static List<String> modus = new ArrayList<String>();
    
    public ItemAddListener(SurvivalChallenge instance){
        this.plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void interact(PlayerInteractEvent e) throws IOException{
        if(e.getAction().toString().contains("RIGHT")){
            if(e.getPlayer().getItemInHand() != null){
                if(modus.contains(e.getPlayer().getName())){
                    File f = new File(plugin.getDataFolder(),"items.yml");
                    FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                    if(cfg.getConfigurationSection("items") == null){
                       cfg.createSection("items");
                    }
                    
                    for(String s : cfg.getConfigurationSection("items").getKeys(false)){
                        if(cfg.getItemStack("items." + s).equals(e.getPlayer().getItemInHand())){
                            cfg.set("items." + s, null);
                            cfg.save(f);
                            e.getPlayer().sendMessage(plugin.getPrefix() + "Dieses Item war bereits vorhanden, es wurde entfernt!");
                            return;
                        }
                    }
                    cfg.set("items." + (cfg.getConfigurationSection("items").getKeys(false).size()+1), e.getPlayer().getItemInHand());
                    cfg.save(f);
                    e.getPlayer().sendMessage(plugin.getPrefix() + "Item wurde hinzugefügt!");
                }
            }
        }
    }
    public static List<String> getPlayers(){
        return modus;
    }
}
