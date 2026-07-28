# ADR-0003 — Use GIN Index for JSONB Metadata

- Status: Accepted
- Date: 2026-07-28
- Deciders: ReconX Team

## Context

ReconX frequently searches instrument metadata stored in a JSONB column. Typical queries filter on JSON attributes rather than exact column values. With approximately 50,000 trades processed daily and millions of records over the retention period, query performance is critical.

Alternatives considered:
- B-tree index.
- No index.
- GIN index using `jsonb_path_ops`.

## Decision

Use a PostgreSQL GIN index with the `jsonb_path_ops` operator class on the `metadata` JSONB column. This provides efficient indexing for JSON path lookups while keeping index size manageable.

## Consequences

**Positive**
- Faster JSONB search performance.
- Optimized for containment queries.
- Smaller index than a standard GIN index.
- Improves API response times for metadata searches.

**Negative**
- Slightly slower insert and update operations.
- PostgreSQL-specific implementation.
- Additional storage required for the index.