import { Component, OnInit } from '@angular/core';
import { EventService } from '../../services/event.service';
import { Event, CreateEventDto } from '../../models/event.model';
import { CreateEventDialogComponent } from './create-event-dialog/create-event-dialog.component';
import { FormsModule } from '@angular/forms'

@Component({
  selector: 'app-events',
  standalone: true,
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css'],
  imports: [FormsModule,CreateEventDialogComponent]
})
export class EventsComponent implements OnInit {
  events: Event[] = [];
  searchId: number | null = null;
  showDialog = false;

  constructor(private eventService: EventService) {}

  ngOnInit(): void {
    this.loadAllEvents();
  }

  loadAllEvents(): void {
    this.eventService.getAllEvents().subscribe(
      (data) => this.events = data,
      (error) => console.error('Error loading events:', error)
    );
  }

  searchEvent(): void {
    if (this.searchId) {
      this.eventService.getEventById(this.searchId).subscribe(
        (event) => this.events = [event],
        (error) => {
          console.error('Error searching event:', error);
          this.events = [];
        }
      );
    }
  }

  showCreateDialog(): void {
    this.showDialog = true;
  }

  hideCreateDialog(): void {
    this.showDialog = false;
  }

  createEvent(eventDto: CreateEventDto): void {
    this.eventService.createEvent(eventDto).subscribe(
      (event) => {
        this.events.push(event);
        this.hideCreateDialog();
      },
      (error) => console.error('Error creating event:', error)
    );
  }

  updateEvent(event: Event): void {
    console.log('Update event:', event);
  }

  deleteEvent(id: number): void {
    this.eventService.deleteEvent(id).subscribe(
      () => {
        this.events = this.events.filter(event => event.id !== id);
      },
      (error) => console.error('Error deleting event:', error)
    );
  }
}
