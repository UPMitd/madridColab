package org.xcolab.client.fusion.utils;

public enum FusionStatus {
    ACCEPTED("accepted"),
    REJECTED("rejected"),
    PENDING("pending");

    private final String value;

    FusionStatus(String value){
        this.value=value;
    }

    public String getValue(){
        return value;
    }
}
