package org.xcolab.client.members.exceptions;

public class MessageNotFoundException extends Exception {
    public MessageNotFoundException(long messageId) {
        super("Message with id " + messageId + " does not exist");
    }
}
