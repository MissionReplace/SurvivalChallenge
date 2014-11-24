/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Web;

import de.TerraStormDE.SurvivalChallenge.ChallengeModul.TBasicModul;
import de.TerraStormDE.SurvivalChallenge.Events.SCModulInstallEvent;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import org.bukkit.Bukkit;

/**
 *
 * @author Hannes
 */
public class TModulDownloadUtil
{
    
    public static List<String> fetchModulList(){
        try
        {
            List<String> list = new ArrayList<>();
            URL url = new URL("http://getshock.net/sc/list.php");
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            String item = null;
            while((item = reader.readLine()) != null){
                System.out.println(item);
                if(!item.isEmpty()){
                    item = item.replace("<br/>", "");
                    item = item.replace("\n", "");
                    
                    list.add(item);
                }
            }
            return list;
            
        } catch (MalformedURLException ex)
        {
            
        } catch (IOException ex)
        {
           
        }
        return null;
    }
    public static boolean installModul(int id){
        boolean success;
        
        List<String> list = fetchModulList();
        if(id >= list.size()){
            success = false;
        } else {
            try
            {
                SCModulInstallEvent event = new SCModulInstallEvent(list.get(id));
                if(event.isCancelled()){
                    return false;
                }
                
                startDownload(list.get(id));
                success = true;
            } catch (IOException ex)
            {
                success = false;
            }
        }
        
        return success;
    }
    
    private static void startDownload(String modul_name) throws IOException{
        for(TBasicModul m : TBasicModul.getChallenges()){
            if(m.getPlugin().getName().equalsIgnoreCase(modul_name)){
                Bukkit.broadcastMessage(SurvivalChallenge.getInstance().getPrefix() + "§cDieses Modul ist bereits installiert!");
                return;
            }
        }
        
        modul_name+= ".jar";
        // Grundwert
        
        System.out.println(modul_name + ".jar");
        URL url = new URL("http://getshock.net/sc/moduls/" + modul_name);
        
        final URLConnection conn = url.openConnection();
        long lenght = getFileSize(conn);
        System.out.println(lenght);
        
        double fortschritt = 0;
        
        final InputStream is = new BufferedInputStream(conn.getInputStream());
        final OutputStream os = new BufferedOutputStream(new FileOutputStream("plugins/" + modul_name));
        byte[] chunk = new byte[1024];
        int chunkSize;
        int steps = 0;
        
        
        while ((chunkSize = is.read(chunk)) != -1) {
            
            fortschritt+= chunkSize;
            
            steps++;
            if(steps == 100){
                steps = 0;
                long prozent = Math.round((fortschritt/lenght)*100);
                
                Bukkit.broadcastMessage(SurvivalChallenge.getInstance().getPrefix() + "[§e" + modul_name + "§7] Fortschritt: §e" + prozent + "%");
            }
            
            os.write(chunk, 0, chunkSize);
            
        }
        os.flush(); // Necessary for Java < 6
        os.close();
        is.close();
        
        Bukkit.broadcastMessage(SurvivalChallenge.getInstance().getPrefix() + "[§e" + modul_name + "§7] Download complete!");
        Bukkit.reload();
        
    }
    private static long getFileSize(URLConnection connection){
        return Long.valueOf(connection.getHeaderField("Content-Length"));
    }
}
