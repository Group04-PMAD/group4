You are an enterprise software architect. Write an Architecture Decision Record (ADR) in the Michael Nygard format (Title, Status, Context, Decision, Consequences).

System: ReconX, a near-production trade reconciliation platform.
Stack: PostgreSQL 16, Spring Boot 3, Kafka, React.
Scale: ~50,000 trades/day, 5-year retention, 10 concurrent recon analysts.

Decision to record: Store instrument metadata using PostgreSQL JSONB.

Alternatives considered:
- Fixed relational columns
- Plain text storage
- PostgreSQL JSONB

Constraints:
- Flexible metadata
- Efficient querying
- Future scalability

Format: Markdown, under 300 words.