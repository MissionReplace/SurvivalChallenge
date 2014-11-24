/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Util;

import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;

/**
 *
 * @author Hannes
 */
public class TBungeeUtil
{
    
    public static void sendPlayer(Player p,String server){
        try
        {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream stream = new DataOutputStream(b);
            
            stream.writeUTF("Connect");
            stream.writeUTF(server);
            
            p.sendPluginMessage(SurvivalChallenge.getInstance(), server,b.toByteArray());
        } catch (IOException ex)
        {
            Logger.getLogger(TBungeeUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
