package org.xcolab.client.fusion.beans;

import org.xcolab.client.contest.pojo.Contest;
import org.xcolab.client.fusion.utils.FusionStatus;
import org.xcolab.client.members.pojo.Member;
import org.xcolab.client.proposals.pojo.Proposal;



public class FusionBean {

    Long id;
    Member toUser;
    Member fromUser;
    Proposal toProposal;
    Proposal fromProposal;
    FusionStatus status;
    Contest newContest;
    Proposal newProposal;
    String requestText;
    String commonText;

    public FusionBean(){}

    public FusionBean(Long id, Member toUser, Member fromUser,
            Proposal toProposal, Proposal fromProposal,
            FusionStatus status, Contest newContest,
            Proposal newProposal, String requestText, String commonText) {
        this.id = id;
        this.toUser = toUser;
        this.fromUser = fromUser;
        this.toProposal = toProposal;
        this.fromProposal = fromProposal;
        this.status = status;
        this.newContest = newContest;
        this.newProposal = newProposal;
        this.requestText = requestText;
        this.commonText = commonText;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Member getToUser() {
        return toUser;
    }

    public void setToUser(Member toUser) {
        this.toUser = toUser;
    }

    public Member getFromUser() {
        return fromUser;
    }

    public void setFromUser(Member fromUser) {
        this.fromUser = fromUser;
    }

    public Proposal getToProposal() {
        return toProposal;
    }

    public void setToProposal(Proposal toProposal) {
        this.toProposal = toProposal;
    }

    public Proposal getFromProposal() {
        return fromProposal;
    }

    public void setFromProposal(Proposal fromProposal) {
        this.fromProposal = fromProposal;
    }

    public FusionStatus getStatus() {
        return status;
    }

    public void setStatus(FusionStatus status) {
        this.status = status;
    }

    public Contest getNewContest() {
        return newContest;
    }

    public void setNewContest(Contest newContest) {
        this.newContest = newContest;
    }

    public Proposal getNewProposal() {
        return newProposal;
    }

    public void setNewProposal(Proposal newProposal) {
        this.newProposal = newProposal;
    }

    public String getRequestText() {
        return requestText;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public String getCommonText() {
        return commonText;
    }

    public void setCommonText(String commonText) {
        this.commonText = commonText;
    }
}
