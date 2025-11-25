package dev.birt.engine;

public enum OutputFormat {
    PDF("pdf"), HTML("html"), DOCX("docx"),
    XLSX("xslx"), PPTX("pptx");

    private final String formatName;

    OutputFormat(String formatName) {
        this.formatName = formatName;
    }

    public String getFormatName() {
        return formatName;
    }
}
