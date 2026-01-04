import { Routes } from '@angular/router';
import { LoginComponent } from './features/auth/login/login.component';
import { DashboardComponent } from './features/projects/dashboard/dashboard.component';
import { WorkspaceComponent } from './features/projects/workspace/workspace.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'dashboard', component: DashboardComponent },
  { path: 'editor/:projectId', component: WorkspaceComponent },
  { path: '**', redirectTo: 'login' }
];