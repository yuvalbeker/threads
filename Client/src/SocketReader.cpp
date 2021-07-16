#include "../include/SocketReader.h"


SocketReader::SocketReader(ConnectionHandler* handler, std::mutex& mtx, std::condition_variable& cv, bool& shouldTerminate): isTerminate(false), connectionHandler(handler), mtx(mtx), cv(cv), shouldTerminate(shouldTerminate){}

void SocketReader::run() {

    while (!isTerminate) {
        short opcode = ReadTwoBytes();
        short massageOpcode = ReadTwoBytes();
        if(opcode == -1 || massageOpcode == -1){ //break if there was Disconnected
            break;}
        if(opcode == 13) {
            ErrorHandler(massageOpcode);
        }
        else if(opcode == 12) {
            AckHandler(massageOpcode);
        }
    }
}

void SocketReader::ErrorHandler(short messageOpcode){
    std::cout << "ERROR " << messageOpcode << std::endl; //prints "ERROR <Message Opcode>"
    if(messageOpcode == 4){
        // logout message
        cv.notify_all(); //notify keyboardreader
    }
}

void SocketReader::AckHandler(short messageOpcode){
    std::string message;
    //  use: connectionHandler.getFrameAscii() and then get the answer without the null char at the end
    if(!connectionHandler->getFrameAscii(message, '\0')){
        std::cout << "Disconnected. Exiting..." << std::endl;
        setisTerminate(true);
        return;
    }
    std::cout << "ACK " << messageOpcode << std::endl; //prints "Ack <Message Opcode>"
    if(message != "")
        std::cout << message << std::endl; //prints Optional
    if(messageOpcode == 4){
        // logout message
        setisTerminate(true);
        shouldTerminate = true;
        cv.notify_all(); //notify keyboardreader
    }
}

void SocketReader:: setisTerminate(bool Terminate) {
    isTerminate = Terminate;
}

short SocketReader::ReadTwoBytes(){
    short answer;
    char bytes[2];
    if(!connectionHandler->getBytes(bytes, 2)){
        std::cout << "Disconnected. Exiting..." << std::endl;
        setisTerminate(true);
        return -1;
    }
    else {
        answer = CharArrayToshort(bytes);
        return answer;
    }
}

short SocketReader::CharArrayToshort(char* bytesArray){
    short result = (short)((bytesArray[0] & 0xff) << 8);
    result += (short)(bytesArray[1] & 0xff);
    return result;
}