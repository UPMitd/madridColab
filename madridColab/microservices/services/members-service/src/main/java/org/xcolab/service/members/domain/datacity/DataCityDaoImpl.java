package org.xcolab.service.members.domain.datacity;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.xcolab.model.tables.pojos.DataCity;
import org.xcolab.model.tables.records.DataCityRecord;
import org.xcolab.service.members.util.email.AccountDetailsEmmaAPI;

import java.util.Optional;

import static org.xcolab.model.Tables.DATA_CITY;


@Repository
public class DataCityDaoImpl implements DataCityDao {

    private final DSLContext dslContext;
    private static final Logger log = LoggerFactory.getLogger(AccountDetailsEmmaAPI.class);


    @Autowired
    public DataCityDaoImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Optional<DataCity> get(long userId) {
        final Record record = dslContext.select()
                .from(DATA_CITY)
                .where(DATA_CITY.ID_USER.eq(userId))
                .fetchOne();
        if (record == null) {
            return Optional.empty();
        }
        return Optional.of(record.into(DataCity.class));
    }

    @Override
    public DataCity create(DataCity dataCity) {

        final DataCityRecord record = dslContext.insertInto(DATA_CITY)
                .set(DATA_CITY.ID_USER, dataCity.getIdUser())
                .set(DATA_CITY.TWITTER, dataCity.getTwitter())
                .set(DATA_CITY.WEB, dataCity.getWeb())
                .set(DATA_CITY.AUT_COMMUNITY, dataCity.getAutCommunity())
                .set(DATA_CITY.MUNICIPALITY, dataCity.getMunicipality())
                .returning(DATA_CITY.ID_USER)
                .fetchOne();

        /*if (record == null) {
            throw new IllegalStateException("Failed to retrieve id after creating UpmRoleRegistry");
        }
        return record.into(CommunityRegistry.class);*/
        return dataCity;
    }
}
