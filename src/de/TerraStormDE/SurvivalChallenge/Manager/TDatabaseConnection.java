package de.TerraStormDE.SurvivalChallenge.Manager;

import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import org.bukkit.Bukkit;

public class TDatabaseConnection {
	
	private String user;
	private String passwort;
	private String host;
	private String db;
	
	private Connection con;
        
        public TDatabaseConnection(String user,String passwort,String database,String host){
            this.user = user;
            this.passwort = passwort;
            this.db = database;
            this.host = host;
        }
	
	public void openConnection(){
		try {
			Connection con = DriverManager.getConnection("jdbc:mysql://" + host + ":3306/" + db, user, passwort);
			this.con = con;
			System.out.println("[SurvivalChallenge] Verbindung zur MySQL Datenbank hergestellt.");
		} catch (SQLException e) {
                    Bukkit.broadcastMessage("[SurvivalChallenge] Verbindung Fehlgeschlagen, Plugin wird gestoppt!");
			Bukkit.getPluginManager().disablePlugin(SurvivalChallenge.getInstance());
		}
	}
	public void closeConnection(){
		try {
			if(con != null && !con.isClosed() && con.isValid(1)){
				con.close();
				System.out.println("[SurvivalChallenge] Verbindung zur MySQL Datenbank geschlossen.");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void queryUpdate(String query){
		Statement st = null;
		Connection con = this.con;
		try {
			st = con.createStatement();
			st.executeUpdate(query);
		} catch (SQLException e) {
			System.out.println("[SurvivalChallenge] Fehler beim Ausf�hren des MySQL Befehls: " + query);
			e.printStackTrace();
		}
	}
	public void closeResources(PreparedStatement st, ResultSet rs){
		try{
			if(st != null){
				st.close();
			}
			if(rs != null){
				rs.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
	}
	public void reconnect(){
		closeConnection();
		openConnection();
	}
	public String getUser(){
		return user;
	}
	public String getPasswort(){
		return passwort;
	}
	public String getHost(){
		return host;
	}
	public String getDatabase(){
		return db;
	}
	public int getPort(){
		return 3306;
	}
	public Connection getConnection(){
		return this.con;
	}
	public void setUser(String user){
		this.user = user;
	}
	public void setPasswort(String passwort){
		this.passwort = passwort;
	}
	public void setHost(String host){
		this.host = host;
	}
	public void setDatabase(String database){
		this.db = database;
	}
	@Deprecated
	public void setConnection(Connection con){
		this.con = con;
	}
	public boolean hasConnection(){
		try {
			if(getConnection() != null && !getConnection().isClosed() && getConnection().isValid(1)){
				return true;
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	
}
