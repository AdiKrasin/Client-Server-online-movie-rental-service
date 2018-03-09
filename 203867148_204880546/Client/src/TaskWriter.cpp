#include <iostream>
#include "../include/ConnectionHandler.h"
#include <boost/thread.hpp>

class TaskWriter {
private:
    ConnectionHandler &_handler;

public:
    TaskWriter(ConnectionHandler &handler) : _handler(handler) {}

    void operator()() {
        while (!_handler.getShould_terminate()) {
                const short bufsize = 1024;
                char buf[bufsize];
                std::cin.getline(buf, bufsize);
                std::string line(buf);
                size_t len = line.length();

                if (!_handler.sendLine(line)) {
                    break;
                }
                if (line.compare("SIGNOUT")==0){
                    while(!_handler.getSignoutAck()){
                        boost::this_thread::sleep(boost::posix_time::milliseconds(10));
                    }
                    _handler.setWriterThreadFinished(true);
                    if(_handler.isSignout())
                        break;
                    _handler.setSignoutAck(false);
                }

            }



    }



};
 