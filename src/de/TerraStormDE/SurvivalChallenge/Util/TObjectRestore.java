/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Util;

import java.util.HashMap;

/**
 *
 * @author Hannes
 */
public class TObjectRestore
{
    private HashMap<String,Object> objects;
    
    public TObjectRestore(){
        objects = new HashMap<>();
    }
    
    public void addObject(String name,Object ob){
        objects.put(name, ob);
    }
    public Object getObject(String name){
        return objects.get(name);
    }
    public HashMap<String,Object> getObjects(){
        return (HashMap<String, Object>) objects.clone();
    }
}
