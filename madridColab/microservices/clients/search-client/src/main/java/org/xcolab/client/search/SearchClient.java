package org.xcolab.client.search;

import org.xcolab.client.search.pojo.SearchPojo;
import org.xcolab.util.http.client.RestResource;
import org.xcolab.util.http.client.RestResource1;
import org.xcolab.util.http.client.queries.ListQuery;
import org.xcolab.util.http.exceptions.EntityNotFoundException;

import java.util.List;

public final class SearchClient {

    //TODO COLAB-2594: API naming (singular collection?)
    private static final RestResource<SearchPojo, Long> searchResource = new RestResource1<>(
            SearchResource.SEARCH, SearchPojo.TYPES);

    public static List<SearchPojo> search(Integer startRecord, Integer limitRecord, String filter, String query) {
        ListQuery<SearchPojo> searchPojoListQuery = searchResource.list();
        if (startRecord != null && limitRecord != null) {
            searchPojoListQuery.addRange(startRecord,limitRecord);
        }

        if (filter != null) {
            searchPojoListQuery.optionalQueryParam("filter", filter);
        }
        if (query != null) {
            searchPojoListQuery.optionalQueryParam("query", query);
        }
        return searchPojoListQuery.execute();

    }
    public static Integer searchCount(String sort, String query) {
        try {
            return searchResource.collectionService("count", Integer.class)
                    .optionalQueryParam("sort", sort)
                    .optionalQueryParam("query", query)
                    .getChecked();
        } catch (EntityNotFoundException e) {
            return 0;
        }

    }


}
