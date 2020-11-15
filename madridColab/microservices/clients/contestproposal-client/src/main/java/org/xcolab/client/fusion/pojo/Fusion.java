package org.xcolab.client.fusion.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import java.sql.Timestamp;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(Include.NON_NULL)
public class Fusion {

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
    private String requestText;
    private String commonText;

    public Fusion() {

    }

    public Fusion(Long id, Long fromUserId, Long toUserId, Long fromProposalId,
            Long toProposalId, Timestamp createdAt, Timestamp updatedAt, String status,
            Long contestId, Long proposalId, String requestText, String commonText) {
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
        this.requestText = requestText;
        this.commonText = commonText;
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

    @Override
    public String toString() {
        return "Fusion{" +
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
                ", requestText='" + requestText + '\'' +
                ", commonText='" + commonText + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Fusion fusion = (Fusion) o;
        return Objects.equals(id, fusion.id) &&
                Objects.equals(fromUserId, fusion.fromUserId) &&
                Objects.equals(toUserId, fusion.toUserId) &&
                Objects.equals(fromProposalId, fusion.fromProposalId) &&
                Objects.equals(toProposalId, fusion.toProposalId) &&
                Objects.equals(createdAt, fusion.createdAt) &&
                Objects.equals(updatedAt, fusion.updatedAt) &&
                Objects.equals(status, fusion.status) &&
                Objects.equals(contestId, fusion.contestId) &&
                Objects.equals(proposalId, fusion.proposalId) &&
                Objects.equals(requestText, fusion.requestText) &&
                Objects.equals(commonText, fusion.commonText);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(id, fromUserId, toUserId, fromProposalId, toProposalId, createdAt, updatedAt,
                        status, contestId, proposalId, requestText, commonText);
    }
}
