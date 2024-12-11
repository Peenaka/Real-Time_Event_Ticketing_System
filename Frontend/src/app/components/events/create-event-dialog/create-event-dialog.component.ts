import { Component, EventEmitter, Output } from '@angular/core';
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
   @Output() onSubmitEvent = new EventEmitter<CreateEventDto>();
  @Output() onCloseDialog = new EventEmitter<void>();
  event: CreateEventDto = {
    eventName: '',
    eventCode: '',
    status: 'CREATED'
  };

  onSubmit(): void {
    this.onSubmitEvent.emit(this.event); // Emit event data to parent
  }

  onClose(): void {
    this.onCloseDialog.emit(); // Emit close dialog action to parent
  }
}
