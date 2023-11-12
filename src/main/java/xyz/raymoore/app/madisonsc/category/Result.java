package xyz.raymoore.app.madisonsc.category;

public enum Result {
    win("W"),
    loss("L"),
    tie("T");

    private final String code;

    Result(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
