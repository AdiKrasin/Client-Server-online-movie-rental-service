#include "../include/ConnectionHandler.h"

using boost::asio::ip::tcp;

using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;

ConnectionHandler::ConnectionHandler(string host, short port): host_(host), port_(port), io_service_(), socket_(io_service_),should_terminate(false){}

ConnectionHandler::~ConnectionHandler() {
    close();
}

bool ConnectionHandler::connect() {
     try {
        tcp::endpoint endpoint(boost::asio::ip::address::from_string(host_), port_); // the server endpoint
        boost::system::error_code error;
        socket_.connect(endpoint, error);
        if (error)
            throw boost::system::system_error(error);
    }
    catch (std::exception& e) {
        return false;
    }
    return true;
}

bool ConnectionHandler::getBytes(char bytes[], unsigned int bytesToRead) {
    size_t tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToRead > tmp ) {
            tmp += socket_.read_some(boost::asio::buffer(bytes+tmp, bytesToRead-tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        return false;
    }
    return true;
}

bool ConnectionHandler::sendBytes(const char bytes[], int bytesToWrite) {
    int tmp = 0;
    boost::system::error_code error;
    try {
        while (!error && bytesToWrite > tmp ) {
            tmp += socket_.write_some(boost::asio::buffer(bytes + tmp, bytesToWrite - tmp), error);
        }
        if(error)
            throw boost::system::system_error(error);
    } catch (std::exception& e) {
        return false;
    }
    return true;
}

bool ConnectionHandler::getLine(std::string& line) {
    return getFrameAscii(line, '\n');
}

bool ConnectionHandler::sendLine(std::string& line) {
    return sendFrameAscii(line, '\n');
}

bool ConnectionHandler::getFrameAscii(std::string& frame, char delimiter) {
    char ch;
    try {
        do{
            getBytes(&ch, 1);
            frame.append(1, ch);
        }while (delimiter != ch);
    } catch (std::exception& e) {
        return false;
    }
    return true;
}

bool ConnectionHandler::sendFrameAscii(const std::string& frame, char delimiter) {
    bool result=sendBytes(frame.c_str(),frame.length());
    if(!result) return false;
    return sendBytes(&delimiter,1);
}

// Close down the connection properly.
void ConnectionHandler::close() {
    try{
        socket_.close();
        should_terminate=true;
    } catch (...) {
    }
}

bool ConnectionHandler::getShould_terminate() const {
    return should_terminate;
}

void ConnectionHandler::setShould_terminate(bool should_terminate) {
    ConnectionHandler::should_terminate = should_terminate;
}

volatile bool ConnectionHandler::getSignoutAck() const {
    return signoutAck;
}

void ConnectionHandler::setSignoutAck(volatile bool signoutAck) {
    ConnectionHandler::signoutAck = signoutAck;
}

bool ConnectionHandler::isSignout() const {
    return signout;
}

void ConnectionHandler::setSignout(bool signout) {
    ConnectionHandler::signout = signout;
}

volatile bool ConnectionHandler::getWriterThreadFinished() const {
    return writerThreadFinished;
}

void ConnectionHandler::setWriterThreadFinished(volatile bool writerThreadFinished) {
    ConnectionHandler::writerThreadFinished = writerThreadFinished;
}

