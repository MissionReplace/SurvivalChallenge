/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.ChallengeModul;

import de.TerraStormDE.SurvivalChallenge.Enums.TGameStatus;
import de.TerraStormDE.SurvivalChallenge.Events.SCModulLoadEvent;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

/**
 *
 * @author Hannes
 */
public class TBasicModul
{
    private String name,display,description;
    private int id;
    private int votes;
    private Plugin plugin;
    private List<Listener> listener;
    private ItemStack vote_item;
    private boolean isvote;
    
    private static List<TBasicModul> challanges = new ArrayList<TBasicModul>();
    private static List<String> vote_list = new ArrayList<String>();
    
    public TBasicModul(Plugin plugin,String name,String displayname,String description,ItemStack vote_item)
    {
        this.name = name;
        this.display = displayname;
        this.id = challanges.size()+1;
        this.votes = 0;
        this.plugin = plugin;
        listener = new ArrayList<>();
        this.vote_item = vote_item;
        this.description = description;
        this.isvote = false;
        
        SCModulLoadEvent event = new SCModulLoadEvent(name);
        Bukkit.getPluginManager().callEvent(event);
        if(!event.isCancelled()){
            for(Player all : Bukkit.getOnlinePlayers()){
                if(!all.isOp()){
                    continue;
                }
                all.sendMessage(SurvivalChallenge.getInstance().getPrefix() + "Ein Modul wurde hinzugefügt [\"§e" + name + "§7\"]");
            }
        } else {
            Bukkit.getPluginManager().disablePlugin(plugin);
            return;
        }
        
        challanges.add(this);
    }
    
    public String getName()
    {
        return name;
    }
    public String getDisplayName()
    {
        return display;
    }
    public String getDescription(){
        return description;
    }
    public boolean isVoteModul(){
        return isvote;
    }
    public int getVotes(){
        return votes;
    }
    public int getID(){
        return id;
    }
    public Plugin getPlugin(){
        return plugin;
    }
    public ItemStack getVoteItem(){
        return vote_item;
    }
    
    public void addListener(Listener listener){
        this.listener.add(listener);
    }
    
    @Deprecated
    public void setVoteModul(boolean b){
        if(SurvivalChallenge.getInstance().getGameManager().getCurrentGameStatus() == TGameStatus.LOBBY){
            this.isvote = b;
        }
    }
    
    @Deprecated
    public void activateListener(){
        for(Listener l : listener){
            Bukkit.getPluginManager().registerEvents(l, SurvivalChallenge.getInstance());
        }
    }
    public void addVote(String p_name){
        if(!vote_list.contains(p_name)){
            vote_list.add(p_name);
            this.votes++;
        }
    }
    public void resetVotes(){
        this.votes = 0;
    }
    
    public static TBasicModul getChallenge(String name){
        TBasicModul c = null;
        for(TBasicModul ch : challanges){
            if(ch.getName().equalsIgnoreCase(name)){
                c = ch;
                break;
            }
        }
        return c;
    }
    public static TBasicModul getByPluginName(String name){
        for(TBasicModul m : challanges){
            if(m.getPlugin().getName().equalsIgnoreCase(name)){
                return m;
            }
        }
        return null;
    }
    public static TBasicModul getByDisplayName(String dname,boolean use_color){
        for(TBasicModul modul : challanges){
            String d = modul.getDisplayName();
            if(use_color){
                d = d.replace("&", "§");
                dname = dname.replace("&", "§");
            } else {
                d = ChatColor.stripColor(d.replace("&", "§"));
                dname = ChatColor.stripColor(dname.replace("&", "§"));
            }
            if(d.equalsIgnoreCase(dname)){
                return modul;
            }
        }
        return null;
    }
    public static TBasicModul[] getChallenges(){
        TBasicModul[] list = new TBasicModul[challanges.size()];
        return challanges.toArray(list);
    }
    public static TBasicModul getRandomModul(){
        return challanges.get(new Random().nextInt(challanges.size()));
    }
    public static boolean hasAlreadyVoted(String name){
        return vote_list.contains(name);
    }
    public static void resetAllVotes(){
        for(TBasicModul c : challanges){
            c.resetVotes();
        }
        vote_list.clear();
    }
}
