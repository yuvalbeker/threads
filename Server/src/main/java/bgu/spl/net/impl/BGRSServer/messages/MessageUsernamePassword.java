package bgu.spl.net.impl.BGRSServer.messages;

import bgu.spl.net.impl.BGRSServer.BGRSProtocol;

public interface MessageUsernamePassword extends Message<String> {
    String getUsername();
    String getPassword();
}
