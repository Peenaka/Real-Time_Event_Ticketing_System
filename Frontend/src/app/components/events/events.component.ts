import { Component, OnInit } from '@angular/core';
import { EventService } from '../../services/event.service';
import { Event, CreateEventDto } from '../../models/event.model';
import { CreateEventDialogComponent } from './create-event-dialog/create-event-dialog.component';
import { FormsModule } from '@angular/forms'
import { CommonModule } from '@angular/common';

// @Component({
//   selector: 'app-events',
//   standalone: true,
//   templateUrl: './events.component.html',
//   styleUrls: ['./events.component.css'],
//   imports: [FormsModule,CreateEventDialogComponent,CommonModule]
// })
// export class EventsComponent implements OnInit {
//   events: Event[] = [];
//   searchId: number | null = null;
//   showDialog = false;

//   constructor(private eventService: EventService) {}

//   ngOnInit(): void {
//     this.loadAllEvents();
//   }

//   loadAllEvents(): void {
//     this.eventService.getAllEvents().subscribe(
//       (data) => this.events = data,
//       (error) => console.error('Error loading events:', error)
//     );
//   }

//   searchEvent(): void {
//     if (this.searchId) {
//       this.eventService.getEventById(this.searchId).subscribe(
//         (event) => this.events = [event],
//         (error) => {
//           console.error('Error searching event:', error);
//           this.events = [];
//         }
//       );
//     }
//   }

//   showCreateDialog(): void {
//     this.showDialog = true;
//   }

//   hideCreateDialog(): void {
//     this.showDialog = false;
//   }

//   createEvent(eventDto: CreateEventDto): void {
//     this.eventService.createEvent(eventDto).subscribe(
//       (event) => {
//         this.events.push(event);
//         this.hideCreateDialog();
//       },
//       (error) => console.error('Error creating event:', error)
//     );
//   }

//   updateEvent(event: Event): void {
//     console.log('Update event:', event);
//   }

//   deleteEvent(id: number): void {
//     this.eventService.deleteEvent(id).subscribe(
//       () => {
//         this.events = this.events.filter(event => event.id !== id);
//       },
//       (error) => console.error('Error deleting event:', error)
//     );
//   }
// }


@Component({
  selector: 'app-events',
  standalone: true,
  templateUrl: './events.component.html',
  styleUrls: ['./events.component.css'],
  imports: [FormsModule, CreateEventDialogComponent, CommonModule],
})

export class EventsComponent implements OnInit {
  events: Event[] = [];
  searchId: number | null = null;
  showDialog = false;
  editEvent: Event | null = null;

  constructor(private eventService: EventService) {}

  ngOnInit(): void {
    this.loadAllEvents();
  }

  loadAllEvents(): void {
    this.eventService.getAllEvents().subscribe({
      next: (response) => this.events = response.data,
      error: (err) => console.error('Error loading events:', err),
    });
  }

  searchEvent(): void {
    if (this.searchId) {
      this.eventService.getEventById(this.searchId).subscribe({
        next: (response) => this.events = [response.data],
        error: (err) => {
          console.error('Error searching event:', err);
          this.events = [];
        },
      });
    }
  }

  showCreateDialog(): void {
    this.showDialog = true;
  }

  hideCreateDialog(): void {
    this.showDialog = false;
  }

  updateEvent(event: Event): void {
  this.showDialog = true; // Open the dialog
  this.editEvent = { ...event }; // Populate the dialog with the selected event data
}

// Adjust createEvent to handle both create and update
createEvent(eventDto: CreateEventDto): void {
  if (this.editEvent) {
    // If updating an event
    this.eventService.updateEvent(this.editEvent.id, eventDto).subscribe({
      next: (updatedEvent) => {
        // Update the local list with the updated event
        const index = this.events.findIndex((e) => e.id === this.editEvent?.id);
        if (index !== -1) this.events[index] = updatedEvent;
        this.editEvent = null; // Clear edit state
        this.hideCreateDialog();
      },
      error: (err) => console.error('Error updating event:', err),
    });
  } else {
    // If creating a new event
    this.eventService.createEvent(eventDto).subscribe({
      next: (event) => {
        this.events.push(event);
        this.hideCreateDialog();
      },
      error: (err) => console.error('Error creating event:', err),
    });
  }
}

  deleteEvent(id: number): void {
    this.eventService.deleteEvent(id).subscribe({
      next: () => {
        this.events = this.events.filter(event => event.id !== id);
      },
      error: (err) => console.error('Error deleting event:', err),
    });
  }
}
