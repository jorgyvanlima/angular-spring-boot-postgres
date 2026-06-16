import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { ApiService } from '../../core/services/api.service';
import { SystemSetting } from '../../core/models/system-setting.model';

@Component({
  selector: 'app-settings',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './settings.component.html',
  styleUrl: './settings.component.css'
})
export class SettingsComponent implements OnInit {
  settings: SystemSetting[] = [];
  settingForm!: FormGroup;
  
  loading = signal<boolean>(true);
  formSaving = false;
  editMode = false;
  selectedSettingId?: number;

  constructor(
    private fb: FormBuilder,
    private apiService: ApiService
  ) {
    this.initForm();
  }

  ngOnInit(): void {
    this.loadSettings();
  }

  initForm(): void {
    this.settingForm = this.fb.group({
      key: ['', [Validators.required, Validators.minLength(3)]],
      value: ['', [Validators.required]],
      category: ['GERAL'],
      description: ['']
    });
  }

  loadSettings(): void {
    this.loading.set(true);
    this.apiService.getSettings().subscribe({
      next: (data) => {
        this.settings = data;
        this.loading.set(false);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  loadForEdit(setting: SystemSetting): void {
    this.editMode = true;
    this.selectedSettingId = setting.id;
    this.settingForm.patchValue({
      key: setting.key,
      value: setting.value,
      category: setting.category || 'GERAL',
      description: setting.description || ''
    });
  }

  cancelEdit(): void {
    this.editMode = false;
    this.selectedSettingId = undefined;
    this.settingForm.reset({
      category: 'GERAL'
    });
  }

  onSubmit(): void {
    if (this.settingForm.invalid) return;

    this.formSaving = true;
    const formValue = this.settingForm.value;
    const settingPayload: SystemSetting = {
      ...formValue,
      id: this.selectedSettingId
    };

    this.apiService.saveSetting(settingPayload).subscribe({
      next: () => {
        this.formSaving = false;
        this.cancelEdit();
        this.loadSettings();
      },
      error: () => {
        this.formSaving = false;
      }
    });
  }

  deleteSetting(setting: SystemSetting): void {
    if (!setting.id) return;
    if (confirm(`Tem certeza que deseja excluir a configuração "${setting.key}"?`)) {
      this.apiService.deleteSetting(setting.id).subscribe({
        next: () => {
          this.loadSettings();
        }
      });
    }
  }
}
