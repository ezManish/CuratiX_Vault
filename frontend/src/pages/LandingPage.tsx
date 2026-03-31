import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';
import { 
  Layout, 
  Users, 
  FolderLock, 
  Trophy, 
  ArrowRight
} from 'lucide-react';

const FEATURES = [
  {
    icon: Layout,
    title: 'Structured Project Boards',
    desc: 'Organize every hackathon into a dedicated board with members, files, and timelines in one place.',
    color: '#3b82f6'
  },
  {
    icon: Users,
    title: 'Instant Team Sync',
    desc: 'Invite members via a single shared link. Profiles auto-sync across every board you join.',
    color: '#10b981'
  },
  {
    icon: FolderLock,
    title: 'Unified File Vault',
    desc: 'Store pitch decks, code archives, and design assets — tagged, searchable, and always available.',
    color: '#8b5cf6'
  },
  {
    icon: Trophy,
    title: 'Hackathon Records',
    desc: 'Track your participation history, results, venues, and teams across every competition you enter.',
    color: '#f59e0b'
  },
];

export default function LandingPage() {
  const navigate = useNavigate();
  const { user, loading } = useAuthStore();

  useEffect(() => {
    if (!loading && user) {
      navigate('/dashboard', { replace: true });
    }
  }, [loading, user, navigate]);

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      flexDirection: 'column',
      background: 'linear-gradient(to bottom, #0a0a14, #09090b)', // Deep subtle tint
      color: 'var(--color-text)',
      position: 'relative',
      overflowX: 'hidden',
    }}>
      {/* ─── Background Depth ─── */}
      <div style={{
        position: 'absolute',
        top: 0, left: 0, right: 0, bottom: 0,
        zIndex: 0,
        pointerEvents: 'none',
      }}>
        {/* Main Hero Glow */}
        <div style={{
          position: 'absolute',
          top: '-30%', left: '50%', transform: 'translateX(-50%)',
          width: '90vw', height: '60vw',
          background: 'radial-gradient(circle, rgba(99, 102, 241, 0.18) 0%, transparent 70%)',
          filter: 'blur(140px)',
        }} />
        {/* Accent Glows */}
        <div style={{
          position: 'absolute',
          top: '25%', left: '-10%',
          width: '50vw', height: '50vw',
          background: 'radial-gradient(circle, rgba(139, 92, 246, 0.12) 0%, transparent 70%)',
          filter: 'blur(120px)',
        }} />
        <div style={{
          position: 'absolute',
          bottom: '10%', right: '-10%',
          width: '60vw', height: '60vw',
          background: 'radial-gradient(circle, rgba(59, 130, 246, 0.12) 0%, transparent 70%)',
          filter: 'blur(120px)',
        }} />
      </div>

      {/* ─── Navbar ─── */}
      <header className="glass-navbar">
        <div style={{
          maxWidth: 1200,
          margin: '0 auto',
          padding: '0 24px',
          height: 64,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
            <div style={{
              width: 32, height: 32, borderRadius: 8,
              background: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center',
              boxShadow: '0 0 20px rgba(255,255,255,0.1)',
            }}>
              <span style={{ fontSize: 16, color: '#000', fontWeight: 800 }}>◈</span>
            </div>
            <span style={{ fontWeight: 800, fontSize: 18, letterSpacing: '-0.04em' }}>CURATIX VAULT</span>
          </div>
          <div style={{ display: 'flex', gap: 12 }}>
            <button
              onClick={() => navigate('/login')}
              className="btn-ghost"
              style={{ fontSize: 14 }}
            >
              Sign In
            </button>
            <button
              onClick={() => navigate('/login')}
              className="btn-primary"
              style={{ padding: '8px 24px', fontSize: 13 }}
            >
              Get Started
            </button>
          </div>
        </div>
      </header>

      {/* ─── Hero Section ─── */}
      <main style={{ flex: 1, zIndex: 1, position: 'relative' }}>
        <section style={{
          padding: '160px 24px 80px',
          textAlign: 'center',
          maxWidth: 900,
          margin: '0 auto',
        }}>
          <div className="animate-reveal" style={{ animationDelay: '0.1s' }}>
            <div className="badge" style={{ marginBottom: 32, padding: '6px 14px', fontSize: 12 }}>
              ✦ &nbsp; V1.0 IS LIVE &nbsp; — &nbsp; BUILT FOR BUILDERS
            </div>
          </div>

          <h1 className="animate-reveal" style={{
            fontSize: 'clamp(44px, 8vw, 92px)',
            fontWeight: 800,
            letterSpacing: '-0.06em',
            lineHeight: 0.95,
            marginBottom: 32,
            animationDelay: '0.2s',
          }}>
            Your hackathon journey, <br />
            <span className="text-gradient">organized.</span>
          </h1>

          <p className="animate-reveal" style={{
            fontSize: 'clamp(16px, 2vw, 20px)',
            color: 'var(--color-text-secondary)',
            maxWidth: 600,
            margin: '0 auto 48px',
            lineHeight: 1.6,
            animationDelay: '0.3s',
          }}>
            CuratiX Vault is your team's command center. Manage boards, 
            vault files, and sync profiles across every competition.
          </p>

          <div className="animate-reveal" style={{ 
            display: 'flex', gap: 16, flexWrap: 'wrap', justifyContent: 'center',
            animationDelay: '0.4s'
          }}>
            <button
              onClick={() => navigate('/login')}
              className="btn-primary"
              style={{ padding: '16px 40px', fontSize: 16, height: 56 }}
            >
              Get Started Free <ArrowRight size={18} />
            </button>
            <button
              onClick={() => navigate('/login')}
              className="btn-secondary"
              style={{ padding: '16px 40px', fontSize: 16, height: 56 }}
            >
              Sign In
            </button>
          </div>
        </section>

        {/* ─── Features Grid ─── */}
        <section style={{ padding: '40px 24px 80px', maxWidth: 1000, margin: '0 auto' }}>
          <div style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fit, minmax(400px, 1fr))',
            gap: 20,
          }}>
            {FEATURES.map((f, i) => (
              <div
                key={f.title}
                className="card animate-reveal"
                style={{ 
                  padding: '24px 32px',
                  animationDelay: `${0.5 + (i * 0.1)}s`,
                  display: 'flex',
                  flexDirection: 'column',
                  gap: 16,
                  minHeight: 180,
                }}
              >
                <div style={{
                  width: 40, height: 40,
                  borderRadius: 10,
                  background: `${f.color}15`,
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  border: `1px solid ${f.color}30`,
                }}>
                  <f.icon size={20} color={f.color} />
                </div>
                <div>
                  <h3 style={{ fontSize: 18, fontWeight: 700, marginBottom: 8, letterSpacing: '-0.02em' }}>
                    {f.title}
                  </h3>
                  <p style={{ fontSize: 14, color: 'var(--color-text-secondary)', lineHeight: 1.5 }}>
                    {f.desc}
                  </p>
                </div>
              </div>
            ))}
          </div>
        </section>

        {/* ─── CTA Footer Section ─── */}
        <section style={{ padding: '80px 24px 120px', textAlign: 'center' }}>
          <h2 style={{ fontSize: 'clamp(32px, 5vw, 44px)', fontWeight: 800, marginBottom: 20, letterSpacing: '-0.04em' }}>
            Ready to lead your team?
          </h2>
          <p style={{ color: 'var(--color-text-secondary)', fontSize: 16, marginBottom: 40 }}>
            Join hundreds of teams organizing their hackathon journey.
          </p>
          <button
            onClick={() => navigate('/login')}
            className="btn-primary"
            style={{ padding: '16px 40px', fontSize: 16, height: 56 }}
          >
            Launch Your Vault ◈
          </button>
        </section>
      </main>

      {/* ─── Footer ─── */}
      <footer style={{
        borderTop: '1px solid var(--color-border)',
        padding: '32px 24px',
        maxWidth: 1000,
        margin: '0 auto',
        width: '100%',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'space-between',
        color: 'var(--color-text-muted)',
        fontSize: 12,
        flexWrap: 'wrap',
        gap: 20,
      }}>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
          <div style={{
            width: 24, height: 24, borderRadius: 6,
            background: '#fff', display: 'flex', alignItems: 'center', justifyContent: 'center',
          }}>
            <span style={{ fontSize: 12, color: '#000', fontWeight: 800 }}>◈</span>
          </div>
          <span style={{ fontWeight: 800, fontSize: 14, color: 'var(--color-text)' }}>CURATIX VAULT</span>
        </div>
        
        <div style={{ display: 'flex', alignItems: 'center', gap: 24 }}>
          <span>© 2026 CuratiX Vault</span>
          <span style={{ fontFamily: 'JetBrains Mono, monospace', letterSpacing: '0.05em' }}>
            built for builders ✦
          </span>
        </div>
      </footer>

    </div>
  );
}

