import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { TicketConfig, TicketConfigDto } from '../models/config.model';
import { Event } from '../models/event.model';

@Injectable({
  providedIn: 'root',
})
export class ConfigService {
  private configApiUrl = 'http://localhost:8080/ticket-system/api/config';
  private eventApiUrl = 'http://localhost:8080/ticket-system/api/events';
  private email = 'VendorUser@gmail.com';
  private password = 'vendor@user123';

  constructor(private http: HttpClient) {}

  private addAuthParams(url: string): string {
    const params = new HttpParams()
      .set('email', this.email)
      .set('password', this.password);
    return `${url}?${params.toString()}`;
  }

  configureEvent(eventId: number, config: TicketConfigDto): Observable<TicketConfig> {
    const url = this.addAuthParams(`${this.configApiUrl}/event/${eventId}`);
    return this.http.post<any>(url, config).pipe(
      map(response => ({
        ...response.data,
        eventId: response.data.id,
        eventName: response.data.eventName
      }))
    );
  }

  getEventConfig(eventId: number): Observable<TicketConfig> {
    const configUrl = this.addAuthParams(`${this.configApiUrl}/event/${eventId}`);
    return this.http.get<any>(configUrl).pipe(
      map(response => ({
        eventId: response.data.id,
        eventName: response.data.eventName,
        totalTickets: response.data.totalTickets,
        ticketReleaseRate: response.data.ticketReleaseRate,
        customerRetrievalRate: response.data.customerRetrievalRate,
        maxTicketCapacity: response.data.maxTicketCapacity
      }))
    );
  }

  resetEventConfig(eventId: number): Observable<void> {
    const url = this.addAuthParams(`${this.configApiUrl}/event/${eventId}`);
    return this.http.delete<void>(url);
  }
}





// import { Injectable } from '@angular/core';
// import { HttpClient, HttpParams } from '@angular/common/http';
// import { Observable, forkJoin } from 'rxjs';
// import { map } from 'rxjs/operators';
// import { TicketConfig, TicketConfigDto } from '../models/config.model';
// import { Event } from '../models/event.model';

// @Injectable({
//   providedIn: 'root',
// })
// export class ConfigService {
//   private configApiUrl = 'http://localhost:8080/ticket-system/api/config';
//   private eventApiUrl = 'http://localhost:8080/ticket-system/api/events';
//   private email = 'VendorUser@gmail.com';
//   private password = 'vendor@user123';

//   constructor(private http: HttpClient) {}

//   private addAuthParams(url: string): string {
//     const params = new HttpParams()
//       .set('email', this.email)
//       .set('password', this.password);
//     return `${url}?${params.toString()}`;
//   }

//   configureEvent(eventId: number, config: TicketConfigDto): Observable<TicketConfig> {
//     const url = this.addAuthParams(`${this.configApiUrl}/event/${eventId}`);
//     return this.http.post<TicketConfig>(url, config);
//   }

//   getEventConfig(eventId: number): Observable<TicketConfig> {
//     const configUrl = this.addAuthParams(`${this.configApiUrl}/event/${eventId}`);
//     const eventUrl = this.addAuthParams(`${this.eventApiUrl}/${eventId}`);

//     return forkJoin({
//       config: this.http.get<any>(configUrl),
//       event: this.http.get<Event>(eventUrl),
//     }).pipe(
//       map((response) => {
//         console.log('API Response:', response);
//         return {
//           ...response.config.data,
//           eventId: response.event?.id || null,
//           eventName: response.event?.eventName || '',
//         };
//       })
//     );
//   }

//   resetEventConfig(eventId: number): Observable<void> {
//     const url = this.addAuthParams(`${this.configApiUrl}/event/${eventId}`);
//     return this.http.delete<void>(url);
//   }
// }









// import { Injectable } from '@angular/core';
// import { HttpClient, HttpParams } from '@angular/common/http';
// import { Observable, forkJoin } from 'rxjs';
// import { map } from 'rxjs/operators';
// import { TicketConfig, TicketConfigDto } from '../models/config.model';
// import { Event } from '../models/event.model';

// @Injectable({
//   providedIn: 'root',
// })
// export class ConfigService {
//   private configApiUrl = 'http://localhost:8080/ticket-system/api/config';
//   private eventApiUrl = 'http://localhost:8080/ticket-system/api/events';
//   private email = 'VendorUser@gmail.com';
//   private password = 'vendor@user123';

//   constructor(private http: HttpClient) {}

//   private addAuthParams(url: string): string {
//     const params = new HttpParams()
//       .set('email', this.email)
//       .set('password', this.password);
//     return `${url}?${params.toString()}`;
//   }

//   configureEvent(eventId: number, config: TicketConfigDto): Observable<TicketConfig> {
//     const url = this.addAuthParams(`${this.configApiUrl}/event/${eventId}`);
//     return this.http.post<TicketConfig>(url, config);
//   }

//   getEventConfig(eventId: number): Observable<TicketConfig> {
//     const configUrl = this.addAuthParams(`${this.configApiUrl}/event/${eventId}`);
//     const eventUrl = this.addAuthParams(`${this.eventApiUrl}/${eventId}`);

//     return forkJoin({
//       config: this.http.get<any>(configUrl),
//       event: this.http.get<Event>(eventUrl),
//     }).pipe(
//      map((response) => ({
//   ...response.config.data,
//   eventId: response.event.id, 
//   eventName: response.event.eventName,
//     }))
//     );
//   }

//   resetEventConfig(eventId: number): Observable<void> {
//     const url = this.addAuthParams(`${this.configApiUrl}/event/${eventId}`);
//     return this.http.delete<void>(url);
//   }
// }





















// import { Injectable } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable } from 'rxjs';
// import { TicketConfig, TicketConfigDto } from '../models/config.model';

// @Injectable({
//   providedIn: 'root'
// })
// export class ConfigService {
//   private apiUrl = 'http://localhost:8080/api/config';

//   constructor(private http: HttpClient) {}

//   configureEvent(eventId: number, config: TicketConfigDto): Observable<TicketConfig> {
//     return this.http.post<TicketConfig>(`${this.apiUrl}/event/${eventId}`, config);
//   }

//   getEventConfig(eventId: number): Observable<TicketConfig> {
//     return this.http.get<TicketConfig>(`${this.apiUrl}/event/${eventId}`);
//   }

//   resetEventConfig(eventId: number): Observable<void> {
//     return this.http.delete<void>(`${this.apiUrl}/event/${eventId}`);
//   }
// }