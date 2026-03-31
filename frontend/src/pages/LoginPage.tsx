import { useState } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';
import { Mail, Lock, ArrowRight, Eye, EyeOff } from 'lucide-react';

export default function LoginPage() {
  const { user, loading, error, signInWithGoogle, signInWithEmail, signUpWithEmail, clearError } = useAuthStore();
  const [isSignUp, setIsSignUp] = useState(false);
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  if (user && !loading) return <Navigate to="/dashboard" replace />;

  const handleEmailAuth = async (e: React.FormEvent) => {
    e.preventDefault();
    if (isSignUp) {
      await signUpWithEmail(email, password);
    } else {
      await signInWithEmail(email, password);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'var(--color-bg)',
      padding: 24,
      position: 'relative',
      overflow: 'hidden',
    }}>
      {/* Background gradient orbs */}
      <div style={{
        position: 'absolute',
        width: 600, height: 600,
        borderRadius: '50%',
        background: 'radial-gradient(circle, rgba(99,102,241,0.12) 0%, transparent 70%)',
        top: '-15%', right: '-10%',
        filter: 'blur(60px)',
        pointerEvents: 'none',
      }} />
      <div style={{
        position: 'absolute',
        width: 500, height: 500,
        borderRadius: '50%',
        background: 'radial-gradient(circle, rgba(168,85,247,0.1) 0%, transparent 70%)',
        bottom: '-10%', left: '-5%',
        filter: 'blur(60px)',
        pointerEvents: 'none',
      }} />

      <div className="animate-reveal" style={{
        width: '100%',
        maxWidth: 1000,
        position: 'relative',
        zIndex: 1,
        display: 'flex',
        flexDirection: 'row',
        alignItems: 'center',
        justifyContent: 'space-between',
        gap: 80,
        flexWrap: 'wrap', // Auto-stack on mobile
      }}>
        {/* Left Side: Brand Identity */}
        <div style={{ flex: 1, minWidth: 320, textAlign: 'left' }}>
          <div style={{
            width: 56, height: 56,
            borderRadius: 16,
            background: 'white',
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            marginBottom: 24,
            boxShadow: '0 12px 32px rgba(255, 255, 256, 0.15)',
          }}>
            <span style={{ fontSize: 28, color: '#000', fontWeight: 800, lineHeight: 1 }}>◈</span>
          </div>
          <h1 style={{
            fontSize: 'clamp(44px, 6vw, 72px)', 
            fontWeight: 800,
            color: 'white',
            letterSpacing: '-0.06em',
            lineHeight: 0.9,
            marginBottom: 20,
          }}>
            CuratiX Vault
          </h1>
          <p style={{ 
            color: 'var(--color-text-secondary)', 
            fontSize: 18, 
            fontWeight: 400,
            maxWidth: 400,
            lineHeight: 1.6,
            opacity: 0.8
          }}>
            Your team's hackathon command center
          </p>
        </div>

        {/* Right Side: Auth Card */}
        <div className="card" style={{ 
          flex: '0 0 460px', 
          padding: '40px 40px 48px', 
          display: 'flex', 
          flexDirection: 'column', 
          gap: 32,
          minWidth: 320 
        }}>
          {/* Google Sign In */}
          <button
            onClick={signInWithGoogle}
            disabled={loading}
            style={{
              width: '100%',
              display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 12,
              padding: '14px 24px',
              height: 52,
              background: 'white',
              color: '#000',
              fontWeight: 700,
              fontSize: 15,
              border: 'none',
              borderRadius: 14,
              cursor: 'pointer',
              transition: 'all 0.3s cubic-bezier(0.4, 0, 0.2, 1)',
              boxShadow: 'var(--shadow-glow)',
            }}
            onMouseOver={e => (e.currentTarget.style.transform = 'translateY(-2px)')}
            onMouseOut={e => (e.currentTarget.style.transform = 'translateY(0)')}
          >
            <svg width="20" height="20" viewBox="0 0 48 48"><path fill="#FFC107" d="M43.6 20.1H42V20H24v8h11.3C33.9 33.6 29.3 36 24 36c-6.6 0-12-5.4-12-12s5.4-12 12-12c3 0 5.8 1.1 7.9 3l5.7-5.7C34 5.7 29.3 4 24 4 12.9 4 4 12.9 4 24s8.9 20 20 20 20-8.9 20-20c0-1.3-.2-2.7-.4-3.9z" /><path fill="#FF3D00" d="m6.3 14.7 6.6 4.8C14.5 15.5 18.8 12 24 12c3 0 5.8 1.1 7.9 3l5.7-5.7C34 5.7 29.3 4 24 4 16.3 4 9.7 8.3 6.3 14.7z" /><path fill="#4CAF50" d="M24 44c5.2 0 9.9-1.6 13.4-4.5l-6.2-5.2C29.5 35.4 26.9 36 24 36c-5.3 0-9.8-3.4-11.4-8l-6.5 5C9.5 39.6 16.2 44 24 44z" /><path fill="#1976D2" d="M43.6 20.1H42V20H24v8h11.3c-.8 2.2-2.2 4.1-3.9 5.5l6.2 5.2C37.1 39.2 44 34 44 24c0-1.3-.2-2.7-.4-3.9z" /></svg>
            Continue with Google
          </button>

          {/* Divider */}
          <div style={{
            display: 'flex', alignItems: 'center', gap: 14,
            margin: '4px 0',
            color: 'var(--color-text-muted)',
            fontSize: 12,
            fontWeight: 600,
            textTransform: 'uppercase',
            letterSpacing: '0.06em'
          }}>
            <div style={{ flex: 1, height: 1, background: 'var(--color-border)' }} />
            or
            <div style={{ flex: 1, height: 1, background: 'var(--color-border)' }} />
          </div>

          {/* Email/Password form */}
          <form onSubmit={handleEmailAuth} style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
            <div>
              <label className="input-label" style={{ marginBottom: 12, fontSize: 12 }}>Email address</label>
              <div style={{ position: 'relative' }}>
                <Mail size={16} style={{
                  position: 'absolute', left: 16, top: '50%', transform: 'translateY(-50%)',
                  color: 'var(--color-text-muted)',
                }} />
                <input
                  type="email"
                  value={email}
                  onChange={e => setEmail(e.target.value)}
                  className="input-field"
                  placeholder="you@example.com"
                  required
                  style={{ paddingLeft: 44, height: 52 }}
                />
              </div>
            </div>

            <div>
              <label className="input-label" style={{ marginBottom: 12, fontSize: 12 }}>Password</label>
              <div style={{ position: 'relative' }}>
                <Lock size={16} style={{
                  position: 'absolute', left: 16, top: '50%', transform: 'translateY(-50%)',
                  color: 'var(--color-text-muted)',
                }} />
                <input
                  type={showPassword ? 'text' : 'password'}
                  value={password}
                  onChange={e => setPassword(e.target.value)}
                  className="input-field"
                  placeholder="••••••••"
                  required
                  minLength={6}
                  style={{ paddingLeft: 44, paddingRight: 44, height: 52 }}
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  style={{
                    position: 'absolute', right: 14, top: '50%', transform: 'translateY(-50%)',
                    background: 'none', border: 'none', cursor: 'pointer',
                    color: 'var(--color-text-muted)',
                  }}
                >
                  {showPassword ? <EyeOff size={16} /> : <Eye size={16} />}
                </button>
              </div>
            </div>

            {error && (
              <div style={{
                padding: '12px 16px',
                background: 'rgba(239, 68, 68, 0.1)',
                border: '1px solid rgba(239, 68, 68, 0.2)',
                borderRadius: 12,
                color: '#f87171',
                fontSize: 14,
              }}>
                {error}
              </div>
            )}

            <button type="submit" className="btn-primary" disabled={loading} style={{ width: '100%', padding: '14px', borderRadius: 14, height: 52, fontSize: 15, display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 10 }}>
              {loading ? 'Please wait...' : (isSignUp ? 'Create Account' : 'Sign In')}
              {!loading && <ArrowRight size={18} />}
            </button>
          </form>

          {/* Toggle sign in / sign up */}
          <p style={{
            textAlign: 'center',
            fontSize: 13, color: 'var(--color-text-secondary)',
          }}>
            {isSignUp ? 'Already have an account?' : "Don't have an account?"}{' '}
            <button
              onClick={() => { setIsSignUp(!isSignUp); clearError(); }}
              style={{
                background: 'none', border: 'none', cursor: 'pointer',
                color: 'white', fontWeight: 700,
                fontSize: 13,
              }}
            >
              {isSignUp ? 'Sign in' : 'Sign up'}
            </button>
          </p>
        </div>
      </div>
    </div>
  );
}
