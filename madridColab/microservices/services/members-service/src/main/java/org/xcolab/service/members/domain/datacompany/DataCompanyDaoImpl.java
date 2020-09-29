package org.xcolab.service.members.domain.datacompany;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import org.xcolab.model.tables.pojos.DataCompany;
import org.xcolab.model.tables.records.DataCompanyRecord;
import org.xcolab.service.members.util.email.AccountDetailsEmmaAPI;

import java.util.Optional;

import static org.xcolab.model.Tables.DATA_COMPANY;


@Repository
public class DataCompanyDaoImpl implements DataCompanyDao {

    private final DSLContext dslContext;
    private static final Logger log = LoggerFactory.getLogger(AccountDetailsEmmaAPI.class);


    @Autowired
    public DataCompanyDaoImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Optional<DataCompany> get(long userId) {
        final Record record = dslContext.select()
                .from(DATA_COMPANY)
                .where(DATA_COMPANY.ID_USER.eq(userId))
                .fetchOne();
        if (record == null) {
            return Optional.empty();
        }
        return Optional.of(record.into(DataCompany.class));
    }

    @Override
    public DataCompany create(DataCompany dataCompany) {
        final DataCompanyRecord record = dslContext.insertInto(DATA_COMPANY)
                .set(DATA_COMPANY.ID_USER, dataCompany.getIdUser())
                .set(DATA_COMPANY.TWITTER, dataCompany.getTwitter())
                .set(DATA_COMPANY.WEB, dataCompany.getWeb())
                .set(DATA_COMPANY.IS_COMMUNITY, dataCompany.getIsCommunity())
                .set(DATA_COMPANY.ID_SECTOR_COMPANY, dataCompany.getIdSectorCompany())
                .returning(DATA_COMPANY.ID_USER)
                .fetchOne();

        /*if (record == null) {
            throw new IllegalStateException("Failed to retrieve id after creating UpmRoleRegistry");
        }
        return record.into(CommunityRegistry.class);*/
        return dataCompany;
    }
}
