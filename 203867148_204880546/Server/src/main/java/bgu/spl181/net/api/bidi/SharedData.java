package bgu.spl181.net.api.bidi;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This class holds the map that contains that information regarding the users.
 */
public class SharedData {
	
	protected ConcurrentHashMap<String,User> mapOfUsersData;
	protected Object toLock=new Integer(0);
	protected Object rentToLock=new Integer(0);
	
	/**
	 * Builder - initializes the required field.
	 * @param map - The map that contains the information regarding the users.
	 */
	public SharedData(ConcurrentHashMap<String,User> map) {
		this.mapOfUsersData=map;
		
	}
	
	/**
	 * @return the map that contains the information regarding the users. 
	 */
	 public  ConcurrentHashMap<String,User> getMapOfUsersData(){
		    return mapOfUsersData;
	    }

}