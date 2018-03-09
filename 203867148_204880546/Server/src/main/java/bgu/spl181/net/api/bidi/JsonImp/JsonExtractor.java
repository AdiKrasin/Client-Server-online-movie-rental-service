package bgu.spl181.net.api.bidi.JsonImp;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;

import bgu.spl181.net.api.bidi.Movie;
import bgu.spl181.net.api.bidi.MovieUser;
import bgu.spl181.net.api.bidi.User;


/**
 * @author sagi 
 * The JsonExtractor class holds BlockBuster and UserManagment as parameters and it is used in order to read/write the Json files to the Database.  
 *
 */
public class JsonExtractor {

    protected BlockBuster blockBuster;
    protected UserManagement userManagement;
    protected Gson gson;

    /**
     * Constructing the Gson in order to set the long fields as String in addition to setting the pretty printing and implementing the initial reading.
     */
    public JsonExtractor(){
    	GsonBuilder gsonBuilder = new GsonBuilder();
    	gsonBuilder.setLongSerializationPolicy(LongSerializationPolicy.STRING);
    	gson = gsonBuilder.disableHtmlEscaping().setPrettyPrinting().create();
        getCurrentJsons(); // Implementing the initial reading. 
    }

    /**
     * Instantiate the current Users' ConcurrentHashMap within the Database's folder. 
     * @return - The current Users' ConcurrentHashMap.
     */
    public ConcurrentHashMap<String, User> getCurrentUsersDatabase() {
        ConcurrentHashMap<String, User> hashMapOfUsersData = new ConcurrentHashMap<>();
        ArrayList<MovieUser> movieUsersArr = userManagement.getUsers(); 
        for (User movieUser : movieUsersArr) { // Iterating each MovieUser and adding it to the map in case it does not already within it.
            if (!(hashMapOfUsersData.containsKey(movieUser.getUsername()))) {
                hashMapOfUsersData.put(movieUser.getUsername(), movieUser);
            }
        }
        return hashMapOfUsersData;
    }

    /**
     * Instantiate the current Movies' ConcurrentHashMap within the Database's folder. 
     * @return - The current Movies' ConcurrentHashMap.
     */
    public ConcurrentHashMap<String, Movie> getCurrentMoviesDatabase() { 
        ConcurrentHashMap<String, Movie> hashMapOfMoviesData = new ConcurrentHashMap<>();
        ArrayList<Movie> moviesArr = blockBuster.getMovies();
        for (Movie movie: moviesArr) { // Iterating each Movie and adding it to the map in case it does not already within it.
            if (!(hashMapOfMoviesData.containsKey(movie.getName())))
                hashMapOfMoviesData.put(movie.getName(), movie);
        }
        return hashMapOfMoviesData;
    }

    /**
     * Perform a reading from both "Movies.json" and "Users.json" and inserting it into the BlockBuster and UserManagment accordingly. 
     */
    public void getCurrentJsons() {
        //String workingDir=System.getProperty("user.dir"); CHECK
        //String moviesJsonPath=workingDir+ "\\Database\\Movies.json";
        //String usersJsonPath=workingDir+ "\\Database\\Users.json";
        try {
            FileReader moviesReader = new FileReader("Database/Movies.json");
            FileReader usersReader = new FileReader("Database/Users.json");


            blockBuster = gson.fromJson(moviesReader, BlockBuster.class);
            userManagement = gson.fromJson(usersReader, UserManagement.class);
        } catch (FileNotFoundException ex) {
            System.out.println("File not found");
        }
    }
    
    /**
     * Perform a writing into the Database folder according to the given maps. 
     * In case the received parameter is "null", the  json file will not be rewritten. 
     * @param moviesHashMap - Movies' ConcurrentHashMap.
     * @param userHashMap - Users' ConcurrentHashMap
     */
    public void updateJsons(ConcurrentHashMap<String, Movie> moviesHashMap, ConcurrentHashMap<String, User> userHashMap) {
    	if(moviesHashMap!=null) { // In case the movies' map is not null, performs a writing into "Movies.json":
    		blockBuster=new BlockBuster((new ArrayList<Movie> (moviesHashMap.values()))); // Extracting the Movie's values and constructing it into ArrayList.
    		try(Writer moviesWriter = new FileWriter("Database/Movies.json")){ // Writing into "Movies.json"
    			gson.toJson(blockBuster, moviesWriter);
    			// Extracting the Movie's values and constructing it into ArrayList.// Extracting the Movie's values and constructing it into ArrayList.
    		}
    		catch (IOException e) {
    			System.out.println("Movies.json has not been updated");
    		}
    	}
    	if(userHashMap!=null) { // In case the users' map is not null, performs a writing into "Users.json":
    		ArrayList<MovieUser> arr=new ArrayList<>();
    		for(Map.Entry<String, User> entry: userHashMap.entrySet()) { // Extracting the MovieUser's values and constructing it into ArrayList.
    			arr.add((MovieUser) entry.getValue());
    		}
    		userManagement=new UserManagement(arr);
    		try(Writer usersWriter = new FileWriter("Database/Users.json")){
    			gson.toJson(userManagement, usersWriter);
    		} 
    		catch (IOException e) {
    			System.out.println("Users.json has not been updated");
    		}
    	}
    }
    
    public static void main(String[] args) {
    	JsonExtractor exec=new JsonExtractor();
    	ConcurrentHashMap<String, Movie> movieHashMap=exec.getCurrentMoviesDatabase();
    	ConcurrentHashMap<String, User> userHashMap=exec.getCurrentUsersDatabase();
    	movieHashMap.put("Krasins", new Movie (10,"Krasins",200,new Vector<String>(),50,60));
    	exec.updateJsons(movieHashMap,userHashMap);
        }
}