import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class InvitationService {
  private apiUrl = 'http://localhost:8080/api/invitations';

  constructor(private http: HttpClient) {}

  // Send an invite
  sendInvitation(projectId: number, email: string): Observable<any> {
    const body = { email: email };
    return this.http.post(\\/\\, body);
  }

  // Respond to invite
  respondToInvitation(invitationId: number, accept: boolean): Observable<any> {
    return this.http.put(\\/\?accept=\\, {});
  }

  // ✅ FIXED: Calls endpoint without email param (Backend extracts it from Token)
  getInvitations(): Observable<any[]> {
    return this.http.get<any[]>(this.apiUrl);
  }
}
