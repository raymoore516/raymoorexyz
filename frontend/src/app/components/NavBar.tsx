import { useRef, useState } from 'react';
import { NavLink, useLocation } from 'react-router-dom';

const pages = [
  { path: '/', label: 'Home' },
  { path: '/madisonsc', label: 'Madison SC' },
];

export default function NavBar() {
  const [isOpen, setIsOpen] = useState(false);
  const drawer = useRef<HTMLDialogElement>(null);
  const { pathname } = useLocation();
  const currentPage = pages.find(({ path }) => (
    path === '/' ? pathname === path : pathname === path || pathname.startsWith(`${path}/`)
  ));

  function openMenu() {
    drawer.current?.showModal();
    setIsOpen(true);
  }

  function closeMenu() {
    drawer.current?.close();
  }

  return (
    <>
      <header className="site-header">
        <button
          className="icon-button"
          type="button"
          aria-label="Open navigation"
          aria-controls="site-navigation"
          aria-expanded={isOpen}
          onClick={openMenu}
        >
          <svg aria-hidden="true" viewBox="0 0 16 16">
            <path d="M2 4h12M2 8h12M2 12h12" />
          </svg>
        </button>
        <span className="site-name">raymoore.xyz</span>
        <span className="header-divider" aria-hidden="true">/</span>
        <span className="current-page">{currentPage?.label ?? 'Page not found'}</span>
      </header>

      <dialog
        ref={drawer}
        id="site-navigation"
        className="navigation-drawer"
        aria-label="Main navigation"
        onClose={() => setIsOpen(false)}
        onCancel={() => setIsOpen(false)}
        onClick={(event) => {
          if (event.target === event.currentTarget) closeMenu();
        }}
      >
        <div className="drawer-panel">
          <div className="drawer-header">
            <span className="site-name">raymoore.xyz</span>
            <button className="icon-button close-button" type="button" aria-label="Close navigation" onClick={closeMenu}>
              <svg aria-hidden="true" viewBox="0 0 16 16">
                <path d="M3 3l10 10M13 3L3 13" />
              </svg>
            </button>
          </div>

          <nav className="drawer-links">
            {pages.map(({ path, label }) => (
              <NavLink
                key={path}
                to={path}
                end={path === '/'}
                className={({ isActive }) => isActive ? 'active' : undefined}
                onClick={closeMenu}
              >
                {label}
              </NavLink>
            ))}
          </nav>
        </div>
      </dialog>
    </>
  );
}
