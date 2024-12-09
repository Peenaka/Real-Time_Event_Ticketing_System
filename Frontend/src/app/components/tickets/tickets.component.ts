import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TicketService } from '../../services/ticket.service';
import { Ticket, TicketDto, TicketStats } from '../../models/ticket.model';

@Component({
  selector: 'app-tickets',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tickets.component.html',
  styleUrls: ['./tickets.component.css']
})
export class TicketsComponent {
  searchEventId: number | null = null;
  searchTicketId: number | null = null;
  eventStats: TicketStats | null = null;
  allEventStats: TicketStats[] = [];
  ticketDetails: Ticket | null = null;
  allTickets: Ticket[] = [];

  newTicket: TicketDto = {
    eventName: '',
    price: 0,
    eventDateTime: new Date(),
    venue: '',
    isVIP: false,
    ticketCount: 0
  };

  constructor(private ticketService: TicketService) {
    this.loadAllStats();
    this.loadAllTickets();
  }

  loadAllStats(): void {
    this.ticketService.getAllTicketStats().subscribe(
      (stats) => {
        this.allEventStats = stats;
      },
      (error) => console.error('Error fetching all event stats:', error)
    );
  }

  loadAllTickets(): void {
    this.ticketService.getAllTickets().subscribe(
      (tickets) => {
        this.allTickets = tickets;
      },
      (error) => console.error('Error fetching all tickets:', error)
    );
  }

  getEventStats(): void {
    if (this.searchEventId) {
      this.ticketService.getTicketStats(this.searchEventId).subscribe(
        (stats) => {
          this.eventStats = stats;
        },
        (error) => console.error('Error fetching event stats:', error)
      );
    }
  }

  createTicket(): void {
    const vendorId = 1;
    const eventId = this.searchEventId;

    if (eventId) {
      this.ticketService.createTicket(vendorId, eventId, this.newTicket).subscribe(
        (ticket) => {
          console.log('Ticket created:', ticket);
          this.resetForm();
          this.loadAllStats();
          this.loadAllTickets();
        },
        (error) => console.error('Error creating ticket:', error)
      );
    }
  }

  getTicketDetails(): void {
    if (this.searchTicketId) {
      this.ticketService.getTicketDetails(this.searchTicketId).subscribe(
        (ticket) => {
          this.ticketDetails = ticket;
        },
        (error) => console.error('Error fetching ticket details:', error)
      );
    }
  }

  purchaseTicket(ticketId: number): void {
    const customerId = 1;
    const eventId = this.searchEventId;

    if (eventId) {
      this.ticketService.purchaseTicket(eventId, customerId).subscribe(
        (ticket) => {
          console.log('Ticket purchased:', ticket);
          this.loadAllStats();
          this.loadAllTickets();
        },
        (error) => console.error('Error purchasing ticket:', error)
      );
    }
  }

  private resetForm(): void {
    this.newTicket = {
      eventName: '',
      price: 0,
      eventDateTime: new Date(),
      venue: '',
      isVIP: false,
      ticketCount: 0
    };
  }
}