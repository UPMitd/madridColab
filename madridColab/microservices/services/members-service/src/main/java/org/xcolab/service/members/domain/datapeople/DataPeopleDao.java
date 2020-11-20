package org.xcolab.service.members.domain.datapeople;

import org.xcolab.model.tables.pojos.CommunityRegistry;
import org.xcolab.model.tables.pojos.DataPeople;

import java.util.Optional;

public interface DataPeopleDao {
    Optional<DataPeople> get(long userId);
    DataPeople create(DataPeople pojo);
}
