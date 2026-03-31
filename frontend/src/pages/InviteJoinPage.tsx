import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';
import { invitesApi } from '../api/endpoints';
import { Shield, Users, LogIn } from 'lucide-react';
import toast from 'react-hot-toast';

export default function InviteJoinPage() {
  const { token } = useParams<{ token: string }>();
  const navigate = useNavigate();
  const { user, signInWithGoogle, loading: authLoading } = useAuthStore();

  const [preview, setPreview] = useState<any>(null);
  const [loading, setLoading] = useState(true);
  const [joining, setJoining] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (token) loadPreview();
  }, [token]);

  const loadPreview = async () => {
    try {
      const { data } = await invitesApi.preview(token!);
      setPreview(data);
    } catch (e: any) {
      setError(e.response?.data?.error || 'This invite link is invalid or expired.');
    } finally {
      setLoading(false);
    }
  };

  const handleJoin = async () => {
    if (!user) {
      await signInWithGoogle();
      return;
    }
    setJoining(true);
    try {
      const { data } = await invitesApi.join(token!);
      toast.success(data.message);
      navigate(`/boards/${data.boardId}`);
    } catch (e: any) {
      toast.error(e.response?.data?.error || 'Failed to join');
    } finally {
      setJoining(false);
    }
  };

  // Auto-join if user is already logged in and preview loaded
  useEffect(() => {
    if (user && preview && !joining) {
      handleJoin();
    }
  }, [user, preview]);

  return (
    <div style={{
      minHeight: '100vh',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      background: 'var(--color-bg)',
      padding: 24,
    }}>
      <div className="animate-scale-in glass-card" style={{ padding: 48, maxWidth: 440, width: '100%', textAlign: 'center' }}>
        {loading ? (
          <div>
            <div className="skeleton" style={{ width: 60, height: 60, borderRadius: '50%', margin: '0 auto 20px' }} />
            <div className="skeleton" style={{ height: 22, width: 200, margin: '0 auto 12px' }} />
            <div className="skeleton" style={{ height: 16, width: 260, margin: '0 auto' }} />
          </div>
        ) : error ? (
          <div>
            <div style={{
              width: 64, height: 64, borderRadius: '50%',
              background: 'rgba(239, 68, 68, 0.12)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              margin: '0 auto 20px',
            }}>
              <Shield size={28} color="#ef4444" />
            </div>
            <h2 style={{ fontSize: 20, fontWeight: 700, marginBottom: 8 }}>Invalid Invite</h2>
            <p style={{ fontSize: 14, color: 'var(--color-text-muted)', marginBottom: 24 }}>{error}</p>
            <button className="btn-primary" onClick={() => navigate('/login')}>
              Go to Login
            </button>
          </div>
        ) : (
          <div>
            <div style={{
              width: 64, height: 64, borderRadius: '50%',
              background: 'linear-gradient(135deg, var(--color-primary), var(--color-accent))',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              margin: '0 auto 20px',
              boxShadow: '0 4px 24px rgba(99, 102, 241, 0.35)',
            }}>
              <Users size={28} color="white" />
            </div>
            <h2 style={{ fontSize: 22, fontWeight: 800, marginBottom: 8 }}>
              You're invited!
            </h2>
            <p style={{ fontSize: 15, color: 'var(--color-text-secondary)', marginBottom: 8 }}>
              Join <strong>{preview.boardName}</strong>
            </p>
            <span className={`badge badge-${preview.role.toLowerCase()}`} style={{ marginBottom: 24, display: 'inline-flex' }}>
              as {preview.role}
            </span>

            <div style={{ marginTop: 24 }}>
              <button className="btn-primary" onClick={handleJoin} disabled={joining || authLoading} style={{ width: '100%', padding: 14 }}>
                {joining ? 'Joining...' : user ? 'Join Board' : 'Sign in & Join'}
                <LogIn size={16} />
              </button>
            </div>
          </div>
        )}
      </div>
    </div>
  );
}
