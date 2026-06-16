import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { SystemSetting } from '../models/system-setting.model';

export interface HealthStatus {
  status: string;
  database: string;
  error?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  private statusUrl = '/api/status';
  private settingsUrl = '/api/settings';

  constructor(private http: HttpClient) {}

  getHealthStatus(): Observable<HealthStatus> {
    return this.http.get<HealthStatus>(this.statusUrl);
  }

  getSettings(): Observable<SystemSetting[]> {
    return this.http.get<SystemSetting[]>(this.settingsUrl);
  }

  saveSetting(setting: SystemSetting): Observable<SystemSetting> {
    return this.http.post<SystemSetting>(this.settingsUrl, setting);
  }

  deleteSetting(id: number): Observable<void> {
    return this.http.delete<void>(`${this.settingsUrl}/${id}`);
  }
}
