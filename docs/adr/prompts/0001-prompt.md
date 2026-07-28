You are an enterprise software architect. Write an Architecture Decision Record (ADR) in the Michael Nygard format (Title, Status, Context, Decision, Consequences).

System: ReconX, a near-production trade reconciliation platform.
Stack: PostgreSQL 16, Spring Boot 3, Kafka, React.
Scale: ~50,000 trades/day, 5-year retention, 10 concurrent recon analysts.

Decision to record: Partition the trades table by trade_date.

Alternatives considered:
- Single unpartitioned table
- Hash partitioning
- Range partitioning by trade_date

Constraints:
- High daily trade volume
- Fast reporting queries
- Efficient archival

Format: Markdown, under 300 words.