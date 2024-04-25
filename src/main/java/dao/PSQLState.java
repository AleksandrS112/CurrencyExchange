package dao;

enum PSQLState {

    NOT_NULL ("23502"),
    UNIQUE ("23505"),
    CHECK ("23514"),
    FOREIGN_KEY ("23503"),
    VARCHAR_LENGTH ("22001"),
    NUMERIC ("22003");

    private final String state;

    PSQLState(String state) {
        this.state = state;
    }

    String getState() {
        return this.state;
    }

}
