package bgu.spl181.net.api.bidi;

import com.google.gson.annotations.SerializedName;

/**
 * This class holds the information regarding the user.
 */
public class User {
	
	@SerializedName("username")
	protected String username;
    @SerializedName("password")
	protected String password;
    
	/**
	 * Builder - Initializes the required fields.
	 * @param username - The user's name.
	 * @param password = The user's password. 
	 */
	public User(String username, String password) {
		this.username=username;
		this.password=password;
	}
	
	/**
	 * @return The user's name.
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * @return The user's password. 
	 */
	public String getPassword() {
		return password;
	}
}