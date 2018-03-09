package bgu.spl181.net.api.bidi;

import java.util.Map;
import java.util.Vector;

import bgu.spl181.net.api.bidi.SharedMovieData;
import bgu.spl181.net.api.bidi.MovieUser;

/**
 * This  class is the class of the MovieRentalProtocol, in which we process all of the messaged relevant
 * to the MovieRental. Therefore, all of the requests starting with the word REQUEST are being handled
 * in this class.
 */
public class MovieRentalProtocol<T> extends BidiMessagingProtocolImp<T> {
	
	/**
	 * Builder- Initializes the required field.
	 * @param sharedData - The SharedData Object. 
	 */
	public MovieRentalProtocol(SharedData sharedData) {
		super(sharedData);
	}
	
	/**
	 * This function processes the request.
	 * @param msg = The message which should be processed. 
	 */
	public void process(T message) {
		String messageString=(String) message;
		String[] arr=messageString.split(" ");
		switch(arr[0]) {
		case "REGISTER":
			super.process(message);
		break;
		case "LOGIN":
			super.process(message);
			break;
		case "SIGNOUT":
			super.process(message);
			break;
		case "REQUEST":
			processRequest(message);
			break;
		}
	}
	
	/**
	 * This function processes the request.
	 * @param msg = The message which should be processed. 
	 */
	public void processRequest(T message) {
		String messageString=(String) message;
		String[] arr=messageString.split(" ");
		switch(arr[1]) {
		case "balance":
			processBalance(message);
			break;
		case "info":
			processInfo(message);
			break;
		case "rent":
			processRent(message);
			break;
		case "return":
			processReturn(message);
			break;
			//Admin section
		case "addmovie":
			processAddMovie(message);
			break;
		case "remmovie":
			processRemMovie(message);
			break;
		case "changeprice":
			processChangePrice(message);
			break;
		}
	}
	
	/**
	 * This function processes the balance requests.
	 * @param msg = The message which should be processed. 
	 */
	public void processBalance(T message) {
		String messageString=(String) message;
		String[] arr=messageString.split(" ");
		switch(arr[2]) {
		case "info":
			processBalanceInfo(message);
			break;
		case "add":
			processBalanceAdd(message);
			break;
			}
		}
	
	/**
	 * This function is in charge of sending the information regarding the credit of the client to the client.
	 * @param msg = The message which should be processed. 
	 */
	public void processBalanceInfo(T msg) {
		if(loggedIn.compareAndSet(true, true)) { // In case this client is currently logged in:
			Long balance = new Long(((MovieUser)(sharedData.getMapOfUsersData().get(myName))).getBalance());
			connections.send(connectionId, (T)("ACK balance "+balance.toString())); // Sending the proper ACK message
		}
		else { // Otherwise:
			connections.send(connectionId, (T)("ERROR request balance info failed")); // Sending the proper ERROR message
		}
	}
	
	/**
	 * This function is in charge of adding the required amount of money to the credit of the client.
	 * @param msg = The message which should be processed. 
	 */
	public void processBalanceAdd(T msg) {
		String messageString=(String) msg;
		String[] arr=messageString.split(" ");
		String amountString = arr[3];
		Integer amount= Integer.valueOf(amountString);
		if(loggedIn.compareAndSet(true, true)) { // In case this client is currently logged in:
			((MovieUser)(sharedData.getMapOfUsersData().get(myName))).increaseBalanceBy(amount.intValue());
			Long newBalance = new Long(((MovieUser)(sharedData.getMapOfUsersData().get(myName))).getBalance());
			synchronized (sharedData.toLock){ // Changing the json accordingly:
				jsonExtractor.updateJsons(null, sharedData.getMapOfUsersData());
			}
			connections.send(connectionId, (T)("ACK balance "+newBalance.toString()+ " added "+amount.toString())); // Sending the proper ACK message
	
		}
			else { // Otherwise:
				connections.send(connectionId, (T)("ERROR request balance add " + amount.toString() + " failed")); // Sending the proper ERROR message
			}
		
	}
	
