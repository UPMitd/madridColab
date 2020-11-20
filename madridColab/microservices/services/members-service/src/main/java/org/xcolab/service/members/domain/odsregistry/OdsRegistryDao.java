package org.xcolab.service.members.domain.odsregistry;

import org.xcolab.model.tables.pojos.CommunityRegistry;
import org.xcolab.model.tables.pojos.OdsRegistry;

import java.util.Optional;

public interface OdsRegistryDao {
    Optional<OdsRegistry> get(long userId);
    OdsRegistry create(OdsRegistry pojo);
}
