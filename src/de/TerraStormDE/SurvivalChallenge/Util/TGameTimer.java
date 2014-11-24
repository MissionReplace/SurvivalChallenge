package de.TerraStormDE.SurvivalChallenge.Util;
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import java.security.InvalidParameterException;
import java.util.HashMap;
import org.bukkit.Bukkit;

/**
 *
 * @author Hannes
 */
public abstract class TGameTimer {
    
    private static HashMap<String,TGameTimer> timers = new HashMap<String, TGameTimer>();
    private String name;
    private int count,before;
    private int beetween;
    private int task_count;
    private boolean isrunning;
    
    public TGameTimer(String name,int count){
        if(timers.containsKey(name.toLowerCase())){
            throw new InvalidParameterException("GameTimer already exists! [" + name + "]");
        }
        timers.put(name.toLowerCase(), this);
        this.isrunning = false;
        this.name = name.toLowerCase();
        this.count = count;
        this.before = count;
        this.beetween = 1;
    }
    
    public abstract boolean performHeader();
    public abstract void performBody();
    
    public void start(int beetween,final boolean restart_count){
        if(isRunning()){
            return;
        }
        this.beetween = beetween;
        this.isrunning = true;
        
        if(!performHeader()){
            this.isrunning = false;
            return;
        }
        this.task_count = Bukkit.getScheduler().scheduleSyncRepeatingTask(SurvivalChallenge.getInstance(), new Runnable() {

            @Override
            public void run() {
                if(isrunning && count > 0){
                    count--;
                    performBody();
                } else {
                    if(restart_count){
                        isrunning = true;
                        count = before;
                    } else {
                        Bukkit.getScheduler().cancelTask(task_count);
                        count = before;
                        isrunning = false;
                    }
                }
            }
        }, this.beetween*20, this.beetween*20);
    }
    public void stop(boolean reset_count){
        isrunning = false;
        if(reset_count){
            count = before;
        }
        Bukkit.getScheduler().cancelTask(task_count);
    }
    public boolean isRunning(){
        return isrunning;
    }
    public int getCount(){
        return count;
    }
    public void setCount(int count){
        this.count = count;
    }
    
    public static TGameTimer getGameTimer(String name){
        return timers.get(name.toLowerCase());
    }
    public static TGameTimer[] getGameTimers(){
        TGameTimer[] list = new TGameTimer[timers.size()];
        return timers.values().toArray(list);
    }
}
