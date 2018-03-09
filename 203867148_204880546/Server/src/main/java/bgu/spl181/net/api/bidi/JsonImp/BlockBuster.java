package bgu.spl181.net.api.bidi.JsonImp;

import java.util.ArrayList;
import com.google.gson.annotations.SerializedName;
import bgu.spl181.net.api.bidi.Movie;

/**
 * 
 * The BlockBuster class holds an ArrayList of movies, which will be used for Json's parsing purposes. 
 *
 */
public class BlockBuster {

	 @SerializedName("movies")
	    protected ArrayList<Movie> movies;

	 /**
	  * @param movies - An ArrayList of movies.
	  */
	 public BlockBuster(ArrayList<Movie> movies) {
		 this.movies=movies;
	 }
	 
	 /**
	  * @return An ArrayList of movies.
	  */
	 public ArrayList<Movie> getMovies() {
	        return movies;
	    }
}
