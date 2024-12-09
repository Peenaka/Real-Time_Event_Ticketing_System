export interface LoginDto {
  email: string;
  password: string;
}

export interface CustomerRegistrationDto extends LoginDto {
  name: string;
  isVIP: boolean;
}

export interface VendorRegistrationDto extends LoginDto {
  name: string;
  companyName: string;
}