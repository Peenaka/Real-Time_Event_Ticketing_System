import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Event } from '../models/event.model';
import { CreateEventDto } from '../models/event.model';

@Injectable({
  providedIn: 'root',
})
export class EventService {
  private apiUrl = 'http://localhost:8080/ticket-system/api/events';

  constructor(private http: HttpClient) {}

  getAllEvents(): Observable<{ data: Event[] }> {
    return this.http.get<{ data: Event[] }>(this.apiUrl);
  }

  getEventById(id: number): Observable<{ data: Event }> {
    return this.http.get<{ data: Event }>(`${this.apiUrl}/${id}`);
  }

   createEvent(event: CreateEventDto): Observable<Event> {
    const apiUrlWithParams = `${this.apiUrl}?email=VendorUser@gmail.com&password=vendor@user123`;
    return this.http.post<Event>(apiUrlWithParams, event);
  }
   updateEvent(id: number, event: CreateEventDto): Observable<Event> {
  const apiUrlWithParams = `${this.apiUrl}/${id}?email=VendorUser@gmail.com&password=vendor@user123`;
  return this.http.put<Event>(apiUrlWithParams, event);
}

  deleteEvent(id: number): Observable<void> {
  const apiUrlWithParams = `${this.apiUrl}/${id}?email=VendorUser@gmail.com&password=vendor@user123`;
  return this.http.delete<void>(apiUrlWithParams);
}


}






// @Injectable({
//   providedIn: 'root'
// })
// export class EventService {
//   private apiUrl = 'http://localhost:8080/ticket-system/api/events';

//   constructor(private http: HttpClient) {}

//   getAllEvents(): Observable<Event[]> {
//     return this.http.get<Event[]>(this.apiUrl);
//   }

//   getEventById(id: number): Observable<Event> {
//     return this.http.get<Event>(`${this.apiUrl}/${id}`);
//   }

//   createEvent(event: CreateEventDto): Observable<Event> {
//     return this.http.post<Event>(this.apiUrl, event);
//   }

//   deleteEvent(id: number): Observable<void> {
//     return this.http.delete<void>(`${this.apiUrl}/${id}`);
//   }

//   updateEvent(id: number, event: Event): Observable<Event> {
//     return this.http.put<Event>(`${this.apiUrl}/${id}`, event);
//   }
// }

// import { Injectable } from '@angular/core';
// import { HttpClient } from '@angular/common/http';
// import { Observable } from 'rxjs';
// import { map } from 'rxjs/operators';
// import { Event, CreateEventDto } from '../models/event.model';
