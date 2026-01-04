import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
// If this import fails, remove it and use the hardcoded string below for now
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  // Use environment or fallback to localhost directly
  private apiUrl = 'http://localhost:8080/api/auth';

  constructor(private http: HttpClient, private router: Router) {}

  login(email: string, password: string): Observable<any> {
    // FIXED: Uses backticks (`) correctly
    return this.http.post<any>(`${this.apiUrl}/login`, { email, password }).pipe(
      tap(response => {
        if (response && response.token) {
          localStorage.setItem('token', response.token);
          if (response.email) localStorage.setItem('userEmail', response.email);
          if (response.id) localStorage.setItem('userId', response.id);
        }
      })
    );
  }

  register(user: any): Observable<any> {
    // FIXED: Uses backticks (`) correctly
    return this.http.post(`${this.apiUrl}/register`, user);
  }

  logout() {
    localStorage.clear();
    this.router.navigate(['/login']);
  }

  getToken(): string | null { return localStorage.getItem('token'); }
  getEmail(): string { return localStorage.getItem('userEmail') || ''; }
  
  // FIXED: Added missing method
  getUserId(): string | null { return localStorage.getItem('userId'); }
  
  isAuthenticated(): boolean { return !!this.getToken(); }
}