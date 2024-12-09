import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Ticket, TicketDto, TicketStats } from '../models/ticket.model';

@Injectable({
  providedIn: 'root'
})
export class TicketService {
  private apiUrl = 'http://localhost:8080/ticket-system/api/tickets';

  constructor(private http: HttpClient) {}

  createTicket(vendorId: number, eventId: number, ticket: TicketDto): Observable<Ticket> {
    return this.http.post<Ticket>(`${this.apiUrl}/vendor/${vendorId}/event/${eventId}`, ticket);
  }

  getAllTicketStats(): Observable<TicketStats[]> {
    return this.http.get<TicketStats[]>(`${this.apiUrl}/stats`);
  }

  getTicketStats(eventId: number): Observable<TicketStats> {
    return this.http.get<TicketStats>(`${this.apiUrl}/event/${eventId}/stats`);
  }

  getAllTickets(): Observable<Ticket[]> {
    return this.http.get<Ticket[]>(`${this.apiUrl}`);
  }

  getTicketDetails(ticketId: number): Observable<Ticket> {
    return this.http.get<Ticket>(`${this.apiUrl}/${ticketId}`);
  }

  purchaseTicket(eventId: number, customerId: number): Observable<Ticket> {
    return this.http.post<Ticket>(`${this.apiUrl}/event/${eventId}/purchase/${customerId}`, {});
  }
}