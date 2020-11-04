package org.xcolab.service.contest.domain.contestfusion;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import org.xcolab.model.tables.pojos.ContestFusion;
import org.xcolab.model.tables.records.ContestFusionRecord;
import org.xcolab.service.contest.exceptions.NotFoundException;

import static org.xcolab.model.Tables.CONTEST_FUSION;

@Repository
public class ContestFusionDaoImpl implements ContestFusionDao {

    private final DSLContext dslContext;

    @Autowired
    public ContestFusionDaoImpl(DSLContext dslContext) {
        Assert.notNull(dslContext, "DSLContext bean is required");
        this.dslContext = dslContext;
    }

    @Override
    public ContestFusion create(ContestFusion contestFusion) {

        ContestFusionRecord ret = this.dslContext.insertInto(CONTEST_FUSION)
                .set(CONTEST_FUSION.ID_FUSION, contestFusion.getIdFusion())
                .set(CONTEST_FUSION.CONTEST_ID_1, contestFusion.getContestId_1())
                .set(CONTEST_FUSION.CONTEST_ID_2, contestFusion.getContestId_2())
                .returning(CONTEST_FUSION.ID_FUSION)
                .fetchOne();
        if (ret != null) {
            contestFusion.setIdFusion(ret.getValue(CONTEST_FUSION.ID_FUSION));
            return contestFusion;
        } else {
            return null;
        }
    }

    @Override
    public boolean update(ContestFusion contestFusion) {
        return dslContext.update(CONTEST_FUSION)
                .set(CONTEST_FUSION.ID_FUSION, contestFusion.getIdFusion())
                .set(CONTEST_FUSION.CONTEST_ID_1, contestFusion.getContestId_1())
                .set(CONTEST_FUSION.CONTEST_ID_2, contestFusion.getContestId_2())
                .where(CONTEST_FUSION.ID_FUSION.eq(contestFusion.getIdFusion()))
                .execute() > 0;
    }

    @Override
    public ContestFusion get(Long id) throws NotFoundException {

        final Record record =  this.dslContext.selectFrom(CONTEST_FUSION)
                .where(CONTEST_FUSION.ID_FUSION.eq(id))
                .fetchOne();

        if (record == null) {
            throw new NotFoundException("ContestFusion with id " + id + " does not exist");
        }
        return record.into(ContestFusion.class);

    }
}
