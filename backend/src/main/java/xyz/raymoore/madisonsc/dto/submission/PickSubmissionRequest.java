package xyz.raymoore.madisonsc.dto.submission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record PickSubmissionRequest(
        @NotBlank(message = "Contestant is required") String contestant,
        @NotNull(message = "Picks are required")
        @Size(min = 1, max = 5, message = "Between one and five picks are required")
        List<@NotBlank(message = "Pick shorthand cannot be blank") String> picks
) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String contestant;
        private List<String> picks;

        public Builder contestant(String value) {
            contestant = value;
            return this;
        }

        public Builder picks(List<String> value) {
            picks = value;
            return this;
        }

        public PickSubmissionRequest build() {
            return new PickSubmissionRequest(contestant, picks);
        }
    }
}
