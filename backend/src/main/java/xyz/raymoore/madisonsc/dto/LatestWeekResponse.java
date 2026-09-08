package xyz.raymoore.madisonsc.dto;

import org.jspecify.annotations.Nullable;

public record LatestWeekResponse(
        @Nullable Integer year,
        @Nullable Integer week
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private @Nullable Integer year;
        private @Nullable Integer week;

        public Builder year(@Nullable Integer value) {
            year = value;
            return this;
        }

        public Builder week(@Nullable Integer value) {
            week = value;
            return this;
        }

        public LatestWeekResponse build() {
            return new LatestWeekResponse(year, week);
        }
    }
}
