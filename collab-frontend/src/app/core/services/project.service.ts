import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface ProjectDto {
  id?: number;
  name: string;
  description: string;
  role?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ProjectService {
  private apiUrl = 'http://localhost:8080/api/projects';
  private fileUrl = 'http://localhost:8080/api/files'; 

  constructor(private http: HttpClient) {}

  createProject(name: string, description: string, ownerId: number): Observable<ProjectDto> {
    const project = { name, description };
    return this.http.post<ProjectDto>(`${this.apiUrl}/${ownerId}`, project);
  }

  getProjects(ownerId: number): Observable<ProjectDto[]> {
    return this.http.get<ProjectDto[]>(`${this.apiUrl}/${ownerId}`);
  }

  getProjectDetails(id: number): Observable<ProjectDto> {
    return this.http.get<ProjectDto>(`${this.apiUrl}/details/${id}`);
  }

  deleteProject(projectId: number, userId: number): Observable<string> {
    const params = new HttpParams().set('userId', userId);
    return this.http.delete(`${this.apiUrl}/${projectId}`, { 
      params, 
      responseType: 'text' 
    });
  }

  // ✅ THIS WAS MISSING
  getFileContent(fileId: number): Observable<any> {
    return this.http.get<any>(`${this.fileUrl}/${fileId}`);
  }
}