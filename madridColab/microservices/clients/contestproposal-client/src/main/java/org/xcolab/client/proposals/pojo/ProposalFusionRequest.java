package org.xcolab.client.proposals.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.springframework.core.ParameterizedTypeReference;

import org.xcolab.util.http.client.types.TypeProvider;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProposalFusionRequest implements Serializable {

    private static final long serialVersionUID = -416641117;

    public static final TypeProvider<ProposalFusionRequest> TYPES =
            new TypeProvider<>(ProposalFusionRequest.class,
                    new ParameterizedTypeReference<List<ProposalFusionRequest>>() {
                    });

    private Long id;
    private Long fromUserId;
    private Long toUserId;
    private Long fromProposalId;
    private Long toProposalId;
    private Timestamp createdAt;
    private Timestamp updatedAt;
    private String    status;
    private Long contestId;
    private Long proposalId;
    private String    requestText;
    private String    commonText;

    public ProposalFusionRequest() {}

    public ProposalFusionRequest(ProposalFusionRequest value) {
        this.id = value.id;
        this.fromUserId = value.fromUserId;
        this.toUserId = value.toUserId;
        this.fromProposalId = value.fromProposalId;
        this.toProposalId = value.toProposalId;
        this.createdAt = value.createdAt;
        this.updatedAt = value.updatedAt;
        this.status = value.status;
        this.contestId = value.contestId;
        this.proposalId = value.proposalId;
        this.requestText = value.requestText;
        this.commonText = value.commonText;
    }

    public ProposalFusionRequest(Long id, Long fromUserId, Long toUserId, Long fromProposalId,
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

    public Long getFromUserId() {
        return fromUserId;
    }

    public Long getToUserId() {
        return toUserId;
    }

    public Long getFromProposalId() {
        return fromProposalId;
    }

    public Long getToProposalId() {
        return toProposalId;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public String getStatus() {
        return status;
    }

    public Long getContestId() {
        return contestId;
    }

    public Long getProposalId() {
        return proposalId;
    }

    public String getRequestText() {
        return requestText;
    }

    public String getCommonText() {
        return commonText;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setFromUserId(Long fromUserId) {
        this.fromUserId = fromUserId;
    }

    public void setToUserId(Long toUserId) {
        this.toUserId = toUserId;
    }

    public void setFromProposalId(Long fromProposalId) {
        this.fromProposalId = fromProposalId;
    }

    public void setToProposalId(Long toProposalId) {
        this.toProposalId = toProposalId;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setContestId(Long contestId) {
        this.contestId = contestId;
    }

    public void setProposalId(Long proposalId) {
        this.proposalId = proposalId;
    }

    public void setRequestText(String requestText) {
        this.requestText = requestText;
    }

    public void setCommonText(String commonText) {
        this.commonText = commonText;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        ProposalFusionRequest that = (ProposalFusionRequest) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(fromUserId, that.fromUserId) &&
                Objects.equals(toUserId, that.toUserId) &&
                Objects.equals(fromProposalId, that.fromProposalId) &&
                Objects.equals(toProposalId, that.toProposalId) &&
                Objects.equals(createdAt, that.createdAt) &&
                Objects.equals(updatedAt, that.updatedAt) &&
                Objects.equals(status, that.status) &&
                Objects.equals(contestId, that.contestId) &&
                Objects.equals(proposalId, that.proposalId) &&
                Objects.equals(requestText, that.requestText) &&
                Objects.equals(commonText, that.commonText);
    }

    @Override
    public int hashCode() {
        return Objects
                .hash(id, fromUserId, toUserId, fromProposalId, toProposalId, createdAt, updatedAt,
                        status, contestId, proposalId, requestText, commonText);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("id", id)
                .append("fromUserId", fromUserId)
                .append("toUserId", toUserId)
                .append("fromProposalId", fromProposalId)
                .append("toProposalId", toProposalId)
                .append("createdAt", createdAt)
                .append("updatedAt", updatedAt)
                .append("status", status)
                .append("contestId", contestId)
                .append("proposalId", proposalId)
                .append("requestText", requestText)
                .append("commonText", commonText)
                .toString();
    }
}
