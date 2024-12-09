import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { Observable } from 'rxjs';
import { ApiResponse } from '../../../models/response.model';
import { Customer, Vendor } from '../../../models/user.model';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink]
})
export class LoginComponent {
  loginForm: FormGroup;
  userType: 'customer' | 'vendor' = 'customer';
  error: string = '';
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

onSubmit() {
  if (this.loginForm.valid && !this.isLoading) {
    this.isLoading = true;
    this.error = '';
    const loginDto = this.loginForm.value;
    let loginObservable: Observable<ApiResponse<Customer | Vendor>>;

    if (this.userType === 'customer') {
      loginObservable = this.authService.loginCustomer(loginDto);
    } else {
      loginObservable = this.authService.loginVendor(loginDto);
    }

    loginObservable.subscribe({
      next: (response) => {
        if (response.success) {
          // Save user data in local storage or session storage
          localStorage.setItem('user', JSON.stringify(response.data));
          localStorage.setItem('userType', this.userType);

          // Redirect to the home page
          this.router.navigate(['/home']).then(() => {
            console.log('Redirected to /home successfully');
          }).catch((error) => {
            console.error('Navigation error:', error);
          });
        } else {
          this.error = response.message;
        }
      },
      error: (error) => {
        this.error = 'Login failed. Please try again.';
        console.error('Login error:', error);
      },
      complete: () => {
        this.isLoading = false;
      }
    });
  }
}
}