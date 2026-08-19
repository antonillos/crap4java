package crap4java;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

final class JsonReportFormatter {

    private JsonReportFormatter() {
    }

    static String format(List<MethodMetrics> entries, double threshold) {
        List<MethodMetrics> sorted = entries.stream()
                .sorted(Comparator
                        .comparing((MethodMetrics e) -> e.crapScore() == null)
                        .thenComparing(e -> e.crapScore() == null ? 0.0 : -e.crapScore())
                        .thenComparing(MethodMetrics::sourceFile,
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparingInt(MethodMetrics::startLine)
                        .thenComparing(MethodMetrics::methodName))
                .toList();

        StringBuilder json = new StringBuilder("{\n");
        json.append("  \"schema\": \"https://github.com/antonillos/crap4java/report-v1.json\",\n");
        json.append("  \"threshold\": ").append(number(threshold)).append(",\n");
        json.append("  \"entries\": [\n");
        for (int i = 0; i < sorted.size(); i++) {
            MethodMetrics entry = sorted.get(i);
            json.append("    {\n");
            field(json, "method", entry.methodName(), true);
            field(json, "class", entry.className(), true);
            field(json, "file", entry.sourceFile(), true);
            numberField(json, "line", entry.startLine(), true);
            numberField(json, "end_line", entry.endLine(), true);
            numberField(json, "complexity", entry.complexity(), true);
            nullableNumberField(json, "coverage_percent", entry.coveragePercent(), true);
            nullableNumberField(json, "crap", entry.crapScore(), true);
            field(json, "status", status(entry, threshold), false);
            json.append("    }").append(i + 1 == sorted.size() ? "\n" : ",\n");
        }
        json.append("  ],\n");
        json.append("  \"summary\": {\n");
        json.append("    \"functions\": ").append(sorted.size()).append(",\n");
        json.append("    \"covered_functions\": ").append(sorted.stream().filter(e -> e.coveragePercent() != null).count()).append(",\n");
        json.append("    \"missing_coverage\": ").append(sorted.stream().filter(e -> e.coveragePercent() == null).count()).append(",\n");
        json.append("    \"threshold_violations\": ").append(sorted.stream().filter(e -> e.crapScore() != null && e.crapScore() > threshold).count()).append("\n");
        json.append("  }\n");
        json.append("}\n");
        return json.toString();
    }

    private static String status(MethodMetrics entry, double threshold) {
        if (entry.crapScore() == null) {
            return "missing-coverage";
        }
        return entry.crapScore() > threshold ? "warning" : "ok";
    }

    private static void field(StringBuilder json, String name, String value, boolean comma) {
        json.append("      \"").append(escape(name)).append("\": ");
        if (value == null) {
            json.append("null");
        } else {
            json.append("\"").append(escape(value)).append("\"");
        }
        json.append(comma ? ",\n" : "\n");
    }

    private static void numberField(StringBuilder json, String name, int value, boolean comma) {
        json.append("      \"").append(name).append("\": ").append(value).append(comma ? ",\n" : "\n");
    }

    private static void nullableNumberField(StringBuilder json, String name, Double value, boolean comma) {
        json.append("      \"").append(name).append("\": ");
        json.append(value == null ? "null" : number(value));
        json.append(comma ? ",\n" : "\n");
    }

    private static String number(double value) {
        return String.format(Locale.ROOT, "%.6f", value);
    }

    private static String escape(String value) {
        StringBuilder escaped = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            switch (character) {
                case '\\' -> escaped.append("\\\\");
                case '"' -> escaped.append("\\\"");
                case '\n' -> escaped.append("\\n");
                case '\r' -> escaped.append("\\r");
                case '\t' -> escaped.append("\\t");
                default -> escaped.append(character);
            }
        }
        return escaped.toString();
    }
}
