import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { Ticket, TicketDto, TicketStats } from '../models/ticket.model';
import { AuthStateService } from './auth-state.service';

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private apiUrl = 'http://localhost:8080/ticket-system/api/tickets';

  constructor(
    private http: HttpClient,
    private authState: AuthStateService
  ) {}

  private addAuthParams(url: string): string {
    const user = this.authState.getCurrentUserValue();
    if (!user) {
      throw new Error('User must be authenticated');
    }

    const params = new HttpParams()
      .set('email', user.email)
      .set('password', user.password);
    return `${url}?${params.toString()}`;
  }

  createTicket(eventId: number, ticket: TicketDto): Observable<Ticket> {
    if (!this.authState.isVendor()) {
      throw new Error('Only vendors can create tickets');
    }

    const user = this.authState.getCurrentUserValue();
    const url = this.addAuthParams(`${this.apiUrl}/vendor/${user?.id}/event/${eventId}`);
    return this.http.post<any>(url, ticket).pipe(
      map(response => response.data)
    );
  }

  getAllTicketStats(): Observable<TicketStats[]> {
    const url = this.addAuthParams(`${this.apiUrl}/stats`);
    return this.http.get<any>(url).pipe(
      map(response => response.data)
    );
  }

  getTicketStats(eventId: number): Observable<TicketStats> {
    const url = this.addAuthParams(`${this.apiUrl}/event/${eventId}/stats`);
    return this.http.get<any>(url).pipe(
      map(response => ({
        eventId: response.data.eventId,
        eventName: response.data.eventName,
        totalTickets: response.data.totalTickets,
        availableTickets: response.data.availableTickets,
        soldTickets: response.data.soldTickets,
        maxCapacity: response.data.maxCapacity
      }))
    );
  }

  getAllTickets(): Observable<Ticket[]> {
    const url = this.addAuthParams(`${this.apiUrl}/all`);
    return this.http.get<any>(url).pipe(
      map(response => response.data)
    );
  }

  getTicketDetails(ticketId: number): Observable<Ticket> {
    const url = this.addAuthParams(`${this.apiUrl}/${ticketId}`);
    return this.http.get<any>(url).pipe(
      map(response => response.data)
    );
  }
 

  purchaseTicket(eventId: number): Observable<Ticket> {
    if (!this.authState.isCustomer()) {
      throw new Error('Only customers can purchase tickets');
    }
    

    const user = this.authState.getCurrentUserValue();
    console.log(user);
    const url = `${this.apiUrl}/event/${user?.id}/purchase/${eventId}`;
    return this.http.post<any>(url, {}).pipe(
      map(response => response.data)
    );
  }
}
