package xyz.raymoore.madisonsc.domain;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "pick", schema = "madisonsc")
public record Pick(
        @Id @Column("pick_id") UUID pickId,
        @Column("entry_date") Instant entryDate,
        @Column("contestant_id") UUID contestantId,
        int year,
        int week,
        String team,
        Boolean underdog,
        BigDecimal line,
        String result
) {

    @SuppressWarnings("unused")
    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("unused")
    public static final class Builder {
        private UUID pickId;
        private Instant entryDate;
        private UUID contestantId;
        private int year;
        private int week;
        private String team;
        private Boolean underdog;
        private BigDecimal line;
        private String result;

        public Builder pickId(UUID value) {
            pickId = value;
            return this;
        }

        public Builder entryDate(Instant value) {
            entryDate = value;
            return this;
        }

        public Builder contestantId(UUID value) {
            contestantId = value;
            return this;
        }

        public Builder year(int value) {
            year = value;
            return this;
        }

        public Builder week(int value) {
            week = value;
            return this;
        }

        public Builder team(String value) {
            team = value;
            return this;
        }

        public Builder underdog(Boolean value) {
            underdog = value;
            return this;
        }

        public Builder line(BigDecimal value) {
            line = value;
            return this;
        }

        public Builder result(String value) {
            result = value;
            return this;
        }

        public Pick build() {
            return new Pick(pickId, entryDate, contestantId, year, week, team, underdog, line, result);
        }
    }
}
