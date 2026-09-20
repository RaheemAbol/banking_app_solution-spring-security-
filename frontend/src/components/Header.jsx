export default function Header({ user, onLogout, busy }) {
  return <header className="header">
    <div className="header-inner">
      <a className="brand" href="/" aria-label="Northbank home">
        <span className="brand-mark" aria-hidden="true">N<span /></span>
        <span>NORTHBANK<small>Banking portal</small></span>
      </a>
      {user && <nav className="header-nav" aria-label="Banking navigation">
        <a href="#accounts">{user.role === 'ADMIN' ? 'Customer accounts' : 'My accounts'}</a>
        {user.role !== 'ADMIN' && <a href="#activity">Activity</a>}
      </nav>}
      <div className="header-account">
        {user ? <>
          <span className="identity">{user.displayName}<span className="role">{user.role}</span></span>
          <button className="secondary" onClick={onLogout} disabled={busy}>Sign out</button>
        </> : <span className="header-caption">Personal banking</span>}
      </div>
    </div>
  </header>;
}
