package bgu.spl.net.impl.BGRSServer.messages;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

public class ERRORMessages implements ResponseMessage{
    private final short Opcode = 13;
    private final short MessageOpcode;

    public ERRORMessages(short messageOpcode) {
        MessageOpcode = messageOpcode;
    }

    public short getOpcode() {
        return Opcode;
    }

    public Short getMessageOpcode() {
        return MessageOpcode;
    }

    public Message execute(BGRSProtocol protocol){
        return null;
    }

    public byte[] getEncode(){
        int length = 4; //2 bytes of Opcode + 2 bytes of MessageOpcode + '\0'
        byte[] OpcodeBytes = ResponseMessage.shortToBytes(Opcode);
        byte[] MessageOpcodeBytes = ResponseMessage.shortToBytes(MessageOpcode);
        byte[] output = new byte[length];

        System.arraycopy(OpcodeBytes, 0, output, 0, 2); //insert opcode to output bytes array
        System.arraycopy(MessageOpcodeBytes, 0, output, 2, 2); //insert MessageOpcode to output bytes array

        return output; //uses utf8 by default
    }


}
