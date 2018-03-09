package bgu.spl181.net.api.bidi;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import bgu.spl181.net.api.bidi.ConnectionsImp;
import bgu.spl181.net.api.bidi.JsonImp.JsonExtractor;

/**
 * Basic protocol which processes messages that are related to the User, such as registration, login and sign out. 
 *
 */
public class BidiMessagingProtocolImp<T> implements BidiMessagingProtocol<T>{
	
	protected int connectionId;
	protected Connections<T> connections;
	protected boolean shouldTerminate=false;
	protected SharedData sharedData;
	protected AtomicBoolean loggedIn = new AtomicBoolean(false);
	protected String myName;
	protected String type="normal";
	protected JsonExtractor jsonExtractor=new JsonExtractor();

	/**
	 * Builder - Initializes the required field.
	 * @param sharedData - The SharedData Object. 
	 */
	public BidiMessagingProtocolImp(SharedData sharedData){
		this.sharedData=sharedData;
		
	}
	
	/**
	 * This function initializes the required fields.
	 * @param connectionId - The unique connectionId of the connectionHandler. 
	 * @param connections = The connections Object.
	 */
	@Override
	public void start(int connectionId, Connections<T> connections) {
		this.connectionId=connectionId;
		this.connections=connections;
	}
	
	/**
	 * This function processes the required message.
	 * @param msg = The message which should be processed. 
	 */
	@Override
	public void process(T message) {
		String messageString=(String) message;
		String[] arr=messageString.split(" ");
		switch(arr[0]) {
		case "REGISTER":
			processRegister(message);
		break;
		case "LOGIN":
			processLogin(message);
			break;
		case "SIGNOUT":
			processSignout(message);
			break;
		}
	}
	/**
	 * @return true if the connection should terminate, false otherwise.
	 */
	@Override
	public boolean shouldTerminate() {
		return shouldTerminate;
	}
	/**
	 * This function modifies the value of shouldTerminate.
	 */
	public void modifyShouldTerminate() {
		shouldTerminate=!shouldTerminate;
	}
	
	/**
	 * This function registers a new client.
	 * @param msg = The message which should be processed. 
	 */
	public void processRegister(T msg) {
		boolean validInfo=true; // A boolean which will indicate whether or not the request was a valid one in terms of syntax
		String messageString=(String) msg; String userName=""; String country="";String password="";
		String[] arr=messageString.split(" ");
		try { // Verifying the userName appears as it should
		userName=arr[1];}
		catch (Exception e) {
			validInfo=false;} // Verifying the password appears as it should:
		try{ password=arr[2]; }
		catch (Exception e) {
			validInfo=false;}
		if (arr.length>3) { // In case we received information regarding the origin country, verifying it appears in the way that it should
			try {
				for (int i=3; i<arr.length; i++) {
				country=country +" "+ arr[i];}
				country=country.substring(1);
				if(country.substring(0, 8).equals("country=")) {
				country=country.substring(9, country.length()-1);
				if(country.length()==0) {
					validInfo=false;}}
				else {
					validInfo=false;}}
			catch (Exception e) {
				validInfo=false;}}
		else {
			validInfo=false;}
		Vector<MovieBase> movieList = new Vector<>();
		if( validInfo && (!(sharedData.mapOfUsersData.containsKey(userName))) ) { //In case this is a valid registration request:
			processRegisterHelper(userName, password, country, movieList);}
		else { // Otherwise: sending the proper ERROR message
			connections.send(connectionId, (T)"ERROR registration failed");}}
	/**
	 * This function assists the processRegister function to complete it's work.
	 * @param userName = The name of the new user.
	 * @param password = The password of the new user.
	 * @param country = The origin country of the new user.
	 * @param movieList - An empty movie list.
	 */
	public void processRegisterHelper(String userName, String password, String country, Vector<MovieBase> movieList) {
		MovieUser user= new MovieUser(userName, password, "normal", country, 0, movieList); // Initializing the newly added user
		sharedData.mapOfUsersData.put(userName, user); // Putting the newly added user into our shared data
		synchronized (sharedData.toLock){ // Changing the json accordingly:
			jsonExtractor.updateJsons(null, sharedData.mapOfUsersData);}
		connections.send(connectionId, (T)"ACK registration succeeded"); // Sending the proper ACK message
	}
	
	/**
	 * This function affords the client to login into the system.
	 * @param msg = The message which should be processed. 
	 */
	public void processLogin(T msg) {
		String messageString=(String) msg;
		String[] arr=messageString.split(" ");
		String userName=arr[1];
		String password=arr[2];
		if(sharedData.getMapOfUsersData().containsKey(userName) && (sharedData.getMapOfUsersData().get(userName)).getPassword().equals(password) && loggedIn.compareAndSet(false, true)) {//In case the client is yet to be registered and the password matches the userName
			((ConnectionsImp<T>)connections).getMapOfLoggedIn().put(connectionId, ((ConnectionsImp<T>)connections).getMapOfConnectionHandlers().get(connectionId));
			myName=userName;
			if(((MovieUser)sharedData.getMapOfUsersData().get(userName)).getType().equals("admin")) {
				type="admin";
			}
			connections.send(connectionId, (T)"ACK login succeeded"); // Sending the proper ACK message
		}
		else { // Otherwise:
			connections.send(connectionId, (T)"ERROR login failed"); // Sending the proper ERROR message
		}
	}
	
	/**
	 * This function affords the client to sign out of the system.
	 * @param msg = The message which should be processed. 
	 */
	public void processSignout(T msg) { 
		if(loggedIn.compareAndSet(true, false)) { // In case the client is logged in:
			((ConnectionsImp<T>)connections).getMapOfLoggedIn().remove(connectionId);
			connections.send(connectionId, (T)"ACK signout succeeded"); // Sending the proper ACK message
			modifyShouldTerminate();
		}
		else { // Otherwise:
			connections.send(connectionId, (T)"ERROR signout failed"); // Sending the proper ERROR message
		}
		
	}
}