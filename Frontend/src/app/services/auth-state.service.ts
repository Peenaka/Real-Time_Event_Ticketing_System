import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { Customer, Vendor } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthStateService {
  private currentUserSubject = new BehaviorSubject<Customer | Vendor | null>(null);
  private userTypeSubject = new BehaviorSubject<'customer' | 'vendor' | null>(null);

  constructor() {
    this.loadUserFromStorage();
  }

  private loadUserFromStorage() {
    const userStr = localStorage.getItem('user');
    const userType = localStorage.getItem('userType') as 'customer' | 'vendor' | null;
    
    if (userStr && userType) {
      this.currentUserSubject.next(JSON.parse(userStr));
      this.userTypeSubject.next(userType);
    }
  }

  setCurrentUser(user: Customer | Vendor | null, userType: 'customer' | 'vendor' | null) {
    if (user && userType) {
      localStorage.setItem('user', JSON.stringify(user));
      localStorage.setItem('userType', userType);
    } else {
      localStorage.removeItem('user');
      localStorage.removeItem('userType');
    }
    
    this.currentUserSubject.next(user);
    this.userTypeSubject.next(userType);
  }

  getCurrentUser(): Observable<Customer | Vendor | null> {
    return this.currentUserSubject.asObservable();
  }

  getCurrentUserValue(): Customer | Vendor | null {
    return this.currentUserSubject.value;
  }

  getUserType(): Observable<'customer' | 'vendor' | null> {
    return this.userTypeSubject.asObservable();
  }

  getUserTypeValue(): 'customer' | 'vendor' | null {
    return this.userTypeSubject.value;
  }

  logout() {
    this.setCurrentUser(null, null);
  }

  isAuthenticated(): boolean {
    return !!this.currentUserSubject.value;
  }

  isVendor(): boolean {
    return this.userTypeSubject.value === 'vendor';
  }

  isCustomer(): boolean {
    return this.userTypeSubject.value === 'customer';
  }
}