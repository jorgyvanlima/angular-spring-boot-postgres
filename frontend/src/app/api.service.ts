import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface HealthStatus {
  status: string;
  database: string;
  error?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private apiUrl = '/api/status';

  constructor(private http: HttpClient) {}

  getHealthStatus(): Observable<HealthStatus> {
    return this.http.get<HealthStatus>(this.apiUrl);
  }
}
