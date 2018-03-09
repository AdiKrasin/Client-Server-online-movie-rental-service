package bgu.spl181.net.api.bidi;

import java.util.concurrent.ConcurrentHashMap;

/**
 * This class holds the maps that contains the information regarding the users and the movies.
 */
public class SharedMovieData extends SharedData{
	
	protected ConcurrentHashMap<String,Movie> mapOfMoviesData;
	
	/**
	 * Builder - initializes the required field.
	 * @param map - The map that contains the information regarding the users.
	 */
	public SharedMovieData(ConcurrentHashMap<String, User> map) {
		super(map);
	}
	
	/**
	 * Builder - initializes the required fields.
	 * @param map1 - The map that contains the information regarding the users.
	 * @param map2 = The map that contains the information regarding the movies. 
	 */	
	public SharedMovieData(ConcurrentHashMap<String, User> map1, ConcurrentHashMap<String, Movie> map2) {
		super(map1);
		this.mapOfMoviesData=map2;
	}
	
	/**
	 * @return the map that contains the information regarding the movies.
	 */
    public ConcurrentHashMap<String,Movie> getMapOfMoviesData(){
        return mapOfMoviesData;

    }
   

}