package org.xcolab.client.members;

import org.xcolab.util.http.client.CoLabService;
import org.xcolab.util.http.client.enums.ResourceEnum;

public enum UserResource implements ResourceEnum {
    USER("members"),
    USER_CATEGORY("membercategories"),
    LOGIN_LOG("loginLogs"),
    LOGIN_TOKEN("loginTokens"),
    MESSAGES("messages"),
    ROLE_GROUP("roleGroups"),
    STAFF_MEMBER("staffMembers"),
    USER_GROUP("usersGroups"),
    PLATFORM_TEAM("platformteams"),
    SSO_CLIENT_DETAILS("ssoClientDetails"),
    DATA_PEOPLE("dataPeople"),
    DATA_COMPANY("dataCompany"),
    DATA_CITY("dataCity"),
    ODS_REGISTRY("odsRegistry"),
    COMMUNITY_REGISTRY("communityRegistry")
    ;

    private final String resourceName;

    UserResource(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public String getResourceName() {
        return resourceName;
    }

    @Override
    public CoLabService getCoLabService() {
        return CoLabService.MEMBER;
    }
}
