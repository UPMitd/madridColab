package org.xcolab.service.members.domain.datacity;

import org.xcolab.model.tables.pojos.CommunityRegistry;
import org.xcolab.model.tables.pojos.DataCity;
import org.xcolab.model.tables.pojos.DataPeople;

import java.util.Optional;

public interface DataCityDao {
    Optional<DataCity> get(long userId);
    DataCity create(DataCity pojo);
}
