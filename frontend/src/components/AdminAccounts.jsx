import { money } from '../format.js';
export default function AdminAccounts({ accounts, onStatus, busy }) {
  return <section className="panel admin-panel" id="accounts">
    <div className="section-heading"><h2>Customer accounts</h2><span className="muted">{accounts.length} accounts</span></div>
    {!accounts.length ? <p className="empty">No accounts to display.</p> : <div className="table-scroll"><table>
      <thead><tr><th>Customer</th><th>Account</th><th>Balance</th><th>Status</th><th>Action</th></tr></thead>
      <tbody>{accounts.map(a => <tr key={a.id}>
        <td><strong>{a.owner.displayName}</strong><span className="transaction-meta">{a.owner.email}</span></td>
        <td>#{a.id} · {a.accountType}</td><td>{money(a.balance)}</td>
        <td><span className={'badge ' + (a.status === 'FROZEN' ? 'frozen' : '')}>{a.status}</span></td>
        <td><button className="secondary" disabled={busy} aria-label={`${a.status === 'ACTIVE' ? 'Freeze' : 'Reactivate'} account ${a.id}`}
          onClick={() => onStatus(a.id, a.status === 'ACTIVE' ? 'FROZEN' : 'ACTIVE')}>
          {a.status === 'ACTIVE' ? 'Freeze' : 'Reactivate'}
        </button></td>
      </tr>)}</tbody>
    </table></div>}
  </section>;
}
