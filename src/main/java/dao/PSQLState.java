package dao;

public enum PSQLState {

    NOT_NULL_STATE ("23502"),
    UNIQUE_STATE ("23505"),
    CHECK_STATE ("23514"),
    FOREIGN_KEY_STATE ("23503"),
    VARCHAR_LENGTH_STATE ("22001");

    public final String state;

    PSQLState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

}
