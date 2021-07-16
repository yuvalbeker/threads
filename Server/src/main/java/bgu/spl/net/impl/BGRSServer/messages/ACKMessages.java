package bgu.spl.net.impl.BGRSServer.messages;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

public class ACKMessages implements ResponseMessage {
    private final short Opcode = 12;
    private final short MessageOpcode;
    private String Optional = "";

    public ACKMessages(short messageOpcode) {
        MessageOpcode = messageOpcode;
    }

    public ACKMessages(short messageOpcode, String Optional) {
        MessageOpcode = messageOpcode;
        this.Optional = Optional;
    }

    public Short getMessageOpcode() {
        return MessageOpcode;
    }

    public String getOptional() {
        return Optional;
    }

    @Override
    public short getOpcode() {
        return Opcode;
    }

    public Message execute(BGRSProtocol protocol){ return null; }

    public byte[] getEncode(){
        int length = 5; //2 bytes of Opcode + 2 bytes of MessageOpcode + '\0'
        byte[] OpcodeBytes = ResponseMessage.shortToBytes(Opcode);
        byte[] MessageOpcodeBytes = ResponseMessage.shortToBytes(MessageOpcode);
        byte[] OptionalBytes = null;
        if(!Optional.equals("")){
            OptionalBytes = getOptional().getBytes();
            length += OptionalBytes.length;
        }
        byte[] delimiter = new byte[1]; // '\0'
        delimiter[0] = 0x00;

        byte[] output = new byte[length];

        System.arraycopy(OpcodeBytes, 0, output, 0, 2); //insert opcode to output bytes array
        System.arraycopy(MessageOpcodeBytes, 0, output, 2, 2); //insert MessageOpcode to output bytes array

        if(!Optional.equals("")) {
            System.arraycopy(OptionalBytes, 0, output, 4, OptionalBytes.length); //insert Optional to output bytes array
            System.arraycopy(delimiter, 0, output, OptionalBytes.length + 4, 1); //insert '\0' to output bytes array
        }
        else
            System.arraycopy(delimiter, 0, output, 4, 1); //insert '\0' to output bytes array

        return output; //uses utf8 by default
    }

}
