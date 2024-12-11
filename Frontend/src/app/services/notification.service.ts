import { Injectable } from '@angular/core';

interface Notification {
  message: string;
  type: 'success' | 'error';
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  notifications: Notification[] = [];

  showNotification(message: string, type: 'success' | 'error'): void {
    const notification: Notification = { message, type };
    this.notifications.push(notification);

    setTimeout(() => {
      this.notifications = this.notifications.filter(n => n !== notification);
    }, 5000);
  }
}
