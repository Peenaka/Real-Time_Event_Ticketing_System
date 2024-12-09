import { Component } from '@angular/core';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../../services/auth.service';
import { Observable } from 'rxjs';
import { Customer, Vendor } from '../../../models/user.model';
import { ApiResponse } from '../../../models/response.model';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css'],
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink]
})
export class RegisterComponent {
  registerForm: FormGroup;
  userType: 'customer' | 'vendor' = 'customer';
  error: string = '';
  isLoading: boolean = false;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {
    this.registerForm = this.fb.group({
      name: ['', [Validators.required]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      isVIP: [false],
      companyName: ['']
    });
  }

  onUserTypeChange() {
    if (this.userType === 'customer') {
      this.registerForm.get('companyName')?.clearValidators();
      this.registerForm.get('isVIP')?.enable();
    } else {
      this.registerForm.get('companyName')?.setValidators(Validators.required);
      this.registerForm.get('isVIP')?.disable();
    }
    this.registerForm.get('companyName')?.updateValueAndValidity();
  }

  onSubmit() {
    if (this.registerForm.valid && !this.isLoading) {
      this.isLoading = true;
      this.error = '';
      const registerDto = this.registerForm.value;
      let registerObservable: Observable<ApiResponse<Customer | Vendor>>;

      if (this.userType === 'customer') {
        registerObservable = this.authService.registerCustomer(registerDto);
      } else {
        registerObservable = this.authService.registerVendor(registerDto);
      }

      registerObservable.subscribe({
        next: (response) => {
          if (response.success) {
            this.router.navigate(['/login']);
          } else {
            this.error = response.message;
          }
        },
        error: (error) => {
          this.error = error;
        },
        complete: () => {
          this.isLoading = false;
        }
      });
    }
  }
}