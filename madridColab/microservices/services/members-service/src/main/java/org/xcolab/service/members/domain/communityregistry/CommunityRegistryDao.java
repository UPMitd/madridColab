package org.xcolab.service.members.domain.communityregistry;

import org.xcolab.model.tables.pojos.CommunityRegistry;

import java.util.Optional;

public interface CommunityRegistryDao {
    Optional<CommunityRegistry> get(long userId);
    CommunityRegistry create(CommunityRegistry pojo);
}