	/**
	 * This function returns the required information regarding a requested movie or regarding
	 * which movies there are.
	 * @param msg = The message which should be processed. 
	 */
	public void processInfo(T msg) { 
		String movieName="";
		String messageString=(String) msg;
		String[] arr=messageString.split(" ");
		if (arr.length>2) { // In case the client requested info regarding a specific movie, extracting the name of the given movie:
			for(int i=2; i<arr.length; i++) {
				movieName=movieName+" "+arr[i];}
			movieName=movieName.substring(2,movieName.length()-1);}
		if(loggedIn.compareAndSet(true, true)) { // In case this client is currently logged in:
			if (movieName.equals("")) { // In case the client is not requesting info regarding a specific movie:
				String stringListOfMovies="";
				synchronized (sharedData.rentToLock){
				for(Map.Entry<String, Movie> entry:  ((SharedMovieData)sharedData).getMapOfMoviesData().entrySet()) {
					stringListOfMovies=stringListOfMovies+" "+ '"' + entry.getKey() + '"';}
				}
				stringListOfMovies=stringListOfMovies.substring(1);
				connections.send(connectionId, (T)("ACK info "+stringListOfMovies)); // Sending the proper ACK message
			}
			else { // Otherwise, the client is requesting info regarding a specific movie, therefore:
				if(!(((SharedMovieData)sharedData).getMapOfMoviesData().containsKey(movieName))) { // In case the movie does not exist:
					connections.send(connectionId, (T)("ERROR request info " + '"' + movieName + '"' + " failed")); // Sending the proper ERROR message
				}
				else { // Otherwise, the movie exists, therefore:
					processInfoHelper(movieName);
				}}}
		else { // Otherwise, the client is not currently logged in, therefore:
			if (movieName.equals("")) {
				connections.send(connectionId, (T)("ERROR request info failed")); // Sending the proper ERROR message
			}
			else {
			connections.send(connectionId, (T)("ERROR request info " + '"' + movieName + '"' + " failed")); // Sending the proper ERROR message
			}}}
	
	
	/**
	 * This function helps the processInfo function to finish it's job.
	 * @param movieName = The name of the movie which the info is required about. 
	 */
	public void processInfoHelper(String movieName) {
		Movie movie = ((SharedMovieData)sharedData).getMapOfMoviesData().get(movieName);
		Long availableAmount = new Long(movie.getAvailableAmount());
		Long price = new Long(movie.getPrice());
		Vector<String> bannedCountries = movie.getBannedCountries();
		String stringAvailableAmount = availableAmount.toString();
		String stringPrice = price.toString();
		String stringBannedCountries="";
		if (bannedCountries.size()>0) {
		for (int i=0; i<bannedCountries.size(); i++) {
			stringBannedCountries=stringBannedCountries+" "+ '"' +bannedCountries.get(i) + '"';}
		stringBannedCountries=stringBannedCountries.substring(1);
		}
		if(bannedCountries.size()>0)
			connections.send(connectionId,(T)("ACK info "+ '"' + movieName+ '"' + " "+stringAvailableAmount+ " "+stringPrice + " "+stringBannedCountries)); // Sending the proper ACK message
		else
			connections.send(connectionId,(T)("ACK info "+ '"' + movieName+ '"' + " "+stringAvailableAmount+ " "+stringPrice)); // Sending the proper ACK message

	}
	
	/**
	 * This function assists the client with renting the required movie.
	 * @param msg = The message which should be processed. 
	 */
	public void processRent(T msg) {
		String movieName="";
		String messageString=(String) msg;
		String[] arr=messageString.split(" ");
		for(int i=2; i<arr.length; i++) { // Extracting the name of the requested movie:
			movieName=movieName+" "+arr[i];}
		movieName=movieName.substring(2,movieName.length()-1);
		if(loggedIn.compareAndSet(true, true)) { // In case this client is currently logged in:
			synchronized (sharedData.rentToLock){
				boolean canRent=true; // Initializing a boolean which will indicate whether or not the client can rent the required movie
				if (!(((SharedMovieData)sharedData).getMapOfMoviesData().containsKey(movieName))) { // In case the movie does not exist:
					canRent=false;
				}
				if(canRent) {
				Movie movie = ((SharedMovieData)sharedData).getMapOfMoviesData().get(movieName);
				MovieUser user = (MovieUser)sharedData.getMapOfUsersData().get(myName);
				if (canRent && user.getBalance()<movie.getPrice()) { // In case the client does not have enough credit in order to rent the movie:
					canRent=false;}
				if (canRent && movie.availableAmount==0) { // In case there are not available copies of the movie:
					canRent=false;}
				if (canRent && movie.bannedCountries.contains(user.getCountry())) { // In case the country of the client is among the banned countries list of this movie:
					canRent=false;}
				for (int i=0; canRent && i<user.getMovies().size(); i++) { // In case the client is currently renting this movie:
					if(user.getMovies().get(i).getName().equals(movieName)) {
						canRent=false;}}}
				if(canRent) { // Eventually, in case the client can rent the requested movie:
					processRentHelper(movieName);}
				else { // Otherwise, the client can not rent this movie, therefore: sending the proper ERROR message
					connections.send(connectionId, (T)("ERROR request rent " + '"' + movieName  +'"' + " failed"));}}}
		else { // Otherwise, in case the client is not currently logged in, therefore: sending the proper ERROR message
			connections.send(connectionId, (T)("ERROR request rent " + '"' + movieName + '"' + " failed"));}}
	
