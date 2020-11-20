package org.xcolab.view.pages.fusion.beans;

import org.hibernate.validator.constraints.NotBlank;

import java.io.Serializable;

public class FusionRequestBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private String fromUserId;

    @NotBlank
    private String toUserId;

    @NotBlank
    private String fromProposalId;

    @NotBlank
    private String toProposalId;

    private String requestText;

    public FusionRequestBean() {

    }

    public FusionRequestBean(String fromUserId, String toUserId,
            String fromProposalId, String toProposalId, String requestText) {
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.fromProposalId = fromProposalId;
        this.toProposalId = toProposalId;
        this.requestText = requestText;
    }


    public String getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(String fromUserId) {
        this.fromUserId = fromUserId;
    }

    public String getToUserId() {
        return toUserId;
    }

    public void setToUserId(String toUserId) {
        this.toUserId = toUserId;
    }

    public String getFromProposalId() {
        return fromProposalId;
    }

    public void setFromProposalId(String fromProposalId) {
        this.fromProposalId = fromProposalId;
    }

    public String getToProposalId() {
        return toProposalId;
    }

    public void setToProposalId(String toProposalId) {
        this.toProposalId = toProposalId;
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    @Override
    public String toString() {
        return "FusionRequestBean{"+
                " fromUserId='" + fromUserId + '\'' +
                ", toUserId='" + toUserId + '\'' +
                ", fromProposalId='" + fromProposalId + '\'' +
                ", toProposalId='" + toProposalId + '\'' +
                ", requestText='" + requestText + '\'' +
                '}';
    }
}
