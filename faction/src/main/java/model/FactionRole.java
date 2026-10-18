package model;

public enum FactionRole {
    LEADER,
    COLEADER,
    MOD,
    MEMBER;

    public boolean canInvite() {
        return this == LEADER || this == COLEADER || this == MOD;
    }

    public boolean canKick() {
        return this == LEADER || this == COLEADER;
    }

    public boolean canClaim() {
        return this == LEADER || this == COLEADER || this == MOD;
    }

    public boolean canSetHome() {
        return this == LEADER || this == COLEADER;
    }

    public boolean isAtLeast(FactionRole other) {
        return ordinal() <= other.ordinal();
    }

    public FactionRole promote() {
        switch (this) {
            case MEMBER:
                return MOD;
            case MOD:
                return COLEADER;
            default:
                return this;
        }
    }

    public FactionRole demote() {
        switch (this) {
            case COLEADER:
                return MOD;
            case MOD:
                return MEMBER;
            default:
                return this;
        }
    }
}
