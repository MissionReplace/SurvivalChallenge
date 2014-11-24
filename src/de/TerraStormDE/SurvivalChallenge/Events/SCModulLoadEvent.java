/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Events;

import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Hannes
 */
public class SCModulLoadEvent extends Event implements Cancellable
{
    
    public String modul_name;
    private boolean c;
    private static final HandlerList handlers = new HandlerList();

    public SCModulLoadEvent(String modul_name){
        this.modul_name = modul_name;
        this.c = false;
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
    
    
    
}
