import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';
import { LayoutDashboard, LogOut, User } from 'lucide-react';

export default function Navbar() {
  const { dbUser, signOut } = useAuthStore();
  const navigate = useNavigate();
  const location = useLocation();

  const handleSignOut = async () => {
    await signOut();
    navigate('/');
  };

  return (
    <nav style={{
      position: 'sticky',
      top: 0,
      zIndex: 40,
      background: 'var(--color-bg)',
      borderBottom: '1px solid var(--color-border)',
    }}>
      <div style={{
        maxWidth: 1200,
        margin: '0 auto',
        padding: '0 24px',
        height: 64,
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
      }}>
        {/* Logo */}
        <Link to="/dashboard" style={{
          display: 'flex', alignItems: 'center', gap: 10,
          textDecoration: 'none', color: 'var(--color-text)',
        }}>
          <div style={{
            width: 32, height: 32,
            borderRadius: 'var(--radius-sm)',
            background: 'var(--color-text)',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}>
            <span style={{ fontSize: 16, color: 'var(--color-bg)', fontWeight: 700, lineHeight: 1 }}>◈</span>
          </div>
          <span style={{
            fontSize: 16, fontWeight: 700,
            letterSpacing: '-0.02em',
            color: 'var(--color-text)',
          }}>
            CuratiX Vault
          </span>
        </Link>

        {/* Right side */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
          {location.pathname !== '/dashboard' && (
            <Link to="/dashboard" className="btn-ghost">
              <LayoutDashboard size={14} />
              <span className="text-sm font-medium">Dashboard</span>
            </Link>
          )}

          <Link to="/profile" style={{
            display: 'flex', alignItems: 'center', gap: 8,
            padding: '4px 12px 4px 4px',
            background: 'var(--color-bg-secondary)',
            borderRadius: 'var(--radius-full)',
            border: '1px solid var(--color-border)',
            textDecoration: 'none',
            color: 'inherit',
            transition: 'all 0.2s',
          }} className="hover:border-white hover:bg-white/5 group">
            {dbUser?.photoUrl ? (
              <img
                src={dbUser.photoUrl}
                alt=""
                style={{
                  width: 28, height: 28,
                  borderRadius: '50%',
                  objectFit: 'cover',
                }}
              />
            ) : (
              <div style={{
                width: 28, height: 28,
                borderRadius: '50%',
                background: 'var(--color-surface)',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
                border: '1px solid var(--color-border)',
              }}>
                <User size={14} color="var(--color-text-secondary)" />
              </div>
            )}
            <span style={{
              fontSize: 13, fontWeight: 500,
              color: 'var(--color-text)',
              maxWidth: 120, overflow: 'hidden',
              textOverflow: 'ellipsis', whiteSpace: 'nowrap',
            }}>
              {dbUser?.displayName || dbUser?.email || 'User'}
            </span>
          </Link>

          <button onClick={handleSignOut} className="btn-ghost" title="Sign out" style={{ padding: '8px' }}>
            <LogOut size={16} />
          </button>
        </div>
      </div>
    </nav>
  );
}

