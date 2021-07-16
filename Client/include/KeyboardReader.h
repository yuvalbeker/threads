#ifndef _KEYBOARDREADER_H
#define _KEYBOARDREADER_H

#include <thread>
#include <iostream>
#include <connectionHandler.h>
#include <mutex>
#include <condition_variable>


class KeyboardReader {
public:
    /** constructor */
    KeyboardReader(ConnectionHandler* handler, std::mutex& mtx, std::condition_variable& cv, bool& shouldTerminate);
    void run();
    void setisTerminate(bool Terminate); // set isTerminate


private:
    bool isTerminate; // indicates whether we should terminate
    ConnectionHandler* connectionHandler;
    std::mutex& mtx;
    std::condition_variable& cv;
    bool& shouldTerminate; //shared resource
    static std::vector<std::string> splitline(std::string& line, char tosplit); // split a line to words according to the tosplit char
    bool sendUsernamePasswordMessage(short opcode, std::vector<std::string>& message); //converts a message to opcode-username-0-password-0 format and sends to server
    bool sendOpcodeMessage(short opcode); //converts a message to opcode and sends to server
    bool sendUsernameMessage(short opcode, std::vector<std::string>& message); //converts a message to opcode-username-0 and sends to server
    bool sendCourseNumber(short opcode, std::vector<std::string>& message); //converts a message to opcode-CourseNumber and sends to server
    static void shortToCharArray(short num, char* bytesArray); // converts short number to char array

};


#endif //_KEYBOARDREADER_H