package xyz.raymoore.madisonsc.domain;

import java.time.Instant;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "contestant", schema = "madisonsc")
public record Contestant(
        @Id @Column("contestant_id") UUID contestantId,
        @Column("entry_date") Instant entryDate,
        String name
) {

    @SuppressWarnings("unused")
    public static Builder builder() {
        return new Builder();
    }

    @SuppressWarnings("unused")
    public static final class Builder {
        private UUID contestantId;
        private Instant entryDate;
        private String name;

        public Builder contestantId(UUID value) {
            contestantId = value;
            return this;
        }

        public Builder entryDate(Instant value) {
            entryDate = value;
            return this;
        }

        public Builder name(String value) {
            name = value;
            return this;
        }

        public Contestant build() {
            return new Contestant(contestantId, entryDate, name);
        }
    }
}
