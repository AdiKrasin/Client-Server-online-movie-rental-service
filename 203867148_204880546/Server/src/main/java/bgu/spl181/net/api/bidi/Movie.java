package bgu.spl181.net.api.bidi;

import java.util.Vector;

import com.google.gson.annotations.SerializedName;
/**
 * 
 * Extends the MovieBase and will handle any requests of the movie. 
 *
 */
public class Movie extends MovieBase {
	
    
    @SerializedName("price")
	protected long price;
    @SerializedName("bannedCountries")
	protected Vector<String> bannedCountries;
    @SerializedName("availableAmount")
	protected long availableAmount;
    @SerializedName("totalAmount")
	protected long totalAmount;
	
    /**
     * Basic constructor:
     * @param id - The movie's ID.
     * @param name - The movie's name.
     */
    public Movie(long id, String name) {
		super(id,name);
	}
    
    /**
     * Default constructor:
     * @param id - The movie's ID.
     * @param name - The movie's name.
     * @param price - The movie's price.
     * @param bannedCountries - The movie's vector of banned countries. 
     * @param availableAmount - The movie's available amount.
     * @param totalAmount - The movie's total amount.
     */
	public Movie(long id, String name, long price, Vector<String> bannedCountries, long availableAmount, long totalAmount ) {
		super(id,name);
		this.price=price;
		this.bannedCountries=bannedCountries;
		this.availableAmount=availableAmount;
		this.totalAmount=totalAmount;
	}
	
	/**
	 * 
	 * @return The movie's ID.
	 */
	public long getId() {
		return id;
	}
	
	/**
	 * 
	 * @return The movie's price.
	 */
	public long getPrice() {
		return price;
	}
	
	/**
	 * Setting the price to the received parameter. 
	 * @param price - The new movie's price.
	 */
	public void setPrice(long price) {
		this.price=price;
	}
	
	/**
	 * 
	 * @return The movie's vector of banned countries. 
	 */
	public Vector<String> getBannedCountries() {
		return bannedCountries;
	}
	
	/**
	 * 
	 * @return The movie's available amount.
	 */
	public long getAvailableAmount() {
		return availableAmount;
	}
	
	/**
	 * 
	 * @return The movie's total amount.
	 */
	public long getTotalAmount() {
		return totalAmount;
	}
	
	/**
	 * Printing the movie's parameters 
	 */
	public String toString() {
		return ("<" + id + ", " + name + ", " + price + ", b:" + bannedCountries + ", AA:" + availableAmount + ", TA:" + totalAmount + ">");
	}
	
	/**
	 * Decreasing the movie's available amount by one.
	 */
	public void decreaseAvailableAmount() {
		availableAmount=availableAmount-1;
	}
	
	/**
	 * Increasing the movie's available amount by one.
	 */
	public void increaseAvailableAmount() {
		availableAmount=availableAmount+1;
	}

}
