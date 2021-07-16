package bgu.spl.net.impl.BGRSServer;

import bgu.spl.net.api.MessagingProtocol;
import bgu.spl.net.impl.BGRSServer.messages.Message;

public class BGRSProtocol implements MessagingProtocol<Message> {

    private boolean shouldTerminate;
    private String userName;


    public BGRSProtocol(){
        this.shouldTerminate = false;
    }
    @Override
    public Message process(Message msg) {
        return msg.execute(this);
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public boolean isShouldTerminate() {
        return shouldTerminate;
    }

    public void setShouldTerminate(boolean shouldTerminate) {
        this.shouldTerminate = shouldTerminate;
    }

}
