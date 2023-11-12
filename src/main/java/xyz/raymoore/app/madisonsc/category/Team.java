package xyz.raymoore.app.madisonsc.category;

public enum Team {
    ARI("Arizona", "Cardinals"),
    ATL("Atlanta", "Falcons"),
    BAL("Baltimore", "Ravens"),
    BUF("Buffalo", "Bills"),
    CAR("Carolina", "Panthers"),
    CHI("Chicago", "Bears"),
    CIN("Cincinnati", "Bengals"),
    CLE("Cleveland", "Browns"),
    DAL("Dallas", "Cowboys"),
    DEN("Denver", "Broncos"),
    DET("Detroit", "Lions"),
    GB("Green Bay", "Packers"),
    HOU("Houston", "Texans"),
    IND("Indianapolis", "Colts"),
    JAC("Jacksonville", "Jaguars"),
    KC("Kansas City", "Chiefs"),
    LV("Las Vegas", "Raiders"),
    LAC("Los Angeles", "Chargers"),
    LAR("Los Angeles", "Rams"),
    MIA("Miami", "Dolphins"),
    MIN("Minnesota", "Vikings"),
    NE("New England", "Patriots"),
    NO("New Orleans", "Saints"),
    NYG("New York", "Giants"),
    NYJ("New York", "Jets"),
    PHI("Philadelphia", "Eagles"),
    PIT("Pittsburgh", "Steelers"),
    SF("San Francisco", "49ers"),
    SEA("Seattle", "Seahawks"),
    TB("Tampa Bay", "Buccaneers"),
    TEN("Tennessee", "Titans"),
    WAS("Washington", "Commanders");

    private final String location;
    private final String name;

    Team(String location, String name) {
        this.location = location;
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public String getName() {
        return name;
    }
}
