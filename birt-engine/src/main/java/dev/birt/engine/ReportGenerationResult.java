package dev.birt.engine;

import java.util.List;

public record ReportGenerationResult(Status status, List<ReportGenerationError> errors) {

    public ReportGenerationResult(Status status) {
        this(status, List.of());
    }

    public record ReportGenerationError(Throwable exception, String errorMessage){}

    public enum Status {
        SUCCESS, FAILURE
    }


}
