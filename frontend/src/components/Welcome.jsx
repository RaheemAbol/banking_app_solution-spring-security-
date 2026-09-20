import AuthForm from './AuthForm.jsx';

export default function Welcome({ bankInfo, onSubmit, busy }) {
  return <div className="welcome-layout">
    <section className="welcome">
      <p className="eyebrow">NORTHBANK PERSONAL BANKING</p>
      <h1>Your accounts.<br />All in one place.</h1>
      <p className="welcome-intro">A clear view of your balances and every move you make.</p>
      <ul className="welcome-features">
        <li><span aria-hidden="true">01</span><div><strong>Checking & savings</strong><p>Keep your everyday funds and savings in view.</p></div></li>
        <li><span aria-hidden="true">02</span><div><strong>Simple transfers</strong><p>Move money between your own accounts.</p></div></li>
        <li><span aria-hidden="true">03</span><div><strong>Account activity</strong><p>Review deposits, withdrawals and transfers.</p></div></li>
      </ul>
      <div className="bank-info"><h2>Branch information</h2><p>{bankInfo || 'Loading branch information…'}</p></div>
    </section>
    <AuthForm onSubmit={onSubmit} busy={busy} />
  </div>;
}
