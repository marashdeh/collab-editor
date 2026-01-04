import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';
import { DiscussionDto, CommentDto } from '../models/discussion.models';

@Injectable({
  providedIn: 'root'
})
export class DiscussionService {
  private apiUrl = `${environment.apiUrl}/discussions`;
  private commentUrl = `${environment.apiUrl}/comments`;

  constructor(private http: HttpClient) {}

  // --- DISCUSSIONS ---

  getDiscussionsByFile(fileId: number): Observable<DiscussionDto[]> {
    return this.http.get<DiscussionDto[]>(`${this.apiUrl}/${fileId}`);
  }

  createDiscussion(fileId: number, userId: number, topic: string): Observable<DiscussionDto> {
    const params = new HttpParams().set('topic', topic);
    return this.http.post<DiscussionDto>(`${this.apiUrl}/${fileId}/${userId}`, {}, { params });
  }

  // --- COMMENTS ---

  // GET /api/comments/{discussionId}
  getComments(discussionId: number): Observable<CommentDto[]> {
    return this.http.get<CommentDto[]>(`${this.commentUrl}/${discussionId}`);
  }

  // POST /api/comments/{discussionId}/{userId}?content=...
  addComment(discussionId: number, userId: number, content: string): Observable<CommentDto> {
    const params = new HttpParams().set('content', content);
    return this.http.post<CommentDto>(`${this.commentUrl}/${discussionId}/${userId}`, {}, { params });
  }
}
