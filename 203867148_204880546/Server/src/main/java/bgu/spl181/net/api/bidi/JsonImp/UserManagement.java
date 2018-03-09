package bgu.spl181.net.api.bidi.JsonImp;

import java.util.ArrayList;
import com.google.gson.annotations.SerializedName;
import bgu.spl181.net.api.bidi.MovieUser;

/**
 * 
 * The UserManagement class holds an ArrayList of MovieUsers, which will be used for Json's parsing purposes. 
 *
 */
public class UserManagement {
	
	 @SerializedName("users")
	    protected ArrayList<MovieUser> users;
	 
	 /**
	  * @param movies - An ArrayList of MovieUser.
	  */
	 public UserManagement(ArrayList<MovieUser> users) {
		 this.users=users;
	 }

	 /**

	  * @return - An ArrayList of MovieUser.
	  */
	public ArrayList<MovieUser> getUsers() {
	        return users;
	    }
}
