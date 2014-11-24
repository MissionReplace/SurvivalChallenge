/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge;

import de.TerraStormDE.SurvivalChallenge.Manager.TBackupManager;
import de.TerraStormDE.SurvivalChallenge.ChestManager.TChestContainer;
import de.TerraStormDE.SurvivalChallenge.ChestManager.TChestListener;
import de.TerraStormDE.SurvivalChallenge.ChestManager.TChestManager;
import de.TerraStormDE.SurvivalChallenge.Commands.MapCommands;
import de.TerraStormDE.SurvivalChallenge.Commands.ModulCommands;
import de.TerraStormDE.SurvivalChallenge.Commands.StatsCommands;
import de.TerraStormDE.SurvivalChallenge.Enums.TConfigPath;
import de.TerraStormDE.SurvivalChallenge.Listener.BlockListener;
import de.TerraStormDE.SurvivalChallenge.Listener.GameStateListener;
import de.TerraStormDE.SurvivalChallenge.Listener.ItemAddListener;
import de.TerraStormDE.SurvivalChallenge.Listener.PlayerDeathListener;
import de.TerraStormDE.SurvivalChallenge.Listener.PlayerMessageListener;
import de.TerraStormDE.SurvivalChallenge.Listener.RankingListener;
import de.TerraStormDE.SurvivalChallenge.Listener.SpectatorListener;
import de.TerraStormDE.SurvivalChallenge.Listener.TagAPIListener;
import de.TerraStormDE.SurvivalChallenge.Listener.VoteListener;
import de.TerraStormDE.SurvivalChallenge.Manager.TDatabaseConnection;
import de.TerraStormDE.SurvivalChallenge.Manager.TGameManager;
import de.TerraStormDE.SurvivalChallenge.Manager.TRankingObjectManager;
import de.TerraStormDE.SurvivalChallenge.Manager.TStatsConnection;
import de.TerraStormDE.SurvivalChallenge.Manager.TVoteManager;
import de.TerraStormDE.SurvivalChallenge.Timer.TBreakTimer;
import de.TerraStormDE.SurvivalChallenge.Timer.TDMGraceTimer;
import de.TerraStormDE.SurvivalChallenge.Timer.TDeathmatchTimer;
import de.TerraStormDE.SurvivalChallenge.Timer.TGameGraceTimer;
import de.TerraStormDE.SurvivalChallenge.Timer.TGameSpawnsTimer;
import de.TerraStormDE.SurvivalChallenge.Timer.TInGameTimer;
import de.TerraStormDE.SurvivalChallenge.Timer.TLobbyTimer;
import de.TerraStormDE.SurvivalChallenge.UserDatabase.UserAPI;
import de.TerraStormDE.SurvivalChallenge.Util.TBlockList;
import de.TerraStormDE.SurvivalChallenge.Util.TDeathmatchArena;
import de.TerraStormDE.SurvivalChallenge.Util.TGameMap;
import de.TerraStormDE.SurvivalChallenge.Util.TGamePlayer;
import de.TerraStormDE.SurvivalChallenge.Util.TSpawnUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.WorldCreator;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;


public class SurvivalChallenge extends JavaPlugin {
    
    
    private static SurvivalChallenge instance;
    private TVoteManager tvm;
    private TGameManager tgm;
    private TDatabaseConnection mysql;
    private TStatsConnection stats_mysql;
    private UserAPI userapi;
    private TChestManager chest_manager;
    private TChestContainer global_container;
    private Location lobby;
    private TBackupManager backup;
    private TRankingObjectManager ranking_m;
    private TBlockList whitelist;
    

