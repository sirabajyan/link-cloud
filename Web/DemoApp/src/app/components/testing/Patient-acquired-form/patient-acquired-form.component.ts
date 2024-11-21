import {Component, EventEmitter, Output} from '@angular/core';
import {CommonModule} from '@angular/common';
import {MatInputModule} from '@angular/material/input';
import {MatSnackBar, MatSnackBarModule} from '@angular/material/snack-bar';
import {
  FormArray,
  FormBuilder, FormControl,
  FormGroup,
  FormsModule,
  ReactiveFormsModule,
  Validators
} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatCardModule} from '@angular/material/card';
import {MatTabsModule} from '@angular/material/tabs';
import {MatButtonModule} from '@angular/material/button';
import {MatIconModule} from '@angular/material/icon';
import {MatExpansionModule} from '@angular/material/expansion';
import {TestService} from "../../../services/gateway/testing.service";

@Component({
  selector: 'app-patient-acquired-form',
  standalone: true,
  imports: [
    CommonModule,
    MatSnackBarModule,
    FormsModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    MatToolbarModule,
    MatCardModule,
    MatTabsModule,
    MatButtonModule,
    MatIconModule,
    MatExpansionModule
  ],
  templateUrl: './patient-acquired-form.component.html',
  styleUrls: ['./patient-acquired-form.component.scss']
})
export class PatientAcquiredFormComponent {
  @Output() eventGenerated = new EventEmitter<string>();

  patientForm!: FormGroup;

  patients: string[] = []; // Array to store patient names directly

  constructor(private fb: FormBuilder, private testService: TestService, private snackBar: MatSnackBar) {
    this.patientForm = this.fb.group({
      patient: [''],
      facilityId: new FormControl('MyFacility', Validators.required)
    });
  }

  get facilityIdControl(): FormControl {
    return this.patientForm.get('facilityId') as FormControl;
  }


  // Add patient name to array
  addPatient(): void {
    const patientNameControl = this.patientForm.get('patient');
    if (patientNameControl && patientNameControl.valid) {
      this.patients.push(patientNameControl.value); // Add to array
      patientNameControl.reset(); // Reset the input field
    }
  }

  // Remove patient name by index
  removePatient(index: number): void {
    this.patients.splice(index, 1);
  }


  generateEvent() {
    if (this.patientForm.valid) {
      this.testService.generatePatientAcquiredEvent(this.facilityIdControl.value, this.patients).subscribe(data => {
        if (data) {

          this.eventGenerated.emit(data.id);

          this.snackBar.open(data.message, '', {
            duration: 3500,
            panelClass: 'success-snackbar',
            horizontalPosition: 'end',
            verticalPosition: 'top'
          });
        }
      })
    }
    else {
      this.snackBar.open(`A valid patient id is required to initialize a Patient Event.`, '', {
        duration: 3500,
        panelClass: 'error-snackbar',
        horizontalPosition: 'end',
        verticalPosition: 'top'
      });
    }
  }
}
