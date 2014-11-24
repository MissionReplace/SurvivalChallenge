/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package de.TerraStormDE.SurvivalChallenge.Enums;

import java.util.Arrays;

/**
 *
 * @author Hannes
 */
public enum TConfigPath {
    
    PLUGIN_PREFIX("messages.prefix","&7[&eSurvivalChallenge&7] "),
    
    MESSAGE_JOIN("messages.join","&e%p% &7hat das Spiel betreten!"),
    MESSAGE_QUIT("messages.quit","&e%p% &7hat das Spiel verlassen!"),
    MESSAGE_LOBBY_START("messages.lobby_start","Es sind nun genügend Spieler online!%n%Das Spiel startet in kürze..."),
    MESSAGE_LOBBY_BREAK("messages.lobby_break","Es sind nicht genügend Spieler online!%n%Der Lobbycountdown wurde abgebrochen!"),
    MESSAGE_LOBBY_COUNT("messages.lobby_count","In %count% Sekunden beginnt das Spiel!"),
    MESSAGE_VOTE_WIN("messages.vote_win","&7Das Voting wurde beendet!%n%Die Map &e%map% hat gewonnen!"),
    MESSAGE_MODUL_WIN("messages.modul_win","&7Es wird mit dem Modul &e%modul% &7gespielt!"),
    MESSAGE_GAME_COUNT("messages.game_start","Das Spiel startet in &e%count% &7Sekunden!"),
    MESSAGE_INFO_START("messages.info","Es wird auf der Map &e%map% &7gespielt.%n%Die Map wurde von &e%builder% &7gebaut"),
    MESSAGE_GAME_MOVE_COUNT("messages.game_move","In &e%count% &7Sekunden könnt ihr euch bewegen!"),
    MESSAGE_GAME_GREACE("messages.greace_periode","In &e%count% &7Sekunden könnt ihr kämpfen!"),
    MESSAGE_NOT_ENOTH_PLAYER("messages.not_enoth_player","Es sind nicht genügend Spieler online!"),
    MESSAGE_DEATHMATCH_COUNT("messages.deathmatch_count","Das &eDeathmatch &7startet in &e%count% &7Sekunden!"),
    MESSAGE_DEATHMATCH_GRACE_COUNT("messages.deathmatch_grace_count","In &e%count% &7Sekunden ist die Schutzzeit vorbei!"),
    MESSAGE_SERVER_RESTART("messages.restart_server","Der Server restartet in &e%count% &7Sekunden!"),
    
    MESSAGE_PLAYER_DEATH("messages.player_death","&e%entity% &7wurde von &e%killer%&7 getötet!"),
    
    MYSQL_USER("mysql.user","user"),
    MYSQL_PASSWORT("mysql.passwort","passwort"),
    MYSQL_DATABASE("mysql.database","database"),
    MYSQL_HOST("mysql.host","host"),
    
    
    SETTINGS_MIN_PLAYER("settings.min_player",4),
    SETTINGS_MAX_PLAYER("settings.max_player",12),
    SETTNGS_GAME_LENGHT("settings.game_lenght",25),
    SETTINGS_LOBBY_COUNT("settings.lobby_count",60),
    
    SETTINGS_DEATHMATCH_GRACE_COUNT("settings.deathmatch_grace",10),
    SETTINGS_DEATHMATCH_START_AMOUNT("settings.deathmatch_start",2),
    SETTINGS_GAME_GRACE("settings.game_grace",40),
    SETTINGS_GAME_MOVE_SEND("settings.send_game_move_at",5),
    SETTINGS_SERVER_FALLBACK("settings.fallback_server","Lobby"),
    SETTINGS_ENABLE_MODULS("settings.enable_moduls",true),
    
    
    SETTINGS_STATS_TOP_ORDER("statssystem.stats_order_type","kills"),
    SETTINGS_STATS_TOP_AMOUNT("statssystem.top_stats_amount",5),
    
    
    SETTINGS_VOTE_TO_COUNT("votesystem.to_count",25),
    SETTINGS_VOTE_MAP_AMOUNT("votesystem.map_amount",3),
    SETTINGS_VOTE_MODUL_RANDOM("votesystem.get_random_modul",false),
    SETTINGS_VOTE_MODUL_AMOUNT("votesystem.modul_amount","auto"),
    SETTINGS_VOTE_ITEM_POS_DIFF("votesystem.vote_item_pos_diff",2),
    
    SETTINGS_MAP_VOTE_ITEM("votesystem.map.item_id",339),
    SETTINGS_MAP_VOTE_ITEM_NAME("votesystem.map.item_name","&6&lMapvoting"),
    SETTINGS_MAP_VOTE_INV_NAME("votesystem.map.inventory_name","&6&lMapvoting"),
    
    SETTINGS_SC_VOTE_ITEM("votesystem.modul.item_id",55),
    SETTINGS_SC_VOTE_ITEM_NAME("votesystem.modul.item_name","&6&lModulvoting"),
    SETTINGS_SC_VOTE_INV_NAME("votesystem.modul.inventory_name","&6&lModulvoting"),
    
    VOTE_BOARD_SWITCH_DELAY("voteboard.switch_delay",5),
    MAP_VOTE_BOARD_NAME("voteboard.map.name","&eVoting"),
    MAP_VOTE_BOARD_ROW("voteboard.map.row","&e(%id%) %map%"),
    
    
    SC_VOTE_BOARD_NAME("voteboard.sc.name","&eSurvivalChallenges"),
    SC_VOTE_BOARD_ROW("voteboard.sc.row","&e(%id%) %challenge%"),
    
    
    CHEST_ROWS("chest.rows",3),
    CHEST_NAME("chest.name","Chest"),
    CHEST_MAX_ITEMS("chest.max_items",6),
    CHEST_BLOCK("chest.block","54"),
    CHEST_REFILL("chest.enable_refill",true),
    CHEST_REFILL_TIME("chest.refill_time",18000),
    
    RANKING_ORDER_TYPE("ranking.order_type","kills"),
    RANKING_SINGS_LINES("ranking.sings.lines",Arrays.asList("&a&l%name%","&c&lKills: &8%kills%","&9&lDeaths: &8%deaths%")),
    
    NICK_ITEM_ID("nicksystem.item_id","421"),
    NICK_ITEM_NAME("nicksystem.item_name","&6&lNickchanger"),
    NICK_ITEM_PERMISSIONS("nicksystem.item_permissions","sc.nick"),
    NICK_ITEM_DENY("nicksystem.item_deny","Diese Funktion kannst du nur als &ePremiumspieler &7nutzen!"),
    NICK_SET_AUTO("nicksystem.set_auto",false);
    
    private String path;
    private Object value;
    
    private TConfigPath(String path,Object default_value){
        this.path = path;
        this.value = default_value;
    }
    
    public String getPath(){
        return path;
    }
    public Object getDefaultValue(){
        return value;
    }
    
}
