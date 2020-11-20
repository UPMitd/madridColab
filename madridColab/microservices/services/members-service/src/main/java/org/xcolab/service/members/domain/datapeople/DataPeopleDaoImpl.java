package org.xcolab.service.members.domain.datapeople;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.xcolab.model.tables.pojos.CommunityRegistry;
import org.xcolab.model.tables.pojos.DataPeople;
import org.xcolab.model.tables.records.CommunityRegistryRecord;
import org.xcolab.model.tables.records.DataPeopleRecord;
import org.xcolab.service.members.util.email.AccountDetailsEmmaAPI;

import java.util.Optional;

import static org.xcolab.model.Tables.COMMUNITY_REGISTRY;
import static org.xcolab.model.Tables.DATA_PEOPLE;


@Repository
public class DataPeopleDaoImpl implements DataPeopleDao {

    private final DSLContext dslContext;
    private static final Logger log = LoggerFactory.getLogger(AccountDetailsEmmaAPI.class);


    @Autowired
    public DataPeopleDaoImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Optional<DataPeople> get(long userId) {
        final Record record = dslContext.select()
                .from(DATA_PEOPLE)
                .where(DATA_PEOPLE.ID_USER.eq(userId))
                .fetchOne();
        if (record == null) {
            return Optional.empty();
        }
        return Optional.of(record.into(DataPeople.class));
    }

    @Override
    public DataPeople create(DataPeople dataPeople) {
        log.error("DataPeopleDaoImpl "+dataPeople.toString());
        System.out.println("DataPeopleDaoImpl "+dataPeople.toString());

        final DataPeopleRecord record = dslContext.insertInto(DATA_PEOPLE)
                .set(DATA_PEOPLE.ID_USER, dataPeople.getIdUser())
                .set(DATA_PEOPLE.TWITTER, dataPeople.getTwitter())
                .set(DATA_PEOPLE.LINKEDIN, dataPeople.getLinkedin())
                .set(DATA_PEOPLE.IS_COMMUNITY, dataPeople.getIsCommunity())
                .set(DATA_PEOPLE.ID_SECTOR, dataPeople.getIdSector())
                .set(DATA_PEOPLE.OTHER, dataPeople.getOther())
                .set(DATA_PEOPLE.COD_POSTAL, dataPeople.getCodPostal())
                .returning(DATA_PEOPLE.ID_USER)
                .fetchOne();

        /*if (record == null) {
            throw new IllegalStateException("Failed to retrieve id after creating UpmRoleRegistry");
        }
        return record.into(CommunityRegistry.class);*/
        return dataPeople;
    }
}
