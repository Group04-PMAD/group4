import React from 'react';

function TradeRowImpl({ trade, onClick }) {
  return (
    <>
      <span onClick={() => onClick?.(trade)}>{trade.tradeRef}</span>
      <span>{trade.symbol ?? trade.instrument}</span>
      <span>{trade.qty ?? trade.quantity}</span>
      <span>{trade.price}</span>
      <span>{trade.status}</span>
    </>
  );
}

function areEqual(prev, next) {
  return (
    prev.trade.id === next.trade.id &&
    prev.trade.status === next.trade.status &&
    prev.trade.price === next.trade.price &&
    prev.onClick === next.onClick
  );
}

const TradeRow = React.memo(TradeRowImpl, areEqual);

export default TradeRow;