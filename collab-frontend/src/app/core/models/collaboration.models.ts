export interface InvitationDto {
  id?: number;
  email: string;
  status?: 'PENDING' | 'ACCEPTED' | 'REJECTED';
  sentAt?: string;
  projectId: number;
  projectName?: string;
  inviterName?: string;
}

export interface CollaboratorDto {
  id: number;
  userId: number;
  username: string;
  role: string; // 'OWNER' | 'EDITOR' | 'VIEWER'
  color?: string;
}
