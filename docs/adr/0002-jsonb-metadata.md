# ADR-0002 — Store Instrument Metadata as JSONB

- Status: Accepted
- Date: 2026-07-28
- Deciders: ReconX Team

## Context

ReconX manages multiple financial instruments including equities, bonds, FX, and derivatives. Each asset class contains different metadata attributes, making a fixed relational schema difficult to maintain. The platform processes approximately 50,000 trades per day with a 5-year retention period, requiring a flexible yet efficient storage solution.

Alternatives considered:
- Add separate columns for every possible attribute.
- Store metadata as plain text.
- Store metadata using PostgreSQL JSONB.

## Decision

Store instrument metadata in a PostgreSQL JSONB column named `metadata`. This allows different asset classes to maintain their own attributes without requiring frequent schema changes while supporting efficient querying and indexing.

## Consequences

**Positive**
- Flexible schema for multiple asset classes.
- Eliminates frequent database migrations.
- Supports efficient JSON queries and indexing.
- Simplifies future feature additions.

**Negative**
- PostgreSQL-specific implementation.
- JSON validation must be handled by the application.
- Large JSON documents may slightly increase storage requirements.