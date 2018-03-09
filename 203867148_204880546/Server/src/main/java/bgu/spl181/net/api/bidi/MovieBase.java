package bgu.spl181.net.api.bidi;

import com.google.gson.annotations.SerializedName;

/**
 * 
 * The movie's base data structure which includes its id and name.
 *
 */
public class MovieBase {
	
	@SerializedName("id")
	protected long id;
    @SerializedName("name")
	protected String name;
   
    
    /**
     * Basic constructor:
     * @param id - The movie's ID.
     * @param name - The movie's name.
     */
    public MovieBase(long id, String name) {
		this.id=id;
		this.name=name;
	}
    
    /**
     * Basic constructor:
     * @return name - The movie's name.
     */
    public String getName() {
    	return name;
    }
}