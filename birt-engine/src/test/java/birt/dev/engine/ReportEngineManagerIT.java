package birt.dev.engine;

import dev.birt.engine.*;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class ReportEngineManagerIT {

    private static final Logger logger = LoggerFactory.getLogger(ReportEngineManagerIT.class);

    @Test
    void startStop() {
        ReportEngineManager engine = new ReportEngineManager();
        engine.start();
        assertThat(engine.isStarted()).isTrue();
        engine.shutdown();
        assertThat(engine.isStarted()).isFalse();
    }

    @Test
    void runReportWithSeparateRenderTask() {
        assertThat(runReport(true).status()).isEqualTo(ReportGenerationResult.Status.SUCCESS);
    }

    @Test
    void runAndRenderReport() {
        assertThat(runReport(false).status()).isEqualTo(ReportGenerationResult.Status.SUCCESS);
    }

    private ReportGenerationResult runReport(boolean isSeparateRenderTask) {
        ReportEngineManager engine = new ReportEngineManager();
        engine.start();
        String designPath = "reporting/report/events.rptdesign";
        try (InputStream reportDesign = new FileInputStream(designPath)) {
            ReportGenerationResult result = engine.runReport(reportDesign,
                    new RunConfiguration(isSeparateRenderTask),
                    new RenderConfiguration(OutputFormat.PDF, Path.of("output.pdf")));
            engine.shutdown();
            result.errors().stream().forEach(error -> logger.error(error.errorMessage(), error.exception()));
            return result;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}