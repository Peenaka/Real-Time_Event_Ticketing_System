// export interface Event {
//   id: number;
//   name: string;
//   code: string;
//   status: string;
//   isConfigured: boolean;
// }

export interface CreateEventDto {
  eventName: string;
  eventCode: string;
  status: string;
}

export interface Event {
  id: number;
  eventName: string;
  eventCode: string;
  status: string;
  configured: boolean;
}
