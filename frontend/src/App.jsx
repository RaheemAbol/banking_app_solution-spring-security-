import { useEffect, useState } from 'react';
import { request } from './api.js';
import { currentUser, register, login, logout } from './auth.js';
import Header from './components/Header.jsx';
import Welcome from './components/Welcome.jsx';
import CustomerDashboard from './components/CustomerDashboard.jsx';
import AdminAccounts from './components/AdminAccounts.jsx';

export default function App() {
  const [user, setUser] = useState(null);
  const [accounts, setAccounts] = useState([]);
  const [selectedId, setSelectedId] = useState(null);
  const [transactions, setTransactions] = useState([]);
  const [adminAccounts, setAdminAccounts] = useState([]);
  const [bankInfo, setBankInfo] = useState('');
  const [notice, setNotice] = useState(null);
  const [busy, setBusy] = useState(true);
  const isAdmin = user?.role === 'ADMIN';

  function clearAccounts() {
    setAccounts([]); setSelectedId(null); setTransactions([]); setAdminAccounts([]);
  }
  function showError(error) {
    if (error.status === 401) { setUser(null); clearAccounts(); }
    setNotice({ error: true, text: error.message });
  }
  async function loadBankInfo() {
    try { setBankInfo(await request('/api/public/info')); }
    catch { setBankInfo('Branch information is currently unavailable.'); }
  }
  async function loadAccounts(person, preferredId = selectedId) {
    if (!person) { clearAccounts(); return; }
    if (person.role === 'ADMIN') {
      const all = await request('/api/admin/accounts');
      setAdminAccounts(all); setAccounts([]); setTransactions([]); return;
    }
    const list = await request('/api/accounts');
    setAccounts(list);
    const id = list.some(a => a.id === preferredId) ? preferredId : list[0]?.id;
    setSelectedId(id || null);
    setTransactions(id ? await request(`/api/accounts/${id}/transactions`) : []);
  }
  useEffect(() => {
    async function start() {
      await loadBankInfo();
      try { const person = await currentUser(); setUser(person); await loadAccounts(person); }
      catch (error) { showError(error); }
      finally { setBusy(false); }
    }
    start();
  }, []);

  async function authenticate(mode, name, email, password) {
    setBusy(true); setNotice(null);
    try {
      if (mode === 'register') {
        await register(name, email, password);
        setNotice({ text: 'Account created. Sign in with your new credentials.' });
      } else {
        const person = await login(email, password);
        setUser(person); clearAccounts();
        await loadAccounts(person, null);
      }
      return true;
    } catch (error) { showError(error); return false; }
    finally { setBusy(false); }
  }
  async function signOut() {
    setBusy(true); setNotice(null);
    try { await logout(); setUser(null); clearAccounts(); }
    catch (error) { showError(error); }
    finally { setBusy(false); }
  }
  async function refresh() {
    setBusy(true); setNotice(null);
    try { const person = await currentUser(); setUser(person); await loadAccounts(person); await loadBankInfo(); }
    catch (error) { showError(error); }
    finally { setBusy(false); }
  }
  async function chooseAccount(id) {
    setBusy(true); setNotice(null);
    try { const history = await request(`/api/accounts/${id}/transactions`); setSelectedId(id); setTransactions(history); }
    catch (error) { showError(error); }
    finally { setBusy(false); }
  }
  async function change(path, method, fields, message) {
    setBusy(true); setNotice(null);
    try {
      await request(path, method, fields);
      // A saved operation must never look like a failure merely because a refresh failed.
      setNotice({ text: message });
      try { await loadAccounts(user); }
      catch { setNotice({ text: message + ' Refresh the page to reload the latest balance.' }); }
      return true;
    } catch (error) { showError(error); return false; }
    finally { setBusy(false); }
  }
  function moveMoney(id, operation, amount, description) {
    return change(`/api/accounts/${id}/${operation}`, 'POST', { amount, description },
      operation === 'deposits' ? 'Deposit completed.' : 'Withdrawal completed.');
  }
  function transfer(fromAccountId, toAccountId, amount) {
    return change('/api/transfers', 'POST', { fromAccountId, toAccountId, amount }, 'Transfer completed.');
  }
  function changeStatus(id, status) {
    return change(`/api/accounts/${id}/status`, 'PATCH', { status },
      status === 'FROZEN' ? 'Account frozen.' : 'Account reactivated.');
  }

  return <>
    <Header user={user} onLogout={signOut} busy={busy} />
    <main className="page">
      {notice && <div role="alert" className={'notice ' + (notice.error ? 'error' : 'success')}>{notice.text}</div>}
      {!user ? <Welcome bankInfo={bankInfo} onSubmit={authenticate} busy={busy} /> : <>
        <div className="page-heading">
          <div><p className="eyebrow">{isAdmin ? 'ADMINISTRATION' : 'PERSONAL BANKING'}</p>
            <h1>{isAdmin ? 'Account oversight' : 'Your accounts'}</h1>
            <p>{isAdmin ? 'Review customer accounts and manage their availability.' : 'Your balances, transfers and activity in one place.'}</p>
          </div>
          <button className="secondary" disabled={busy} onClick={refresh}>Refresh</button>
        </div>
        {isAdmin ? <AdminAccounts accounts={adminAccounts} onStatus={changeStatus} busy={busy} /> :
          <CustomerDashboard accounts={accounts} selectedId={selectedId} transactions={transactions}
            onSelect={chooseAccount} onMoveMoney={moveMoney} onTransfer={transfer} busy={busy} />}
      </>}
    </main>
    <footer>Classroom banking simulation. Deposits and withdrawals use fictional funds.</footer>
  </>;
}
