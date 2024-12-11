import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { TicketService } from '../../services/ticket.service';
import { AuthStateService } from '../../services/auth-state.service';
import { Ticket, TicketDto, TicketStats } from '../../models/ticket.model';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-tickets',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './tickets.component.html',
  styleUrls: ['./tickets.component.css']
})
export class TicketsComponent implements OnInit {
  searchEventId: number | null = null;
  searchTicketId: number | null = null;
  eventStats: TicketStats | null = null;
  allEventStats: TicketStats[] = [];
  ticketDetails: Ticket | null = null;
  allTickets: Ticket[]|null = null;
  error: string = '';

  newTicket: TicketDto = {
    eventName: '',
    eventId: 0,
    price: 0,
    eventDateTime: new Date(),
    venue: '',
    isVIP: false,
    ticketCount: 0
  };

  constructor(
    private ticketService: TicketService,
    private authState: AuthStateService,
    private router: Router,
    private snackBar: MatSnackBar
  ) {}

  ngOnInit() {
    if (!this.authState.isAuthenticated()) {
      this.router.navigate(['/login']);
      return;
    }
    
    this.loadAllStats();
    this.loadAllTickets();
  }

  get isVendor(): boolean {
    return this.authState.isVendor();
  }

  get isCustomer(): boolean {
    return this.authState.isCustomer();
  }

  loadAllStats(): void {
    this.ticketService.getAllTicketStats().subscribe({
      next: (stats) => {
        this.allEventStats = stats;
      },
      error: (error) => {
        console.error('Error fetching all event stats:', error);
        this.error = 'Failed to load event statistics';
      }
    });
  }


  loadAllTickets(): void {
    this.ticketService.getAllTickets().subscribe({
      next: (tickets) => {
        this.allTickets = tickets;
        this.ticketDetails = null;
      },
      error: (error) => {
        console.error('Error fetching all tickets:', error);
        this.error = 'Failed to load tickets';
      }
    });
  }

  showNotification(message: string, action: string = '', duration: number = 3000): void {
    this.snackBar.open(message, action, {
      duration, // Duration in milliseconds
      horizontalPosition: 'center', // 'start', 'center', 'end', 'left', 'right'
      verticalPosition: 'top', // 'top' or 'bottom'
    });
  }

  getEventStats(): void {
    if (this.searchEventId) {
      this.ticketService.getTicketStats(this.searchEventId).subscribe({
        next: (stats) => {
          this.eventStats = stats;
        },
        error: (error) => {
          console.error('Error fetching event stats:', error);
          this.error = 'Failed to load event statistics';
        }
      });
    }
  }

  createTicket(): void {
    if (!this.isVendor) {
      this.error = 'Only vendors can create tickets';
      return;
    }

    const eventId = this.searchEventId;
    if (!eventId) {
      this.error = 'Please provide an event ID';
      return;
    }

    this.ticketService.createTicket(eventId, this.newTicket).subscribe({
      next: (ticket) => {
        console.log('Ticket created:', ticket);
        this.resetForm();
        this.loadAllStats();
        this.loadAllTickets();
        this.error = '';
      },
      error: (error) => {
        console.error('Error creating ticket:', error);
        this.error = 'Failed to create ticket';
      }
    });
  }

  getTicketDetails(): void {
    if (this.searchTicketId) {
      this.ticketService.getTicketDetails(this.searchTicketId).subscribe({
        next: (ticket) => {
          this.ticketDetails = ticket;
          this.allTickets = null;
          this.error = '';
          console.log('Ticket details:', ticket);
          console.error('Error fetching ticket details:', this.error);
           this.showNotification('Failed to load ticket details',this.error,3000);	
        },
        error: (error) => {
          this.showNotification('Failed to load ticket details');	
          console.error('Error fetching ticket details:', error);
          this.error = 'Failed to load ticket details';
        }
      });
    }
  }

  purchaseTicket(eventId: number): void {
    if (!this.isCustomer) {
      this.error = 'Only customers can purchase tickets';
      return;
    }

    this.ticketService.purchaseTicket(eventId).subscribe({
      next: (ticket) => {
        console.log('Ticket purchased:', ticket);
        this.loadAllStats();
        this.loadAllTickets();
        this.error = '';
      },
      error: (error) => {
        console.error('Error purchasing ticket:', error);
        this.error = 'Failed to purchase ticket';
      }
    });
  }

  private resetForm(): void {
    this.newTicket = {
      eventId: 0,
      eventName: '',
      price: 0,
      eventDateTime: new Date(),
      venue: '',
      isVIP: false,
      ticketCount: 0
    };
  }
}