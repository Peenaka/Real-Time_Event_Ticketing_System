import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, retry } from 'rxjs/operators';
import { LoginDto, CustomerRegistrationDto, VendorRegistrationDto } from '../models/auth.model';
import { Customer, Vendor } from '../models/user.model';
import { ApiResponse } from '../models/response.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_URL = environment.apiUrl + '/auth';
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept': 'application/json'
    }),
    withCredentials: true // Important for CORS with credentials
  };

  constructor(private http: HttpClient) {}

  private handleError(error: HttpErrorResponse) {
    let errorMessage = 'An error occurred';
    
    if (error.error instanceof ErrorEvent) {
      // Client-side error
      errorMessage = error.error.message;
    } else {
      // Server-side error
      if (error.status === 403) {
        errorMessage = 'Access forbidden. Please check your credentials.';
      } else if (error.status === 401) {
        errorMessage = 'Unauthorized access. Please login again.';
      } else if (error.error?.message) {
        errorMessage = error.error.message;
      } else {
        errorMessage = `Error Code: ${error.status}\nMessage: ${error.message}`;
      }
    }
    
    return throwError(() => errorMessage);
  }

  registerCustomer(dto: CustomerRegistrationDto): Observable<ApiResponse<Customer>> {
    return this.http.post<ApiResponse<Customer>>(
      `${this.API_URL}/customer/register`, 
      dto, 
      this.httpOptions
    ).pipe(
      retry(1),
      catchError(this.handleError)
    );
  }

  registerVendor(dto: VendorRegistrationDto): Observable<ApiResponse<Vendor>> {
    return this.http.post<ApiResponse<Vendor>>(
      `${this.API_URL}/vendor/register`, 
      dto, 
      this.httpOptions
    ).pipe(
      retry(1),
      catchError(this.handleError)
    );
  }

  loginCustomer(dto: LoginDto): Observable<ApiResponse<Customer>> {
    return this.http.post<ApiResponse<Customer>>(
      `${this.API_URL}/customer/login`, 
      dto, 
      this.httpOptions
    ).pipe(
      catchError(this.handleError)
    );
  }

  loginVendor(dto: LoginDto): Observable<ApiResponse<Vendor>> {
    return this.http.post<ApiResponse<Vendor>>(
      `${this.API_URL}/vendor/login`, 
      dto, 
      this.httpOptions
    ).pipe(
      catchError(this.handleError)
    );
  }
}