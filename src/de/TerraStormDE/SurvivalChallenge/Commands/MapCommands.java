/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Commands;

import de.TerraStormDE.SurvivalChallenge.ChallengeModul.TBasicModul;
import de.TerraStormDE.SurvivalChallenge.Listener.BlockListener;
import de.TerraStormDE.SurvivalChallenge.Listener.ItemAddListener;
import de.TerraStormDE.SurvivalChallenge.Manager.TRankingObjectManager;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import de.TerraStormDE.SurvivalChallenge.Util.TDeathmatchArena;
import de.TerraStormDE.SurvivalChallenge.Util.TGameMap;
import de.TerraStormDE.SurvivalChallenge.Util.TSpawnUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

/**
 *
 * @author Hannes
 */
public class MapCommands implements CommandExecutor
{
    private final SurvivalChallenge plugin;
    
    public MapCommands(SurvivalChallenge instance){
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
        
        if(cmd.getName().equalsIgnoreCase("sc")){
            if(args.length == 0){
                if(p.hasPermission("sc.info")){
                    p.sendMessage(plugin.getPrefix() + "Name: §eSurvivalChallenge");
                    p.sendMessage(plugin.getPrefix() + "Author: §eTerraStormDE");
                    p.sendMessage(plugin.getPrefix() + "Version: §e" + plugin.getDescription().getVersion());
                    p.sendMessage(plugin.getPrefix() + "Build: §eALPHA-BUILD");
                    p.sendMessage(plugin.getPrefix() + "Help: §e/sc help");
                    return true;
                }
                
            } else if(args[0].equalsIgnoreCase("addarena")){
                // sc addarena [name] [builder] 
                if(p.hasPermission("sc.addarena")){
                    if(args.length == 6){
                        try {
                            String name = args[1];
                            String display = args[2];
                            String builder = args[3];
                            String item = args[4];
                            String world = args[5];
                            
                            
                            File f = new File(plugin.getDataFolder() + "/Arenen",name + ".yml");
                            if(f.exists()){
                                p.sendMessage(plugin.getPrefix() + "§cDiese Arena existiert bereits!");
                                return true;
                            }
                            if(TGameMap.getByDisplayName(display, false) != null){
                                p.sendMessage(plugin.getPrefix() + "Dieser Displayname ist bereits vergeben!");
                                return true;
                            }
                            if(Bukkit.getWorld(world)==null){
                                p.sendMessage(plugin.getPrefix() + "Diese Welt ist nicht vorhanden!");
                                return true;
                            }
                            
                            
                            f.createNewFile();
                            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                            try{
                                int id = 0;
                                
                                if(item.contains(":")){
                                    String[] values = item.split(":");
                                    id = Integer.valueOf(values[0]);
                                } else {
                                    id = Integer.valueOf(item);
                                }
                                
                                if(Material.getMaterial(id) == null){
                                    p.sendMessage(plugin.getPrefix() + "Dieses Material ist ungültig!");
                                    return true;
                                }
                                cfg.set("data.vitem", item);
                            }catch(NumberFormatException e){
                                p.sendMessage(plugin.getPrefix() + "Die Item-ID ist fehlerhaft!");
                                return true;
                            }
                            cfg.set("settings.display", display);
                            cfg.set("settings.builder", builder);
                            cfg.set("data.world", world);
                            cfg.save(f);
                            p.sendMessage(plugin.getPrefix() + "Arena erstellt!");
                            
                            
                            TGameMap map = new TGameMap(p.getWorld(), name, null);
                            map.setBuilder(builder);
                            map.setDisplayName(display);
                            return true;
                        } catch (IOException ex) {
                            Logger.getLogger(MapCommands.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        p.sendMessage(plugin.getPrefix() + "§e/sc addarena <name> <displayname> <builder> <vote-item-id> <world>");
                        return true;
                    }
                }
            } else if(args[0].equalsIgnoreCase("addspawn")){
                if(p.hasPermission("sc.addspawn")){
                    if(args.length == 2){
                        try {
                            String arena = args[1];
                            if(TGameMap.getGameMap(arena) == null){
                                p.sendMessage(plugin.getPrefix() + "Diese Arena ist nicht vorhanden!");
                                return true;
                            }
                            File f = new File(plugin.getDataFolder() + "/Arenen",arena + ".yml");
                            if(!f.exists()){
                                return true;
                            }
                            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                            List<String> spawns = cfg.getStringList("data.spawns");
                            if(spawns == null){
                                spawns = new ArrayList<>();
                            }
                            spawns.add(TSpawnUtil.compileSpawn(p.getLocation()));
                            TGameMap.getGameMap(arena).addSpawn(p.getLocation());
                            cfg.set("data.spawns", spawns);
                            cfg.save(f);
                            p.sendMessage(plugin.getPrefix() + "Spawn wurde gesetzt §e(" + spawns.size() + ")");
                            return true;
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                        
                    } else {
                        p.sendMessage(plugin.getPrefix() + "§e/sc addspawn <arena>");
                        return true;
                    }
                }
            } else if(args[0].equalsIgnoreCase("maps")){
                if(p.hasPermission("sc.maps")){
                    if(TGameMap.getGameMaps().length == 0){
                        p.sendMessage(plugin.getPrefix() + "Es wurde §ekeine §7Arenen gefunden!");
                        return true;
                    }
                    for(TGameMap map : TGameMap.getGameMaps()){
                        p.sendMessage(plugin.getPrefix() + "- §e" + map.getName());
                    }
                    return true;
                }
            } else if(args[0].equalsIgnoreCase("delarena")){
                if(p.hasPermission("sc.delarena")){
                    if(args.length == 2){
                        TGameMap map = TGameMap.getGameMap(args[1]);
                        if(map == null){
                            p.sendMessage(plugin.getPrefix() + "Diese Arena ist §enicht §7vorhanden!");
                            return true;
                        }
                        
                        new File(plugin.getDataFolder() + "/Arenen",args[1] + ".yml").delete();
                        TGameMap.deleteMap(args[1]);
                        p.sendMessage(plugin.getPrefix() + "Die Arena \"§e" + args[1] + "§7\" wurde gelöscht!");
                        return true;
                    } else {
                        p.sendMessage(plugin.getPrefix() + "§e/sc delarena <name>");
                        return true;
                    }
                }
            } else if(args[0].equalsIgnoreCase("imode")){
                if(p.hasPermission("sc.imode")){
                    if(ItemAddListener.getPlayers().contains(p.getName())){
                        p.sendMessage(plugin.getPrefix() + "Du bist nun nicht mehr im §eItemModus.");
                        ItemAddListener.getPlayers().remove(p.getName());
                        return true;
                    } else {
                        p.sendMessage(plugin.getPrefix() + "Du bist nun im §eItemModus.");
                        p.sendMessage(plugin.getPrefix() + "Mach Rechtsklick mit einem §eItem§7, um es hinzuzufügen!");
                        ItemAddListener.getPlayers().add(p.getName());
                        return true;
                    }
                }
            } else if(args[0].equalsIgnoreCase("bmode")){
                if(p.hasPermission("sc.bmode")){
                    if(ItemAddListener.getPlayers().contains(p.getName())){
                        p.sendMessage(plugin.getPrefix() + "Bitte verlasse zuerst den ItemMode!");
                        return true;
                    }
                    if(BlockListener.containsPlayer(p.getName())){
                        p.sendMessage(plugin.getPrefix() + "Du bist nun nicht mehr im §eBlockModus.");
                        BlockListener.removePlayerFromMode(p.getName());
                        return true;
                    } else {
                        p.sendMessage(plugin.getPrefix() + "Du bist nun im §eBlockModus.");
                        p.sendMessage(plugin.getPrefix() + "Mach Rechtsklick auf einen §eBlock§7, um ihn hinzuzufügen!");
                        BlockListener.addPlayerToMode(p.getName());
                        return true;
                    }
                }
            }else if(args[0].equalsIgnoreCase("adddmspawn")){
                if(p.hasPermission("sc.adddmspawn")){
                    if(args.length == 2){
                        try {
                            TGameMap map = TGameMap.getGameMap(args[1]);
                            if(map == null){
                                p.sendMessage(plugin.getPrefix() + "Diese Arena ist §enicht §7vorhanden!");
                                return true;
                            }
                            File f = new File(plugin.getDataFolder() + "/Arenen",args[1] + ".yml");
                            FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                            List<String> spawns = cfg.getStringList("dm.spawns");
                            if(spawns == null){
                                spawns = new ArrayList<>();
                            }
                            if(map.getDeathmatchArena() == null){
                                map.setDeathmatchArena(new TDeathmatchArena(plugin, p.getWorld()));
                            }
                            spawns.add(TSpawnUtil.compileSpawn(p.getLocation()));
                            map.getDeathmatchArena().addSpawn(p.getLocation());
                            cfg.set("dm.spawns", spawns);
                            cfg.save(f);
                            p.sendMessage(plugin.getPrefix() + "Spawn wurde hinzugefügt §e(" + spawns.size() + ")");
                            return true;
                        } catch (IOException ex) {
                            Logger.getLogger(MapCommands.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        p.sendMessage(plugin.getPrefix() + "§e/sc adddmspawn <arena>");
                        return true;
                    }
                }
            } else if(args[0].equalsIgnoreCase("setlobby")){
                if(p.hasPermission("sc.setlobby")){
                    FileConfiguration cfg = plugin.getConfig();
                    cfg.set("lobby.x", p.getLocation().getX());
                    cfg.set("lobby.y", p.getLocation().getY());
                    cfg.set("lobby.z", p.getLocation().getZ());
                    cfg.set("lobby.yaw", p.getLocation().getYaw());
                    cfg.set("lobby.pitch", p.getLocation().getPitch());
                    cfg.set("lobby.world", p.getWorld().getName());
                    plugin.saveConfig();
                    p.sendMessage(plugin.getPrefix() + "Lobby wurde gesetzt!");
                    plugin.setLobby(p.getLocation());
                    return true;
                }
            } else if(args[0].equalsIgnoreCase("cbackup")){
                if(p.hasPermission("sc.backup.create")){
                    if(args.length < 2){
                        p.sendMessage(plugin.getPrefix() + "§e/sc cbackup <arena>");
                        return true;
                    }
                    TGameMap map = TGameMap.getGameMap(args[1]);
                    if(map == null){
                        p.sendMessage(plugin.getPrefix() + "Diese Arena existiert nicht!");
                        return true;
                    }
                    p.sendMessage(plugin.getPrefix() + "Backup wird erstellt...");
                    plugin.getBackupManager().createBackup(map.getWorld().getName());
                    if(map.getDeathmatchArena() != null){
                        plugin.getBackupManager().createBackup(map.getDeathmatchArena().getWorld().getName());
                    }
                    p.sendMessage(plugin.getPrefix() + "Backup wurde erstellt!");
                    return true;
                }
                
            /*} else if(args[0].equalsIgnoreCase("lbackup")){
                if(p.hasPermission("sc.backup.load")){
                    if(args.length < 2){
                        p.sendMessage(plugin.getPrefix() + "§e/sc lbackup <arena>");
                        return true;
                    }
                    TGameMap map = TGameMap.getGameMap(args[1]);
                    if(map == null){
                        p.sendMessage(plugin.getPrefix() + "Diese Arena existiert nicht!");
                        return true;
                    }
                    if(!plugin.getBackupManager().existsBackup(p.getWorld().getName())){
                        p.sendMessage(plugin.getPrefix() + "Backup wurde nicht gefunden! [World]");
                        return true;
                    }
                    if(map.getBackup() == null){
                        p.sendMessage(plugin.getPrefix() + "Backup wurde nicht gefunden! [Map]");
                        return true;
                    }
                    
                    
                    String world = p.getWorld().getName();
                    for(Player all : map.getWorld().getPlayers()){
                        all.teleport(Bukkit.getWorld("world").getSpawnLocation());
                    }
                    p.sendMessage(plugin.getPrefix() + "Backup wird geladen...");
                    plugin.getBackupManager().loadBackup(map.getBackup());
                    p.sendMessage(plugin.getPrefix() + "Backup geladen!");
                    return true;
                }*/
            } else if(args[0].equalsIgnoreCase("check")){
                if(p.hasPermission("sc.check")){
                    if(args.length == 2){
                        String arena = args[1];
                        TGameMap map = TGameMap.getGameMap(arena);
                        if(map == null){
                            p.sendMessage(plugin.getPrefix() + "Diese Arena ist §enicht §7vorhanden!");
                            return true;
                        }
                        map.sendReadyMessage(p);
                        
                        
                        return true;
                    } else {
                        p.sendMessage(plugin.getPrefix() + "§e/sc check <arena>");
                        return true;
                    }
                }
            } else if(args[0].equalsIgnoreCase("sethead")){
                if(p.hasPermission("sc.sethead")){
                    if(args.length == 2){
                        try{
                            int rank = Integer.valueOf(args[1]);
                            plugin.getRankingManager().createRankingObject(p.getLocation(), rank, TRankingObjectManager.TRankingObjectType.RANKING_HEAD);
                            p.sendMessage(plugin.getPrefix() + "Rankinghead erstellt!");
                            return true;
                        } catch(NumberFormatException e){
                            p.sendMessage(plugin.getPrefix() + "Ungültiger Rang");
                            return true;
                        }
                    } else {
                        p.sendMessage(plugin.getPrefix() + "§e/sc sethead <ranking-pos>");
                        return true;
                    }
                }
            } else if(args[0].equalsIgnoreCase("help")){
                if(p.hasPermission("sc.info")){
                    p.sendMessage(plugin.getPrefix() + "§e/sc");
                    p.sendMessage(plugin.getPrefix() + "§e/sc help");
                    p.sendMessage(plugin.getPrefix() + "§e/sc addarena <name> <displayname> <builder> <vote-item-id> <world>");
                    p.sendMessage(plugin.getPrefix() + "§e/sc delarena <arena>");
                    p.sendMessage(plugin.getPrefix() + "§e/sc sethead <pos>");
                    p.sendMessage(plugin.getPrefix() + "§e/sc check <arena>");
                    p.sendMessage(plugin.getPrefix() + "§e/sc setlobby");
                    p.sendMessage(plugin.getPrefix() + "§e/sc cbackup");
                    return true;
                }
            }
        }
        
        
        return false;
    }
    
}
