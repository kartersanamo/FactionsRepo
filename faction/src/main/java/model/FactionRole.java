package model;

public enum FactionRole {
    LEADER,
    COLEADER,
    MOD,
    MEMBER,
    RECRUIT,
    ALT;

    public boolean isAlt() {
        return this == ALT;
    }

    public String prefix() {
        switch (this) {
            case LEADER:
                return "***";
            case COLEADER:
                return "**";
            case MOD:
                return "*";
            case MEMBER:
                return "+";
            case RECRUIT:
                return "-";
            case ALT:
            default:
                return "";
        }
    }

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
            case RECRUIT:
                return MEMBER;
            case MEMBER:
                return MOD;
            case MOD:
                return COLEADER;
            case ALT:
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
            case MEMBER:
                return RECRUIT;
            case ALT:
            default:
                return this;
        }
    }
}
