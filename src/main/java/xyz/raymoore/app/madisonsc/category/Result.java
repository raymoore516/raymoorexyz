package xyz.raymoore.app.madisonsc.category;

public enum Result {
    win("W"),
    loss("L"),
    tie("T");

    private final String code;

    Result(String code) {
        this.code = code;
    }

    public static Result findByCode(String code) {
        for (Result result : values()) {
            if (result.getCode().equals(code)) {
                return result;
            }
        }

        return null;
    }

    public String getCode() {
        return code;
    }
}
