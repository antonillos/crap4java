# Security Policy

## Reporting a Vulnerability

Please report suspected vulnerabilities privately to the repository owner
through [GitHub private vulnerability reporting](https://github.com/antonillos/crap4java/security/advisories/new),
when available. Do not open a public issue for an unpatched vulnerability.

Include, when possible:

- the affected release, commit, or JAR checksum;
- the command and project configuration used;
- clear reproduction steps;
- expected and actual behavior;
- potential impact; and
- a suggested mitigation or fix.

You may redact source code, JaCoCo reports, and project data before sharing.

## Scope

This policy covers the crap4java CLI, its JSON and human-readable report
formatters, JaCoCo XML parsing, source analysis, and build-tool integration.

crap4java can execute project tests through Maven or makevn and reads project
source files and JaCoCo reports. Run it only against projects and inputs you
trust, and use a sandbox when analyzing untrusted repositories.

## Supported Versions

| Version | Supported |
|---|---|
| 0.1.x | Yes |
| Older versions | No |

Security fixes are applied to the latest release on a best-effort basis.
