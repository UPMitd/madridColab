package org.xcolab.service.contest.domain.contestfusion;

import org.xcolab.model.tables.pojos.ContestFusion;
import org.xcolab.model.tables.pojos.ProposalFusionRequest;
import org.xcolab.service.contest.exceptions.NotFoundException;

public interface ContestFusionDao {

    ContestFusion create(
            ContestFusion ContestFusion);

    ContestFusion get(Long id) throws NotFoundException;

    boolean update(ContestFusion ContestFusion);
}
