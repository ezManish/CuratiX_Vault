import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { boardsApi, filesApi, invitesApi, exportApi } from '../api/endpoints';
import { useAuthStore } from '../stores/authStore';
import {
  Users, Trophy, FolderOpen, Settings, LayoutDashboard, Plus, Search, Download,
  ExternalLink, Trash2, Upload, Link2, Copy, Check,
  Calendar, MapPin, Tag, FileText,
  Image, Code2, ArrowLeft, Phone
} from 'lucide-react';
import toast, { Toaster } from 'react-hot-toast';

type TabId = 'overview' | 'members' | 'files' | 'settings';

export default function BoardPage() {
  const { boardId } = useParams<{ boardId: string }>();
  const navigate = useNavigate();
  const { dbUser } = useAuthStore();
  const bid = parseInt(boardId || '0');

  const [activeTab, setActiveTab] = useState<TabId>('overview');
  const [board, setBoard] = useState<any>(null);
  const [members, setMembers] = useState<any[]>([]);
  const [files, setFiles] = useState<any[]>([]);
  const [userRole, setUserRole] = useState<string>('VIEWER');
  const [loading, setLoading] = useState(true);
  const [searchQuery, setSearchQuery] = useState('');
  const [errorMsg, setErrorMsg] = useState<string | null>(null);

  // Modal states
  const [showAddMember, setShowAddMember] = useState(false);
  const [showUploadFile, setShowUploadFile] = useState(false);


  const canEdit = userRole === 'OWNER' || userRole === 'EDITOR';
  const isOwner = userRole === 'OWNER';

  useEffect(() => {
    loadBoard();
  }, [bid]);

  const loadBoard = async () => {
    try {
      // Step 1: Load the board — this is critical. If it fails, show error.
      const boardRes = await boardsApi.get(bid);
      setBoard(boardRes.data);

      // Step 2: Load secondary data in parallel. Failures here are non-fatal.
      const [membersRes, filesRes] = await Promise.allSettled([
        boardsApi.getMembers(bid),
        filesApi.list(bid),
      ]);

      if (membersRes.status === 'fulfilled') {
        setMembers(membersRes.value.data);
        const myMembership = membersRes.value.data.find(
          (m: any) => m.user?.firebaseUid === dbUser?.firebaseUid
        );
        if (myMembership) setUserRole(myMembership.role);
      }
      if (filesRes.status === 'fulfilled') setFiles(filesRes.value.data);
    } catch (e: any) {
      const status = e?.response?.status;
      const msg = e?.response?.data?.error ?? e?.message ?? 'unknown';
      const detail = `HTTP ${status ?? 'ERR'}: ${msg} (bid=${bid})`;
      console.error('[loadBoard] FAILED:', detail, e);
      setErrorMsg(detail);
    } finally {
      setLoading(false);
    }
  };

  const handleExportCsv = async () => {
    try {
      const response = await exportApi.csv(bid);
      const blob = new Blob([response.data], { type: 'text/csv' });
      const url = URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `board-${bid}-members.csv`;
      a.click();
      URL.revokeObjectURL(url);
      toast.success('CSV downloaded!');
    } catch (e) {
      toast.error('Export failed');
    }
  };

  const handleDeleteBoard = async () => {
    if (!confirm('Are you sure you want to delete this board? This cannot be undone.')) return;
    try {
      await boardsApi.delete(bid);
      toast.success('Board deleted');
      navigate('/dashboard');
    } catch (e) {
      toast.error('Failed to delete board');
    }
  };

  const tabs: { id: TabId; label: string; icon: any }[] = [
    { id: 'overview', label: 'Overview', icon: LayoutDashboard },
    { id: 'members', label: 'Members', icon: Users },
    { id: 'files', label: 'Files', icon: FolderOpen },
    { id: 'settings', label: 'Settings', icon: Settings },
  ];

  if (loading) {
    return (
      <div className="animate-fade-in" style={{ padding: '40px 0' }}>
        <div className="skeleton" style={{ height: 40, width: 300, marginBottom: 24 }} />
        <div className="skeleton" style={{ height: 200, marginBottom: 16 }} />
        <div className="skeleton" style={{ height: 200 }} />
      </div>
    );
  }

  if (!board) {
    return (
      <div className="empty-state" style={{ marginTop: 100 }}>
        <h3>Board not found</h3>
        <p>This board may have been deleted or you don't have access.</p>
        {errorMsg && (
          <p style={{
            marginTop: 12, padding: '10px 16px',
            background: 'rgba(239,68,68,0.12)',
            border: '1px solid rgba(239,68,68,0.3)',
            borderRadius: 8,
            color: '#ef4444',
            fontFamily: 'monospace',
            fontSize: 13,
            maxWidth: 600,
            wordBreak: 'break-all',
          }}>
            ⚠️ {errorMsg}
          </p>
        )}
        <button className="btn-primary" onClick={() => navigate('/dashboard')} style={{ marginTop: 16 }}>
          Back to Dashboard
        </button>
      </div>
    );
  }

  return (
    <div className="animate-fade-in">
      <Toaster position="top-right" toastOptions={{
        style: { background: 'var(--color-bg-tertiary)', color: 'var(--color-text)', border: '1px solid var(--color-border)' }
      }} />

      {/* Board Header */}
      <div style={{ marginBottom: 28 }}>
        <button className="btn-ghost" onClick={() => navigate('/dashboard')} style={{ marginBottom: 12 }}>
          <ArrowLeft size={16} /> Back to Dashboard
        </button>
        <div style={{ display: 'flex', alignItems: 'center', gap: 16 }}>
          <div style={{
            width: 52, height: 52,
            borderRadius: 'var(--radius-md)',
            background: `${board.coverColor || '#6366f1'}20`,
            display: 'flex', alignItems: 'center', justifyContent: 'center',
            fontSize: 26,
            border: `1px solid ${board.coverColor || '#6366f1'}30`,
          }}>
            {board.coverEmoji || '📋'}
          </div>
          <div>
            <h1 style={{ fontSize: 26, fontWeight: 800 }}>{board.name}</h1>
            {board.description && (
              <p style={{ fontSize: 14, color: 'var(--color-text-muted)', marginTop: 2 }}>
                {board.description}
              </p>
            )}
          </div>
          <span className={`badge badge-${userRole.toLowerCase()}`} style={{ marginLeft: 'auto' }}>
            {userRole}
          </span>
        </div>
      </div>

      {/* Tabs */}
      <div className="tabs" style={{ marginBottom: 28 }}>
        {tabs.map(tab => (
          <button
            key={tab.id}
            className={`tab ${activeTab === tab.id ? 'active' : ''}`}
            onClick={() => setActiveTab(tab.id)}
          >
            <tab.icon size={14} style={{ display: 'inline', marginRight: 6, verticalAlign: '-2px' }} />
            {tab.label}
          </button>
        ))}
      </div>

      {/* Tab Content */}
      <div className="animate-fade-in" key={activeTab}>
        {activeTab === 'overview' && (
          <OverviewTab board={board} members={members} files={files} setActiveTab={setActiveTab} />
        )}
        {activeTab === 'members' && (
          <MembersTab
            boardId={bid} members={members} canEdit={canEdit} searchQuery={searchQuery}
            setSearchQuery={setSearchQuery} showAddMember={showAddMember} setShowAddMember={setShowAddMember}
            onExportCsv={handleExportCsv} onReload={loadBoard} dbUser={dbUser} 
          />
        )}
        {activeTab === 'files' && (
          <FilesTab
            boardId={bid} files={files} canEdit={canEdit}
            showUploadFile={showUploadFile} setShowUploadFile={setShowUploadFile}
            onReload={loadBoard}
          />
        )}
        {activeTab === 'settings' && (
          <SettingsTab
            boardId={bid} board={board} isOwner={isOwner}
            onDeleteBoard={handleDeleteBoard} onReload={loadBoard}
          />
        )}
      </div>
    </div>
  );
}

