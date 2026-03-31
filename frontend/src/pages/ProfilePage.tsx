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
  CheckCircle2, 
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
        className="flex items-center gap-2 text-indigo-400 hover:text-indigo-300 transition-colors mb-8 group"
      >
        <ArrowLeft size={20} className="group-hover:-translate-x-1 transition-transform" />
        Back to Dashboard
      </button>

      <div className="flex flex-col md:flex-row gap-8 items-start mb-12">
        <div className="relative group">
          <div className="w-32 h-32 rounded-3xl overflow-hidden glass border-2 border-white/20 shadow-2xl relative">
            <img 
              src={dbUser?.photoUrl || `https://ui-avatars.com/api/?name=${encodeURIComponent(formData.displayName)}&background=6366f1&color=fff`} 
              alt="Profile" 
              className="w-full h-full object-cover"
            />
          </div>
          <div className="absolute -bottom-2 -right-2 bg-indigo-500 p-2 rounded-xl shadow-lg border-2 border-white/10">
            <CheckCircle2 size={18} className="text-white" />
          </div>
        </div>

        <div className="flex-1">
          <h1 className="text-4xl font-extrabold mb-2 tracking-tight">
            User <span className="text-transparent bg-clip-text bg-gradient-to-r from-indigo-400 to-purple-400">Settings</span>
          </h1>
          <p className="text-slate-400 max-w-lg">
            This is your global profile. Details you provide here will be automatically sync'd to team boards when you join.
          </p>
        </div>
      </div>

      <form onSubmit={handleSubmit} className="space-y-8">
        {/* Basic Info */}
        <div className="glass p-8 rounded-[2rem] border border-white/10 space-y-6">
          <h2 className="text-xl font-bold flex items-center gap-2 mb-4">
            <User size={20} className="text-indigo-400" />
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
        <div className="glass p-8 rounded-[2rem] border border-white/10 space-y-6">
          <h2 className="text-xl font-bold flex items-center gap-2 mb-4">
            <Link2 size={20} className="text-indigo-400" />
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
        <div className="glass p-8 rounded-[2rem] border border-white/10 space-y-6">
          <h2 className="text-xl font-bold flex items-center gap-2 mb-4">
            <FileText size={20} className="text-indigo-400" />
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

        <div className="flex items-center justify-end gap-4 pb-12">
          <button 
            type="button"
            onClick={() => navigate('/dashboard')}
            className="px-8 py-3 rounded-2xl bg-white/5 hover:bg-white/10 border border-white/10 transition-all font-medium"
          >
            Cancel
          </button>
          <button 
            type="submit"
            disabled={loading}
            className="btn-primary px-12 py-3 rounded-2xl flex items-center gap-2 shadow-xl hover:shadow-indigo-500/20 disabled:opacity-50 disabled:cursor-not-allowed"
          >
            {loading ? <Loader2 className="w-5 h-5 animate-spin" /> : 'Save Changes'}
          </button>
        </div>
      </form>

      {/* Global CSS for input-field if not exists */}
      <style>{`
        .input-field {
          width: 100%;
          background: rgba(255, 255, 255, 0.05);
          border: 1px solid rgba(255, 255, 255, 0.1);
          border-radius: 1rem;
          padding: 0.75rem 1rem;
          color: white;
          transition: all 0.2s;
          outline: none;
        }
        .input-field:focus {
          background: rgba(255, 255, 255, 0.08);
          border-color: rgba(99, 102, 241, 0.5);
          box-shadow: 0 0 0 4px rgba(99, 102, 241, 0.1);
        }
        .glass {
          background: rgba(255, 255, 255, 0.03);
          backdrop-filter: blur(12px);
          -webkit-backdrop-filter: blur(12px);
        }
      `}</style>
    </div>
  );
}
