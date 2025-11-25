package birt.dev.engine;

import dev.birt.engine.OutputFormat;
import dev.birt.engine.RenderConfiguration;
import org.eclipse.birt.report.engine.api.RenderOption;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

class RenderConfigurationTest {

    @ParameterizedTest
    @CsvSource({
            "PDF, path/to/outputFile.pdf, org.eclipse.birt.report.engine.api.PDFRenderOption",
            "HTML, path/to/outputFile.html,org.eclipse.birt.report.engine.api.HTMLRenderOption",
            "DOCX, path/to/outputFile.docx,org.eclipse.birt.report.engine.api.DocxRenderOption",
            "XLSX, path/to/outputFile.xlsx,org.eclipse.birt.report.engine.api.EXCELRenderOption",
            "PPTX, path/to/outputFile.pptx,org.eclipse.birt.report.engine.api.RenderOption"
    })
    void toRenderOption(OutputFormat outputFormat,
                        String path,
                        Class<? extends RenderOption> expectedClass) {
        Path expected = Path.of(path);
        RenderOption renderOption = new RenderConfiguration(
                outputFormat,
                expected)
                .toRenderOption();
        assertInstanceOf(expectedClass, renderOption);
        assertThat(renderOption.getOutputFormat()).isEqualTo(outputFormat.getFormatName());
        assertThat(Path.of(renderOption.getOutputFileName())).isEqualTo(expected.toAbsolutePath());

    }
}