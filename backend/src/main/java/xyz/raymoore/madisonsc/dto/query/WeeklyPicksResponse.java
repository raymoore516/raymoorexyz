package xyz.raymoore.madisonsc.dto.query;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.jspecify.annotations.Nullable;

public record WeeklyPicksResponse(
        int year,
        int week,
        String seasonLabel,
        List<Integer> availableYears,
        List<ContestantView> contestants
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private int year;
        private int week;
        private String seasonLabel = "";
        private List<Integer> availableYears = List.of();
        private List<ContestantView> contestants = List.of();

        public Builder year(int value) {
            year = value;
            return this;
        }

        public Builder week(int value) {
            week = value;
            return this;
        }

        public Builder seasonLabel(String value) {
            seasonLabel = value;
            return this;
        }

        public Builder availableYears(List<Integer> value) {
            availableYears = value;
            return this;
        }

        public Builder contestants(List<ContestantView> value) {
            contestants = value;
            return this;
        }

        public WeeklyPicksResponse build() {
            return new WeeklyPicksResponse(year, week, seasonLabel, availableYears, contestants);
        }
    }

    public record ContestantView(
            UUID contestantId,
            String name,
            int rank,
            BigDecimal cumulativeWinPercentage,
            RecordView cumulativeRecord,
            RecordView weeklyRecord,
            List<PickView> picks
    ) {

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private UUID contestantId;
            private String name = "";
            private int rank;
            private BigDecimal cumulativeWinPercentage = BigDecimal.ZERO;
            private RecordView cumulativeRecord = new RecordView(0, 0, 0);
            private RecordView weeklyRecord = new RecordView(0, 0, 0);
            private List<PickView> picks = List.of();

            public Builder contestantId(UUID value) {
                contestantId = value;
                return this;
            }

            public Builder name(String value) {
                name = value;
                return this;
            }

            public Builder rank(int value) {
                rank = value;
                return this;
            }

            public Builder cumulativeWinPercentage(BigDecimal value) {
                cumulativeWinPercentage = value;
                return this;
            }

            public Builder cumulativeRecord(RecordView value) {
                cumulativeRecord = value;
                return this;
            }

            public Builder weeklyRecord(RecordView value) {
                weeklyRecord = value;
                return this;
            }

            public Builder picks(List<PickView> value) {
                picks = value;
                return this;
            }

            public ContestantView build() {
                return new ContestantView(
                        contestantId,
                        name,
                        rank,
                        cumulativeWinPercentage,
                        cumulativeRecord,
                        weeklyRecord,
                        picks
                );
            }
        }
    }

    public record RecordView(int wins, int losses, int ties) {

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private int wins;
            private int losses;
            private int ties;

            public Builder wins(int value) {
                wins = value;
                return this;
            }

            public Builder losses(int value) {
                losses = value;
                return this;
            }

            public Builder ties(int value) {
                ties = value;
                return this;
            }

            public RecordView build() {
                return new RecordView(wins, losses, ties);
            }
        }
    }

    public record PickView(
            String team,
            @Nullable Boolean underdog,
            BigDecimal line,
            @Nullable String result
    ) {

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private String team = "";
            private @Nullable Boolean underdog;
            private BigDecimal line = BigDecimal.ZERO;
            private @Nullable String result;

            public Builder team(String value) {
                team = value;
                return this;
            }

            public Builder underdog(@Nullable Boolean value) {
                underdog = value;
                return this;
            }

            public Builder line(BigDecimal value) {
                line = value;
                return this;
            }

            public Builder result(@Nullable String value) {
                result = value;
                return this;
            }

            public PickView build() {
                return new PickView(team, underdog, line, result);
            }
        }
    }
}
