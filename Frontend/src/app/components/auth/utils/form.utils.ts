import { FormGroup, ValidatorFn, Validators } from '@angular/forms';

export function updateFormValidation(
  form: FormGroup,
  userType: 'customer' | 'vendor'
): void {
  const companyNameControl = form.get('companyName');
  const isVIPControl = form.get('isVIP');

  if (userType === 'customer') {
    companyNameControl?.clearValidators();
    companyNameControl?.setValue('');
    isVIPControl?.enable();
  } else {
    companyNameControl?.setValidators([Validators.required]);
    isVIPControl?.disable();
    isVIPControl?.setValue(false);
  }
  
  companyNameControl?.updateValueAndValidity();
}

export const getAuthValidators = () => ({
  name: ['', [Validators.required]],
  email: ['', [Validators.required, Validators.email]],
  password: ['', [Validators.required, Validators.minLength(6)]],
  isVIP: [false],
  companyName: ['']
});