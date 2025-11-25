package dev.birt.engine;

import dev.birt.engine.ReportGenerationResult.ReportGenerationError;
import org.eclipse.birt.core.exception.BirtException;
import org.eclipse.birt.core.framework.Platform;
import org.eclipse.birt.report.engine.api.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class ReportEngineManager {

    private IReportEngine engine;

    private boolean started = false;

    public void start() {

        final EngineConfig config = new EngineConfig();

        config.setLogConfig("log", Level.FINE);

        try {
            Platform.startup(config);
        } catch (BirtException e) {
            throw new IllegalStateException("unable to start the BIRT Platform", e);
        }
        started = true;

        IReportEngineFactory factory = (IReportEngineFactory) Platform
                .createFactoryObject(IReportEngineFactory.EXTENSION_REPORT_ENGINE_FACTORY);
        engine = factory.createReportEngine(config);
        engine.changeLogLevel(Level.WARNING);
    }

    @SuppressWarnings("unchecked")
    public ReportGenerationResult runReport(InputStream reportDesign, RunConfiguration runConfiguration,
                             RenderConfiguration renderConfiguration) {
        try {
            IReportRunnable designRunnable = engine.openReportDesign(reportDesign);

            final IEngineTask renderTask;
            if (runConfiguration.useSeparateRenderTask()) {
                // Run
                IRunTask runTask = engine.createRunTask(designRunnable);
                runTask.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY,
                        ReportEngineManager.class.getClassLoader());
                File rptDocument = File.createTempFile("temp", ".rptdocument");
                String rptDocumentAbsolutePath = rptDocument.getAbsolutePath();
                runTask.run(rptDocumentAbsolutePath);
                ReportGenerationResult reportGenerationResult = checkTaskResult(runTask);
                if (reportGenerationResult.status() != ReportGenerationResult.Status.SUCCESS) {
                    runTask.close();
                    return reportGenerationResult;
                }
                runTask.close();

                // Render
                final IReportDocument reportDocument = engine.openReportDocument(rptDocumentAbsolutePath);
                IRenderTask standaloneRenderTask = engine.createRenderTask(reportDocument);
                standaloneRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY,
                        ReportEngineManager.class.getClassLoader());
                standaloneRenderTask.setRenderOption(renderConfiguration.toRenderOption());
                standaloneRenderTask.render();
                renderTask = standaloneRenderTask;
            } else {
                // Run and Render
                IRunAndRenderTask runAndRenderTask = engine.createRunAndRenderTask(designRunnable);
                runAndRenderTask.getAppContext().put(EngineConstants.APPCONTEXT_CLASSLOADER_KEY,
                        ReportEngineManager.class.getClassLoader());
                runAndRenderTask.setRenderOption(renderConfiguration.toRenderOption());
                runAndRenderTask.run();
                renderTask = runAndRenderTask;
            }
            ReportGenerationResult reportGenerationResult = checkTaskResult(renderTask);
            renderTask.close();
            return reportGenerationResult;
        } catch (EngineException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void shutdown() {
        engine.destroy();
        Platform.shutdown();
        started = false;
    }

    public boolean isStarted() {
        return started;
    }

    private ReportGenerationResult checkTaskResult(IEngineTask task) {

        List taskErrors = task.getErrors();
        if (taskErrors.isEmpty()) {
            return new ReportGenerationResult(ReportGenerationResult.Status.SUCCESS);
        }

        List<ReportGenerationError> errors = new ArrayList<>();
        for (Object error : taskErrors) {
            final ReportGenerationError reportGenerationError;
            if (error instanceof Throwable) {
                reportGenerationError = new ReportGenerationError((Throwable) error, error.toString());
            } else {
                reportGenerationError = new ReportGenerationError(null, String.valueOf(error));
            }
            errors.add(reportGenerationError);
        }

        return new ReportGenerationResult(ReportGenerationResult.Status.FAILURE, errors);
    }
}
