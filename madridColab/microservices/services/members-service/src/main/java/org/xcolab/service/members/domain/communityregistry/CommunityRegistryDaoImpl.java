package org.xcolab.service.members.domain.communityregistry;

import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import org.xcolab.model.tables.pojos.CommunityRegistry;
import org.xcolab.model.tables.records.CommunityRegistryRecord;

import java.util.Optional;

import static org.xcolab.model.Tables.COMMUNITY_REGISTRY;


@Repository
public class CommunityRegistryDaoImpl implements CommunityRegistryDao {

    private final DSLContext dslContext;

    @Autowired
    public CommunityRegistryDaoImpl(DSLContext dslContext) {
        this.dslContext = dslContext;
    }

    @Override
    public Optional<CommunityRegistry> get(long userId) {
        final Record record = dslContext.select()
                .from(COMMUNITY_REGISTRY)
                .where(COMMUNITY_REGISTRY.ID_USER.eq(userId))
                .fetchOne();
        if (record == null) {
            return Optional.empty();
        }
        return Optional.of(record.into(CommunityRegistry.class));
    }

    @Override
    public CommunityRegistry create(CommunityRegistry communityRegistry) {
        final CommunityRegistryRecord record = dslContext.insertInto(COMMUNITY_REGISTRY)
                .set(COMMUNITY_REGISTRY.ID_USER, communityRegistry.getIdUser())
                .set(COMMUNITY_REGISTRY.ID_ROLE, communityRegistry.getIdRole())
                .returning(COMMUNITY_REGISTRY.ID_USER)
                .fetchOne();

        /*if (record == null) {
            throw new IllegalStateException("Failed to retrieve id after creating UpmRoleRegistry");
        }
        return record.into(CommunityRegistry.class);*/
        return communityRegistry;
    }
}
