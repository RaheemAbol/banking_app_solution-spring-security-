import { useState } from 'react';
export default function TransferForm({ account, accounts, onSubmit, busy }) {
  const destinations = accounts.filter(a => a.id !== account.id && a.status === 'ACTIVE');
  const [toId, setToId] = useState('');
  const [amount, setAmount] = useState('');
  const target = destinations.some(a => String(a.id) === toId) ? toId : String(destinations[0]?.id || '');
  const disabled = busy || account.status !== 'ACTIVE' || !target;
  async function submit(event) {
    event.preventDefault();
    if (await onSubmit(account.id, target, amount)) setAmount('');
  }
  return <section className="panel">
    <h2>Move between accounts</h2>
    <p className="muted">From {account.accountType.toLowerCase()} #{account.id}</p>
    <form onSubmit={submit}>
      <label>To account<select aria-label="To account" required value={target} disabled={disabled} onChange={e => setToId(e.target.value)}>
        {!destinations.length && <option value="">No active destination</option>}
        {destinations.map(a => <option value={a.id} key={a.id}>{a.accountType} #{a.id}</option>)}
      </select></label>
      <label>Transfer amount (USD)<input required type="number" min="0.01" step="0.01" value={amount} disabled={disabled} onChange={e => setAmount(e.target.value)} /></label>
      <button disabled={disabled}>Transfer funds</button>
    </form>
  </section>;
}
