package xyz.raymoore.app.madisonsc;

import io.javalin.Javalin;
import io.javalin.http.BadRequestResponse;
import io.javalin.http.Context;
import net.jextra.fauxjo.HomeGroup;
import net.jextra.fauxjo.transaction.Transaction;
import net.jextra.tucker.tucker.Block;
import net.jextra.tucker.tucker.Tucker;
import org.jetbrains.annotations.NotNull;
import xyz.raymoore.Settings;
import xyz.raymoore.Page;
import xyz.raymoore.app.madisonsc.api.Submission;
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
    private final String secret;

    // ---

    public MadisonSC(Settings settings) throws IOException {
        this.ds = settings.getPostgres().useDataSource();
        this.secret = settings.getSecret();
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
            Pages pages = new Pages(conn);
            Page page = pages.buildWeekly(year, week);

            ctx.html(page.render());
        }
    }

    public void submitWeeklyPicks(@NotNull Context ctx) throws Exception {
        if (ctx.header("api-secret") == null || !secret.equals(ctx.header("api-secret"))) {
            throw new BadRequestResponse("Invalid API secret");
        }

        int year = Integer.parseInt(ctx.pathParam("year"));
        int week = Integer.parseInt(ctx.pathParam("week"));

        try (Transaction trans = new Transaction(ds.getConnection())) {
            Submission submission = ctx.bodyAsClass(Submission.class);

            Engine engine = new Engine(trans.getConnection());
            engine.submitWeeklyPicks(year, week, submission);
            trans.commit();

            ctx.json("{\"success\" : true}");
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
                throw new BadRequestResponse("Contestant not found");
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
            pick.setUnderdog(pc[1].startsWith("+"));  // Ties are false
            pick.setLine(pc[1].toUpperCase().contains("PK") ? 0
                    : Double.parseDouble(pc[1].substring(1)));  // Handle "PK" for pick em
            pick.setResult(Result.findByCode(pc[2]));

            return pick;
        }
    }

    // ---

    public static class Pages {
        private final Homes homes;
        private final Tucker tucker;

        public Pages(Connection conn) throws SQLException, IOException {
            this.homes = new Homes(conn);
            this.tucker = new Tucker(new File(TUCK));
        }

        public Page buildWeekly(int year, int week) throws IOException, SQLException {
            Page page = new Page(String.format("MSC Year %d, Week %d", year, week));
            page.addStylesheet(CSS);
            page.addScript(JS);

            Block content = tucker.buildBlock("content");
            page.setContent(content);

            List<Contestant> contestants = homes.getContestantHome().findAll();
            for (Contestant c : contestants) {
                Block contestant = buildWeeklyContestant(year, week, c);
                content.insert("contestant", contestant);
            }

            for (int num = 1; num < 19; num++) {
                String href = String.format("/madisonsc/picks/%d/%d", year, num);
                Block pageButton = tucker.buildBlock("page-button");
                pageButton.setVariable("href", href);
                pageButton.setVariable("num", String.valueOf(num));
                content.insert("page-button", pageButton);
            }

            return page;
        }

        private Block buildWeeklyContestant(int year, int week, Contestant c) throws SQLException {
            Block contestant = tucker.buildBlock("contestant");
            contestant.setVariable("name", c.getName());

            List<Pick> weekPicks = homes.getPickHome().findByContestantYearWeek(c, year, week);
            if (weekPicks.size() == 0) {
                Block message = tucker.buildBlock("message");
                message.setVariable("message", "No picks found");
                contestant.insert("message", message);
            } else {
                Block table = tucker.buildBlock("table");
                contestant.insert("table", table);
                for (Pick p : weekPicks) {
                    Block row = tucker.buildBlock("table-row");
                    table.insert("row", row);

                    String team = p.getTeam().name();
                    String src = String.format("/madisonsc/img/logo/%s.gif", team);
                    String spread = p.getLine() == 0.0 ? "PK"
                            : String.format("%s%s", p.isUnderdog() ? "+" : "-", p.getLine());

                    row.setVariable("src", src);
                    row.setVariable("team", team);
                    row.setVariable("spread", spread);
                    row.setVariable("result", p.getResult() == null ? "TBD" : p.getResult().name().toUpperCase());
                }
            }

            Block weekly = tucker.buildBlock("weekly-summary");
            weekly.setVariable("week", String.valueOf(week));
            weekly.setVariable("record", Result.calculateRecord(weekPicks));
            contestant.insert("summary", weekly);

            List<Pick> yearPicks = homes.getPickHome().findByContestantYear(c, year)
                    .stream().filter(p -> p.getWeek() <= week).toList();  // Filter "future" picks for "this" week

            Block yearly = tucker.buildBlock("yearly-summary");
            yearly.setVariable("record", Result.calculateRecord(yearPicks));
            contestant.insert("summary", yearly);

            return contestant;
        }
    }
}
