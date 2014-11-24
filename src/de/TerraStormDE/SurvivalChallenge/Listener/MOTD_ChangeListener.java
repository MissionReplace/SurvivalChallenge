/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Listener;

import de.TerraStormDE.SurvivalChallenge.Enums.TGameStatus;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.server.ServerListPingEvent;

/**
 *
 * @author Hannes
 */
public class MOTD_ChangeListener implements Listener
{
    
    private final SurvivalChallenge plugin;
    
    public MOTD_ChangeListener(SurvivalChallenge instance){
        this.plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void login(final PlayerLoginEvent e){
        TGameStatus status = plugin.getGameManager().getCurrentGameStatus();
        /*if(status == TGameStatus.DEATH_MATCH || status == TGameStatus.DEATH_MATCH_GRACE_PERIOD || status == TGameStatus.DEATH_MATCH_ON_SPAWNS ||
                status == TGameStatus.GAME_GRACE_PERIOD || status == TGameStatus.GAME_ON_SPAWNS || status == TGameStatus.IN_GAME){
            
            
        } else {
            
        }*/
        if(status == TGameStatus.END){
            e.disallow(null, plugin.getPrefix() + "§eDer Server restartet in wenigen Sekunden.");
        }
    }
    
    @EventHandler
    public void change_motd(ServerListPingEvent e){
        TGameStatus status = plugin.getGameManager().getCurrentGameStatus();
        if(status == TGameStatus.BEFORE_LOBBY || status == TGameStatus.LOBBY){
            e.setMotd("§8[§a§lLobby§8]");
        }
        if(status == TGameStatus.GAME_GRACE_PERIOD || status == TGameStatus.GAME_ON_SPAWNS || status == TGameStatus.IN_GAME){
            e.setMotd("§8[§c§lInGame§8]");
        }
        if(status == TGameStatus.DEATH_MATCH || status == TGameStatus.DEATH_MATCH_GRACE_PERIOD || status == TGameStatus.DEATH_MATCH_ON_SPAWNS){
            e.setMotd("§8[§4§lDeathMatch§8]");
        }
        if(status == TGameStatus.END){
            e.setMotd("§8[§4§lRestarting§8]");
        }
    }
}
