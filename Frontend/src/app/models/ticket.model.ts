export interface Ticket {
  id?: number;
  eventName: string;
  price: number;
  eventDateTime: Date;
  venue: string;
  isVIP: boolean;
  isAvailable: boolean;
  isPurchased: boolean;
}

export interface TicketDto {
  eventName: string;
  price: number;
  eventDateTime: Date;
  venue: string;
  isVIP: boolean;
  ticketCount: number;
}

export interface TicketStats {
  eventId: number;
  eventName: string;
  totalTickets: number;
  availableTickets: number;
  soldTickets: number;
  maxCapacity: number;
}