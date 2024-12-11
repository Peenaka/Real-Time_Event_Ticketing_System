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
  email: string;
  password: string;
}

export interface Vendor extends User {
  companyName: string;
  email: string;
  password: string;
}