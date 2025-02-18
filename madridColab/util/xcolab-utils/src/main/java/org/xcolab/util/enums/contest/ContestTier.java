package org.xcolab.util.enums.contest;

import org.springframework.util.Assert;

public enum ContestTier {
    NONE(0L, "None"),
    BASIC(1L, "Tier I"),
    REGION_SECTOR(2L, "Tier II (region-sector)"),
    REGION_AGGREGATE(3L, "Tier III (region)"),
    GLOBAL(4L, "Tier IV (global)");

    public long getTierType() {
        return tierType;
    }

    private final long tierType;

    public String getTierName() {
        return tierName;
    }

    private final String tierName;

    ContestTier (long tierType, String tierName) {
        this.tierType = tierType;
        this.tierName = tierName;
    }

    public static ContestTier getContestTierByTierType(Long tierType)  {
        Assert.notNull(tierType, "TierType cannot be null");
        for (ContestTier contestTier : ContestTier.values()) {
            if (contestTier.getTierType() == tierType) {
                return contestTier;
            }
        }
        throw new IllegalArgumentException("No ContestTierType " + tierType);
    }
}
