import { Component } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { RegisterRequest } from '../../../core/models/auth.models';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-register',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gray-900 text-white">
      <div class="bg-gray-800 p-8 rounded-lg shadow-lg w-full max-w-md">
        <h2 class="text-3xl font-bold mb-6 text-center text-green-500">Create Account</h2>
        
        <form [formGroup]="registerForm" (ngSubmit)="onSubmit()">
          <div class="mb-4">
            <label class="block text-sm mb-2">Username</label>
            <input formControlName="username" type="text" 
              class="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:border-green-500 text-white">
          </div>

          <div class="mb-4">
            <label class="block text-sm mb-2">Email</label>
            <input formControlName="email" type="email" 
              class="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:border-green-500 text-white">
          </div>
          
          <div class="mb-6">
            <label class="block text-sm mb-2">Password</label>
            <input formControlName="password" type="password" 
              class="w-full p-2 rounded bg-gray-700 border border-gray-600 focus:outline-none focus:border-green-500 text-white">
          </div>

          <button type="submit" [disabled]="registerForm.invalid"
            class="w-full bg-green-600 hover:bg-green-700 text-white font-bold py-2 px-4 rounded transition disabled:opacity-50">
            Register
          </button>
        </form>

        <p class="mt-4 text-center text-sm text-gray-400">
          Already have an account? <a routerLink="/login" class="text-blue-400 hover:underline">Login</a>
        </p>
      </div>
    </div>
  `
})
export class RegisterComponent {
  registerForm = this.fb.group({
    username: ['', [Validators.required, Validators.minLength(3)]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required, Validators.minLength(6)]]
  });

  constructor(private fb: FormBuilder, private authService: AuthService, private router: Router) {}

  onSubmit() {
    if (this.registerForm.valid) {
      const request: RegisterRequest = this.registerForm.value;
      this.authService.register(request).subscribe({
        next: () => {
            alert('Registration successful! Please login.');
            this.router.navigate(['/login']);
        },
        error: (err: any) => alert('Registration failed.')
      });
    }
  }
}
