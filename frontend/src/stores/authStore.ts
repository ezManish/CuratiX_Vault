import { create } from 'zustand';
import {
  auth,
  googleProvider,
  signInWithPopup,
  signInWithEmailAndPassword,
  createUserWithEmailAndPassword,
  signOut as firebaseSignOut,
  onIdTokenChanged,
  type User,
} from '../lib/firebase';
import api from '../api/client';

interface DbUser {
  id: number;
  firebaseUid: string;
  email: string;
  displayName: string;
  photoUrl: string;
  admissionNo?: string;
  enrollmentNo?: string;
  phone?: string;
  githubUrl?: string;
  linkedinUrl?: string;
  bio?: string;
}

interface AuthState {
  user: User | null;
  dbUser: DbUser | null;
  loading: boolean;
  error: string | null;

  initialize: () => () => void;
  signInWithGoogle: () => Promise<void>;
  signInWithEmail: (email: string, password: string) => Promise<void>;
  signUpWithEmail: (email: string, password: string) => Promise<void>;
  signOut: () => Promise<void>;
  clearError: () => void;
  setDbUser: (user: DbUser) => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  user: null,
  dbUser: null,
  loading: true,
  error: null,

  initialize: () => {
    const unsubscribe = onIdTokenChanged(auth, async (user) => {
      if (user) {
        set({ user, loading: true });
        try {
          const { data } = await api.post('/api/auth/sync');
          set({ dbUser: data, loading: false, error: null });
        } catch (err) {
          set({ loading: false, error: 'Failed to sync user' });
        }
      } else {
        set({ user: null, dbUser: null, loading: false });
      }
    });
    return unsubscribe;
  },

  signInWithGoogle: async () => {
    set({ loading: true, error: null });
    try {
      await signInWithPopup(auth, googleProvider);
    } catch (err: any) {
      set({ loading: false, error: err.message || 'Google sign-in failed' });
    }
  },

  signInWithEmail: async (email, password) => {
    set({ loading: true, error: null });
    try {
      await signInWithEmailAndPassword(auth, email, password);
    } catch (err: any) {
      set({ loading: false, error: err.message || 'Sign-in failed' });
    }
  },

  signUpWithEmail: async (email, password) => {
    set({ loading: true, error: null });
    try {
      await createUserWithEmailAndPassword(auth, email, password);
    } catch (err: any) {
      set({ loading: false, error: err.message || 'Sign-up failed' });
    }
  },

  signOut: async () => {
    await firebaseSignOut(auth);
    set({ user: null, dbUser: null });
  },

  clearError: () => set({ error: null }),
  setDbUser: (dbUser) => set({ dbUser }),
}));
