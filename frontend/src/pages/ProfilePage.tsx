import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuthStore } from '../stores/authStore';
import { usersApi } from '../api/endpoints';
import { 
  User, 
  Mail, 
  Phone, 
  Globe, 
  ExternalLink, 
  Link2, 
  FileText, 
  IdCard, 
  ArrowLeft,
  Loader2
} from 'lucide-react';
import toast from 'react-hot-toast';

export default function ProfilePage() {
  const { dbUser, setDbUser } = useAuthStore();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [initialLoading, setInitialLoading] = useState(true);

  const [formData, setFormData] = useState({
    displayName: '',
    admissionNo: '',
    enrollmentNo: '',
    phone: '',
    githubUrl: '',
    linkedinUrl: '',
    bio: '',
  });

  useEffect(() => {
    fetchProfile();
  }, []);

  const fetchProfile = async () => {
    try {
      const { data } = await usersApi.getMe();
      setFormData({
        displayName: data.displayName || '',
        admissionNo: data.admissionNo || '',
        enrollmentNo: data.enrollmentNo || '',
        phone: data.phone || '',
        githubUrl: data.githubUrl || '',
        linkedinUrl: data.linkedinUrl || '',
        bio: data.bio || '',
      });
    } catch (err) {
      toast.error('Failed to load profile');
    } finally {
      setInitialLoading(false);
    }
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true);
    try {
      const { data } = await usersApi.updateMe(formData);
      setDbUser(data);
      toast.success('Profile updated successfully!');
      // Update local dbUser if needed, or just let the next sync handle it
    } catch (err) {
      toast.error('Failed to update profile');
    } finally {
      setLoading(false);
    }
  };

  if (initialLoading) {
    return (
      <div className="flex items-center justify-center min-h-[60vh]">
        <Loader2 className="w-8 h-8 animate-spin text-indigo-500" />
      </div>
    );
  }

  return (
    <div className="max-w-4xl mx-auto py-8 px-4 animate-fade-in text-white">
      <button 
        onClick={() => navigate('/dashboard')}
        className="flex items-center gap-2 text-indigo-400 hover:text-indigo-300 transition-colors group"
        style={{ marginBottom: 32 }}
      >
        <ArrowLeft size={20} className="group-hover:-translate-x-1 transition-transform" />
        Back to Dashboard
      </button>

      <div className="flex flex-col md:flex-row gap-8 items-center" style={{ marginBottom: 48 }}>
        <div className="relative group" style={{ marginTop: 8 }}>
          {/* Glowing border ring */}
          <div style={{
            padding: 3,
            borderRadius: 20,
            background: 'linear-gradient(135deg, rgba(255,255,255,0.2) 0%, rgba(255,255,255,0.04) 50%, rgba(255,255,255,0.15) 100%)',
            boxShadow: '0 0 0 1px rgba(255,255,255,0.08)',
          }}>
            <div style={{ width: 120, height: 120, borderRadius: 16, overflow: 'hidden' }}>
              <img 
                src={dbUser?.photoUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(formData.displayName)}&background=18181b&color=fff`} 
                alt="Profile" 
                style={{ width: '100%', height: '100%', objectFit: 'cover', display: 'block' }}
              />
            </div>
          </div>
        </div>

        <div className="flex-1">
          <h1 className="text-3xl font-bold mb-2 tracking-tight text-text">
            User Settings
          </h1>
          <p className="text-slate-400 max-w-lg">
            This is your global profile. Details you provide here will be automatically sync'd to team boards when you join.
          </p>
        </div>
      </div>

      <form onSubmit={handleSubmit} style={{ display: 'flex', flexDirection: 'column', gap: 40 }}>
        {/* Basic Info */}
        <div className="card" style={{ padding: 40, display: 'flex', flexDirection: 'column', gap: 32 }}>
          <h2 className="text-lg font-bold flex items-center gap-2 mb-4 text-text">
            <User size={18} className="text-text-secondary" />
            General Information
          </h2>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-2">
              <label className="text-sm font-medium text-slate-400 ml-1">Full Name</label>
              <input 
                type="text"
                required
                placeholder="e.g. John Doe"
                className="input-field"
                value={formData.displayName}
                onChange={e => setFormData({...formData, displayName: e.target.value})}
              />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium text-slate-400 ml-1">Email Address</label>
              <div className="input-field opacity-60 flex items-center gap-2 cursor-not-allowed">
                <Mail size={16} />
                {dbUser?.email}
              </div>
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium text-slate-400 ml-1 flex items-center gap-1">
                <IdCard size={14} /> Admission No
              </label>
              <input 
                type="text"
                placeholder="e.g. ADM202301"
                className="input-field"
                value={formData.admissionNo}
                onChange={e => setFormData({...formData, admissionNo: e.target.value})}
              />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium text-slate-400 ml-1 flex items-center gap-1">
                <IdCard size={14} /> Enrollment No
              </label>
              <input 
                type="text"
                placeholder="e.g. ENR202301"
                className="input-field"
                value={formData.enrollmentNo}
                onChange={e => setFormData({...formData, enrollmentNo: e.target.value})}
              />
            </div>
          </div>
        </div>

        {/* Contact & Professional */}
        <div className="card" style={{ padding: 40, display: 'flex', flexDirection: 'column', gap: 32 }}>
          <h2 className="text-lg font-bold flex items-center gap-2 mb-4 text-text">
            <Link2 size={18} className="text-text-secondary" />
            Links & Reach
          </h2>
          
          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            <div className="space-y-2">
              <label className="text-sm font-medium text-slate-400 ml-1 flex items-center gap-2">
                <Phone size={14} /> Phone Number
              </label>
              <input 
                type="tel"
                placeholder="e.g. +1 234 567 890"
                className="input-field"
                value={formData.phone}
                onChange={e => setFormData({...formData, phone: e.target.value})}
              />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium text-slate-400 ml-1 flex items-center gap-2">
                <Globe size={14} /> GitHub Profile
              </label>
              <input 
                type="url"
                placeholder="https://github.com/your-username"
                className="input-field"
                value={formData.githubUrl}
                onChange={e => setFormData({...formData, githubUrl: e.target.value})}
              />
            </div>
            <div className="space-y-2">
              <label className="text-sm font-medium text-slate-400 ml-1 flex items-center gap-2">
                <ExternalLink size={14} /> LinkedIn Profile
              </label>
              <input 
                type="url"
                placeholder="https://linkedin.com/in/your-profile"
                className="input-field"
                value={formData.linkedinUrl}
                onChange={e => setFormData({...formData, linkedinUrl: e.target.value})}
              />
            </div>
          </div>
        </div>

        {/* Bio */}
        <div className="card" style={{ padding: 40, display: 'flex', flexDirection: 'column', gap: 32 }}>
          <h2 className="text-lg font-bold flex items-center gap-2 mb-4 text-text">
            <FileText size={18} className="text-text-secondary" />
            Biography
          </h2>
          <div className="space-y-2">
            <label className="text-sm font-medium text-slate-400 ml-1">About You</label>
            <textarea 
              rows={4}
              placeholder="Tell us about yourself..."
              className="input-field resize-none min-h-[120px]"
              value={formData.bio}
              onChange={e => setFormData({...formData, bio: e.target.value})}
            />
          </div>
        </div>

        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'flex-end', gap: 12, paddingBottom: 48 }}>
          <button 
            type="button"
            onClick={() => navigate('/dashboard')}
            className="btn-secondary"
          >
            Cancel
          </button>
          <button 
            type="submit"
            disabled={loading}
            className="btn-primary"
            style={{ padding: '10px 24px', fontSize: 14 }}
          >
            {loading ? <Loader2 className="w-4 h-4 animate-spin" /> : 'Save Changes'}
          </button>
        </div>
      </form>
    </div>
  );
}