	/**
	 * This function helps the processRent function complete it's job.
	 * @param movieName = The name of the movie that's about to be rented.
	 */
	public void processRentHelper(String movieName) {
		Movie movie = ((SharedMovieData)sharedData).getMapOfMoviesData().get(movieName);
		MovieUser user = (MovieUser)sharedData.getMapOfUsersData().get(myName);
		user.decreaseBalanceBy(movie.price);
		movie.decreaseAvailableAmount();
		user.getMovies().add(new MovieBase (movie.getId(), movie.getName()));
		synchronized (sharedData.toLock){ // Changing the json accordingly:
			jsonExtractor.updateJsons(((SharedMovieData)sharedData).getMapOfMoviesData(), sharedData.getMapOfUsersData());}
		connections.send(connectionId, (T)("ACK rent " + '"' + movieName + '"' + " success")); // Sending the proper ACK message
		Long amount=new Long(movie.getAvailableAmount());
		String stringAmount=amount.toString(); 
		Long price = new Long(movie.getPrice());
		String stringPrice = price.toString();
		((ConnectionsImp)connections).LoggedInBroadcast((T)("BROADCAST movie " + '"' + movieName+ '"' + " " + stringAmount + " " +stringPrice));
	}
	
	
	/**
	 * This function assists the client with returning a required movie.
	 * @param msg = The message which should be processed. 
	 */
	public void processReturn(T msg) {
		String movieName="";
		String messageString=(String) msg;
		String[] arr=messageString.split(" ");
		for(int i=2; i<arr.length; i++) { // Extracting the name of the requested movie:
			movieName=movieName+" "+arr[i];}
		movieName=movieName.substring(2, movieName.length()-1);
		if(loggedIn.compareAndSet(true, true)) { // In case this client is currently logged in:
			boolean canReturn=true; // Initializing a boolean which will indicate whether or not the client can return the provided movie
			if (!(((SharedMovieData)sharedData).getMapOfMoviesData().containsKey(movieName))) { // In case the movie does not exist:
				canReturn=false;}
			if (!canReturn) {
				connections.send(connectionId, (T)("ERROR request return " + '"' + movieName + '"' + " failed")); // Sending the proper ERROR message
			}
			if(canReturn) {
			Movie movie = ((SharedMovieData)sharedData).getMapOfMoviesData().get(movieName);
			MovieUser user = (MovieUser)sharedData.getMapOfUsersData().get(myName);
			canReturn=false;
			for (int i=0; !canReturn && i<user.getMovies().size(); i++) { // In case the client is not renting this movie:
				if(user.getMovies().get(i).getName().equals(movieName)) {
					canReturn=true;}}
			if(canReturn) { // In case the client can return the movie:
				processReturnHelper(user, movieName, movie);}
			else { //Otherwise, the client can not return the movie, therefore:
				connections.send(connectionId, (T)("ERROR request return " + '"' + movieName + '"' + " failed")); // Sending the proper ERROR message
					}}}
		
		else { //Otherwise, in the case the client is not currently logged in, therefore:
			connections.send(connectionId, (T)("ERROR request return " + '"' + movieName+ '"' + " failed")); // Sending the proper ERROR message
			}}
	
	
	/**
	 * This function assists the processReturn function in completing it's work.
	 * @param user = The user that is returning the movie.
	 * @param movieName = The name of the movie that is about to be returned.
	 * @param movie = The movie that is about to be returned. 
	 */
	public void processReturnHelper(MovieUser user, String movieName, Movie movie) {
		boolean isFound=false;
		for (int i=0; !isFound && i<user.getMovies().size(); i++) { // In case the client is not renting this movie:
			if(user.getMovies().get(i).getName().equals(movieName)) {
				isFound=true;}
			if(isFound) {
				user.getMovies().remove(i);}}
		connections.send(connectionId, (T)("ACK return " + '"' +movieName + '"' + "success")); // Sending the proper ACK message
		synchronized (sharedData.rentToLock){
			movie.increaseAvailableAmount();
			Long amount = new Long(movie.getAvailableAmount());
			String stringAmount = amount.toString();
			Long price = new Long(movie.getPrice());
			String stringPrice = price.toString();
			((ConnectionsImp)connections).LoggedInBroadcast((T)("BROADCAST movie " + '"' + movieName+ '"' + " " + stringAmount+ " " +stringPrice));}
		synchronized (sharedData.toLock){ // Changing the json accordingly:
			jsonExtractor.updateJsons(((SharedMovieData)sharedData).getMapOfMoviesData(), sharedData.getMapOfUsersData());}
	}
	
