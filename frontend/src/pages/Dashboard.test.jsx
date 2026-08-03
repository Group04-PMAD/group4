import { describe, it, expect, vi } from 'vitest';
import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { ThemeProvider } from '@context/ThemeContext.jsx';
import { AuthContext } from '@context/AuthContext.jsx';
import Dashboard from './Dashboard.jsx';

vi.mock('@hooks/useTradeStream.js', () => ({
  useTradeStream: () => ({
    trades: [],
    isConnected: true,
  }),
}));

const trades = [
  {
    id: 1,
    tradeRef: 'TRD-0001',
    quantity: 100,
    price: 250,
    status: 'MATCHED',
    instrument: 'SAP'
  },
  {
    id: 2,
    tradeRef: 'TRD-0002',
    quantity: 50,
    price: 251,
    status: 'UNMATCHED',
    instrument: 'SAP'
  }
];

function renderWithProviders(ui) {
  return render(
    <AuthContext.Provider
      value={{
        user: { token: 'test-token', role: 'TRADER' },
        login: vi.fn(),
        logout: vi.fn(),
      }}
    >
      <ThemeProvider>
        <MemoryRouter>{ui}</MemoryRouter>
      </ThemeProvider>
    </AuthContext.Provider>
  );
}

describe('Dashboard', () => {
  it('shows summary cards', () => {
    renderWithProviders(<Dashboard trades={trades} />);

    expect(
      screen.getByRole('heading', { name: /portfolio value/i })
    ).toBeInTheDocument();

    expect(
      screen.getByRole('heading', { name: /^matched trades$/i })
    ).toBeInTheDocument();

    expect(
      screen.getByRole('heading', { name: /^unmatched trades$/i })
    ).toBeInTheDocument();

    expect(screen.getByText(/37,550/)).toBeInTheDocument();
  });
});
