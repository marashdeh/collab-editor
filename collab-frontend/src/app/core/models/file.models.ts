export interface ProjectFileDto {
  id?: number;
  name: string;
  content?: string;
  projectId: number;
  folderId?: number;
  path?: string;
}

export interface ProjectFileContentDto {
  id: number;
  name: string;
  content: string;
}

// ✅ NEW: Version Model
export interface FileVersionDto {
  id: number;
  versionNumber: number;
  editedAt: string;
  content: string;
}
