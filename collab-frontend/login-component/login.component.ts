import { Component } from '@angular/core';
import { Router } from '@angular/router';
// import { AuthService } from '../services/auth.service'; // Uncomment and adjust path

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  username = '';
  password = '';
  
  // UI State variables
  errorMessage: string = '';
  isLoading: boolean = false;
  shakeError: boolean = false;

  // Inject your actual AuthService here
  constructor(private router: Router /*, private authService: AuthService */) {}

  onLogin() {
    this.errorMessage = '';
    this.shakeError = false;
    this.isLoading = true;

    // SIMULATION: Replace this block with your actual authService.login() call
    console.log('Attempting login...');
    
    // Simulating a network delay
    setTimeout(() => {
      this.isLoading = false;

      // MOCK LOGIC: Fail if username is empty or specific mock user
      if (!this.username || this.username === 'unknown') {
        this.handleError({ status: 404 });
      } else {
        // Success
        this.router.navigate(['/dashboard']); 
      }
    }, 1500);
  }

  // Helper to handle errors nicely
  private handleError(err: any) {
    if (err.status === 404) {
      this.errorMessage = "Account not found. Please register first.";
    } else if (err.status === 401) {
      this.errorMessage = "Incorrect password.";
    } else {
      this.errorMessage = "Something went wrong. Try again.";
    }
    this.triggerShake();
  }

  triggerShake() {
    this.shakeError = true;
    setTimeout(() => this.shakeError = false, 500);
  }
}
