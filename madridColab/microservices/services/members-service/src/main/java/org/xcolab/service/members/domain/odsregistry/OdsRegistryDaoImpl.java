package org.xcolab.service.members.domain.odsregistry;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import org.xcolab.model.tables.pojos.OdsRegistry;
import org.xcolab.model.tables.records.OdsRegistryRecord;
import org.xcolab.service.members.domain.odsregistry.OdsRegistryDao;

import java.util.Optional;

import static org.xcolab.model.Tables.COMMUNITY_REGISTRY;
import static org.xcolab.model.Tables.ODS_REGISTRY;


@Repository
public class OdsRegistryDaoImpl implements OdsRegistryDao {

    private final DSLContext dslContext;

    @Autowired
    public OdsRegistryDaoImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Optional<OdsRegistry> get(long userId) {
        final Record record = dslContext.select()
                .from(ODS_REGISTRY)
                .where(ODS_REGISTRY.ID_USER.eq(userId))
                .fetchOne();
        if (record == null) {
            return Optional.empty();
        }
        return Optional.of(record.into(OdsRegistry.class));
    }

    @Override
    public OdsRegistry create(OdsRegistry odsRegistry) {
        final OdsRegistryRecord record = dslContext.insertInto(ODS_REGISTRY)
                .set(ODS_REGISTRY.ID_USER, odsRegistry.getIdUser())
                .set(ODS_REGISTRY.ID_ODS, odsRegistry.getIdOds())
                .returning(ODS_REGISTRY.ID_USER)
                .fetchOne();

        /*if (record == null) {
            throw new IllegalStateException("Failed to retrieve id after creating UpmRoleRegistry");
        }
        return record.into(CommunityRegistry.class);*/
        return odsRegistry;
    }
}