	/**
	 * This function adds a new movie.
	 * @param msg = The message which should be processed. 
	 */
	public void processAddMovie(T msg) {
		String messageString=(String) msg;
		int firstQuationOfName=messageString.indexOf('"'); //Extracting the movie's name by the char (") 
		int lastQuationOfName=messageString.indexOf('"', firstQuationOfName+1);
		String name=messageString.substring(firstQuationOfName+1,lastQuationOfName);
		boolean legalRequest=type.equals("admin");// Flag in order to indicate if the user is actually an admin.
		if(loggedIn.compareAndSet(true, true) && legalRequest) { // In case this user is indeed an admin and is currently logged in
			messageString=messageString.substring(lastQuationOfName+2);
			String[] arr=messageString.split(" ");
			Integer amount= Integer.valueOf(arr[0]);
			String priceString=arr[1];
			Integer price= Integer.valueOf(priceString);
			String bannedCountryString=messageString.substring((arr[0].length()+arr[1].length()-1)); // Subtracting the price and amount within the string. 
			Vector<String> bannedCountries=new Vector<>();
			if(((SharedMovieData) sharedData).getMapOfMoviesData().containsKey(name)|| price<=0 || amount<=0 ) { // Verifying that the new movie's name does not exist and its price and amount are greater than 0.
				legalRequest=false;}
			else { // In case its a legal request:
				if (arr.length>2) { // In case the admin requested to add banned countries:
					bannedCountryString=bannedCountryString.substring(1); // Removing the additional space.
					while(!bannedCountryString.equals("")) {
						int firstQuation=bannedCountryString.indexOf('"');
						int lastQuation=bannedCountryString.indexOf('"', firstQuation+1);
						String country=bannedCountryString.substring(firstQuation+1,lastQuation);
						bannedCountries.add(country);
						bannedCountryString=bannedCountryString.substring(lastQuation+1);}}
					long maxID=processAddmovieMaxID();
				Movie newMovie=new Movie(maxID,name,price.longValue(),bannedCountries,amount,amount);
				processAddmovieHelper(newMovie);}}
		if(legalRequest==false) { // Otherwise:
			connections.send(connectionId, (T)("ERROR request addmovie " + '"' + name + '"' +" failed")); // Sending the proper ERROR message
		}}
	
	/**
	 * This function assists the processAddMovie function in extracting the maxID. 
	 * @return The maxID.
	 */
	public long processAddmovieMaxID() {
		long maxID=0; // Initial ID. 
		for(Map.Entry<String, Movie> entry:((SharedMovieData) sharedData).getMapOfMoviesData().entrySet()) {
			Movie tempMovie=entry.getValue();
			if(tempMovie.getId()>maxID) {
				maxID=tempMovie.getId();}}
		maxID++; // Increasing the new movie's ID to be the highest ID in the system+1;
		return maxID;
	}
	
	
	/**
	 * This function assists the processAddMovie function in completing its task. 
	 */
	public void processAddmovieHelper(Movie newMovie) {
		synchronized (sharedData.rentToLock) {
		((SharedMovieData) sharedData).getMapOfMoviesData().put(newMovie.getName(), newMovie);
		connections.send(connectionId, (T)("ACK addmovie "+ '"' + newMovie.getName() + '"' + " success")); // Sending the proper ACK message
		((ConnectionsImp)connections).LoggedInBroadcast((T)("BROADCAST movie " + '"' + newMovie.getName() + '"' + " " + newMovie.getTotalAmount() + " " + newMovie.getPrice())); // Broadcasting the proper BROADCAST message.
		}
		synchronized (sharedData.toLock){ // Changing the json accordingly:
			jsonExtractor.updateJsons(((SharedMovieData) sharedData).getMapOfMoviesData(), null);}
		
	}
	
