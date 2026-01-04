import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProjectFileDto, FileVersionDto } from '../models/file.models';

@Injectable({
  providedIn: 'root'
})
export class FileService {
  private apiUrl = 'http://localhost:8080/api/files'; 

  constructor(private http: HttpClient) {}

  // 1. Get Active Files
  getFilesByProject(projectId: number): Observable<ProjectFileDto[]> {
    return this.http.get<ProjectFileDto[]>(`${this.apiUrl}/project/${projectId}`);
  }

  // 2. Create File
  createFile(file: { name: string, projectId: number, content: string }): Observable<ProjectFileDto> {
    return this.http.post<ProjectFileDto>(`${this.apiUrl}`, { ...file, folderId: null });
  }

  // 3. Get File Content (For Editor)
  getFileContent(fileId: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${fileId}`);
  }

  // 4. Update File Content
  updateFile(fileId: number, content: string): Observable<ProjectFileDto> {
    return this.http.put<ProjectFileDto>(`${this.apiUrl}/${fileId}/update`, { content });
  }

  // 5. Delete File (Soft Delete)
  deleteFile(fileId: number): Observable<string> {
    return this.http.delete(`${this.apiUrl}/${fileId}`, { responseType: 'text' });
  }

  // ==========================================
  //  TRASH & HISTORY FEATURES (Required for Workspace)
  // ==========================================

  // 6. Get Deleted Files (Trash Bin)
  getDeletedFiles(projectId: number): Observable<ProjectFileDto[]> {
    return this.http.get<ProjectFileDto[]>(`${this.apiUrl}/project/${projectId}/trash`);
  }

  // 7. Restore File from Trash
  restoreFile(fileId: number): Observable<string> {
    return this.http.put(`${this.apiUrl}/${fileId}/restore`, {}, { responseType: 'text' });
  }

  // 8. Get Version History
  getFileVersions(fileId: number): Observable<FileVersionDto[]> {
    return this.http.get<FileVersionDto[]>(`${this.apiUrl}/${fileId}/versions`);
  }

  // 9. Restore Specific Version
  restoreVersionTo(fileId: number, versionId: number): Observable<string> {
    return this.http.put(`${this.apiUrl}/${fileId}/versions/${versionId}/restore`, {}, { responseType: 'text' });
  }
}