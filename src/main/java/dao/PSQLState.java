package dao;

public enum PSQLState {

    NOT_NULL ("23502"),
    UNIQUE ("23505"),
    CHECK ("23514"),
    FOREIGN_KEY ("23503"),
    VARCHAR_LENGTH ("22001");

    private final String state;

    PSQLState(String state) {
        this.state = state;
    }

    public String getState() {
        return this.state;
    }

}
