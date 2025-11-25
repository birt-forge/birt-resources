package dev.birt.engine;

import org.eclipse.birt.report.engine.api.*;

import java.nio.file.Path;
import java.util.Objects;

public record RenderConfiguration(
        OutputFormat outputFormat,
        Path outputFile
        ) {

    public RenderOption toRenderOption() {
        Objects.requireNonNull(outputFormat);
        Objects.requireNonNull(outputFile);
        RenderOption renderOption = switch (outputFormat) {
            case PDF -> new PDFRenderOption();
            case HTML -> new HTMLRenderOption();
            case DOCX -> new DocxRenderOption();
            case XLSX -> new EXCELRenderOption();
            default -> new RenderOption();
        };

        renderOption.setOutputFormat(outputFormat.getFormatName());
        renderOption.setOutputFileName(outputFile.toAbsolutePath().toString());
        return renderOption;
    }
}
