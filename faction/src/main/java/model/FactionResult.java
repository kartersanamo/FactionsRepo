package model;

/**
 * Outcome of a faction action. The system returns this; commands map it to messages.
 */
public enum FactionResult {
    SUCCESS,
    NOT_IN_FACTION,
    NO_PERMISSION,
    NOT_YOUR_CLAIM,
    ALREADY_CLAIMED,
    NOT_ENOUGH_POWER,
    FAILED;

    public boolean ok() {
        return this == SUCCESS;
    }
}
