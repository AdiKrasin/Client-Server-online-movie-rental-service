package bgu.spl181.net.impl.BBtpc;

import bgu.spl181.net.api.bidi.JsonImp.JsonExtractor;
import bgu.spl181.net.api.bidi.MessageEncoderDecoderImp;
import bgu.spl181.net.api.bidi.MovieRentalProtocol;
import bgu.spl181.net.api.bidi.SharedData;
import bgu.spl181.net.api.bidi.SharedMovieData;
import bgu.spl181.net.srv.Server;

public class TPCMain {
	
	

	public static void main(String[] args) {
	JsonExtractor initialJsonExtractor=new JsonExtractor();
	SharedData sharedData = new SharedMovieData(initialJsonExtractor.getCurrentUsersDatabase(), initialJsonExtractor.getCurrentMoviesDatabase());
	Server<String> server=Server.threadPerClient(
					Integer.decode(args[0]).intValue(),     //The port
                   () -> new MovieRentalProtocol<>(sharedData),  //protocol factory
                   () -> new MessageEncoderDecoderImp<>()); //encdec factory
	
    server.serve();
	}
}
