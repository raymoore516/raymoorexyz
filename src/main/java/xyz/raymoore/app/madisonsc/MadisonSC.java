package xyz.raymoore.app.madisonsc;

import io.javalin.Javalin;
import io.javalin.http.Context;
import net.jextra.fauxjo.HomeGroup;
import net.jextra.fauxjo.transaction.Transaction;
import net.jextra.tucker.tucker.Block;
import net.jextra.tucker.tucker.Tucker;
import org.jetbrains.annotations.NotNull;
import xyz.raymoore.Settings;
import xyz.raymoore.Page;
import xyz.raymoore.app.madisonsc.bean.Submission;
import xyz.raymoore.app.madisonsc.category.Result;
import xyz.raymoore.app.madisonsc.category.Team;
import xyz.raymoore.app.madisonsc.db.Contestant;
import xyz.raymoore.app.madisonsc.db.Pick;
import xyz.raymoore.javalin.Routes;

import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

public class MadisonSC implements Routes {
    public static final String TUCK = "src/main/webapp/madisonsc/blocks.thtml";
    public static final String CSS = "/madisonsc/style.css";
    public static final String JS = "/madisonsc/script.js";

    private final DataSource ds;

    // ---

    public MadisonSC(Settings settings) throws IOException {
        this.ds = settings.getPostgres().useDataSource();
    }

    public void register(Javalin app) {
        app.get("madisonsc/picks/{year}/{week}", this::renderWeeklyPicks);
        app.post("madisonsc/picks/{year}/{week}", this::submitWeeklyPicks);
    }

    // ---

    public void renderWeeklyPicks(@NotNull Context ctx) throws SQLException, IOException {
        int year = Integer.parseInt(ctx.pathParam("year"));
        int week = Integer.parseInt(ctx.pathParam("week"));

        try (Connection conn = ds.getConnection()) {
            Homes homes = new Homes(conn);
            Pages pages = new Pages();

            String title = String.format("Madison SC: Year %d, Week %d", year, week);
            List<Pick> picks = homes.getPickHome().findByYearAndWeek(year, week);
            Page page = pages.buildWeeklyPicks(title, picks);
            ctx.html(page.render());
        }
    }

    public void submitWeeklyPicks(@NotNull Context ctx) throws Exception {
        int year = Integer.parseInt(ctx.pathParam("year"));
        int week = Integer.parseInt(ctx.pathParam("week"));

        try (Transaction trans = new Transaction(ds.getConnection())) {
            Engine engine = new Engine(trans.getConnection());
            Submission submission = ctx.bodyAsClass(Submission.class);

            engine.submitWeeklyPicks(year, week, submission);
            trans.commit();
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

    // ---

    public static class Engine {
        private final Homes homes;

        public Engine(Connection conn) throws SQLException {
            this.homes = new Homes(conn);
        }

        public void submitWeeklyPicks(int year, int week, Submission submission) throws SQLException {
            Contestant contestant = homes.getContestantHome().findByName(submission.getContestant());
            if (contestant == null) {
                throw new SQLException("Contestant not found");
            }

            for (String shorthand : submission.getPicks()) {
                Pick bean = parseWeeklyPick(year, week, shorthand);
                bean.setContestantId(contestant.getId());
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

    public static class Pages {
        private final Tucker tucker;

        public Pages() throws IOException {
            this.tucker = new Tucker(new File(TUCK));
        }

        public Page buildWeeklyPicks(String title, List<Pick> picks) throws IOException {
            Page page = new Page(title);
            page.addStylesheet(CSS);
            page.addScript(JS);

            Block content = tucker.buildBlock("content");
            page.setContent(content);

            for (Pick p : picks) {
                Block pick = tucker.buildBlock("pick");
                pick.setVariable("foo", p.toString());
                content.insert("pick", pick);
            }

            return page;
        }
    }
}
