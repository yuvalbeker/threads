#include "../include/KeyboardReader.h"

KeyboardReader:: KeyboardReader(ConnectionHandler* handler, std::mutex& mtx, std::condition_variable& cv, bool& shouldTerminate): isTerminate(false), connectionHandler(handler), mtx(mtx), cv(cv), shouldTerminate(shouldTerminate){}

void KeyboardReader::run() {
    while (!isTerminate){
        const short bufsize = 1024;
        char buf[bufsize];
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        std::vector<std::string> message =  splitline(line, ' ');  //saves the message as an array of its words

        short opcode;
        bool ans;

        if(message[0] == "ADMINREG"){
            opcode = 1;
            ans = sendUsernamePasswordMessage(opcode, message);
        }

        else if(message[0] == "STUDENTREG"){
            opcode = 2;
            ans = sendUsernamePasswordMessage(opcode, message);
        }

        else if(message[0] == "LOGIN"){
            opcode = 3;
            ans = sendUsernamePasswordMessage(opcode, message);
        }

        else if(message[0] == "LOGOUT"){
            opcode = 4;
            ans = sendOpcodeMessage(opcode);
            if(ans && !shouldTerminate) { //waiting to response from server
                std::unique_lock<std::mutex> lck(mtx);
                cv.wait(lck);
            }
            if(shouldTerminate)
                setisTerminate(true);
        }

        else if(message[0] == "COURSEREG") {
            opcode = 5;
            ans = sendCourseNumber(opcode, message);
        }

        else if(message[0] == "KDAMCHECK"){
            opcode = 6;
            ans = sendCourseNumber(opcode, message);
        }

        else if(message[0] == "COURSESTAT"){
            opcode = 7;
            ans = sendCourseNumber(opcode, message);
        }

        else if(message[0] == "STUDENTSTAT"){
            opcode = 8;
            ans = sendUsernameMessage(opcode, message);
        }

        else if(message[0] == "ISREGISTERED"){
            opcode = 9;
            ans = sendCourseNumber(opcode, message);
        }

        else if(message[0] == "UNREGISTER"){
            opcode = 10;
            ans = sendCourseNumber(opcode, message);
        }

        else if(message[0] == "MYCOURSES"){
            opcode = 11;
            ans = sendOpcodeMessage(opcode);
        }

        if (!ans) {
            std::cout << "Disconnected. Exiting..." << std::endl;
            setisTerminate(true);
        }
    }
}

std::vector<std::string> KeyboardReader:: splitline(std::string& line, char tosplit){
    std::vector<std::string> output;

    while(line.find(tosplit) != std::string::npos){
        int indexTosplit = line.find(tosplit);
        output.push_back(line.substr(0, indexTosplit));
        line = line.substr(indexTosplit + 1, line.length());
    }
    output.push_back(line);
    return output;
}

bool KeyboardReader::sendUsernamePasswordMessage(short opcode, std::vector<std::string>& message){
    bool ans = sendUsernameMessage( opcode, message); // sends the opcode and username to server
    if(ans)
        ans = connectionHandler->sendFrameAscii(message[2], '\0'); // sends the password to server
    return ans;
}

bool KeyboardReader::sendOpcodeMessage(short opcode){
    char opcodArray[2];
    shortToCharArray(opcode, opcodArray);
    return connectionHandler->sendBytes(opcodArray, 2); // sends the opcode to server
}

bool KeyboardReader::sendUsernameMessage(short opcode, std::vector<std::string>& message){
    bool ans = sendOpcodeMessage(opcode); // sends the opcode to server
    if(ans)
        ans = connectionHandler->sendFrameAscii(message[1], '\0'); // sends the username to server
    return ans;
}

bool KeyboardReader::sendCourseNumber(short opcode, std::vector<std::string>& message){
    bool ans = sendOpcodeMessage(opcode); // sends the opcode to server
    if(ans){
        char coursenumbytes[2];
        short coursenum = (short)stoi(message[1]); //converts string to int
        shortToCharArray(coursenum, coursenumbytes); //converts short to byte array
        ans = connectionHandler->sendBytes(coursenumbytes, 2); // sends the opcode to server
    }
    return ans;
}

void KeyboardReader::shortToCharArray(short num, char* bytesArray){
    bytesArray[0] = ((num >> 8) & 0xFF);
    bytesArray[1] = (num & 0xFF);
}

void KeyboardReader:: setisTerminate(bool Terminate) {
    isTerminate = Terminate;
}

