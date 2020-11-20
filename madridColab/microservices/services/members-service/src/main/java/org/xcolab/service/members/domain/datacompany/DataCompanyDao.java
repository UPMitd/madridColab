package org.xcolab.service.members.domain.datacompany;

import org.xcolab.model.tables.pojos.DataCompany;
import org.xcolab.model.tables.pojos.DataPeople;

import java.util.Optional;

public interface DataCompanyDao {
    Optional<DataCompany> get(long userId);
    DataCompany create(DataCompany pojo);
}
