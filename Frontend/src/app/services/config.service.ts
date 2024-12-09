import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';
import { TicketConfig, TicketConfigDto } from '../models/config.model';
import { Event } from '../models/event.model';

@Injectable({
  providedIn: 'root'
})
export class ConfigService {
  private configApiUrl = 'http://localhost:8080/ticket-system/api/config';
  private eventApiUrl = 'http://localhost:8080/ticket-system/api/events';

  constructor(private http: HttpClient) {}

  configureEvent(eventId: number, config: TicketConfigDto): Observable<TicketConfig> {
    return this.http.post<TicketConfig>(`${this.configApiUrl}/event/${eventId}`, config);
  }

  getEventConfig(eventId: number): Observable<TicketConfig> {
    return forkJoin({
      config: this.http.get<any>(`${this.configApiUrl}/event/${eventId}`),
      event: this.http.get<Event>(`${this.eventApiUrl}/${eventId}`)
    }).pipe(
      map(response => ({
        ...response.config.data,
        eventName: response.event.name
      }))
    );
  }

  resetEventConfig(eventId: number): Observable<void> {
    return this.http.delete<void>(`${this.configApiUrl}/event/${eventId}`);
  }
}






















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