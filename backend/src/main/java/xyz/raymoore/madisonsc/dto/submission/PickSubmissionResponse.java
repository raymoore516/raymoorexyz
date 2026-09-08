package xyz.raymoore.madisonsc.dto.submission;

public record PickSubmissionResponse(boolean success) {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private boolean success;

        public Builder success(boolean value) {
            success = value;
            return this;
        }

        public PickSubmissionResponse build() {
            return new PickSubmissionResponse(success);
        }
    }
}