    @Override
    public void onEnable() {
        
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        
        instance = this;
        tvm = new TVoteManager(instance);
        tgm = new TGameManager(instance);
        chest_manager = new TChestManager(instance);
        global_container = new TChestContainer();
        backup = new TBackupManager(instance);
        ranking_m = new TRankingObjectManager(instance);
        
        new File(getDataFolder() + "/backups").mkdir();
        for(File f : new File(getDataFolder() + "/backups").listFiles()){
            try
            {
                if(!new File(f.getName()).exists()){
                    getBackupManager().copyDir(f, new File(f.getName()));
                }
                Bukkit.createWorld(new WorldCreator(f.getName()));
            } catch (IOException ex)
            {
                Logger.getLogger(SurvivalChallenge.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        loadConfig();
        loadMaps();
        loadItems();
        loadBlockLists();
        
        mysql = new TDatabaseConnection(convertPath(TConfigPath.MYSQL_USER).toString(), convertPath(TConfigPath.MYSQL_PASSWORT).toString(),convertPath(TConfigPath.MYSQL_DATABASE).toString(), convertPath(TConfigPath.MYSQL_HOST).toString());
        mysql.openConnection();
        stats_mysql = new TStatsConnection(instance);
        userapi = new UserAPI(instance);
        userapi.createTable();
        
        getRankingManager().loadAll();
        
        
        new TBreakTimer(instance, "break_count", 10);
        new TDMGraceTimer(instance, "dm_grace",getConfig().getInt(TConfigPath.SETTINGS_DEATHMATCH_GRACE_COUNT.getPath()));
        new TDeathmatchTimer(instance,"dm_count", 60*4);
        new TGameGraceTimer(instance, "game_grace", getConfig().getInt(TConfigPath.SETTINGS_GAME_GRACE.getPath()));
        new TInGameTimer(instance, "game_count", 60*20);
        new TLobbyTimer(instance, "lobby_count", getConfig().getInt(TConfigPath.SETTINGS_LOBBY_COUNT.getPath()));
        new TGameSpawnsTimer(instance, "game_spawns_timer", 10);
        
        new PlayerMessageListener(instance);
        new TChestListener(instance);
        new BlockListener(instance);
        new GameStateListener(instance);
        new PlayerDeathListener(instance);
        new SpectatorListener(instance);
        new ItemAddListener(instance);
        new VoteListener(instance);
        new RankingListener(instance);
        new TagAPIListener(instance);
      
        getChestManager().addContainer(global_container);
        
        getCommand("sc").setExecutor(new MapCommands(instance));
        getCommand("stats").setExecutor(new StatsCommands(instance));
        getCommand("modul").setExecutor(new ModulCommands(instance));
        
        for(Player all : Bukkit.getOnlinePlayers()){
            new TGamePlayer(all.getName(), all.getUniqueId(), false);
            for(Player all2 : Bukkit.getOnlinePlayers()){
                all.showPlayer(all2);
                all2.showPlayer(all);
            }
            
            all.setFlying(false);
            all.setAllowFlight(false);
        }
        new File(getDataFolder() + "/backups").mkdir();
        
        
    }
    

    @Override
    public void onDisable() {
       if(mysql.hasConnection()){
           mysql.closeConnection();
       }
       if(getBackupManager().isGameResetAllowed()){
           TGameMap gm = getVoteManager().getWinnedMap();
           getBackupManager().loadBackup(gm.getBackup());
           getBackupManager().loadBackup(gm.getDeathmatchArena().getBackup());
       }
       if(whitelist != null){
           File f = new File(getDataFolder(),"materials.yml");
           FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
           List<String> l = new ArrayList<>();
           for(Material m : whitelist.getBlocks()){
               l.add(m.toString());
           }
           cfg.set("whitelist", l);
           try
           {
               cfg.save(f);
           } catch (IOException ex)
           {
               Logger.getLogger(SurvivalChallenge.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
    }

  
    public static SurvivalChallenge getInstance(){
        return instance;
    }
    public TDatabaseConnection getConnection(){
        return mysql;
    }
    public UserAPI getUserAPI(){
        return userapi;
    }
    public Object convertPath(TConfigPath path){
        if(getConfig().isSet(path.getPath())){
            return getConfig().get(path.getPath());
        }
        return path.getDefaultValue();
    }
    public String getPrefix(){
        return (String) convertPath(TConfigPath.PLUGIN_PREFIX).toString().replace("&", "§");
    }
    
    public TVoteManager getVoteManager(){
        return tvm;
    }
    public TGameManager getGameManager(){
        return tgm;
    }
    public TBackupManager getBackupManager(){
        return backup;
    }
    public TStatsConnection getStatsConnection(){
        return stats_mysql;
    }
    public TChestManager getChestManager(){
        return chest_manager;
    }
    public TRankingObjectManager getRankingManager(){
        return ranking_m;
    }
    public TChestContainer getGlobalChestContainer(){
        return global_container;
    }
    public TBlockList getBlockWhiteList(){
        return whitelist;
    }
    public void setBlockWhiteList(TBlockList list){
        this.whitelist = list;
    }
    public Location getLobby(){
        return this.lobby;
    }
    public void setLobby(Location loc){
        this.lobby = loc;
    }
    
    
    private void loadConfig(){
        File items = new File(getDataFolder(),"items.yml");
        if(!items.exists()){
            try
            {
                items.createNewFile();
            } catch (IOException ex)
            {
                Logger.getLogger(SurvivalChallenge.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        getConfig().options().copyDefaults(true);
        for(TConfigPath path : TConfigPath.values()){
            if(!getConfig().isSet(path.getPath())){
                getConfig().addDefault(path.getPath(), path.getDefaultValue());
            }
        }
        
        if(getConfig().isSet("lobby")){
            double x = getConfig().getDouble("lobby.x");
            double y = getConfig().getDouble("lobby.y");
            double z = getConfig().getDouble("lobby.z");
            float yaw = (float)getConfig().getDouble("lobby.yaw");
            float pitch = (float)getConfig().getDouble("lobby.pitch");
            this.lobby = new Location(Bukkit.getWorld(getConfig().getString("lobby.world")), x, y, z);
            this.lobby.setYaw(yaw);
            this.lobby.setPitch(pitch);
        }
        
        saveConfig();
    }
    private void loadMaps(){
        
        File dir = new File(getDataFolder() + "/Arenen");
        dir.mkdir();
        
        for(File f : dir.listFiles()){
            if(f.getName().contains(".yml")){
                FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
                String name = f.getName().replace(".yml", "");
                
                String display = cfg.getString("settings.display");
                String builder = cfg.getString("settings.builder");
                String world = cfg.getString("data.world");
                
                TGameMap map = new TGameMap(Bukkit.getWorld(world), name, null);
                
                map.setDisplayName(display);
                map.setBuilder(builder);
                
                
                if(cfg.getStringList("data.spawns") != null){
                    List<String> spawns = cfg.getStringList("data.spawns");
                    for(String spawn : spawns){
                        Location loc = TSpawnUtil.decompileSpawn(spawn);
                        map.addSpawn(loc);
                    }
                }
                
                TDeathmatchArena arena = null;
                if(cfg.getStringList("dm.spawns") != null){
                    List<String> spawns = cfg.getStringList("dm.spawns");
                    for(String spawn : spawns){
                        Location loc = TSpawnUtil.decompileSpawn(spawn);
                        if(arena == null){
                            arena = new TDeathmatchArena(instance, loc.getWorld());
                        }
                        arena.addSpawn(loc);
                    }
                }
                
                map.setDeathmatchArena(arena);
                
                String item = cfg.getString("data.vitem");
                ItemStack set = null;
                if(item.contains(":")){
                    String[] values = item.split(":");
                    int id = Integer.valueOf(values[0]);
                    Material m = Material.getMaterial(id);
                    if(m == null){
                        throw new NullPointerException("Material is not valid!");
                    }
                    short data = Short.valueOf(values[1]);
                    set = new ItemStack(m,1,data);
                } else {
                    int id = Integer.valueOf(item);
                    Material m = Material.getMaterial(id);
                    if(m == null){
                        throw new NullPointerException("Material is not valid!");
                    }
                    set = new ItemStack(m);
                }
                map.setVoteItem(set);
                
            }
        }
    }
    public void loadItems(){
        File f = new File(getDataFolder(),"items.yml");
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        if(cfg.isSet("items")){
            for(String s : cfg.getConfigurationSection("items").getKeys(false)){
                ItemStack i = cfg.getItemStack("items." + s);
                getGlobalChestContainer().addItemStack(i);
            }
        }
    }
    public void loadBlockLists() throws NullPointerException{
        File f = new File(getDataFolder(),"materials.yml");
        if(!f.exists()){
            try
            {
                f.createNewFile();
            } catch (IOException ex)
            {
                Logger.getLogger(SurvivalChallenge.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(f);
        if(cfg.getStringList("whitelist") != null){
            this.whitelist = new TBlockList();
            for(String s : cfg.getStringList("whitelist")){
                Material m;
                if((m = Material.valueOf(s)) != null){
                    this.whitelist.addBlock(m);
                }
            }
        }
    }
}
