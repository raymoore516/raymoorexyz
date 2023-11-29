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
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        public static String calculateRecord(List<Pick> picks) {
            int wins = 0, losses = 0, ties = 0;

            for (Pick pick : picks) {
                if (pick.getResult() == null) {
                    continue;  // "TBD" game
                }

                switch (pick.getResult()) {
                    case win -> wins++;
                    case loss -> losses++;
                    case tie -> ties++;
                }
            }

            return String.format("%s-%s-%s", wins, losses, ties);
        }

        public static List<UUID> rankContestants(List<Pick> picks, int week) {
            TreeMap<UUID, Integer> map = new TreeMap<>();

            for (Pick pick : picks) {
                if (pick.getResult() == null || pick.getWeek() > week) {
                    continue;  // "TBD" game
                }

                UUID contestantId = pick.getContestantId();
                int points = map.get(contestantId) == null ? 0 : map.get(contestantId);
                switch (pick.getResult()) {
                    case win -> map.put(contestantId, points + 3);
                    case tie -> map.put(contestantId, points + 1);
                }
            }

            List<UUID> rankedList = new ArrayList<>();
            for (UUID contestantId : map.keySet()) {
                int points = map.get(contestantId);
                for (int i = 0; i < rankedList.size(); i++) {
                    if (points > map.get(rankedList.get(i))) {
                        rankedList.add(i, contestantId);
                        break;
                    }
                }
                if (!rankedList.contains(contestantId)) {
                    rankedList.add(contestantId);
                }
            }

            return rankedList;
        }

        // ---

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
            // pc[2] = "W" or "L" or "T" or null
            String[] ary = value.split("\\s+");  // RegEx to split by all space(s)

            // Build database object
            Pick pick = new Pick();
            pick.setYear(year);
            pick.setWeek(week);
            pick.setTeam(Team.find(ary[0]));
            pick.setUnderdog(ary[1].startsWith("+"));  // Ties are false (doesn't really matter)
            pick.setLine(ary[1].toUpperCase().contains("PK") ? 0
                    : Double.parseDouble(ary[1].substring(1)));  // Handle "PK" for pick em
            pick.setResult(ary.length < 3 ? null : Result.findByCode(ary[2]));

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

            List<Pick> picks = homes.getPickHome().findByYear(year);

            for (UUID contestantId : Engine.rankContestants(picks, week)) {
                Contestant contestant = homes.getContestantHome().find(contestantId);
                Block card = buildWeeklyContestant(year, week, contestant, picks);
                content.insert("contestant", card);
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

        private Block buildWeeklyContestant(int year, int week, Contestant c, List<Pick> picks) {
            List<Pick> weekPicks = picks.stream()
                    .filter(pick -> pick.getWeek() == week && pick.getContestantId().equals(c.getId()))
                    .toList();

            List<Pick> yearPicks = picks.stream()
                    .filter(pick -> pick.getWeek() <= week && pick.getContestantId().equals(c.getId()))
                    .toList();

            Block contestant = tucker.buildBlock("contestant");
            contestant.setVariable("name", c.getName());

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
            weekly.setVariable("record", Engine.calculateRecord(weekPicks));
            contestant.insert("summary", weekly);

            Block yearly = tucker.buildBlock("yearly-summary");
            yearly.setVariable("record", Engine.calculateRecord(yearPicks));
            contestant.insert("summary", yearly);

            return contestant;
        }
    }
}
