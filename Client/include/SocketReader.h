//
// Created by gilsh on 27/12/2020.
//

#ifndef _SOCKETREADER_H
#define _SOCKETREADER_H


#include <condition_variable>
#include "connectionHandler.h"

class SocketReader {
public:
    /** constructor */
    SocketReader(ConnectionHandler* handler, std::mutex& mtx, std::condition_variable& cv, bool& shouldTerminate);
    void run();
    void setisTerminate(bool Terminate); // set isTerminate

private:
    bool isTerminate; // indicates whether we should terminate
    ConnectionHandler* connectionHandler;
    std::mutex& mtx;
    std::condition_variable& cv;
    bool& shouldTerminate; //shared resource
    void ErrorHandler(short messageOpcode); //handle error messages from server
    void AckHandler(short messageOpcode); //handle ack messages from server
    short ReadTwoBytes(); //read two bytes from socket and return it. return -1 if connection is failed.
    static short CharArrayToshort(char* bytesArray); // converts char array to short number
};


#endif //_SOCKETREADER_H