package bgu.spl181.net.api.bidi;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import bgu.spl181.net.srv.bidi.ConnectionHandler;
/**
 * Holds an HashMap of ConnectionHandlers and their unique IDs.
 */
public class ConnectionsImp<T> implements Connections<T> {
	
	protected ConcurrentHashMap<Integer,ConnectionHandler<T>> mapOfConnectionHandlers;
	protected ConcurrentHashMap<Integer, ConnectionHandler<T>> mapOfLoggedIn;
	
	/**
	 * Default constructor
	 */
	public  ConnectionsImp(){
		mapOfConnectionHandlers=new ConcurrentHashMap<>();
		mapOfLoggedIn=new ConcurrentHashMap<>();
	}
	
	/**
	 * 
	 * @return Map of connection handlers which have already logged in. 
	 */
	public ConcurrentHashMap<Integer, ConnectionHandler<T>> getMapOfLoggedIn(){
		return mapOfLoggedIn;
	}
	
	/**
	 * 
	 * @return Map of all the connection handlers. 
	 */
	public ConcurrentHashMap<Integer, ConnectionHandler<T>> getMapOfConnectionHandlers(){
		return mapOfConnectionHandlers;
	}
			
	/**
	 * @param connectionId - The unique connectionId of the connectionHandler. 
	 * @param msg = The message which should be sent. 
	 * @return true if the message was sent and false if the message has not been sent. 
	 */
	@Override
	public boolean send(int connectionId, T msg) {
		try {
		ConnectionHandler<T> connectionHandler=mapOfConnectionHandlers.get(connectionId);
		connectionHandler.send(msg);
		}
		catch(Exception e) {
			return false;
		}
		return true;
	}

	/** 
	 * Broadcast the given message to all the connectionHandlers within the map. 
	 * @param msg = The message which should be sent.  
	 */
	@Override
	public void broadcast(T msg) {
		for(Map.Entry<Integer, ConnectionHandler<T>> entry: mapOfConnectionHandlers.entrySet()) {
			send(entry.getKey(),msg);
		}
		
	}
	/** 
	 * Broadcast the given message to all the logged in connectionHandlers within the map. 
	 * @param msg = The message which should be sent.  
	 */
	public void LoggedInBroadcast(T msg) {
		for(Map.Entry<Integer, ConnectionHandler<T>> entry: mapOfLoggedIn.entrySet()) {
			send(entry.getKey(),msg);
		}
		
	}
	/**
	 * Removes the connectionHandler which its ID was received as a parameter. 
	 * @param connectionId - The unique connectionId of the connectionHandler.   
	 */
	@Override
	public void disconnect(int connectionId) {
		mapOfConnectionHandlers.remove(connectionId);
		
	}
	
	/** 
	 * @return Map of ConnectionHandlers.
	 */
	public ConcurrentHashMap<Integer,ConnectionHandler<T>> getHashMap(){
		return mapOfConnectionHandlers;
	}

}