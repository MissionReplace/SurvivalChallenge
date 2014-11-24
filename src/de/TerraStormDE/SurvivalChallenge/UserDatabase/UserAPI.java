package de.TerraStormDE.SurvivalChallenge.UserDatabase;

import de.TerraStormDE.SurvivalChallenge.SurvivalChallenge;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

public class UserAPI {
    private final SurvivalChallenge plugin;
	
	public UserAPI(SurvivalChallenge instance){
           
            this.plugin = instance;
		
	}
	public UserProfile getUser(UUID uuid){
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = plugin.getConnection().getConnection();
		
		try {
			st = con.prepareStatement("SELECT * FROM userdb WHERE uuid=?");
			st.setString(1, uuid.toString());
			rs = st.executeQuery();
			rs.last();
			if(rs.getRow() != 0){
				rs.first();
				return new UserProfile(rs.getString("name"),UUID.fromString(rs.getString("uuid")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	public UserProfile getUser(String name){
		PreparedStatement st = null;
		ResultSet rs = null;
		Connection con = plugin.getConnection().getConnection();
		
		try {
			st = con.prepareStatement("SELECT * FROM userdb WHERE name=?");
			st.setString(1,name);
			rs = st.executeQuery();
			rs.last();
			if(rs.getRow() != 0){
				rs.first();
				return new UserProfile(rs.getString("name"),UUID.fromString(rs.getString("uuid")));
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	public void createUser(String name,UUID uuid){
		UserProfile n = getUser(name);
		UserProfile u = getUser(uuid);
		
		if(n == null && u == null){
			plugin.getConnection().queryUpdate("INSERT INTO userdb(name,uuid) VALUES ('" + name + "','" + uuid.toString() + "')");
		} else {
			plugin.getConnection().queryUpdate("UPDATE userdb SET name='" + name + "' WHERE uuid='" + uuid.toString() + "'");
		}
	}
        public void createTable(){
            if(plugin.getConnection().hasConnection()){
                plugin.getConnection().queryUpdate("CREATE TABLE IF NOT EXISTS userdb(name VARCHAR(16), uuid VARCHAR(70))");
            }
        }
}
