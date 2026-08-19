# crap4java

`crap4java` is a standalone CRAP metric tool for Java projects, modeled after `crap4clj`.

## Maintained fork

This repository is a maintained fork of `unclebob/crap4java`.
It keeps the original CRAP calculation and Java AST analysis, but adds an
agent- and CI-oriented execution contract:

- stable JSON output for local scripts and cross-language report aggregation;
- analysis of an existing JaCoCo XML report through `--jacoco-xml`, so test and
  coverage execution can be owned by an external build orchestrator such as
  `makevn`;
- `--report-only` mode for non-blocking quality reports;
- configurable thresholds through `--threshold`;
- source file and start/end line locations in every JSON entry;
- machine-readable summary counts for covered, missing-coverage, and threshold
  violation entries.

The fork deliberately does not require an AI agent to parse raw reports. The
command generates structured artifacts locally so any automation only needs to
invoke the tool and inspect a bounded summary.

The original project and this fork are related by source provenance. The fork
maintains its own changes and documents them here rather than presenting these
additional capabilities as part of the upstream project.

It combines method cyclomatic complexity with JaCoCo method coverage and reports CRAP scores.
On each run it deletes stale coverage artifacts, runs coverage, then analyzes the selected files.

## Formula

`CRAP = CC^2 * (1 - coverage)^3 + CC`

- `CC` is cyclomatic complexity.
- `coverage` is method coverage fraction from JaCoCo `INSTRUCTION` counters.

## Coverage Pipeline

For each invocation:

1. Delete stale coverage artifacts:
   - `target/site/jacoco/`
   - `target/jacoco.exec`
2. Run `mvn -q org.jacoco:jacoco-maven-plugin:0.8.12:prepare-agent test org.jacoco:jacoco-maven-plugin:0.8.12:report`
3. Read `target/site/jacoco/jacoco.xml`
4. Analyze selected Java files

## Build and Test

```bash
mvn test
```

## Run

Build the jar:

```bash
mvn -DskipTests package
```

From the project root you want to analyze:

```bash
java -jar target/crap4java-0.1.0-SNAPSHOT.jar
```

## CLI

```text
--help                Print usage to stdout
(no args)             Analyze all Java files under src/
--changed             Analyze changed Java files under src/
<file ...>            Analyze only these files
<directory ...>       Analyze all Java files under each directory's src/ subtree
--format json         Emit a machine-readable JSON report
--jacoco-xml <path>   Analyze an existing JaCoCo XML report without running Maven
--report-only         Report threshold violations without failing
--threshold <number>  Set the CRAP threshold (default: 8.0)
```

Examples:

```bash
java -jar target/crap4java-0.1.0-SNAPSHOT.jar --help
java -jar target/crap4java-0.1.0-SNAPSHOT.jar
java -jar target/crap4java-0.1.0-SNAPSHOT.jar --changed
java -jar target/crap4java-0.1.0-SNAPSHOT.jar src/main/java/demo/Sample.java
java -jar target/crap4java-0.1.0-SNAPSHOT.jar module-a module-b
java -jar target/crap4java-0.1.0-SNAPSHOT.jar --format json --jacoco-xml target/site/jacoco/jacoco.xml --report-only
```

## Exit codes

- `0` success, threshold respected
- `1` invalid CLI usage
- `2` CRAP threshold exceeded (`> 8.0`)

## Notes

- If JaCoCo XML is missing, coverage is reported as `N/A`.
- Report output is sorted by CRAP descending, with `N/A` at the bottom.
