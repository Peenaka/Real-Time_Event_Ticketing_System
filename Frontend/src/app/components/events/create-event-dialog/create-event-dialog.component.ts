import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { CreateEventDto } from '../../../models/event.model';

@Component({
  selector: 'app-create-event-dialog',
  standalone: true,
  templateUrl: './create-event-dialog.component.html',
  styleUrls: ['./create-event-dialog.component.css'],
  imports: [CommonModule, FormsModule]
})
export class CreateEventDialogComponent {
  event: CreateEventDto = {
    eventName: '',
    eventCode: '',
    status: 'UPCOMING'
  };

  onSubmit(): void {
    // This will be handled by the parent component
  }

  onClose(): void {
    // This will be handled by the parent component
  }
}
