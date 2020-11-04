package org.xcolab.client.fusion.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.sql.Timestamp;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class FusionRequest {

    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private Long fromProposalId;
    private Long toProposalId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String status;
    private Long contestId;
    private Long proposalId;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    private String reason;

    public FusionRequest() {
    }

    public FusionRequest(FusionRequest value){
        this.id=value.id;
        this.fromUserId=value.fromUserId;
        this.toUserId=value.toUserId;
        this.fromProposalId=value.fromProposalId;
        this.toProposalId=value.toProposalId;
        this.createdAt=value.createdAt;
        this.updatedAt=value.updatedAt;
        this.status=value.status;
        this.contestId=value.contestId;
        this.proposalId=value.proposalId;
        this.reason=value.reason;
    }

    public FusionRequest(Long id, Long fromUserId, Long toUserId, Long fromProposalId,
            Long toProposalId, Timestamp createdAt, Timestamp updatedAt, String status,
            Long contestId, Long proposalId, String reason) {
        this.id = id;
        this.fromUserId = fromUserId;
        this.toUserId = toUserId;
        this.fromProposalId = fromProposalId;
        this.toProposalId = toProposalId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.status = status;
        this.contestId = contestId;
        this.proposalId = proposalId;
        this.reason = reason;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getFromUserId() {
        return fromUserId;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public Long getFromProposalId() {
        return fromProposalId;
    }

    public void setFromProposalId(Long fromProposalId) {
        this.fromProposalId = fromProposalId;
    }

    public Long getToProposalId() {
        return toProposalId;
    }

    public void setToProposalId(Long toProposalId) {
        this.toProposalId = toProposalId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getContestId() {
        return contestId;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public Long getProposalId() {
        return proposalId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    @Override
    public String toString() {
        return "FusionRequest{" +
                "id=" + id +
                ", fromUserId=" + fromUserId +
                ", toUserId=" + toUserId +
                ", fromProposalId=" + fromProposalId +
                ", toProposalId=" + toProposalId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", status='" + status + '\'' +
                ", contestId=" + contestId +
                ", proposalId=" + proposalId +
                ", reason=" + reason +
                '}';
    }
}
