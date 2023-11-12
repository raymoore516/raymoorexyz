package xyz.raymoore.app.madisonsc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import xyz.raymoore.app.madisonsc.bean.PicksMessage;
import xyz.raymoore.app.madisonsc.category.Team;
import xyz.raymoore.app.madisonsc.db.Pick;

/**
 * This class assumes an active connection set on the Homes singleton
 */
public class StorageEngine {
    public void submitPicks(int year, int week, PicksMessage message) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        ObjectWriter writer = mapper.writerWithDefaultPrettyPrinter();

        System.out.println(writer.writeValueAsString(message));
        for (String pick : message.getPicks()) {
            parse(pick);
        }
    }

    // ---

    private static Pick parse(String value) {
        String[] pc = value.split("\\s+");

        // TODO: Remove this section
        System.out.printf("Team : %s%n", pc[0]);
        System.out.printf("Spread : %s%n", pc[1]);
        System.out.printf("Result : %s%n", pc[2]);

        Team team = Team.find(pc[0]);

        Pick bean = new Pick();
        bean.setTeam(team);

        return bean;
    }
}
