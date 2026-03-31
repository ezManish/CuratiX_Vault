import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';
import { LayoutDashboard, LogOut, User, Shield } from 'lucide-react';

export default function Navbar() {
  const { dbUser, signOut } = useAuthStore();
  const navigate = useNavigate();
  const location = useLocation();

  const handleSignOut = async () => {
    await signOut();
    navigate('/login');
  };

  return (
    <nav style={{
      position: 'sticky',
      top: 0,
      zIndex: 40,
      background: 'rgba(10, 10, 15, 0.85)',
      backdropFilter: 'blur(20px)',
      borderBottom: '1px solid var(--glass-border)',
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
            width: 36, height: 36,
            borderRadius: 'var(--radius-md)',
            background: 'linear-gradient(135deg, var(--color-primary), var(--color-accent))',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            boxShadow: '0 2px 12px rgba(99, 102, 241, 0.3)',
          }}>
            <Shield size={18} color="white" strokeWidth={2.5} />
          </div>
          <span style={{
            fontSize: 18, fontWeight: 700,
            background: 'linear-gradient(135deg, #e0e0ff, #c4b5fd)',
            WebkitBackgroundClip: 'text',
            WebkitTextFillColor: 'transparent',
          }}>
            CuratiX Vault
          </span>
        </Link>

        {/* Right side */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
          {location.pathname !== '/dashboard' && (
            <Link to="/dashboard" className="btn-ghost">
              <LayoutDashboard size={16} />
              Dashboard
            </Link>
          )}

          <Link to="/profile" style={{
            display: 'flex', alignItems: 'center', gap: 10,
            padding: '6px 12px',
            background: 'var(--color-surface)',
            borderRadius: 'var(--radius-full)',
            border: '1px solid var(--color-border)',
            textDecoration: 'none',
            color: 'inherit',
            transition: 'all 0.2s',
          }} className="hover:border-indigo-500/50 hover:bg-indigo-500/5 hover:shadow-lg hover:shadow-indigo-500/5 group">
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
                background: 'linear-gradient(135deg, var(--color-primary), var(--color-accent))',
                display: 'flex', alignItems: 'center', justifyContent: 'center',
              }}>
                <User size={14} color="white" />
              </div>
            )}
            <span style={{
              fontSize: 13, fontWeight: 500,
              color: 'var(--color-text-secondary)',
              maxWidth: 120, overflow: 'hidden',
              textOverflow: 'ellipsis', whiteSpace: 'nowrap',
            }} className="group-hover:text-indigo-400">
              {dbUser?.displayName || dbUser?.email || 'User'}
            </span>
          </Link>

          <button onClick={handleSignOut} className="btn-ghost" title="Sign out">
            <LogOut size={16} />
          </button>
        </div>
      </div>
    </nav>
  );
}
