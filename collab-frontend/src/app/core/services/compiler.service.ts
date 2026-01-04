import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

export interface ExecuteResponse {
  output: string;
  isError: boolean;
}

@Injectable({
  providedIn: 'root'
})
export class CompilerService {
  private apiUrl = `${environment.apiUrl}/compiler/run`;

  constructor(private http: HttpClient) {}

  run(language: string, code: string): Observable<ExecuteResponse> {
    // Map file extensions to backend language names
    let lang = 'java'; 
    if (language.includes('py')) lang = 'python';
    if (language.includes('cpp') || language.includes('c++') || language.includes('c')) lang = 'cpp';
    
    return this.http.post<ExecuteResponse>(this.apiUrl, { language: lang, code });
  }
}
