import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class CollaborationService {
  // Base URLs matching your Controllers
  private inviteUrl = 'http://localhost:8080/api/invitations';
  private collabUrl = 'http://localhost:8080/api/collaborators';

  constructor(private http: HttpClient) {}

  // 1. Get Collaborators (Matches CollaboratorController @GetMapping("/{projectId}"))
  getProjectCollaborators(projectId: number): Observable<any[]> {
    return this.http.get<any[]>(`${this.collabUrl}/${projectId}`);
  }

  // 2. Send Invite (Matches InvitationController @PostMapping("/{projectId}"))
  sendInvitation(projectId: number, email: string): Observable<any> {
    return this.http.post(`${this.inviteUrl}/${projectId}`, { inviteeEmail: email });
  }

  // 3. Get My Invitations (Matches InvitationController @GetMapping)
  // Note: No userId needed, backend extracts it from Token
  getPendingInvitations(): Observable<any[]> {
    return this.http.get<any[]>(`${this.inviteUrl}`);
  }

  // 4. Respond (Matches InvitationController @PutMapping("/{invitationId}"))
  respondToInvitation(invitationId: number, accept: boolean): Observable<any> {
    return this.http.put(`${this.inviteUrl}/${invitationId}`, {}, {
      params: { accept: accept }
    });
  }
}