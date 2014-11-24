/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Events;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Hannes
 */
public class SCModulInstallEvent extends Event implements Cancellable
{
    
    private boolean c;
    private static final HandlerList handlers = new HandlerList();
    private String modul_name;
    private URL url;
    
    public SCModulInstallEvent(String modul_name){
        try
        {
            this.modul_name = modul_name;
            this.url = new URL("http://getshock.net/sc/moduls/" + modul_name + ".jar");
            this.c = false;
        } catch (MalformedURLException ex)
        {
            Logger.getLogger(SCModulInstallEvent.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    @Override
    public boolean isCancelled()
    {
        return c;
    }

    @Override
    public void setCancelled(boolean c)
    {
        this.c = c;
    }
    
    public static final HandlerList getHandlerList(){
        return handlers;
    }
    
    public String getModulName(){
        return modul_name;
    }
    public URL getDownloadURL(){
        return url;
    }
    
    
    
}
