import { Injectable } from '@angular/core';
import { HttpParams } from '@angular/common/http';
import { AuthStateService } from './auth-state.service';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  constructor(private authState: AuthStateService) {}

  protected addAuthParams(url: string): string {
    const user = this.authState.getCurrentUserValue();
    if (!user) {
      throw new Error('User must be authenticated');
    }

    const params = new HttpParams()
      .set('email', user.email)
      .set('password', user.password);
    return `${url}?${params.toString()}`;
  }

  protected handleError(error: any): string {
    console.error('API Error:', error);
    if (error.error?.message) {
      return error.error.message;
    }
    return 'An unexpected error occurred. Please try again.';
  }
}