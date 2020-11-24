package org.xcolab.service.proposal.domain.proposalfusionrequest;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import org.xcolab.model.tables.pojos.Proposal;
import org.xcolab.model.tables.pojos.ProposalFusionRequest;
import org.xcolab.model.tables.records.ProposalFusionRequestRecord;
import org.xcolab.service.contest.exceptions.NotFoundException;

import java.util.List;

import static org.xcolab.model.Tables.PROPOSAL;
import static org.xcolab.model.Tables.PROPOSAL_FUSION_REQUEST;

@Repository
public class ProposalFusionRequestDaoImpl implements ProposalFusionRequestDao {

    private final DSLContext dslContext;

    @Autowired
    public ProposalFusionRequestDaoImpl(DSLContext dslContext) {
        Assert.notNull(dslContext, "DSLContext bean is required");
        this.dslContext = dslContext;
    }

    @Override
    public ProposalFusionRequest create(ProposalFusionRequest proposalFusionRequest) {

        ProposalFusionRequestRecord ret = this.dslContext.insertInto(PROPOSAL_FUSION_REQUEST)
                .set(PROPOSAL_FUSION_REQUEST.ID, proposalFusionRequest.getId())
                .set(PROPOSAL_FUSION_REQUEST.FROM_USER_ID, proposalFusionRequest.getFromUserId())
                .set(PROPOSAL_FUSION_REQUEST.TO_USER_ID, proposalFusionRequest.getToUserId())
                .set(PROPOSAL_FUSION_REQUEST.FROM_PROPOSAL_ID, proposalFusionRequest.getFromProposalId())
                .set(PROPOSAL_FUSION_REQUEST.TO_PROPOSAL_ID, proposalFusionRequest.getToProposalId())
                .set(PROPOSAL_FUSION_REQUEST.CREATED_AT, proposalFusionRequest.getCreatedAt())
                .set(PROPOSAL_FUSION_REQUEST.UPDATED_AT, proposalFusionRequest.getUpdatedAt())
                .set(PROPOSAL_FUSION_REQUEST.STATUS, proposalFusionRequest.getStatus())
                .set(PROPOSAL_FUSION_REQUEST.CONTEST_ID, proposalFusionRequest.getContestId())
                .set(PROPOSAL_FUSION_REQUEST.PROPOSAL_ID, proposalFusionRequest.getProposalId())
                .set(PROPOSAL_FUSION_REQUEST.REQUEST_TEXT, proposalFusionRequest.getRequestText())
                .set(PROPOSAL_FUSION_REQUEST.COMMON_TEXT, proposalFusionRequest.getCommonText())
                .returning(PROPOSAL_FUSION_REQUEST.ID)
                .fetchOne();
        if (ret != null) {
            proposalFusionRequest.setId(ret.getValue(PROPOSAL_FUSION_REQUEST.ID));
            return proposalFusionRequest;
        } else {
            return null;
        }
    }

    @Override
    public boolean update(ProposalFusionRequest proposalFusionRequest) {
        return dslContext.update(PROPOSAL_FUSION_REQUEST)
                .set(PROPOSAL_FUSION_REQUEST.ID, proposalFusionRequest.getId())
                .set(PROPOSAL_FUSION_REQUEST.FROM_USER_ID, proposalFusionRequest.getFromUserId())
                .set(PROPOSAL_FUSION_REQUEST.TO_USER_ID, proposalFusionRequest.getToUserId())
                .set(PROPOSAL_FUSION_REQUEST.FROM_PROPOSAL_ID, proposalFusionRequest.getFromProposalId())
                .set(PROPOSAL_FUSION_REQUEST.TO_PROPOSAL_ID, proposalFusionRequest.getToProposalId())
                .set(PROPOSAL_FUSION_REQUEST.CREATED_AT, proposalFusionRequest.getCreatedAt())
                .set(PROPOSAL_FUSION_REQUEST.UPDATED_AT, proposalFusionRequest.getUpdatedAt())
                .set(PROPOSAL_FUSION_REQUEST.STATUS, proposalFusionRequest.getStatus())
                .set(PROPOSAL_FUSION_REQUEST.CONTEST_ID, proposalFusionRequest.getContestId())
                .set(PROPOSAL_FUSION_REQUEST.PROPOSAL_ID, proposalFusionRequest.getProposalId())
                .set(PROPOSAL_FUSION_REQUEST.REQUEST_TEXT, proposalFusionRequest.getRequestText())
                .set(PROPOSAL_FUSION_REQUEST.COMMON_TEXT, proposalFusionRequest.getCommonText())
                .where(PROPOSAL_FUSION_REQUEST.ID.eq(proposalFusionRequest.getId()))
                .execute() > 0;
    }

    @Override
    public ProposalFusionRequest get(Long id) throws NotFoundException {

        final Record record =  this.dslContext.selectFrom(PROPOSAL_FUSION_REQUEST)
                .where(PROPOSAL_FUSION_REQUEST.ID.eq(id))
                .fetchOne();

        if (record == null) {
            throw new NotFoundException("ProposalFusionRequest with id " + id + " does not exist");
        }
        return record.into(ProposalFusionRequest.class);
    }

    @Override
    public List<ProposalFusionRequest> getAll()  {
        final SelectQuery<ProposalFusionRequestRecord> query =  this.dslContext.selectFrom(PROPOSAL_FUSION_REQUEST)
                .getQuery();

        return query.fetchInto(ProposalFusionRequest.class);
    }

    @Override
    public List<ProposalFusionRequest> getToUserId(Long id)  {
        final SelectQuery<ProposalFusionRequestRecord> query =  this.dslContext.selectFrom(PROPOSAL_FUSION_REQUEST)
                .where(PROPOSAL_FUSION_REQUEST.TO_USER_ID.eq(id))
                .getQuery();

        return query.fetchInto(ProposalFusionRequest.class);
    }

    @Override
    public List<ProposalFusionRequest> getFromUserId(Long id)  {
        final SelectQuery<ProposalFusionRequestRecord> query =  this.dslContext.selectFrom(PROPOSAL_FUSION_REQUEST)
                .where(PROPOSAL_FUSION_REQUEST.FROM_USER_ID.eq(id))
                .getQuery();

        return query.fetchInto(ProposalFusionRequest.class);
    }
}
