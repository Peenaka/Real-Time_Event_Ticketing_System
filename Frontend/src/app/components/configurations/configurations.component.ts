// import { Component } from '@angular/core';
// import { CommonModule } from '@angular/common';
// import { FormsModule } from '@angular/forms';
// import { ConfigService } from '../../services/config.service';
// import { TicketConfigDto, TicketConfig } from '../../models/config.model';
// import { MatSnackBar } from '@angular/material/snack-bar';

// @Component({
//   selector: 'app-configurations',
//   standalone: true,
//   imports: [CommonModule, FormsModule],
//   templateUrl: './configurations.component.html',
//   styleUrls: ['./configurations.component.css'],
// })
// export class ConfigurationsComponent {
//   newConfig: TicketConfigDto & { eventId: number } = {
//     eventId: 0,
//     totalTickets: 0,
//     ticketReleaseRate: 0,
//     customerRetrievalRate: 0,
//     maxTicketCapacity: 0
//   };

//   searchEventId: number | null = null;
//   configurations: TicketConfig[] = [];

//   constructor(private configService: ConfigService, private snackBar: MatSnackBar) {}

//   showMessage(message: string, action: string = 'Close', duration: number = 3000): void {
//     this.snackBar.open(message, action, {
//       duration: duration,
//       verticalPosition: 'top',
//       horizontalPosition: 'center',
//     });
//   }

//   createConfiguration(): void {
//     const configDto: TicketConfigDto = {
//       totalTickets: this.newConfig.totalTickets,
//       ticketReleaseRate: this.newConfig.ticketReleaseRate,
//       customerRetrievalRate: this.newConfig.customerRetrievalRate,
//       maxTicketCapacity: this.newConfig.maxTicketCapacity
//     };

//     this.configService.configureEvent(this.newConfig.eventId, configDto).subscribe(
//       (config) => {
//         this.configurations = [...this.configurations, config];
//         this.resetForm();
//         this.showMessage('Configuration created successfully!');
//       },
//       (error) => console.error('Error creating configuration:', error)
//     );
//   }

//   getConfiguration(): void {
//     if (this.searchEventId) {
//       this.configService.getEventConfig(this.searchEventId).subscribe(
//         (config) => {
//           console.log('Retrieved Config:', config);
//           this.configurations = [config];
//         },
//         (error) => console.error('Error fetching configuration:', error)
//       );
//     }
//   }

//   resetConfiguration(eventId?: number): void {
//     const id = eventId || this.searchEventId;
//     if (id) {
//       this.configService.resetEventConfig(id).subscribe(
//         () => {
//           this.configurations = this.configurations.filter(config => config.eventId !== id);
//         },
//         (error) => console.error('Error resetting configuration:', error)
//       );
//     }
//   }

//   private resetForm(): void {
//     this.newConfig = {
//       eventId: 0,
//       totalTickets: 0,
//       ticketReleaseRate: 0,
//       customerRetrievalRate: 0,
//       maxTicketCapacity: 0
//     };
//   }
// }




import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ConfigService } from '../../services/config.service';
import { TicketConfigDto, TicketConfig } from '../../models/config.model';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-configurations',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './configurations.component.html',
  styleUrls: ['./configurations.component.css'],
})
export class ConfigurationsComponent {
  newConfig: TicketConfigDto & { eventId: number } = {
    eventId: 0,
    totalTickets: 0,
    ticketReleaseRate: 0,
    customerRetrievalRate: 0,
    maxTicketCapacity: 0
  };

  searchEventId: number | null = null;
  configurations: TicketConfig[] = [];

  constructor(private configService: ConfigService,private snackBar: MatSnackBar) {}

  showMessage(message: string, action: string = 'Close', duration: number = 3000): void {
    this.snackBar.open(message, action, {
      duration: duration,
      verticalPosition: 'top', // or 'bottom'
      horizontalPosition: 'center', // or 'start', 'end'
    });
  }

  createConfiguration(): void {
    const configDto: TicketConfigDto = {
      totalTickets: this.newConfig.totalTickets,
      ticketReleaseRate: this.newConfig.ticketReleaseRate,
      customerRetrievalRate: this.newConfig.customerRetrievalRate,
      maxTicketCapacity: this.newConfig.maxTicketCapacity
    };

    this.configService.configureEvent(this.newConfig.eventId, configDto).subscribe(
      (config) => {
        this.configurations = [...this.configurations, config];
        this.resetForm();
        this.showMessage('Configuration created successfully!');
      },
      (error) => console.error('Error creating configuration:', error)
    );
  }

  getConfiguration(): void {
    if (this.searchEventId) {
      this.configService.getEventConfig(this.searchEventId).subscribe(
        (config) => {
          this.configurations = [config];
        },
        (error) => console.error('Error fetching configuration:', error)
      );
    }
  }

  resetConfiguration(eventId?: number): void {
    const id = eventId || this.searchEventId;
    if (id) {
      this.configService.resetEventConfig(id).subscribe(
        () => {
          this.configurations = this.configurations.filter(config => config.eventId !== id);
        },
        (error) => console.error('Error resetting configuration:', error)
      );
    }
  }

  private resetForm(): void {
    this.newConfig = {
      eventId: 0,
      totalTickets: 0,
      ticketReleaseRate: 0,
      customerRetrievalRate: 0,
      maxTicketCapacity: 0
    };
  }
}