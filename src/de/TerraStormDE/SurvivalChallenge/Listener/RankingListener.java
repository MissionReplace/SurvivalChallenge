/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Listener;

import de.TerraStormDE.SurvivalChallenge.Manager.TRankingObjectManager;
import de.TerraStormDE.SurvivalChallenge.Ranking.TRankingHead;
import de.TerraStormDE.SurvivalChallenge.Ranking.TRankingObject;
import de.TerraStormDE.SurvivalChallenge.Ranking.TRankingSign;
import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.SignChangeEvent;

/**
 *
 * @author Hannes
 */
public class RankingListener implements Listener
{
    
    private final SurvivalChallenge plugin;
    
    public RankingListener(SurvivalChallenge instance){
        this.plugin = instance;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler
    public void block_break(BlockBreakEvent e){
        TRankingObject ob;
        if((ob = TRankingObject.getbyLocation(e.getBlock().getLocation())) != null){
            plugin.getRankingManager().removeRankingObjects(e.getBlock().getLocation());
            if(ob instanceof TRankingHead){
                e.getPlayer().sendMessage(plugin.getPrefix() + "Rankinghead wurde erfolgreich entfernt!");
                ((TRankingHead)ob).remove();
            }
            if(ob instanceof TRankingSign){
                e.getPlayer().sendMessage(plugin.getPrefix() + "Rankingschild wurde erfolgreich entfernt!");
                ((TRankingSign)ob).remove();
            }
        }
    }
    
    @EventHandler
    public void sign(SignChangeEvent e){
        if(e.getLine(0).equalsIgnoreCase("[Ranking]")){
            if(e.getLine(1).isEmpty()){
                e.setLine(1, "§c<pos>");
                e.getPlayer().sendMessage(plugin.getPrefix() + "Rangposition fehlt!");
                return;
            }
            try {
                int i = Integer.valueOf(e.getLine(1));
                plugin.getRankingManager().createRankingObject(e.getBlock().getLocation(), i, TRankingObjectManager.TRankingObjectType.RANKING_SIGN);
                e.getPlayer().sendMessage(plugin.getPrefix() + "Rankingsign erstellt!");
            }catch(NumberFormatException ex){
                e.getPlayer().sendMessage(plugin.getPrefix() + "Ungültige Rangposition");
                e.setLine(1, "§c<pos>");
            }
        }
    }
    
}
