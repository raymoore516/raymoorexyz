package xyz.raymoore.app.madisonsc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import io.javalin.Javalin;
import io.javalin.http.Context;
import net.jextra.fauxjo.HomeGroup;
import org.jetbrains.annotations.NotNull;
import xyz.raymoore.AppHomes;
import xyz.raymoore.AppSettings;
import xyz.raymoore.app.madisonsc.bean.*;
import xyz.raymoore.app.madisonsc.category.*;
import xyz.raymoore.app.madisonsc.db.*;
import xyz.raymoore.javalin.Routes;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.UUID;

public class MadisonSC implements Routes {
    private final DataSource ds;

    // ---

    public MadisonSC(AppSettings settings) {
        this.ds = settings.getPostgres().useDataSource();
    }

    public void register(Javalin app) {
        app.get("madisonsc/{year}/{week}", this::renderWeeklyPicks);
        app.post("madisonsc/{year}/{week}", this::submitWeeklyPicks);
    }

    // ---

    public void renderWeeklyPicks(@NotNull Context ctx) throws SQLException {
        try (Connection conn = ds.getConnection()) {
            AppHomes.use().setConnection(conn);

            ctx.html(String.format("The year is %s and the week is %s",
                            ctx.pathParam("year"), ctx.pathParam("week")));
        }
    }

    public void submitWeeklyPicks(@NotNull Context ctx) throws Exception {
        try (Connection conn = ds.getConnection()) {
            Engine engine = new Engine(conn);

            int year = Integer.parseInt(ctx.pathParam("year"));
            int week = Integer.parseInt(ctx.pathParam("week"));
            PicksSubmission submission = ctx.bodyAsClass(PicksSubmission.class);

            engine.submitWeeklyPicks(year, week, submission);
        }
    }

    // ---

    public static class Engine {
        private final Homes homes;

        public Engine(Connection conn) throws SQLException {
            this.homes = new Homes(conn);
        }

        public void setConnection(Connection conn) throws SQLException {
            this.homes.setConnection(conn);
        }

        public void submitWeeklyPicks(int year, int week, PicksSubmission submission) throws SQLException {
            UUID contestantId = new UUID(0, 0);
            for (String shorthand : submission.getPicks()) {
                Pick bean = parseWeeklyPick(year, week, shorthand);
                bean.setContestantId(contestantId);
                homes.getPickHome().insert(bean);
            }
        }

        private static Pick parseWeeklyPick(int year, int week, String value) {
            // pc[0] = "CLE", "IND", "GB", etc.
            // pc[1] = "+3.5", "PK", "-7", etc.
            // pc[2] = "W" or "L" or "T"
            String[] pc = value.split("\\s+");

            // Build database object
            Pick pick = new Pick();
            pick.setYear(year);
            pick.setWeek(week);
            pick.setTeam(Team.find(pc[0]));
            pick.setFavorite(pc[1].startsWith("+"));  // Ties are false
            pick.setLine(pc[1].toUpperCase().contains("PK") ? 0
                    : Double.parseDouble(pc[1].substring(1)));
            pick.setResult(Result.findByCode(pc[2]));

            return pick;
        }
    }

    // ---

    public static class Homes extends HomeGroup {
        public Homes() {
            addHome(Contestant.Home.class, new Contestant.Home());
            addHome(Pick.Home.class, new Pick.Home());
        }

        public Homes(Connection conn) throws SQLException {
            this();
            this.setConnection(conn);
        }

        public Contestant.Home getContestantHome() {
            return getHome(Contestant.Home.class);
        }

        public Pick.Home getPickHome() {
            return getHome(Pick.Home.class);
        }
    }
}
