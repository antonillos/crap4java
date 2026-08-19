# crap4java

`crap4java` is a standalone Java command-line tool that combines cyclomatic
complexity with JaCoCo method coverage to calculate CRAP scores.

## Maintained fork

This repository is a maintained fork of [unclebob/crap4java](https://github.com/unclebob/crap4java).
The original analysis and CRAP formula are preserved. This fork adds:

- stable JSON output with source locations and summary counts;
- optional analysis of an existing JaCoCo XML report;
- `--report-only` mode for non-blocking reports;
- configurable CRAP thresholds;
- optional `makevn` coverage execution for Maven projects.

These additions are maintained in this fork and are not presented as features
of the upstream project.

## Requirements

- Java 17 or newer
- Maven, or [makevn](https://github.com/antonillos/makevn) when using
  `--build-tool makevn`

## Build and test

Using Maven:

```bash
mvn test
mvn -DskipTests package
```

Using makevn:

```bash
makevn init
makevn test
makevn package
```

The resulting executable JAR is `target/crap4java-0.1.0-SNAPSHOT.jar`.

## Usage

Run from the root of the Java project being analyzed:

```bash
java -jar /path/to/crap4java.jar
java -jar /path/to/crap4java.jar --changed
java -jar /path/to/crap4java.jar src/main/java/demo/Sample.java
```

By default, crap4java runs tests with JaCoCo and reads
`target/site/jacoco/jacoco.xml`. To delegate coverage execution to makevn:

```bash
java -jar /path/to/crap4java.jar --build-tool makevn
```

For a pre-generated JaCoCo report, no build tool is required:

```bash
java -jar /path/to/crap4java.jar \
  --format json \
  --jacoco-xml target/site/jacoco/jacoco.xml \
  --report-only
```

## CLI options

```text
--help                Print usage to stdout
(no args)             Analyze all Java files under src/
--changed             Analyze changed Java files under src/
<file ...>            Analyze only the supplied files or directories
--format human|json   Select human-readable or machine-readable output
--jacoco-xml <path>   Use an existing JaCoCo XML report
--build-tool <tool>   Use maven (default) or makevn for coverage execution
--report-only         Report threshold violations without failing
--threshold <number>  Set the CRAP threshold (default: 8.0)
```

## Formula

`CRAP = CC^2 * (1 - coverage)^3 + CC`

`CC` is cyclomatic complexity and `coverage` is the method coverage fraction
reported by JaCoCo.

## Exit codes

- `0`: analysis completed and the threshold was respected;
- `1`: invalid command-line usage;
- `2`: the threshold was exceeded, unless `--report-only` was supplied.

If JaCoCo XML is unavailable, coverage is reported as `N/A`. Reports are
sorted by CRAP score descending.

## License and security

Original work and modifications in this maintained fork are released under the
[MIT License](LICENSE). See [SECURITY.md](SECURITY.md) for vulnerability
reporting and safe-use guidance.
