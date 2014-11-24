/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 *
 * @author Hannes
 */
public class TSpawnUtil
{
    
    public static String compileSpawn(Location loc){
        return loc.getWorld().getName() + ";" + loc.getX() + ";" + loc.getY() + ";" + loc.getZ() + ";" + loc.getYaw() + ";" + loc.getPitch();
    }
    public static Location decompileSpawn(String s){
        String[] values = s.split(";");
        
        String world = values[0];
        double x = Double.valueOf(values[1]);
        double y = Double.valueOf(values[2]);
        double z = Double.valueOf(values[3]);
        float yaw = Float.valueOf(values[4]);
        float pitch = Float.valueOf(values[5]);
        Location loc = new Location(Bukkit.getWorld(world), x, y, z);
        loc.setYaw(yaw);
        loc.setPitch(pitch);
        return loc;
    }
}
