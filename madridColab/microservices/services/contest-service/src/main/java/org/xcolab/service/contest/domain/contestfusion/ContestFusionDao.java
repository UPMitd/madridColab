package org.xcolab.service.contest.domain.contestfusion;

import org.xcolab.model.tables.pojos.ContestFusion;
import org.xcolab.service.contest.exceptions.NotFoundException;

import java.util.List;

public interface ContestFusionDao {

    ContestFusion create(
            ContestFusion ContestFusion);

    ContestFusion get(Long id) throws NotFoundException;

    List<ContestFusion> getByContests(Long contest_id_1, Long contest_id_2);

    boolean update(ContestFusion ContestFusion);
}
