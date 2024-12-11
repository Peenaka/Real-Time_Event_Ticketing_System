// import { Injectable } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable } from 'rxjs';
// import { map, catchError } from 'rxjs/operators';
// import { ApiService } from '../api/api.service';
// import { AuthStateService } from '../auth/auth-state.service';
// import { Ticket, TicketDto } from '../../models/ticket.model';
// import { environment } from '../../environments/environment';

// @Injectable({
//   providedIn: 'root'
// })
// export class TicketApiService extends ApiService {
//   private apiUrl = `${environment.apiUrl}/tickets`;

//   constructor(
//     private http: HttpClient,
//     authState: AuthStateService
//   ) {
//     super(authState);
//   }

//   createTicket(vendorId: number, eventId: number, ticket: TicketDto): Observable<ApiResponse<Ticket>> {
//     const url = this.addAuthParams(`${this.apiUrl}/vendor/${vendorId}/event/${eventId}`);
//     return this.http.post<ApiResponse<Ticket>>(url, ticket).pipe(
//       catchError(error => {
//         throw this.handleError(error);
//       })
//     );
//   }
// }