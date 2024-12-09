export interface User {
  id?: number;
  email: string;
  name: string;
  role?: string;
  createdAt?: Date;
  isActive?: boolean;
}

export interface Customer extends User {
  isVIP: boolean;
}

export interface Vendor extends User {
  companyName: string;
}