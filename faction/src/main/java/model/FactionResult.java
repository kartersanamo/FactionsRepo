package model;

/**
 * Outcome of a faction action. The system returns this; commands map it to messages.
 */
public enum FactionResult {
    SUCCESS,
    NOT_IN_FACTION,
    ALREADY_IN_FACTION,
    NO_PERMISSION,
    NOT_LEADER,
    NOT_YOUR_CLAIM,
    ALREADY_CLAIMED,
    NOT_CLAIMED,
    OTHER_CLAIM,
    NOT_ENOUGH_POWER,
    NOT_ENOUGH,
    INVALID_AMOUNT,
    INVALID_NAME,
    INVALID_RANK,
    NAME_TAKEN,
    FACTION_NOT_FOUND,
    PLAYER_NOT_FOUND,
    NOT_MEMBER,
    ALREADY_MEMBER,
    BANNED,
    NO_INVITE,
    INVITE_MISSING,
    IS_LEADER,
    TARGET_IS_LEADER,
    PERMANENT,
    NO_HOME,
    WARP_MISSING,
    SELF,
    FAILED;

    public boolean ok() {
        return this == SUCCESS;
    }
}
