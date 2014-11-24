/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Hannes
 */
public class TWebUtil
{
    
    public static BufferedReader getReader(URL url) throws IOException{
        return new BufferedReader(new InputStreamReader(url.openStream()));
    }
    public static BufferedReader getReader(String url) throws IOException{
        if(!url.startsWith("http") && !url.startsWith("https")){
            url = "http://" + url;
        }
        return getReader(new URL(url));
    }
    
    public static List<String> getContent(URL url) throws IOException{
        List<String> l = new ArrayList<>();
        
        BufferedReader reader = getReader(url);
        String item;
        while((item = reader.readLine()) != null){
            if(!item.isEmpty()){
                l.add(item);
            }
        }
        return l;
    }
    public static List<String> getContent(String url) throws IOException{
        if(!url.startsWith("http") && !url.startsWith("https")){
            url = "http://" + url;
        }
        return getContent(new URL(url));
    }
}
