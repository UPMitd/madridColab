package org.xcolab.client.members;

import org.xcolab.client.members.pojo.StaffMember;
import org.xcolab.util.http.caching.CacheKeys;
import org.xcolab.util.http.caching.CacheName;
import org.xcolab.util.http.client.RestResource;
import org.xcolab.util.http.client.RestResource1;

import java.util.List;

public class StaffMemberClient {

    private static final RestResource<StaffMember, Long> staffMemberResource =
            new RestResource1<>(UserResource.STAFF_MEMBER, StaffMember.TYPES);

    public static List<StaffMember> getStaffMembersByCategoryId(long categoryId) {
        return staffMemberResource.list()
                .queryParam("categoryId", categoryId)
                .queryParam("limitRecord", Integer.MAX_VALUE) // since not all members are retrieved by default (20)
                .withCache(CacheKeys.withClass(StaffMember.class)
                        .withParameter("categoryId", categoryId).asList(), CacheName.MISC_LONG)
                .execute();
    }
}
