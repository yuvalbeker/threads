package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.impl.BGRSServer.messages.*;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class BGRSMessageEncoderDecoder implements MessageEncoderDecoder<Message> {
    private final ByteBuffer opcode = ByteBuffer.allocate(2); // saves the opcode (type) of message
    private short op;
    private byte[] bytes = new byte[1 << 10]; //start with 1k
    private int len = 0; // current position in bytes array
    private int zerocounter = 0; // count the number of zero we already read
    private int firstzero = 0; // indicates the index of the first zero in the message


    @Override
    public Message decodeNextByte(byte nextByte) {

        if (!opcode.hasRemaining()) { //we read 2 bytes and therefore can take care the specific message
            if (op == 1) {
                String[] result = decodeNextByteUsernamePassword(nextByte);
                if(result != null) {
                    initialize();
                    return new ADMINREGMessages(result[0], result[1]);
                }
            }
            else if (op == 2) {
                String[] result = decodeNextByteUsernamePassword(nextByte);
                if(result != null) {
                    initialize();
                    return new STUDENTREGMessages(result[0], result[1]);
                }
            }
            else if (op == 3) {
                String[] result = decodeNextByteUsernamePassword(nextByte);
                if(result != null) {
                    initialize();
                    return new LOGINMessages(result[0], result[1]);
                }
            }
            else if (op == 5) {
                Short result = decodeNextByte2Bytes(nextByte);
                if(result != null) {
                    initialize();
                    return new COURSEREGMessages(result);
                }
            }
            else if (op == 6) {
                Short result = decodeNextByte2Bytes(nextByte);
                if(result != null) {
                    initialize();
                    return new KDAMCHECKMessages(result);
                }
            }
            else if (op == 7) {
                Short result = decodeNextByte2Bytes(nextByte);
                if(result != null) {
                    initialize();
                    return new COURSESTATMessages(result);
                }
            }
            else if (op == 8) {
                String result = decodeNextByteUsername(nextByte);
                if(result != null) {
                    initialize();
                    return new STUDENTSTATMessages(result);
                }
            }
            else if (op == 9) {
                Short result = decodeNextByte2Bytes(nextByte);
                if(result != null) {
                    initialize();
                    return new ISREGISTEREDMessages(result);
                }
            }
            else if (op == 10) {
                Short result = decodeNextByte2Bytes(nextByte);
                if(result != null) {
                    initialize();
                    return new UNREGISTERMessages(result);
                }
            }
        }
        else {
            opcode.put(nextByte);
            if (!opcode.hasRemaining()) { //we read 2 bytes and therefore can take care the specific message
                opcode.flip();
                op = opcode.getShort(); // read the next 2 bytes from position - get the type of message
                if (op == 4) { //logout message
                    initialize();
                    return new LOGOUTMessages();
                }
                else if (op == 11) { //MYCOURSES message
                    initialize();
                    return new MYCOURSESMessages();
                }
            }
        }
        return null;
    }


    @Override
    public byte[] encode(Message message) {
        return ((ResponseMessage)message).getEncode();
    }

    //gets ready for next message
    public void initialize(){
        opcode.clear();
        len = 0;
        zerocounter = 0;
        firstzero = 0;
    }
    //finishes decoding after 2 zero (after opcode)
    private String[] decodeNextByteUsernamePassword(byte nextByte) {
        if(nextByte == '\0') {
            zerocounter++;
            if(zerocounter == 2){
                String[] result = new String[2]; // {username, password}
                result[0] = new String(bytes, 0, firstzero-1, StandardCharsets.UTF_8);
                result[1] = new String(bytes, firstzero, len - firstzero, StandardCharsets.UTF_8);

                len = 0; //initialize
                zerocounter = 0;
                return result;
            }
            else
                firstzero = len+1;
        }
        if(len >= bytes.length)
            bytes = Arrays.copyOf(bytes, len*2);

        bytes[len] = nextByte;
        len ++;
        return null;
    }

    //finishes decoding after 1 zero (after opcode)
    private String decodeNextByteUsername(byte nextByte) {
        if(nextByte == '\0') {
            String result = new String(bytes, 0, len, StandardCharsets.UTF_8);
            len = 0; //initialize
            return result;
        }
        if(len >= bytes.length)
            bytes = Arrays.copyOf(bytes, len*2);

        bytes[len] = nextByte;
        len ++;
        return null;
    }

    //finishes decoding after 2 bytes (after opcode)
    private Short decodeNextByte2Bytes(byte nextByte) {
        bytes[len] = nextByte;
        len++;
        if(len == 2){ // we read 2 bytes - finish
            return ByteBuffer.wrap(bytes, 0, 2).getShort();
        }
        return null;
    }
}



