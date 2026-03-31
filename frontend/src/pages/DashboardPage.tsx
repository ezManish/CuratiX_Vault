import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';
import { boardsApi, invitationsApi } from '../api/endpoints';
import { Plus, Users, User, FolderOpen, ChevronRight, Layers, Check, X, Bell, Layout } from 'lucide-react';
import toast from 'react-hot-toast';
import { createPortal } from 'react-dom';

interface Board {
  id: number;
  name: string;
  description: string;
  coverColor: string;
  coverEmoji: string;
  platform?: string;
  eventDate?: string;
  venue?: string;
  theme?: string;
  teamName?: string;
  problemStatement?: string;
  projectIdea?: string;
  result?: string;
  prize?: string;
  submissionUrl?: string;
  repoUrl?: string;
  notes?: string;
  createdAt: string;
}

interface NewBoardState {
  [key: string]: string;
  name: string;
  description: string;
  coverColor: string;
  coverEmoji: string;
  platform: string;
  eventDate: string;
  venue: string;
  theme: string;
  teamName: string;
  projectIdea: string;
}

export default function DashboardPage() {
  const { dbUser } = useAuthStore();
  const [boards, setBoards] = useState<Board[]>([]);
  const [loading, setLoading] = useState(true);
  const [showCreate, setShowCreate] = useState(false);
  const [newBoard, setNewBoard] = useState<NewBoardState>({ 
    name: '', 
    description: '', 
    coverColor: '#6366f1', 
    coverEmoji: '📋',
    platform: '',
    eventDate: '',
    venue: '',
    theme: '',
    teamName: '',
    projectIdea: ''
  });
  const [creating, setCreating] = useState(false);
  const [invitations, setInvitations] = useState<any[]>([]);
  const [responding, setResponding] = useState<number | null>(null);

  const COLORS = ['#6366f1', '#8b5cf6', '#ec4899', '#f43f5e', '#f97316', '#22c55e', '#06b6d4', '#3b82f6'];

  useEffect(() => {
    loadBoards();
    loadInvitations();
  }, []);

  const loadBoards = async () => {
    try {
      const { data } = await boardsApi.list();
      setBoards(data);
    } catch (e) {
      console.error('Failed to load boards', e);
    } finally {
      setLoading(false);
    }
  };
  const loadInvitations = async () => {
    try {
      const { data } = await invitationsApi.getMy();
      setInvitations(Array.isArray(data) ? data : []);
    } catch (e) {
      console.error('Failed to load invitations', e);
      toast.error('Failed to load invitations. Check console.');
    }
  };

  const handleCreate = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!newBoard.name.trim()) return;
    setCreating(true);
    try {
      await boardsApi.create(newBoard);
      setShowCreate(false);
      setNewBoard({ 
        name: '', 
        description: '', 
        coverColor: '#6366f1', 
        coverEmoji: '📋',
        platform: '',
        eventDate: '',
        venue: '',
        theme: '',
        teamName: '',
        projectIdea: ''
      });
      loadBoards();
      toast.success('Board created successfully');
    } catch (e) {
      console.error('Failed to create board', e);
      toast.error('Failed to create board');
    } finally {
      setCreating(false);
    }
  };

  const handleInvitation = async (id: number, accept: boolean) => {
    setResponding(id);
    try {
      await invitationsApi.respond(id, accept);
      toast.success(accept ? 'Joined board!' : 'Invitation declined');
      loadInvitations();
      if (accept) loadBoards();
    } catch (e) {
      toast.error('Failed to respond to invitation');
    } finally {
      setResponding(null);
    }
  };

  return (
    <div className="animate-fade-in">
      {/* Header */}
      <div style={{
        display: 'flex', alignItems: 'center', justifyContent: 'space-between',
        marginBottom: 32,
      }}>
        <div>
          <h1 style={{ fontSize: 32, fontWeight: 800, letterSpacing: '-0.04em', marginBottom: 4 }}>
            Welcome back{dbUser?.displayName ? `, ${dbUser.displayName.split(' ')[0]}` : ''}
          </h1>
          <p style={{ color: 'var(--color-text-muted)', fontSize: 15 }}>
            {boards.length} {boards.length === 1 ? 'board' : 'boards'} in your vault
          </p>
        </div>
        <button className="btn-primary" onClick={() => setShowCreate(true)}>
          <Plus size={18} />
          New Board
        </button>
      </div>

      {/* Profile Incomplete Prompt */}
      {!dbUser?.admissionNo && (
        <div className="card animate-slide-up" style={{
          padding: '24px',
          marginBottom: 32,
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'space-between',
        }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
            <div style={{
              width: 56, height: 56, borderRadius: 'var(--radius-lg)',
              background: 'var(--color-bg-secondary)',
              border: '1px solid var(--color-border)',
              boxShadow: 'inset 0 1px 0 rgba(255,255,255,0.05)',
              display: 'flex', alignItems: 'center', justifyContent: 'center',
              color: 'var(--color-primary)'
            }}>
              <User size={24} />
            </div>
            <div>
              <h4 style={{ fontSize: 16, fontWeight: 700, marginBottom: 2 }}>Complete your global profile</h4>
              <p style={{ fontSize: 13, color: 'var(--color-text-muted)', maxWidth: 450 }}>
                Fill in your details once to automatically join team boards with your full profile.
              </p>
            </div>
          </div>
          <Link to="/profile" className="btn-secondary" style={{ padding: '10px 20px', borderRadius: 14 }}>
            Go to Profile
          </Link>
        </div>
      )}

      {/* Invitations Section */}
      {invitations.length > 0 && (
        <div className="animate-slide-up" style={{ marginBottom: 32 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 16 }}>
            <div style={{
              padding: '6px 12px', background: 'rgba(245,158,11,0.1)', color: '#f59e0b',
              borderRadius: 20, fontSize: 12, fontWeight: 700, display: 'flex', alignItems: 'center', gap: 6
            }}>
              <Bell size={12} /> {invitations.length} PENDING REQUEST{invitations.length !== 1 ? 'S' : ''}
            </div>
          </div>
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(350px, 1fr))', gap: 16 }}>
            {invitations.map(invite => (
              <div key={invite.id} className="card" style={{ padding: 20 }}>
                <div style={{ display: 'flex', alignItems: 'flex-start', gap: 14 }}>
                  <div style={{
                    width: 44, height: 44, borderRadius: 'var(--radius-md)',
                    background: 'var(--color-surface)',
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    fontSize: 20, border: '1px solid var(--color-border)'
                  }}>
                    {invite.board?.coverEmoji || '📋'}
                  </div>
                  <div style={{ flex: 1 }}>
                    <h4 style={{ fontSize: 15, fontWeight: 700, marginBottom: 2 }}>{invite.board?.name || 'Unknown Board'}</h4>
                    <p style={{ fontSize: 12, color: 'var(--color-text-muted)' }}>
                      Invited as <strong>{invite.role}</strong> by {invite.inviter?.displayName || invite.inviter?.email || 'System'}
                    </p>
                  </div>
                </div>
                <div style={{ display: 'flex', gap: 10, marginTop: 16 }}>
                  <button 
                    className="btn-primary" 
                    style={{ flex: 1, padding: '8px', fontSize: 13, background: '#22c55e', borderColor: '#22c55e' }}
                    onClick={() => handleInvitation(invite.id, true)}
                    disabled={responding === invite.id}
                  >
                    <Check size={14} /> Accept
                  </button>
                  <button 
                    className="btn-secondary" 
                    style={{ flex: 1, padding: '8px', fontSize: 13 }}
                    onClick={() => handleInvitation(invite.id, false)}
                    disabled={responding === invite.id}
                  >
                    <X size={14} /> Decline
                  </button>
                </div>
              </div>
            ))}
          </div>
        </div>
      )}

      {/* Board Grid */}
      {loading ? (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 20 }}>
          {[1, 2, 3].map(i => (
            <div key={i} className="skeleton" style={{ height: 180, borderRadius: 'var(--radius-lg)' }} />
          ))}
        </div>
      ) : boards.length === 0 ? (
        <div className="empty-state" style={{ marginTop: 80 }}>
          <Layers size={72} />
          <h3>No boards yet</h3>
          <p>Create your first board to start organizing your hackathon team data.</p>
          <button className="btn-primary" onClick={() => setShowCreate(true)} style={{ marginTop: 20 }}>
            <Plus size={16} /> Create Board
          </button>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: 24 }}>
          {boards.map((board, idx) => (
            <Link
              key={board.id}
              to={`/boards/${board.id}`}
              className="card"
              style={{
                padding: 24,
                textDecoration: 'none',
                color: 'inherit',
                cursor: 'pointer',
                position: 'relative',
                overflow: 'hidden',
                animationDelay: `${idx * 60}ms`,
              }}
            >
              {/* Color accent bar */}
              <div style={{
                position: 'absolute', top: 0, left: 0, right: 0, height: 4,
                background: board.coverColor || '#ffffff',
              }} />

              <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between' }}>
                <div style={{
                  width: 44, height: 44,
                  borderRadius: 'var(--radius-md)',
                  background: 'var(--color-surface)',
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  fontSize: 22,
                  border: '1px solid var(--color-border)',
                }}>
                  {board.coverEmoji || '📋'}
                </div>
                <ChevronRight size={18} style={{ color: 'var(--color-text-muted)' }} />
              </div>

              <h3 style={{
                fontSize: 17, fontWeight: 700,
                marginTop: 16, marginBottom: 6,
                overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
              }}>
                {board.name}
              </h3>

              {board.description && (
                <p style={{
                  fontSize: 13, color: 'var(--color-text-muted)',
                  lineHeight: 1.5,
                  display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical',
                  overflow: 'hidden',
                }}>
                  {board.description}
                </p>
              )}

              <div style={{
                display: 'flex', gap: 16, marginTop: 16,
                fontSize: 12, color: 'var(--color-text-muted)',
                alignItems: 'center',
                flexWrap: 'wrap'
              }}>
                <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
                  <Users size={13} /> Members
                </span>
                <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
                  <FolderOpen size={13} /> Files
                </span>
                {board.platform && (
                  <span style={{ display: 'flex', alignItems: 'center', gap: 4 }}>
                    <Layout size={13} /> {board.platform}
                  </span>
                )}
                {board.result && (
                  <span className={`badge ${board.result === 'WINNER' ? 'badge-success' : board.result === 'SHORTLISTED' || board.result === 'TOP_N' ? 'badge-warning' : 'badge-viewer'}`} style={{ marginLeft: 'auto', fontSize: 10 }}>
                    {board.result.replace('_', ' ')}
                  </span>
                )}
              </div>
            </Link>
          ))}
        </div>
      )}

      {/* Create Board Modal */}
      {showCreate && createPortal(
        <div className="modal-overlay" onClick={e => { if (e.target === e.currentTarget) setShowCreate(false); }}>
          <div className="modal-content" style={{ maxWidth: 640 }}>
            <div className="modal-header">
              <div style={{ display: 'flex', flexDirection: 'column', gap: 4 }}>
                <h2 style={{ fontSize: 22, fontWeight: 800 }}>Launch Project Board</h2>
                <p style={{ fontSize: 13, color: 'var(--color-text-muted)' }}>
                  Organize your hackathon journey in one powerful dashboard.
                </p>
              </div>
              <button 
                className="btn-ghost" 
                style={{ fontSize: 20 }} 
                onClick={() => setShowCreate(false)}
              >
                ✕
              </button>
            </div>
            <form onSubmit={handleCreate}>
              <div className="modal-body">
                
                {/* Identity */}
                <div style={{ marginBottom: 32 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 18, color: 'var(--color-text)' }}>
                    <h3 style={{ fontSize: 13, fontWeight: 600, letterSpacing: '0.08em', fontFamily: 'JetBrains Mono, monospace' }}>PROJECT IDENTITY</h3>
                    <div style={{ flex: 1, height: 1, background: 'var(--color-border)' }} />
                  </div>
                  
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20 }}>
                    <div style={{ gridColumn: 'span 2' }}>
                      <label className="input-label">Board / Hackathon Name *</label>
                      <input
                        className="input-field"
                        placeholder="e.g., Team Nexus - SynapHack 3.0"
                        value={newBoard.name}
                        onChange={e => setNewBoard({ ...newBoard, name: e.target.value })}
                        required
                      />
                    </div>
                    <div>
                      <label className="input-label">Platform</label>
                      <input
                        className="input-field"
                        placeholder="Devfolio, Unstop, MLH..."
                        value={newBoard.platform}
                        onChange={e => setNewBoard({ ...newBoard, platform: e.target.value })}
                      />
                    </div>
                    <div>
                      <label className="input-label">Track / Theme</label>
                      <input
                        className="input-field"
                        placeholder="AI-ML, Fintech, Web3..."
                        value={newBoard.theme}
                        onChange={e => setNewBoard({ ...newBoard, theme: e.target.value })}
                      />
                    </div>
                  </div>
                </div>

                {/* Logistics */}
                <div style={{ marginBottom: 32 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 18, color: 'var(--color-text)' }}>
                    <h3 style={{ fontSize: 13, fontWeight: 600, letterSpacing: '0.08em', fontFamily: 'JetBrains Mono, monospace' }}>DATE & VENUE</h3>
                    <div style={{ flex: 1, height: 1, background: 'var(--color-border)' }} />
                  </div>
                  
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20 }}>
                    <div>
                      <label className="input-label">Event Date</label>
                      <input
                        type="date"
                        className="input-field"
                        value={newBoard.eventDate}
                        onChange={e => setNewBoard({ ...newBoard, eventDate: e.target.value })}
                      />
                    </div>
                    <div>
                      <label className="input-label">Location / Venue</label>
                      <input
                        className="input-field"
                        placeholder="College Name or 'Online'"
                        value={newBoard.venue}
                        onChange={e => setNewBoard({ ...newBoard, venue: e.target.value })}
                      />
                    </div>
                  </div>
                </div>

                {/* Team Info */}
                <div style={{ marginBottom: 32 }}>
                  <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 18, color: 'var(--color-text)' }}>
                    <h3 style={{ fontSize: 13, fontWeight: 600, letterSpacing: '0.08em', fontFamily: 'JetBrains Mono, monospace' }}>TEAM & VISION</h3>
                    <div style={{ flex: 1, height: 1, background: 'var(--color-border)' }} />
                  </div>
                  
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr', gap: 20 }}>
                    <div>
                      <label className="input-label">Project Team Name</label>
                      <input
                        className="input-field"
                        placeholder="Name of your squad"
                        value={newBoard.teamName}
                        onChange={e => setNewBoard({ ...newBoard, teamName: e.target.value })}
                      />
                    </div>
                    <div>
                      <label className="input-label">Project Idea / One-Liner</label>
                      <textarea
                        className="input-field"
                        placeholder="What problem are you solving?"
                        value={newBoard.projectIdea}
                        onChange={e => setNewBoard({ ...newBoard, projectIdea: e.target.value })}
                        rows={2}
                      />
                    </div>
                  </div>
                </div>

                {/* Visuals */}
                <div>
                   <label className="input-label">Personalize Board (Emoji & Color)</label>
                   <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
                      <input 
                        style={{ width: 50, height: 46, textAlign: 'center', fontSize: 24, padding: 0 }}
                        className="input-field"
                        value={newBoard.coverEmoji}
                        onChange={e => setNewBoard({...newBoard, coverEmoji: e.target.value})}
                      />
                      <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
                        {COLORS.map(color => (
                          <div
                            key={color}
                            onClick={() => setNewBoard({ ...newBoard, coverColor: color })}
                            style={{
                              width: 32, height: 32, borderRadius: 8, background: color, cursor: 'pointer',
                              border: newBoard.coverColor === color ? '2px solid white' : 'none',
                              boxShadow: newBoard.coverColor === color ? `0 0 10px ${color}80` : 'none'
                            }}
                          />
                        ))}
                      </div>
                   </div>
                </div>

              </div>
              <div className="modal-footer">
                <button type="button" className="btn-secondary" onClick={() => setShowCreate(false)}>
                  Cancel
                </button>
                <button type="submit" className="btn-primary" disabled={creating} style={{ minWidth: 160 }}>
                  {creating ? 'Launching...' : 'Launch Project Board'}
                </button>
              </div>
            </form>
          </div>
        </div>,
        document.body
      )}
    </div>
  );
}
