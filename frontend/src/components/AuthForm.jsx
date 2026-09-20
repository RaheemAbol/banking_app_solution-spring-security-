import { useState } from 'react';
export default function AuthForm({ onSubmit, busy }) {
  const [mode, setMode] = useState('login');
  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  async function submit(event) {
    event.preventDefault();
    const ok = await onSubmit(mode, name, email, password);
    if (ok && mode === 'register') { setMode('login'); setPassword(''); }
  }
  return <section className="auth-panel">
    <p className="eyebrow">ONLINE BANKING</p>
    <h2>{mode === 'login' ? 'Welcome back' : 'Open your demo account'}</h2>
    <p className="auth-description">{mode === 'login' ? 'Sign in to manage your accounts.' : 'Start with a checking and a savings account.'}</p>
    <form onSubmit={submit}>
      {mode === 'register' && <label>Full name<input required maxLength="100" autoComplete="name" value={name} onChange={e => setName(e.target.value)} /></label>}
      <label>Email<input type="email" required autoComplete="username" value={email} onChange={e => setEmail(e.target.value)} /></label>
      <label>Password<input type="password" required minLength="8" autoComplete={mode === 'login' ? 'current-password' : 'new-password'} value={password} onChange={e => setPassword(e.target.value)} /></label>
      <button disabled={busy}>{busy ? 'Please wait…' : mode === 'login' ? 'Sign in' : 'Create account'}</button>
    </form>
    <button type="button" className="text-button" disabled={busy} onClick={() => setMode(mode === 'login' ? 'register' : 'login')}>
      {mode === 'login' ? 'New here? Create an account' : 'Already registered? Sign in'}
    </button>
    {mode === 'register' && <p className="muted">Your checking and savings accounts start at $0.00.</p>}
  </section>;
}
