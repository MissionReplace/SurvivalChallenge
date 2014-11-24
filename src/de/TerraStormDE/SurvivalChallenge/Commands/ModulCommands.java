/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Commands;

import de.TerraStormDE.SurvivalChallenge.ChallengeModul.TBasicModul;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Web.TModulDownloadUtil;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Hannes
 */
public class ModulCommands implements CommandExecutor
{
    private final SurvivalChallenge plugin;
    
    public ModulCommands(SurvivalChallenge instance){
        this.plugin = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        
        if(!(sender instanceof Player)){
            return false;
        }
        Player p = (Player) sender;
        
        if(cmd.getName().equalsIgnoreCase("modul")){
            if(args.length == 0){
                if(p.hasPermission("sc.modul.info")){
                    p.sendMessage(plugin.getPrefix() + "§e/modul list");
                    p.sendMessage(plugin.getPrefix() + "§e/modul info <name>");
                    p.sendMessage(plugin.getPrefix() + "§e/modul install <id>");
                    return true;
                }
            } else if(args[0].equalsIgnoreCase("list")){
                if(p.hasPermission("sc.modul.list")){
                    if(TBasicModul.getChallenges().length == 0){
                        p.sendMessage(plugin.getPrefix() + "Es wurden keine Module gefunden!");
                        return true;
                    }
                    List<String> list = TModulDownloadUtil.fetchModulList();
                    int id = 1;
                    
                    p.sendMessage(plugin.getPrefix() + "Module [gelb = §eInstalliert§7]");
                    for(String s : list){
                        if(TBasicModul.getByPluginName(s) != null){
                            p.sendMessage(plugin.getPrefix() + id + ") §e" + s);
                        } else {
                            p.sendMessage(plugin.getPrefix() + id + ") §7" + s);
                        }
                        id++;
                    }
                    return true;
                }
            } else if(args[0].equalsIgnoreCase("info")){
                if(p.hasPermission("sc.modul.info")){
                    if(args.length == 2){
                        String name = args[1];
                        TBasicModul m = TBasicModul.getByPluginName(name);
                        if(m != null){
                            p.sendMessage(plugin.getPrefix() + "Name: §e" + m.getName());
                            p.sendMessage(plugin.getPrefix() + "Beschreibung: §e" + ChatColor.stripColor(m.getDescription().replace("&", "§")));
                            p.sendMessage(plugin.getPrefix() + "Displayname: §e" + m.getDisplayName());
                            return true;
                        } else {
                            p.sendMessage(plugin.getPrefix() + "Dieses Modul ist nicht vorhanden!");
                            return true;
                        }
                    } else {
                        p.sendMessage(plugin.getPrefix() + "§e/modul info <name>");
                        return true;
                    }
                }
            } else if(args[0].equalsIgnoreCase("install")){
                if(p.hasPermission("sc.modul.info")){
                    if(args.length == 2){
                        try{
                            int id = Integer.valueOf(args[1]);
                            id-= 1;
                            if(!TModulDownloadUtil.installModul(id)){
                                p.sendMessage(plugin.getPrefix() + "Fehler beim installieren des Moduls!");
                            }
                            return true;

                        }catch(NumberFormatException e){
                            p.sendMessage(plugin.getPrefix() + "Ungültige ID!");
                            return true;
                        }
                    } else {
                        p.sendMessage(plugin.getPrefix() + "§e/modul install <id>");
                        return true;
                    }
                }
            }
        }
        
        return false;
    }
    
}
