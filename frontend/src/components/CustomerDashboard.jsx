import { money } from '../format.js';
import AccountList from './AccountList.jsx';
import MoneyForm from './MoneyForm.jsx';
import TransferForm from './TransferForm.jsx';
import TransactionHistory from './TransactionHistory.jsx';

export default function CustomerDashboard({ accounts, selectedId, transactions, onSelect, onMoveMoney, onTransfer, busy }) {
  const selected = accounts.find(account => account.id === selectedId);
  const total = accounts.reduce((sum, account) => sum + Number(account.balance), 0);

  return <div className="banking-layout">
    <aside className="account-sidebar" id="accounts">
      <div className="balance-summary">
        <p className="eyebrow">TOTAL BALANCE</p>
        <p className="total-balance">{money(total)}</p>
        <p className="muted">Across {accounts.length} accounts</p>
      </div>
      <h2 className="sidebar-heading">My accounts</h2>
      <AccountList accounts={accounts} selectedId={selectedId} onSelect={onSelect} busy={busy} />
      <p className="sidebar-note">Select an account to view its transactions and move funds.</p>
    </aside>
    <div className="account-workspace">
      {selected ? <>
        <div className="workspace-heading">
          <div><p className="eyebrow">ACCOUNT #{selected.id}</p><h2>{selected.accountType === 'CHECKING' ? 'Checking' : 'Savings'} overview</h2></div>
          <span className="workspace-currency">USD</span>
        </div>
        <div className="action-grid">
          <MoneyForm key={'money-' + selectedId} account={selected} onSubmit={onMoveMoney} busy={busy} />
          <TransferForm key={'transfer-' + selectedId} account={selected} accounts={accounts} onSubmit={onTransfer} busy={busy} />
        </div>
        <TransactionHistory transactions={transactions} />
      </> : <section className="panel"><h2>Account overview</h2><p className="muted">{busy ? 'Loading your accounts…' : 'No accounts are available to display.'}</p></section>}
    </div>
  </div>;
}
