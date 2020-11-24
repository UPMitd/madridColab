package org.xcolab.service.proposal.domain.proposalfusionrequest;

import org.xcolab.model.tables.pojos.ProposalFusionRequest;
import org.xcolab.service.contest.exceptions.NotFoundException;

import java.util.List;

public interface ProposalFusionRequestDao {

    ProposalFusionRequest create(
            ProposalFusionRequest ProposalFusionRequest);

    ProposalFusionRequest get(Long id) throws NotFoundException;

    boolean update(ProposalFusionRequest ProposalFusionRequest);

    List<ProposalFusionRequest> getAll();

    List<ProposalFusionRequest> getFromUserId(Long id);

    List<ProposalFusionRequest> getToUserId(Long id);
}
