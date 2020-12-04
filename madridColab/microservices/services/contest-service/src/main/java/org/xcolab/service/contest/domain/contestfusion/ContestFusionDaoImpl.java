package org.xcolab.service.contest.domain.contestfusion;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.jooq.SelectQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.xcolab.model.tables.pojos.Contest;
import org.xcolab.model.tables.pojos.ContestDiscussion;
import org.xcolab.model.tables.pojos.ContestFusion;
import org.xcolab.model.tables.pojos.ProposalFusionRequest;
import org.xcolab.model.tables.records.ContestFusionRecord;
import org.xcolab.model.tables.records.ProposalFusionRequestRecord;
import org.xcolab.service.contest.exceptions.NotFoundException;
import org.xcolab.service.contest.utils.promotion.PromotionService;
import java.util.ArrayList;
import java.util.List;

import static org.xcolab.model.Tables.CONTEST;
import static org.xcolab.model.Tables.CONTEST_FUSION;
import static org.xcolab.model.Tables.PROPOSAL_FUSION_REQUEST;

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

    @Override
    public List<ContestFusion> getByContests(Long contest_id_1, Long contest_id_2) {
        ArrayList<ContestFusion> arlcf = new ArrayList<>();
        Record record =
                dslContext.select().from(CONTEST_FUSION)
                        .where(CONTEST_FUSION.CONTEST_ID_1.eq(contest_id_1))
                        .and(CONTEST_FUSION.CONTEST_ID_2.eq(contest_id_2))
                        .fetchOne();
        if (record == null) {
            record = dslContext.select().from(CONTEST_FUSION)
                    .where(CONTEST_FUSION.CONTEST_ID_1.eq(contest_id_2))
                    .and(CONTEST_FUSION.CONTEST_ID_2.eq(contest_id_1))
                    .fetchOne();

            if (record == null)
                return null;
        }
        ContestFusion cf = record.into(ContestFusion.class);
        arlcf.add(cf);
        return arlcf;
    }

    @Override
    public List<ContestFusion> getAll() {
        final SelectQuery<ContestFusionRecord> query =  this.dslContext.selectFrom(CONTEST_FUSION)
                .getQuery();

        return query.fetchInto(ContestFusion.class);
    }
}
