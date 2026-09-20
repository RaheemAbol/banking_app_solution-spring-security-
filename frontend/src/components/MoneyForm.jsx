import { useState } from 'react';
export default function MoneyForm({ account, onSubmit, busy }) {
  const [operation, setOperation] = useState('deposits');
  const [amount, setAmount] = useState('');
  const [description, setDescription] = useState('');
  const frozen = account.status === 'FROZEN';
  async function submit(event) {
    event.preventDefault();
    if (await onSubmit(account.id, operation, amount, description)) { setAmount(''); setDescription(''); }
  }
  return <section className="panel">
    <h2>Deposit or withdraw</h2>
    <p className="muted">{account.accountType} · Account #{account.id}</p>
    {frozen && <p className="inline-warning">This account is frozen. You can still view its history.</p>}
    <form onSubmit={submit}>
      <label>Action<select aria-label="Action" value={operation} onChange={e => setOperation(e.target.value)} disabled={busy || frozen}>
        <option value="deposits">Deposit</option><option value="withdrawals">Withdraw</option>
      </select></label>
      <label>Amount (USD)<input type="number" min="0.01" step="0.01" required value={amount} disabled={busy || frozen} onChange={e => setAmount(e.target.value)} /></label>
      <label>Description (optional)<input maxLength="200" value={description} disabled={busy || frozen} onChange={e => setDescription(e.target.value)} /></label>
      <button disabled={busy || frozen}>{operation === 'deposits' ? 'Deposit funds' : 'Withdraw funds'}</button>
    </form>
  </section>;
}
