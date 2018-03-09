#include <stdlib.h>
#include <iostream>
#include "../include/ConnectionHandler.h"
#include <boost/thread.hpp>
#include "TaskWriter.cpp"


/**
* The server will reply to the exact input which the client has sent.
*/

int main (int argc, char *argv[]) {
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ConnectionHandler connectionHandler(host, port);
    if (!connectionHandler.connect()) {
        return 1;
    } 
    TaskWriter task(connectionHandler);
    boost::thread thread(task);

    while (!connectionHandler.getShould_terminate()) {
        std::string answer;
        size_t len;
        if (!connectionHandler.getLine(answer)) {
            connectionHandler.setShould_terminate(false);
            break;
        }
        len = answer.length();
        // A C string must end with a 0 char delimiter.  When we filled the answer buffer from the socket
        // we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.
        answer.resize(len - 1);
        std::cout << answer << std::endl;
        if (answer.compare("ACK signout succeeded")==0) {
            connectionHandler.setSignout(true);
            connectionHandler.setSignoutAck(true);
            connectionHandler.close();
            thread.join();
            break;
        }
        else if(answer.compare("ERROR signout failed")==0){
            connectionHandler.setSignoutAck(true);
            while(!connectionHandler.getWriterThreadFinished()){
                boost::this_thread::sleep(boost::posix_time::milliseconds(10));
            }
            connectionHandler.setWriterThreadFinished(false);
        }
    }
    return 0;
}
