import api from './client';

// ============ Boards ============
export const boardsApi = {
  list: () => api.get('/api/boards'),
  get: (id: number) => api.get(`/api/boards/${id}`),
  create: (data: Record<string, unknown>) => api.post('/api/boards', data),
  update: (id: number, data: Record<string, unknown>) => api.put(`/api/boards/${id}`, data),
  delete: (id: number) => api.delete(`/api/boards/${id}`),
  getMembers: (boardId: number) => api.get(`/api/boards/${boardId}/members`),
  changeRole: (boardId: number, userId: number, role: string) =>
    api.put(`/api/boards/${boardId}/members/${userId}/role`, { role }),
  removeMember: (boardId: number, userId: number) =>
    api.delete(`/api/boards/${boardId}/members/${userId}`),
  addMemberByEmail: (boardId: number, email: string, role: string) =>
    api.post(`/api/boards/${boardId}/members/email`, { email, role }),
};

// ============ Member Profiles ============
export const profilesApi = {
  list: (boardId: number) => api.get(`/api/boards/${boardId}/profiles`),
  add: (boardId: number, data: Record<string, unknown>) => api.post(`/api/boards/${boardId}/profiles`, data),
  update: (boardId: number, profileId: number, data: Record<string, unknown>) =>
    api.put(`/api/boards/${boardId}/profiles/${profileId}`, data),
  delete: (boardId: number, profileId: number) =>
    api.delete(`/api/boards/${boardId}/profiles/${profileId}`),
  uploadPhoto: (boardId: number, profileId: number, file: File) => {
    const formData = new FormData();
    formData.append('file', file);
    return api.post(`/api/boards/${boardId}/profiles/${profileId}/photo`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
};

// ============ Files ============
export const filesApi = {
  list: (boardId: number) => api.get(`/api/boards/${boardId}/files`),
  upload: (boardId: number, file: File, label?: string, fileType?: string) => {
    const formData = new FormData();
    formData.append('file', file);
    if (label) formData.append('label', label);
    if (fileType) formData.append('fileType', fileType);
    return api.post(`/api/boards/${boardId}/files`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' },
    });
  },
  delete: (boardId: number, fileId: number) =>
    api.delete(`/api/boards/${boardId}/files/${fileId}`),
};

// ============ Invites ============
export const invitesApi = {
  generate: (boardId: number, role: string, maxUses?: number) =>
    api.post(`/api/boards/${boardId}/invite/link`, { role, maxUses }),
  list: (boardId: number) => api.get(`/api/boards/${boardId}/invite/links`),
  revoke: (boardId: number, linkId: number) =>
    api.delete(`/api/boards/${boardId}/invite/links/${linkId}`),
  preview: (token: string) => api.get(`/api/invite/preview/${token}`),
  join: (token: string) => api.post(`/api/invite/join/${token}`),
};

// ============ CSV Export ============
export const exportApi = {
  csv: (boardId: number) =>
    api.get(`/api/boards/${boardId}/export/csv`, { responseType: 'blob' }),
};
// ============ Users ============
export const usersApi = {
  getMe: () => api.get('/api/users/me'),
  updateMe: (data: {
    displayName?: string;
    admissionNo?: string;
    enrollmentNo?: string;
    phone?: string;
    githubUrl?: string;
    linkedinUrl?: string;
    repoUrl?: string;
    bio?: string;
  }) => api.put('/api/users/me', data),
};

// ============ Invitations ============
export const invitationsApi = {
  getMy: () => api.get('/api/invitations/my'),
  respond: (id: number, accept: boolean) =>
    api.post(`/api/invitations/${id}/respond`, { accept }),
};