	/**
	 * This function removes a required movie.
	 * @param msg = The message which should be processed. 
	 */
	public void processRemMovie(T msg) {
		boolean legalRequest=type.equals("admin");// Flag in order to indicate if the user is actually an admin.
		String messageString=(String) msg;
		String[] arr=messageString.split(" ");
		String movieName="";
		if (arr.length>2) { // In case the name contains spaces:
			for(int i=2; i<arr.length; i++) {
				movieName=movieName+" "+arr[i];}
			movieName=movieName.substring(2,movieName.length()-1);}
		if(loggedIn.compareAndSet(true, true) && legalRequest) { // In case this user is indeed an admin and is currently logged in
			synchronized (sharedData.rentToLock) {
				if(((SharedMovieData) sharedData).getMapOfMoviesData().containsKey(movieName)==true){ // In case the movie does exist:
					Movie movieToRemove=((SharedMovieData) sharedData).getMapOfMoviesData().get(movieName);
					if(movieToRemove.getTotalAmount()!=movieToRemove.getAvailableAmount()) { // Verifying that the there are no rented copies, otherwise changing the the flag accordingly
						legalRequest=false;}}
				else { // In case the movie does not exist, modifying the flag accordingly:
					legalRequest=false;}
				if(legalRequest==true) { // Removing the movie:
					((SharedMovieData) sharedData).getMapOfMoviesData().remove(movieName);
					synchronized (sharedData.toLock){ // Changing the json accordingly:
						jsonExtractor.updateJsons(((SharedMovieData) sharedData).getMapOfMoviesData(), null);}
					connections.send(connectionId, (T)("ACK remmovie "+ '"' + movieName+ '"' + " success")); // Sending the proper ACK message
					((ConnectionsImp)connections).LoggedInBroadcast((T)("BROADCAST movie " + '"' + movieName + '"' + " removed")); // Broadcasting the proper BROADCAST message.
				}}}
		if(legalRequest==false) { // Otherwise:
			connections.send(connectionId, (T)("ERROR request remmovie " + '"' + movieName + '"' +" failed")); // Sending the proper ERROR message
		}}
	
	/**
	 * This function changes the price of a required movie.
	 * @param msg = The message which should be processed. 
	 */
	public void processChangePrice(T msg) {
		String messageString=(String) msg;
		//Extracting the movie's name by the char (") 
		int firstQuationOfName=messageString.indexOf('"');
		int lastQuationOfName=messageString.lastIndexOf('"');
		String name=messageString.substring(firstQuationOfName+1,lastQuationOfName);
		boolean legalRequest=type.equals("admin");// Flag in order to indicate if the user is actually an admin.
		if(loggedIn.compareAndSet(true, true) && legalRequest) { // In case this user is indeed an admin and is currently logged in
			messageString=messageString.substring(lastQuationOfName+2);
			String priceString=messageString;
			Integer price= Integer.valueOf(priceString);
			if(!((SharedMovieData) sharedData).getMapOfMoviesData().containsKey(name)|| price<=0) { // Verifying that the movie's name does exist and its price is greater than 0.
				legalRequest=false;}
			else { // In case its a legal request:
				((SharedMovieData) sharedData).getMapOfMoviesData().get(name).setPrice(price);
				synchronized (sharedData.toLock){ // Changing the json accordingly:
					jsonExtractor.updateJsons(((SharedMovieData) sharedData).getMapOfMoviesData(), null);}
				connections.send(connectionId, (T)("ACK changeprice "+ '"' + name + '"' + " success")); // Sending the proper ACK message
				((ConnectionsImp)connections).LoggedInBroadcast((T)("BROADCAST movie " + '"' + name+ '"' + " " + ((SharedMovieData) sharedData).getMapOfMoviesData().get(name).getAvailableAmount() + " " + price)); // Broadcasting the proper BROADCAST message. 
			}}
		if(legalRequest==false) { // Otherwise:
			connections.send(connectionId, (T)("ERROR request changeprice " + '"' + name + '"' +" failed")); // Sending the proper ERROR message
		}}}