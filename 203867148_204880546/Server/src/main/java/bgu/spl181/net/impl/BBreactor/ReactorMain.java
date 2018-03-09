package bgu.spl181.net.impl.BBreactor;

import bgu.spl181.net.api.bidi.JsonImp.JsonExtractor;
import bgu.spl181.net.api.bidi.MessageEncoderDecoderImp;
import bgu.spl181.net.api.bidi.MovieRentalProtocol;
import bgu.spl181.net.api.bidi.SharedData;
import bgu.spl181.net.srv.Server;
import bgu.spl181.net.api.bidi.SharedMovieData;



public class ReactorMain {
	
	public static void main(String[] args) {
		JsonExtractor initialJsonExtractor=new JsonExtractor();
		SharedData sharedData = new SharedMovieData(initialJsonExtractor.getCurrentUsersDatabase(), initialJsonExtractor.getCurrentMoviesDatabase());
	    Server<String> server = Server.reactor(
	            5, // Number of threads.
	            Integer.decode(args[0]).intValue(),   //The port.
	            () -> new MovieRentalProtocol<>(sharedData), // Protocol factory.
	            () -> new MessageEncoderDecoderImp<>()); // Encdec factory.
	    server.serve();
	}
}
