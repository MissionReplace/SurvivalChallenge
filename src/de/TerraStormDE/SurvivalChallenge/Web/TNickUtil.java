/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Web;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.entity.Player;
import org.kitteh.tag.TagAPI;

/**
 *
 * @author Hannes
 */
public class TNickUtil
{
    
    private static HashMap<String,String> nicks = new HashMap<String, String>();
    
    public static List<String> getNicks(){
        try
        {
            return TWebUtil.getContent("http://getshock.net/nick.php?method=list");
        } catch (IOException ex)
        {
            Logger.getLogger(TNickUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }
    public static void addNick(String name){
        try
        {
            URL url = new URL("http://getshock.net/nick.php?method=add&name=" + name);
            url.openStream();
        } catch (MalformedURLException ex)
        {
            Logger.getLogger(TNickUtil.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex)
        {
            Logger.getLogger(TNickUtil.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    public static String getRandomNick(){
        List<String> n = getNicks();
        return n.get(new Random().nextInt(n.size())).replace(" ", "");
    }
    public static void setNick(Player p,String nick){
        nicks.put(p.getName(), nick);
        TagAPI.refreshPlayer(p);
    }
    public static String getNick(String playername){
        return nicks.get(playername);
    }
    public static HashMap<String,String> getNickedPlayers(){
        return nicks;
    }
}
