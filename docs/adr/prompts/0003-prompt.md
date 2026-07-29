You are an enterprise software architect. Write an Architecture Decision Record (ADR) in the Michael Nygard format (Title, Status, Context, Decision, Consequences).

System: ReconX, a near-production trade reconciliation platform.
Stack: PostgreSQL 16, Spring Boot 3, Kafka, React.
Scale: ~50,000 trades/day, 5-year retention, 10 concurrent recon analysts.

Decision to record: Use a GIN (jsonb_path_ops) index instead of a B-tree index for JSONB metadata.

Alternatives considered:
- B-tree index
- No index
- GIN index

Constraints:
- Fast JSON queries
- Large datasets
- PostgreSQL compatibility

Format: Markdown, under 300 words.