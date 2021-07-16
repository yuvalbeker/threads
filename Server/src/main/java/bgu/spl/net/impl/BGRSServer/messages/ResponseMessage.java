package bgu.spl.net.impl.BGRSServer.messages;

public interface ResponseMessage extends Message<Short>{
    Short getMessageOpcode();
    byte[] getEncode();

    public static byte[] shortToBytes(short num)
    {
        byte[] bytesArr = new byte[2];
        bytesArr[0] = (byte)((num >> 8) & 0xFF);
        bytesArr[1] = (byte)(num & 0xFF);
        return bytesArr;
    }
}
