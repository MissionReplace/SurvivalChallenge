package de.TerraStormDE.SurvivalChallenge.UserDatabase;

import java.util.UUID;

public class UserProfile {
	
	
	private UUID uuid;
	private String name;
	
	public UserProfile(String name,UUID uuid){
		this.name = name;
		this.uuid = uuid;
	}
	
	public UUID getUUID(){
		return uuid;
	}
	public String getName(){
		return name;
	}

}
