import { money, timestamp } from '../format.js';
export default function TransactionHistory({ transactions }) {
  return <section className="panel history" id="activity">
    <div className="section-heading"><h2>Transaction history</h2><span className="muted">Most recent first</span></div>
    {!transactions.length ? <p className="empty">No transactions yet.</p> : <div className="table-scroll"><table>
      <thead><tr><th>Date</th><th>Description</th><th>Amount</th><th>Balance after</th></tr></thead>
      <tbody>{transactions.map(t => {
        const debit = ['WITHDRAWAL', 'TRANSFER_OUT'].includes(t.type);
        return <tr key={t.id}>
          <td>{timestamp(t.createdAt)}</td>
          <td><strong>{t.description}</strong><span className="transaction-meta">{t.type.replaceAll('_', ' ')} · Ref {t.reference.slice(0, 8)}</span></td>
          <td className={debit ? 'debit' : 'credit'}>{debit ? '−' : '+'}{money(t.amount)}</td>
          <td>{money(t.balanceAfter)}</td>
        </tr>;
      })}</tbody>
    </table></div>}
  </section>;
}
