// useMemo for portfolio-value calc + useTradeStream live feed.
import React, { memo, Profiler, useMemo } from 'react';
import { withAuth } from '@components/withAuth.jsx';
import { useTradeStream } from '@hooks/useTradeStream.js';

const StatCard = memo(function StatCard({ label, value }) {
  return (
    <article className="stat-card">
      <h3>{label}</h3>
      <p>{value}</p>
    </article>
  );
});

function onRender(id, phase, actualDuration, baseDuration) {
  console.log(`[Profiler] ${id} ${phase} actual=${actualDuration.toFixed(2)}ms base=${baseDuration.toFixed(2)}ms`);
}

function Dashboard({ trades: tradesProp }) {
  const stream = useTradeStream();

  // Prefer explicit prop (used by RTL tests) over the live SSE feed.
  const trades = tradesProp ?? stream.trades;
  const isConnected = tradesProp ? true : stream.isConnected;

  const portfolioValue = useMemo(
    () =>
      trades.reduce(
        (sum, t) => sum + (Number(t.quantity) * Number(t.price) || 0),
        0
      ),
    [trades]
  );

  const { matched, unmatched, breaks } = useMemo(() => {
    let matchedCount = 0;
    let unmatchedCount = 0;
    let breaksCount = 0;

    for (const trade of trades) {
      if (trade.status === 'MATCHED') {
        matchedCount++;
      } else if (trade.status === 'UNMATCHED') {
        unmatchedCount++;
        breaksCount++;
      } else if (trade.status === 'DISPUTED') {
        breaksCount++;
      }
    }

    return {
      matched: matchedCount,
      unmatched: unmatchedCount,
      breaks: breaksCount,
    };
  }, [trades]);

  return (
    <Profiler id="TradeDashboard" onRender={onRender}>
      <section>
      <h2>Dashboard</h2>

      <div className="stat-grid">
        <StatCard
          label="Portfolio value (USD)"
          value={portfolioValue.toLocaleString()}
        />

        <StatCard
          label="Trades streamed"
          value={trades.length}
        />

        <StatCard
          label="Matched trades"
          value={matched}
        />

        <StatCard
          label="Unmatched trades"
          value={unmatched}
        />

        <StatCard
          label="Open breaks"
          value={breaks}
        />
      </div>

      <div role="status" aria-live="polite">
        SSE: {isConnected ? 'connected' : 'disconnected'}
      </div>
          </section>
    </Profiler>
  );
}

export default withAuth(Dashboard);