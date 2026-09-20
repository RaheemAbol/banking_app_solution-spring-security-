import { money } from '../format.js';

export default function AccountList({ accounts, selectedId, onSelect, busy }) {
  return <div className="account-grid">
    {accounts.map(account => <button key={account.id} disabled={busy}
      className={'account-card ' + (selectedId === account.id ? 'selected' : '')}
      aria-pressed={selectedId === account.id}
      aria-label={`${account.accountType} account ${account.id}`}
      onClick={() => onSelect(account.id)}>
      <span className="account-title">
        {account.accountType}
        <span className={'badge ' + (account.status === 'FROZEN' ? 'frozen' : '')}>{account.status}</span>
      </span>
      <span className="account-balance">{money(account.balance)}</span>
      <span className="account-bottom"><span>Account #{account.id} · Current balance</span><span aria-hidden="true">↗</span></span>
    </button>)}
  </div>;
}
