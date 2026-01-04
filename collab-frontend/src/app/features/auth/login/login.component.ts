import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gray-900 text-white">
      <div class="bg-gray-800 p-8 rounded-lg shadow-2xl w-full max-w-md border border-gray-700">
        <h3 class="text-3xl font-bold text-center mb-6 text-blue-400">Welcome Back</h3>
        
        <form (ngSubmit)="onLogin()" class="space-y-4">
          <div>
            <label class="block text-sm font-medium text-gray-400 mb-1">Email</label>
            <input type="email" [(ngModel)]="email" name="email" 
              class="w-full p-3 rounded bg-gray-700 border border-gray-600 focus:border-blue-500 focus:outline-none text-white placeholder-gray-500" 
              placeholder="name@company.com" required>
          </div>
          
          <div>
            <label class="block text-sm font-medium text-gray-400 mb-1">Password</label>
            <input type="password" [(ngModel)]="password" name="password" 
              class="w-full p-3 rounded bg-gray-700 border border-gray-600 focus:border-blue-500 focus:outline-none text-white placeholder-gray-500" 
              placeholder="••••••••" required>
          </div>

          <div *ngIf="errorMessage" class="p-3 bg-red-900/50 border border-red-500 text-red-200 text-sm rounded">
            {{ errorMessage }}
          </div>

          <button type="submit" 
            class="w-full bg-blue-600 hover:bg-blue-500 text-white font-bold py-3 rounded transition-all duration-200 shadow-lg shadow-blue-900/50" 
            [disabled]="isLoading">
            {{ isLoading ? 'Signing in...' : 'Sign In' }}
          </button>
        </form>
        
        <div class="mt-6 text-center text-sm text-gray-400">
          Don't have an account? 
          <a routerLink="/register" class="text-blue-400 hover:text-blue-300 hover:underline">Register here</a>
        </div>
      </div>
    </div>
  `
})
export class LoginComponent {
  email = '';
  password = '';
  errorMessage = '';
  isLoading = false;

  constructor(private authService: AuthService, private router: Router) {}

  onLogin() {
    this.isLoading = true;
    this.errorMessage = '';

    this.authService.login(this.email, this.password).subscribe({
      next: (response: any) => {
        console.log('✅ Login Success! Navigating to dashboard...');
        this.isLoading = false;
        // FIXED: Navigates to the correct route defined in app.routes.ts
        this.router.navigate(['/dashboard']); 
      },
      error: (err: any) => {
        console.error('❌ Login Failed', err);
        this.isLoading = false;
        this.errorMessage = 'Invalid email or password';
      }
    });
  }
}