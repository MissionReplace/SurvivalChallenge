/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Events;

import org.bukkit.World;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *
 * @author Hannes
 */
public class SCCreateBackupEvent extends Event
{
    
    private World backup;
    private static final HandlerList handlers = new HandlerList();
    
    public SCCreateBackupEvent(World backup){
        this.backup = backup;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }
    public static final HandlerList getHandlerList(){
        return handlers;
    }
    public World getBackupWorld(){
        return backup;
    }
    
}
