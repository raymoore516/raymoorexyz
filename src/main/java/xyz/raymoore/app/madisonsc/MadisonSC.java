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

public class MadisonSC implements Routes {
    private final DataSource ds;
    private final Engine engine;

    // ---

    public MadisonSC(AppSettings settings) {
        this.ds = settings.getPostgres().useDataSource();
        this.engine = new Engine();
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
            Homes homes = new Homes();
            homes.setConnection(conn);

            int year = Integer.parseInt(ctx.pathParam("year"));
            int week = Integer.parseInt(ctx.pathParam("week"));
            PicksSubmission submission = ctx.bodyAsClass(PicksSubmission.class);

            engine.submitWeeklyPicks(year, week, submission);
            homes.close();
        }
    }

    // ---

    public static class Engine {
        public void submitWeeklyPicks(int year, int week, PicksSubmission submission) throws Exception {
            ObjectMapper mapper = new ObjectMapper();
            ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

            System.out.println(writer.writeValueAsString(submission));
            for (String pick : submission.getPicks()) {
                parseWeeklyPick(pick);
            }
        }

        private static void parseWeeklyPick(String value) {
            String[] pc = value.split("\\s+");

            // TODO: Remove this section
            System.out.printf("Team : %s%n", pc[0]);
            System.out.printf("Spread : %s%n", pc[1]);
            System.out.printf("Result : %s%n", pc[2]);

            Team team = Team.find(pc[0]);
        }
    }

    // ---

    public static class Homes extends HomeGroup {
        public Homes() {
            addHome(Contestant.Home.class, new Contestant.Home());
            addHome(Pick.Home.class, new Pick.Home());
        }
    }
}
