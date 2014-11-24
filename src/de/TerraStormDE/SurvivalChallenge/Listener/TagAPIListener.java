/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Listener;

import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Web.TNickUtil;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.AsyncPlayerReceiveNameTagEvent;

/**
 *
 * @author Hannes
 */
public class TagAPIListener implements Listener
{
    
    private final SurvivalChallenge plugin;
    
    public TagAPIListener(SurvivalChallenge instance){
        this.plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    
    @EventHandler
    public void refresh(AsyncPlayerReceiveNameTagEvent e){
        String s;
        if((s = TNickUtil.getNick(e.getNamedPlayer().getName())) != null){
            e.setTag(s);
        }
    }
    
}
