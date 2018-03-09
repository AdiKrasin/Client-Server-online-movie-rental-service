package bgu.spl181.net.api.bidi;

import java.util.Vector;

import com.google.gson.annotations.SerializedName;

/**
 * This class holds the information regarding the movie-user.
 */
public class MovieUser extends User {
	
    @SerializedName("type")
	protected String type;
    @SerializedName("country")
	protected String country;
    @SerializedName("balance")
	protected long balance;
    @SerializedName("movies")
	protected Vector<MovieBase> movies;
	
	/**
	 * Builder - Initializes the required fields.
	 * @param name - The user's name.
	 * @param password = The user's password.
	 */
	public MovieUser(String name, String password) {
		super(name, password);
	}
	
	/**
	 * Builder - Initializes the required fields.
	 * @param name - The user's name.
	 * @param password = The user's password.
	 * @param type = The user's type - normal or admin.
	 * @param country = The user's origin country.
	 * @param balance = The user's credit.
	 * @param movies = The list of movies that are currently rented by this user. 
	 */
	public MovieUser(String name, String password, String type, String country, long balance, Vector<MovieBase> movies) {
		super(name, password);
		this.type=type;
		this.country=country;
		this.balance=balance;
		this.movies=movies;
		
	}
	
	/**
	 * @return The type of the user, normal or admin.
	 */
	public String getType() {
		return type;
	}
	
	/**
	 * @return The user's origin country.
	 */
	public String getCountry() {
		return country;
	}
	
	/**
	 * @return The user's credit.
	 */
	public long getBalance() {
		return balance;
	}
	
	/**
	 * @return The list of movies that are currently rented by the user.
	 */
	public Vector<MovieBase> getMovies() {
		return movies;
	}
	/**
	 * @return A description of the user.
	 */
	public String toString() {
		return ("<" + username + ", " + type + ", " + password + ", " + country + ", " + movies + ", " + balance + ">");
	}
	
	/**
	 * This function adds money to the user's credit.
	 * @param amout = The amount of money that should be added to the user's credit.
	 */
	public void increaseBalanceBy(long amount) {
		balance=balance+amount;
	}
	
	/**
	 * This function takes money from the user's credit.
	 * @param amout = The amount of money that should be taken from the user's credit.
	 */
	public void decreaseBalanceBy(long amount) {
		balance=balance-amount;
	}

}