package crap4java;

import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CliArgumentsParserTest {

    @Test
    void noArgsMeansAllSrcFiles() {
        CliArguments args = CliArgumentsParser.parse(new String[]{});
        assertEquals(CliMode.ALL_SRC, args.mode());
    }

    @Test
    void changedFlagMeansChangedSrcFiles() {
        CliArguments args = CliArgumentsParser.parse(new String[]{"--changed"});
        assertEquals(CliMode.CHANGED_SRC, args.mode());
    }

    @Test
    void fileNamesMeanExplicitFiles() {
        CliArguments args = CliArgumentsParser.parse(new String[]{"src/main/java/demo/A.java", "src/main/java/demo/B.java"});
        assertEquals(CliMode.EXPLICIT_FILES, args.mode());
        assertEquals(List.of("src/main/java/demo/A.java", "src/main/java/demo/B.java"), args.fileArgs());
    }

    @Test
    void unknownFlagsAreIgnoredWhenCollectingExplicitFiles() {
        CliArguments args = CliArgumentsParser.parse(new String[]{"src/main/java/demo/A.java", "--bogus", "src/main/java/demo/B.java"});

        assertEquals(CliMode.EXPLICIT_FILES, args.mode());
        assertEquals(List.of("src/main/java/demo/A.java", "src/main/java/demo/B.java"), args.fileArgs());
    }

    @Test
    void helpPrintsUsageMode() {
        CliArguments args = CliArgumentsParser.parse(new String[]{"--help"});
        assertEquals(CliMode.HELP, args.mode());
    }

    @Test
    void changedCannotBeCombinedWithFiles() {
        assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"--changed", "src/main/java/demo/A.java"}));
    }

    @Test
    void plainFilesDoNotTriggerChangedMode() {
        CliArguments args = CliArgumentsParser.parse(new String[]{"src/main/java/demo/A.java"});

        assertEquals(CliMode.EXPLICIT_FILES, args.mode());
        assertEquals(List.of("src/main/java/demo/A.java"), args.fileArgs());
    }

    @Test
    void parsesReportOnlyJsonAndExternalCoverageOptions() {
        CliArguments args = CliArgumentsParser.parse(new String[]{
                "--format", "json",
                "--jacoco-xml", "target/site/jacoco/jacoco.xml",
                "--report-only",
                "--threshold", "12.5"
        });

        assertEquals(CliMode.ALL_SRC, args.mode());
        assertEquals("json", args.format());
        assertEquals(Path.of("target/site/jacoco/jacoco.xml"), args.jacocoXml());
        assertTrue(args.reportOnly());
        assertEquals(12.5, args.threshold(), 0.001);
    }

    @Test
    void parsesMakevnBuildTool() {
        CliArguments args = CliArgumentsParser.parse(new String[]{"--build-tool", "makevn"});

        assertEquals("makevn", args.buildTool());
    }

    @Test
    void rejectsUnknownBuildTool() {
        assertThrows(IllegalArgumentException.class,
                () -> CliArgumentsParser.parse(new String[]{"--build-tool", "gradle"}));
    }
}
