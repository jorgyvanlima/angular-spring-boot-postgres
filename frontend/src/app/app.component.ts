import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ApiService, HealthStatus } from './api.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class App implements OnInit {
  title = 'LNX-JSP Stack Status';
  
  loading = signal<boolean>(true);
  backendConnected = signal<boolean>(false);
  databaseConnected = signal<boolean>(false);
  errorMessage = signal<string>('');
  lastChecked = signal<string>('');

  constructor(private apiService: ApiService) {}

  ngOnInit(): void {
    this.checkHealth();
    // Auto-refresh every 5 seconds
    setInterval(() => {
      this.checkHealth();
    }, 5000);
  }

  checkHealth(): void {
    this.apiService.getHealthStatus().subscribe({
      next: (data: HealthStatus) => {
        this.backendConnected.set(data.status === 'UP');
        this.databaseConnected.set(data.database === 'CONNECTED');
        this.errorMessage.set('');
        this.lastChecked.set(new Date().toLocaleTimeString());
        this.loading.set(false);
      },
      error: (err) => {
        this.backendConnected.set(false);
        this.databaseConnected.set(false);
        this.errorMessage.set(err.message || 'Não foi possível estabelecer contato com o backend.');
        this.lastChecked.set(new Date().toLocaleTimeString());
        this.loading.set(false);
      }
    });
  }
}