/* ==================== OVERVIEW TAB ==================== */
function OverviewTab({ board, members, files, setActiveTab }: any) {
  const stats = [
    { icon: Users, label: 'Members', value: members.length, color: '#6366f1', tab: 'members' as TabId },
    { icon: FolderOpen, label: 'Files', value: files.length, color: '#22c55e', tab: 'files' as TabId },
  ];

  const resultColors: Record<string, string> = {
    WINNER: '#4ade80',
    TOP_N: '#f59e0b',
    SHORTLISTED: '#f59e0b',
    PARTICIPATED: '#94a3b8',
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
      {/* Quick Stats Grid */}
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(240px, 1fr))', gap: 16 }}>
        {stats.map(stat => (
          <div
            key={stat.label}
            className="glass-card"
            onClick={() => setActiveTab(stat.tab)}
            style={{ padding: 24, cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 20 }}
          >
            <div style={{
              width: 52, height: 52, borderRadius: 16,
              background: `${stat.color}15`,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}>
              <stat.icon size={24} color={stat.color} />
            </div>
            <div>
              <div style={{ fontSize: 24, fontWeight: 800 }}>{stat.value}</div>
              <div style={{ fontSize: 13, color: 'var(--color-text-muted)' }}>{stat.label}</div>
            </div>
          </div>
        ))}

        {/* Result Badge Card */}
        {board.result && (
          <div className="glass-card" style={{ padding: 24, display: 'flex', alignItems: 'center', gap: 20 }}>
             <div style={{
              width: 52, height: 52, borderRadius: 16,
              background: `${resultColors[board.result] || '#94a3b8'}15`,
              display: 'flex', alignItems: 'center', justifyContent: 'center',
            }}>
              <Trophy size={24} color={resultColors[board.result] || '#94a3b8'} />
            </div>
            <div>
              <div style={{ 
                fontSize: 14, fontWeight: 800, 
                color: resultColors[board.result] || '#94a3b8',
                letterSpacing: '0.05em'
              }}>
                {board.result.replace('_', ' ')}
              </div>
              <div style={{ fontSize: 13, color: 'var(--color-text-muted)' }}>Project Result</div>
            </div>
          </div>
        )}
      </div>

      <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: 24 }}>
        {/* Left Column: Project Details */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          {/* Section 1: Problem & Solution */}
          <div className="glass-card" style={{ padding: 28 }}>
             <h3 style={{ fontSize: 14, fontWeight: 800, marginBottom: 20, color: 'var(--color-primary)', display: 'flex', alignItems: 'center', gap: 10 }}>
               <FileText size={18} /> PROJECT MISSION
             </h3>
             <div style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
                {board.problemStatement ? (
                  <div>
                    <label style={{ fontSize: 11, fontWeight: 700, color: 'var(--color-text-muted)', display: 'block', marginBottom: 6 }}>PROBLEM STATEMENT</label>
                    <p style={{ fontSize: 15, lineHeight: 1.6 }}>{board.problemStatement}</p>
                  </div>
                ) : (
                  <div style={{ fontStyle: 'italic', color: 'var(--color-text-muted)', fontSize: 14 }}>No problem statement provided.</div>
                )}

                {board.projectIdea && (
                  <div>
                    <label style={{ fontSize: 11, fontWeight: 700, color: 'var(--color-text-muted)', display: 'block', marginBottom: 6 }}>OUR SOLUTION / IDEA</label>
                    <p style={{ fontSize: 15, lineHeight: 1.6 }}>{board.projectIdea}</p>
                  </div>
                )}
             </div>
          </div>

          {/* Section 2: Links and Resources */}
          {(board.submissionUrl || board.repoUrl) && (
            <div className="glass-card" style={{ padding: 28 }}>
               <h3 style={{ fontSize: 14, fontWeight: 800, marginBottom: 20, color: '#10b981', display: 'flex', alignItems: 'center', gap: 10 }}>
                 <Link2 size={18} /> RESOURCES & LINKS
               </h3>
               <div style={{ display: 'flex', flexWrap: 'wrap', gap: 16 }}>
                 {board.submissionUrl && (
                   <a href={board.submissionUrl} target="_blank" rel="noreferrer" className="btn-secondary" style={{ flex: 1, minWidth: 200, justifyContent: 'center' }}>
                     <ExternalLink size={16} /> View Submission
                   </a>
                 )}
                 {board.repoUrl && (
                   <a href={board.repoUrl} target="_blank" rel="noreferrer" className="btn-secondary" style={{ flex: 1, minWidth: 200, justifyContent: 'center' }}>
                     <Code2 size={16} /> GitHub Repository
                   </a>
                 )}
               </div>
            </div>
          )}

          {/* Section 3: Notes */}
          {board.notes && (
            <div className="glass-card" style={{ padding: 28 }}>
               <h3 style={{ fontSize: 14, fontWeight: 800, marginBottom: 12, color: '#94a3b8' }}>INTERNAL NOTES</h3>
               <p style={{ fontSize: 14, color: 'var(--color-text-secondary)', whiteSpace: 'pre-wrap' }}>{board.notes}</p>
            </div>
          )}
        </div>

        {/* Right Column: Meta Info */}
        <div style={{ display: 'flex', flexDirection: 'column', gap: 24 }}>
          <div className="glass-card" style={{ padding: 24 }}>
            <h3 style={{ fontSize: 13, fontWeight: 800, marginBottom: 20, letterSpacing: '0.05em', color: 'var(--color-text-muted)' }}>HACKATHON CONTEXT</h3>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 18 }}>
               <div style={{ display: 'flex', gap: 12 }}>
                 <div style={{ color: 'var(--color-primary-light)' }}><Trophy size={16} /></div>
                 <div>
                   <div style={{ fontSize: 11, fontWeight: 700, color: 'var(--color-text-muted)', marginBottom: 2 }}>PLATFORM</div>
                   <div style={{ fontSize: 14, fontWeight: 600 }}>{board.platform || 'N/A'}</div>
                 </div>
               </div>

               <div style={{ display: 'flex', gap: 12 }}>
                 <div style={{ color: '#f59e0b' }}><Tag size={16} /></div>
                 <div>
                   <div style={{ fontSize: 11, fontWeight: 700, color: 'var(--color-text-muted)', marginBottom: 2 }}>THEME / TRACK</div>
                   <div style={{ fontSize: 14, fontWeight: 600 }}>{board.theme || 'Open Innovation'}</div>
                 </div>
               </div>

               <div style={{ display: 'flex', gap: 12 }}>
                 <div style={{ color: '#10b981' }}><Users size={16} /></div>
                 <div>
                   <div style={{ fontSize: 11, fontWeight: 700, color: 'var(--color-text-muted)', marginBottom: 2 }}>TEAM NAME</div>
                   <div style={{ fontSize: 14, fontWeight: 600 }}>{board.teamName || 'N/A'}</div>
                 </div>
               </div>

               <div style={{ display: 'flex', gap: 12 }}>
                 <div style={{ color: '#ec4899' }}><Calendar size={16} /></div>
                 <div>
                   <div style={{ fontSize: 11, fontWeight: 700, color: 'var(--color-text-muted)', marginBottom: 2 }}>EVENT DATE</div>
                   <div style={{ fontSize: 14, fontWeight: 600 }}>{board.eventDate || 'TBD'}</div>
                 </div>
               </div>

               <div style={{ display: 'flex', gap: 12 }}>
                 <div style={{ color: '#8b5cf6' }}><MapPin size={16} /></div>
                 <div>
                   <div style={{ fontSize: 11, fontWeight: 700, color: 'var(--color-text-muted)', marginBottom: 2 }}>VENUE</div>
                   <div style={{ fontSize: 14, fontWeight: 600 }}>{board.venue || 'Online / Remote'}</div>
                 </div>
               </div>
            </div>
          </div>
          
          {board.prize && (
            <div className="glass-card" style={{ 
              padding: 24, 
              background: 'linear-gradient(135deg, rgba(74, 222, 128, 0.1), transparent)',
              border: '1px solid rgba(74, 222, 128, 0.2)'
            }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 10, color: '#4ade80', marginBottom: 8 }}>
                <Trophy size={18} />
                <span style={{ fontSize: 12, fontWeight: 800 }}>AWARD WON</span>
              </div>
              <div style={{ fontSize: 18, fontWeight: 800 }}>{board.prize}</div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

/* ==================== MEMBERS TAB ==================== */
function MembersTab({ 
  boardId, members, canEdit, searchQuery, setSearchQuery, 
  showAddMember, setShowAddMember, onExportCsv, onReload, 
  dbUser
}: any) {
  const [formData, setFormData] = useState<any>({
    email: '',
    role: 'EDITOR',
  });
  const [saving, setSaving] = useState(false);
  const [removingIds, setRemovingIds] = useState<Set<number>>(new Set());

  const handleAdd = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      await boardsApi.addMemberByEmail(boardId, formData.email, formData.role);
      toast.success('Invitation sent! They will join once they accept.');
      setShowAddMember(false);
      setFormData({ email: '', role: 'EDITOR' });
    } catch (e: any) {
      const msg = e.response?.data?.message || e.message || 'Failed to add member';
      toast.error(msg);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async (userId: number) => {
    if (!userId) {
      toast.error("Cannot remove this member: Missing user data");
      return;
    }
    if (!confirm('Remove this member from the board?')) return;
    
    setRemovingIds(prev => new Set(prev).add(userId));
    try {
      await boardsApi.removeMember(boardId, userId);
      toast.success('Member removed');
      onReload();
    } catch (e: any) {
      toast.error(e.response?.data?.message || 'Failed to remove member');
    } finally {
      setRemovingIds(prev => {
        const next = new Set(prev);
        next.delete(userId);
        return next;
      });
    }
  };

  const handleCopyAllData = () => {
    const header = "Full Name,Admission No,Enrollment No,Email,Phone,GitHub,LinkedIn,Bio";
    const rows = members.map((m: any) => {
      const p = m.profile || {};
      const u = m.user || {};
      return [
        p.fullName || u.displayName || 'Anonymous User',
        p.admissionNo || 'N/A',
        p.enrollmentNo || 'N/A',
        p.email || u.email || 'N/A',
        p.phone || 'N/A',
        p.githubUrl || 'N/A',
        p.linkedinUrl || 'N/A',
        (p.bio || 'N/A').replace(/\n/g, ' ')
      ].map(v => `"${v}"`).join(',');
    });
    const csv = [header, ...rows].join('\n');
    navigator.clipboard.writeText(csv);
    toast.success('All member data copied to clipboard!');
  };

  const copyToClipboard = (text: string, label: string) => {
    if (!text || text === 'N/A') return;
    navigator.clipboard.writeText(text);
    toast.success(`${label} copied!`, { icon: '📋' });
  };

  const filtered = members.filter((m: any) => {
    if (!searchQuery) return true;
    const q = searchQuery.toLowerCase();
    return (
      m.user?.displayName?.toLowerCase().includes(q) ||
      m.user?.email?.toLowerCase().includes(q) ||
      m.role?.toLowerCase().includes(q)
    );
  });

  return (
    <div>
      {/* Toolbar */}
      <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 20, flexWrap: 'wrap' }}>
        <div style={{ position: 'relative', flex: 1, minWidth: 200 }}>
          <Search size={16} style={{ position: 'absolute', left: 12, top: '50%', transform: 'translateY(-50%)', color: 'var(--color-text-muted)' }} />
          <input
            className="input-field"
            placeholder="Search members..."
            value={searchQuery}
            onChange={e => setSearchQuery(e.target.value)}
            style={{ paddingLeft: 36 }}
          />
        </div>
        <button className="btn-secondary" onClick={onExportCsv}>
          <Download size={15} /> CSV
        </button>
        <button className="btn-secondary" onClick={handleCopyAllData}>
          <Copy size={15} /> Copy All
        </button>
        {canEdit && (
          <button className="btn-primary" onClick={() => setShowAddMember(true)}>
            <Plus size={16} /> Add Member
          </button>
        )}
      </div>

      {/* Member cards */}
      {filtered.length === 0 ? (
        <div className="empty-state">
          <Users size={56} />
          <h3>No members yet</h3>
          <p>Add team member profiles to get started.</p>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))', gap: 14 }}>
          {filtered.map((m: any) => (
            <div key={m.id} className="glass-card" style={{ padding: 20 }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginBottom: 12 }}>
                <div style={{
                  width: 40, height: 40, borderRadius: '50%',
                  background: 'linear-gradient(135deg, var(--color-primary), var(--color-accent))',
                  display: 'flex', alignItems: 'center', justifyContent: 'center',
                  color: 'white', fontWeight: 700, fontSize: 16,
                }}>
                  {(m.user?.displayName || m.user?.email || '?')[0].toUpperCase()}
                </div>
                <div style={{ flex: 1 }}>
                  <div style={{ fontWeight: 600, fontSize: 15 }}>{m.user?.displayName || m.user?.email}</div>
                  <div style={{ fontSize: 12, color: 'var(--color-text-muted)' }}>{m.user?.email}</div>
                </div>
                <span className={`badge badge-${m.role?.toLowerCase() || 'viewer'}`}>{m.role}</span>
              </div>
              
              <div style={{ marginTop: 12, padding: '10px 12px', background: 'rgba(255,255,255,0.03)', borderRadius: 12, border: '1px solid rgba(255,255,255,0.05)' }}>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 8, marginBottom: 12 }}>
                  <div style={{ fontSize: 11 }}>
                    <div style={{ color: 'var(--color-text-muted)', marginBottom: 2, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                      Admission No
                      {m.profile?.admissionNo && m.profile.admissionNo !== 'N/A' && (
                        <Copy size={10} className="cursor-pointer hover:text-indigo-400" onClick={() => copyToClipboard(m.profile.admissionNo, 'Admission No')} />
                      )}
                    </div>
                    <div style={{ fontWeight: 500 }}>{m.profile?.admissionNo || 'N/A'}</div>
                  </div>
                  <div style={{ fontSize: 11 }}>
                    <div style={{ color: 'var(--color-text-muted)', marginBottom: 2, display: 'flex', alignItems: 'center', justifyContent: 'space-between' }}>
                      Enrollment No
                      {m.profile?.enrollmentNo && m.profile.enrollmentNo !== 'N/A' && (
                        <Copy size={10} className="cursor-pointer hover:text-indigo-400" onClick={() => copyToClipboard(m.profile.enrollmentNo, 'Enrollment No')} />
                      )}
                    </div>
                    <div style={{ fontWeight: 500 }}>{m.profile?.enrollmentNo || 'N/A'}</div>
                  </div>
                </div>

                <div style={{ display: 'flex', flexWrap: 'wrap', gap: 10, marginBottom: 10 }}>
                  {m.profile?.phone && m.profile.phone !== 'N/A' && (
                    <div style={{ fontSize: 11, display: 'flex', alignItems: 'center', gap: 4, color: 'var(--color-text-muted)' }}>
                      <Phone size={11} /> {m.profile.phone}
                      <Copy size={10} className="ml-1 cursor-pointer hover:text-indigo-400" onClick={() => copyToClipboard(m.profile.phone, 'Phone number')} />
                    </div>
                  )}
                  {m.profile?.githubUrl && m.profile.githubUrl !== 'N/A' && (
                    <a href={m.profile.githubUrl} target="_blank" rel="noreferrer" style={{ fontSize: 11, color: 'var(--color-primary)', display: 'flex', alignItems: 'center', gap: 4 }}>
                      <ExternalLink size={11} /> GitHub
                    </a>
                  )}
                  {m.profile?.linkedinUrl && m.profile.linkedinUrl !== 'N/A' && (
                    <a href={m.profile.linkedinUrl} target="_blank" rel="noreferrer" style={{ fontSize: 11, color: 'var(--color-primary)', display: 'flex', alignItems: 'center', gap: 4 }}>
                      <ExternalLink size={11} /> LinkedIn
                    </a>
                  )}
                </div>

                {m.profile?.bio && m.profile.bio !== 'N/A' && (
                  <div style={{ fontSize: 11, color: 'var(--color-text-muted)', marginTop: 10, borderTop: '1px solid rgba(255,255,255,0.05)', paddingTop: 8, position: 'relative' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 4 }}>
                      <span style={{ fontSize: 10, fontWeight: 600, opacity: 0.6 }}>BIO</span>
                      <Copy size={10} className="cursor-pointer hover:text-indigo-400" onClick={() => copyToClipboard(m.profile.bio, 'Bio')} />
                    </div>
                    <div style={{ fontStyle: 'italic' }}>{m.profile.bio}</div>
                  </div>
                )}
              </div>
              
              <div style={{ display: 'flex', gap: 8, marginTop: 12, justifyContent: 'flex-end' }}>
                {canEdit && m.user?.id && m.user?.firebaseUid !== dbUser?.firebaseUid && (
                  <button 
                    className="btn-ghost" 
                    style={{ fontSize: 12, color: removingIds.has(m.user.id) ? 'var(--color-text-muted)' : '#ef4444' }} 
                    onClick={() => handleDelete(m.user.id)}
                    disabled={removingIds.has(m.user.id)}
                  >
                    <Trash2 size={13} /> {removingIds.has(m.user.id) ? 'Removing...' : 'Remove'}
                  </button>
                )}
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Add Member Modal */}
      {showAddMember && (
        <div className="modal-overlay" onClick={e => { if (e.target === e.currentTarget) setShowAddMember(false); }}>
          <div className="modal-content">
            <div className="modal-header">
              <h2>Invite Member</h2>
              <button className="btn-ghost" onClick={() => setShowAddMember(false)}>✕</button>
            </div>
            <form onSubmit={handleAdd}>
              <div className="modal-body" style={{ display: 'flex', flexDirection: 'column', gap: 20 }}>
                <p style={{ fontSize: 13, color: 'var(--color-text-muted)' }}>
                  Enter the member's email address. Their profile details will be automatically fetched from their global CuratiX account.
                </p>
                <div>
                  <label className="input-label">Email Address *</label>
                  <input 
                    className="input-field" 
                    type="email" 
                    required 
                    value={formData.email} 
                    onChange={e => setFormData({ ...formData, email: e.target.value })} 
                    placeholder="member@example.com" 
                  />
                </div>
                <div>
                  <label className="input-label">Role</label>
                  <select 
                    className="input-field" 
                    value={formData.role} 
                    onChange={e => setFormData({ ...formData, role: e.target.value })}
                  >
                    <option value="EDITOR">EDITOR (Can edit files/records)</option>
                    <option value="VIEWER">VIEWER (Read-only access)</option>
                  </select>
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn-secondary" onClick={() => setShowAddMember(false)}>Cancel</button>
                <button type="submit" className="btn-primary" disabled={saving}>
                  {saving ? 'Adding...' : 'Invite Member'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}


/* ==================== FILES TAB ==================== */
function FilesTab({ boardId, files, canEdit, showUploadFile, setShowUploadFile, onReload }: any) {
  const [file, setFile] = useState<File | null>(null);
  const [label, setLabel] = useState('');
  const [fileType, setFileType] = useState('OTHER');
  const [uploading, setUploading] = useState(false);

  const handleUpload = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!file) return;
    setUploading(true);
    try {
      await filesApi.upload(boardId, file, label || file.name, fileType);
      toast.success('File uploaded!');
      setShowUploadFile(false);
      setFile(null);
      setLabel('');
      onReload();
    } catch (e) {
      toast.error('Upload failed');
    } finally {
      setUploading(false);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm('Delete this file?')) return;
    try {
      await filesApi.delete(boardId, id);
      toast.success('File deleted');
      onReload();
    } catch (e) {
      toast.error('Failed to delete');
    }
  };

  const typeIcons: Record<string, any> = {
    PRESENTATION: FileText,
    DOCUMENTATION: FileText,
    DESIGN: Image,
    CODE: Code2,
    OTHER: FolderOpen,
  };

  const typeColors: Record<string, string> = {
    PRESENTATION: '#f97316',
    DOCUMENTATION: '#3b82f6',
    DESIGN: '#ec4899',
    CODE: '#22c55e',
    OTHER: '#6b7280',
  };

  return (
    <div>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: 20 }}>
        <h3 style={{ fontSize: 16, fontWeight: 700 }}>
          {files.length} File{files.length !== 1 ? 's' : ''}
        </h3>
        {canEdit && (
          <button className="btn-primary" onClick={() => setShowUploadFile(true)}>
            <Upload size={16} /> Upload File
          </button>
        )}
      </div>

      {files.length === 0 ? (
        <div className="empty-state">
          <FolderOpen size={56} />
          <h3>No files uploaded</h3>
          <p>Upload PPTs, docs, or images for your team.</p>
        </div>
      ) : (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(280px, 1fr))', gap: 14 }}>
          {files.map((f: any) => {
            const Icon = typeIcons[f.fileType] || FolderOpen;
            const color = typeColors[f.fileType] || '#6b7280';
            return (
              <div key={f.id} className="glass-card" style={{ padding: 20 }}>
                <div style={{ display: 'flex', alignItems: 'flex-start', gap: 12 }}>
                  <div style={{
                    width: 40, height: 40, borderRadius: 'var(--radius-sm)',
                    background: `${color}18`,
                    display: 'flex', alignItems: 'center', justifyContent: 'center',
                    flexShrink: 0,
                  }}>
                    <Icon size={18} color={color} />
                  </div>
                  <div style={{ flex: 1, minWidth: 0 }}>
                    <h4 style={{
                      fontSize: 14, fontWeight: 600, marginBottom: 2,
                      overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap',
                    }}>
                      {f.label || f.originalFilename}
                    </h4>
                    <div style={{ fontSize: 12, color: 'var(--color-text-muted)' }}>
                      {f.fileSizeKb ? `${(f.fileSizeKb / 1024).toFixed(1)} MB` : ''} · {f.fileType}
                    </div>
                  </div>
                </div>
                <div style={{ display: 'flex', gap: 8, marginTop: 14 }}>
                  <a href={f.cloudinaryUrl} target="_blank" rel="noopener noreferrer" className="btn-ghost" style={{ fontSize: 12 }}>
                    <Download size={13} /> Download
                  </a>
                  {canEdit && (
                    <button className="btn-ghost" style={{ fontSize: 12, color: '#ef4444' }} onClick={() => handleDelete(f.id)}>
                      <Trash2 size={13} />
                    </button>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      )}

      {/* Upload Modal */}
      {showUploadFile && (
        <div className="modal-overlay" onClick={e => { if (e.target === e.currentTarget) setShowUploadFile(false); }}>
          <div className="modal-content">
            <div className="modal-header">
              <h2>Upload File</h2>
              <button className="btn-ghost" onClick={() => setShowUploadFile(false)}>✕</button>
            </div>
            <form onSubmit={handleUpload}>
              <div className="modal-body" style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
                <div>
                  <label className="input-label">File *</label>
                  <input
                    type="file"
                    onChange={e => setFile(e.target.files?.[0] || null)}
                    required
                    className="input-field"
                    accept=".pdf,.ppt,.pptx,.doc,.docx,.png,.jpg,.jpeg,.mp4"
                    style={{ padding: 8 }}
                  />
                  <p style={{ fontSize: 11, color: 'var(--color-text-muted)', marginTop: 4 }}>
                    Max 25 MB · PDF, PPT, DOC, PNG, JPG, MP4
                  </p>
                </div>
                <div>
                  <label className="input-label">Label</label>
                  <input className="input-field" value={label} onChange={e => setLabel(e.target.value)} placeholder="e.g., Final Pitch Deck" />
                </div>
                <div>
                  <label className="input-label">File Type</label>
                  <select className="input-field" value={fileType} onChange={e => setFileType(e.target.value)}>
                    <option value="PRESENTATION">Presentation</option>
                    <option value="DOCUMENTATION">Documentation</option>
                    <option value="DESIGN">Design</option>
                    <option value="CODE">Code</option>
                    <option value="OTHER">Other</option>
                  </select>
                </div>
              </div>
              <div className="modal-footer">
                <button type="button" className="btn-secondary" onClick={() => setShowUploadFile(false)}>Cancel</button>
                <button type="submit" className="btn-primary" disabled={uploading}>
                  {uploading ? 'Uploading...' : 'Upload'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

/* ==================== SETTINGS TAB ==================== */
function SettingsTab({ boardId, board, isOwner, onDeleteBoard, onReload }: any) {
  const [inviteRole, setInviteRole] = useState('VIEWER');
  const [inviteLink, setInviteLink] = useState('');
  const [copied, setCopied] = useState(false);
  const [generating, setGenerating] = useState(false);
  const [saving, setSaving] = useState(false);

  const [formData, setFormData] = useState({
    name: board.name || '',
    description: board.description || '',
    coverColor: board.coverColor || '#6366f1',
    coverEmoji: board.coverEmoji || '📋',
    platform: board.platform || '',
    eventDate: board.eventDate || '',
    venue: board.venue || '',
    theme: board.theme || '',
    teamName: board.teamName || '',
    problemStatement: board.problemStatement || '',
    projectIdea: board.projectIdea || '',
    result: board.result || 'PARTICIPATED',
    prize: board.prize || '',
    submissionUrl: board.submissionUrl || '',
    repoUrl: board.repoUrl || '',
    notes: board.notes || ''
  });

  const handleUpdate = async (e: React.FormEvent) => {
    e.preventDefault();
    setSaving(true);
    try {
      await boardsApi.update(boardId, formData);
      toast.success('Project details updated!');
      onReload();
    } catch (e) {
      toast.error('Failed to update details');
    } finally {
      setSaving(false);
    }
  };

  const generateInviteLink = async () => {
    setGenerating(true);
    try {
      const { data } = await invitesApi.generate(boardId, inviteRole);
      const appUrl = import.meta.env.VITE_APP_URL || window.location.origin;
      setInviteLink(`${appUrl}/invite/${data.token}`);
      toast.success('Invite link generated!');
    } catch (e) {
      toast.error('Failed to generate link');
    } finally {
      setGenerating(false);
    }
  };

  const copyLink = () => {
    navigator.clipboard.writeText(inviteLink);
    setCopied(true);
    setTimeout(() => setCopied(false), 2000);
  };

  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 24, maxWidth: 800 }}>
      {/* Board/Project Details Form */}
      {isOwner && (
        <div className="glass-card" style={{ padding: 28 }}>
          <h3 style={{ fontSize: 18, fontWeight: 800, marginBottom: 24, color: 'var(--color-primary)', display: 'flex', alignItems: 'center', gap: 10 }}>
            <Settings size={20} /> Project Settings
          </h3>
          <form onSubmit={handleUpdate}>
             <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 20, marginBottom: 24 }}>
                <div style={{ gridColumn: 'span 2' }}>
                  <label className="input-label">Project Name *</label>
                  <input className="input-field" required value={formData.name} onChange={e => setFormData({ ...formData, name: e.target.value })} />
                </div>
                <div>
                  <label className="input-label">Platform</label>
                  <input className="input-field" value={formData.platform} onChange={e => setFormData({ ...formData, platform: e.target.value })} placeholder="Devfolio, Unstop..." />
                </div>
                <div>
                  <label className="input-label">Track / Theme</label>
                  <input className="input-field" value={formData.theme} onChange={e => setFormData({ ...formData, theme: e.target.value })} placeholder="AI, Fintech, etc." />
                </div>
                <div>
                  <label className="input-label">Event Date</label>
                  <input className="input-field" type="date" value={formData.eventDate} onChange={e => setFormData({ ...formData, eventDate: e.target.value })} />
                </div>
                <div>
                  <label className="input-label">Venue</label>
                  <input className="input-field" value={formData.venue} onChange={e => setFormData({ ...formData, venue: e.target.value })} placeholder="Location or 'Online'" />
                </div>
                <div>
                   <label className="input-label">Team Name</label>
                   <input className="input-field" value={formData.teamName} onChange={e => setFormData({ ...formData, teamName: e.target.value })} />
                </div>
                <div>
                  <label className="input-label">Result</label>
                  <select className="input-field" value={formData.result} onChange={e => setFormData({ ...formData, result: e.target.value })}>
                    <option value="PARTICIPATED">Participated</option>
                    <option value="SHORTLISTED">Shortlisted</option>
                    <option value="TOP_N">Top N</option>
                    <option value="WINNER">Winner</option>
                  </select>
                </div>
                <div style={{ gridColumn: 'span 2' }}>
                  <label className="input-label">Problem Statement</label>
                  <textarea className="input-field" rows={3} value={formData.problemStatement} onChange={e => setFormData({ ...formData, problemStatement: e.target.value })} placeholder="Describe the challenge..." />
                </div>
                <div style={{ gridColumn: 'span 2' }}>
                  <label className="input-label">Project Idea</label>
                  <textarea className="input-field" rows={3} value={formData.projectIdea} onChange={e => setFormData({ ...formData, projectIdea: e.target.value })} placeholder="Your innovative solution..." />
                </div>
                <div style={{ gridColumn: 'span 2' }}>
                   <label className="input-label">Prize Details</label>
                   <input className="input-field" value={formData.prize} onChange={e => setFormData({ ...formData, prize: e.target.value })} placeholder="What did you win?" />
                </div>
                <div>
                   <label className="input-label">Submission URL</label>
                   <input className="input-field" value={formData.submissionUrl} onChange={e => setFormData({ ...formData, submissionUrl: e.target.value })} placeholder="https://..." />
                </div>
                <div>
                   <label className="input-label">Repository URL</label>
                   <input className="input-field" value={formData.repoUrl} onChange={e => setFormData({ ...formData, repoUrl: e.target.value })} placeholder="https://github.com/..." />
                </div>
                <div style={{ gridColumn: 'span 2' }}>
                   <label className="input-label">Internal Notes</label>
                   <textarea className="input-field" rows={4} value={formData.notes} onChange={e => setFormData({ ...formData, notes: e.target.value })} placeholder="Private team notes..." />
                </div>
             </div>
             <button type="submit" className="btn-primary" disabled={saving}>
               {saving ? 'Updating...' : 'Save All Changes'}
             </button>
          </form>
        </div>
      )}

      {/* Invite Section */}
      <div className="glass-card" style={{ padding: 24 }}>
        <h3 style={{ fontSize: 16, fontWeight: 700, marginBottom: 16, display: 'flex', alignItems: 'center', gap: 8 }}>
          <Link2 size={18} /> Collaborators
        </h3>
        <p style={{ fontSize: 13, color: 'var(--color-text-muted)', marginBottom: 16 }}>
          Generate a shareable link to invite team members. The link will expire in 7 days.
        </p>
        <div style={{ display: 'flex', gap: 12, alignItems: 'flex-end', flexWrap: 'wrap' }}>
          <div>
            <label className="input-label">Invite as</label>
            <select
              className="input-field"
              value={inviteRole}
              onChange={e => setInviteRole(e.target.value)}
              style={{ width: 140 }}
            >
              <option value="VIEWER">Viewer</option>
              <option value="EDITOR">Editor</option>
            </select>
          </div>
          <button className="btn-primary" onClick={generateInviteLink} disabled={generating}>
            {generating ? 'Generate Link' : 'Generate Link'}
          </button>
        </div>

        {inviteLink && (
          <div style={{
            marginTop: 16, padding: 12,
            background: 'var(--color-surface)', borderRadius: 'var(--radius-sm)',
            border: '1px solid var(--color-border)',
            display: 'flex', alignItems: 'center', gap: 8,
          }}>
            <input
              className="input-field"
              value={inviteLink}
              readOnly
              onClick={e => (e.target as HTMLInputElement).select()}
              style={{ border: 'none', background: 'none', flex: 1, fontSize: 13 }}
            />
            <button className="btn-ghost" onClick={copyLink}>
              {copied ? <Check size={16} color="#22c55e" /> : <Copy size={16} />}
            </button>
          </div>
        )}
      </div>

      {/* Danger Zone */}
      {isOwner && (
        <div className="glass-card" style={{
          padding: 24,
          borderColor: 'rgba(239, 68, 68, 0.2)',
        }}>
          <h3 style={{ fontSize: 16, fontWeight: 700, marginBottom: 8, color: '#ef4444' }}>
            Danger Zone
          </h3>
          <p style={{ fontSize: 13, color: 'var(--color-text-muted)', marginBottom: 16 }}>
            Deleting this board is permanent and cannot be undone. All members and files will be lost.
          </p>
          <button className="btn-danger" onClick={onDeleteBoard}>
            <Trash2 size={15} /> Delete Board
          </button>
        </div>
      )}
    </div>
  );
}
