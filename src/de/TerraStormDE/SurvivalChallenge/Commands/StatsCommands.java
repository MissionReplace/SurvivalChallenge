/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Commands;

import de.TerraStormDE.SurvivalChallenge.ChallengeModul.TBasicModul;
import de.TerraStormDE.SurvivalChallenge.Enums.TConfigPath;
import de.TerraStormDE.SurvivalChallenge.Enums.TStatsType;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.UserDatabase.UserProfile;
import de.TerraStormDE.SurvivalChallenge.Util.TGamePlayer;
import de.TerraStormDE.SurvivalChallenge.Util.TObjectRestore;
import java.util.List;
import java.util.UUID;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Hannes
 */
public class StatsCommands implements CommandExecutor
{

    private final SurvivalChallenge plugin;
    
    public StatsCommands(SurvivalChallenge instance){
        this.plugin = instance;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        
        if(!(sender instanceof Player)){
            return true;
        }
        Player p = (Player) sender;
        
        if(!plugin.getConnection().hasConnection()){
            p.sendMessage(plugin.getPrefix() + "Es besteht §ekeine §7Verbindung zur MySQL-DB");
            return true;
        }
        
        if(cmd.getName().equalsIgnoreCase("stats")){
            if(args.length == 0){
                if(p.hasPermission("sc.stats")){
                    TGamePlayer player = TGamePlayer.getGamePlayer(p.getUniqueId());
                    p.sendMessage(plugin.getPrefix() + "Kills: §e" + player.getKills());
                    p.sendMessage(plugin.getPrefix() + "Deaths: §e" + player.getDeaths());
                    if(TBasicModul.getChallenges().length > 0){
                        p.sendMessage(plugin.getPrefix() + "Moduls: ");
                        for(TObjectRestore ob : player.getModulStats()){
                            p.sendMessage("§8- §e" + ob.getObject("name"));
                            p.sendMessage(" §8- §7Games: §e" + ob.getObject("games"));
                            p.sendMessage(" §8- §7Wins:  §e" + ob.getObject("wins"));
                        }
                    }
                    return true;
                } 
            } else if(args[0].equalsIgnoreCase("top")){
                if(p.hasPermission("sc.stats.top")){
                    String order = plugin.convertPath(TConfigPath.SETTINGS_STATS_TOP_ORDER).toString();
                    TStatsType type = null;
                    if(order.equalsIgnoreCase("kills")){
                        type = TStatsType.KILL;
                    } else if(order.equalsIgnoreCase("deaths")){
                        type = TStatsType.DEATH;
                    } else if(order.equalsIgnoreCase("games")){
                        type = TStatsType.GAME;
                    } else if(order.equalsIgnoreCase("wins")){
                        type = TStatsType.WIN;
                    } else if(order.equalsIgnoreCase("points")){
                        type = TStatsType.POINT;
                    } else {
                        type = TStatsType.KILL;
                    }
                    
                    List<UserProfile> players = plugin.getStatsConnection().getTopPlayer(type, (int) plugin.convertPath(TConfigPath.SETTINGS_STATS_TOP_AMOUNT));
                    String space = "                    ";
                    for(UserProfile profile : players){
                        int auto = plugin.getStatsConnection().getStats(profile.getUUID(), type);
                        
                        p.sendMessage(plugin.getPrefix() + profile.getName() + space.substring(profile.getName().length(),space.length()) + " | " + type.getTableRow() + ": §e" + auto);
                    }
                    return true;
                }
            } else {
                p.sendMessage("Coming Soon!");
                return true;
            }
        }
        
        return true;
    }
    
}
