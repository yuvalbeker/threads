package bgu.spl.net.impl.BGRSServer.messages;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

public interface Message<T> {
    short getOpcode();
    Message execute(BGRSProtocol protocol);
    }
