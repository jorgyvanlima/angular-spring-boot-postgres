import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  template: `
    <header class="dashboard-header">
      <div class="header-logo">
        <span class="logo-accent">LNX-JSP</span>
        <span class="logo-text">Architect</span>
      </div>
      <nav class="header-nav">
        <a routerLink="/" routerLinkActive="active" [routerLinkActiveOptions]="{exact: true}">Status Monitor</a>
        <a routerLink="/settings" routerLinkActive="active">Configurações</a>
      </nav>
    </header>
  `,
  styles: [`
    .dashboard-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      padding-bottom: 2rem;
      border-bottom: 1px solid rgba(255, 255, 255, 0.05);
    }

    .header-logo {
      display: flex;
      align-items: center;
      font-size: 1.5rem;
      font-weight: 800;
      letter-spacing: -0.05em;
    }

    .logo-accent {
      color: #38bdf8;
      margin-right: 0.25rem;
      text-shadow: 0 0 15px rgba(56, 189, 248, 0.4);
    }

    .logo-text {
      color: #f1f5f9;
    }

    .header-nav {
      display: flex;
      gap: 1.5rem;
    }

    .header-nav a {
      color: #94a3b8;
      text-decoration: none;
      font-weight: 600;
      font-size: 0.95rem;
      transition: all 0.2s ease;
      padding: 0.5rem 1rem;
      border-radius: 0.5rem;
    }

    .header-nav a:hover {
      color: #f1f5f9;
      background: rgba(255, 255, 255, 0.03);
    }

    .header-nav a.active {
      color: #38bdf8;
      background: rgba(56, 189, 248, 0.1);
      border: 1px solid rgba(56, 189, 248, 0.2);
    }
  `]
})
export class NavbarComponent {}
