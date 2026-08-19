package crap4java;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class JsonReportFormatterTest {

    @Test
    void emitsMachineReadableActionableFields() {
        String report = JsonReportFormatter.format(List.of(
                new MethodMetrics("run", "demo.Sample", "src/main/java/demo/Sample.java", 12, 20, 4, 25.0, 13.0),
                new MethodMetrics("missing", "demo.Sample", "src/main/java/demo/Sample.java", 22, 24, 2, null, null)
        ), 8.0);

        assertTrue(report.contains("\"file\": \"src/main/java/demo/Sample.java\""));
        assertTrue(report.contains("\"line\": 12"));
        assertTrue(report.contains("\"end_line\": 20"));
        assertTrue(report.contains("\"status\": \"warning\""));
        assertTrue(report.contains("\"status\": \"missing-coverage\""));
        assertTrue(report.contains("\"threshold_violations\": 1"));
    }
}
