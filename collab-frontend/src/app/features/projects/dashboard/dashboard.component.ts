import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { ProjectService } from '../../../core/services/project.service';
import { AuthService } from '../../../core/services/auth.service';
import { CollaborationService } from '../../../core/services/collaboration.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="min-h-screen bg-gray-900 text-white flex">
      <aside class="w-64 bg-gray-800 border-r border-gray-700 p-6 flex flex-col">
        <h1 class="text-2xl font-bold bg-gradient-to-r from-blue-400 to-purple-500 bg-clip-text text-transparent mb-8">
          CollabCode
        </h1>
        
        <div class="mb-8 p-4 bg-gray-700/50 rounded-lg border border-gray-600">
          <p class="text-xs text-gray-400 uppercase tracking-wider mb-1">Signed in as</p>
          <p class="font-medium text-blue-300 truncate" [title]="userEmail">
             {{ userEmail }}
          </p>
        </div>

        <nav class="space-y-2 flex-1">
            <button (click)="activeTab = 'PROJECTS'" 
                class="w-full text-left px-4 py-2 rounded font-medium transition-colors"
                [class.bg-gray-700]="activeTab === 'PROJECTS'" 
                [class.text-white]="activeTab === 'PROJECTS'"
                [class.text-gray-400]="activeTab !== 'PROJECTS'">
                📂 My Projects
            </button>
            <button (click)="activeTab = 'INVITATIONS'" 
                class="w-full text-left px-4 py-2 rounded font-medium transition-colors flex justify-between items-center"
                [class.bg-gray-700]="activeTab === 'INVITATIONS'" 
                [class.text-white]="activeTab === 'INVITATIONS'"
                [class.text-gray-400]="activeTab !== 'INVITATIONS'">
                <span>✉️ Invitations</span>
                <span *ngIf="invitations.length > 0" class="bg-red-500 text-white text-xs px-2 py-0.5 rounded-full">{{ invitations.length }}</span>
            </button>
        </nav>

        <button (click)="logout()" class="mt-auto flex items-center gap-2 text-gray-400 hover:text-red-400 transition px-4 py-2">
          Sign Out
        </button>
      </aside>

      <main class="flex-1 p-8">
        
        <ng-container *ngIf="activeTab === 'PROJECTS'">
            <header class="flex justify-between items-center mb-8">
                <h2 class="text-3xl font-bold text-gray-100">My Projects</h2>
                <button (click)="createProject()" class="bg-blue-600 hover:bg-blue-500 text-white px-6 py-2 rounded-lg font-medium shadow-lg transition-all flex items-center gap-2">
                    <span>+</span> New Project
                </button>
            </header>

            <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <div *ngFor="let project of projects" class="group bg-gray-800 border border-gray-700 rounded-xl p-6 hover:border-blue-500/50 transition-all hover:shadow-2xl relative">
                    <div class="flex justify-between items-start mb-4">
                        <div class="h-10 w-10 rounded-lg bg-gradient-to-br from-blue-500 to-purple-600 flex items-center justify-center text-lg font-bold">
                            {{ project.name.charAt(0).toUpperCase() }}
                        </div>
                    </div>
                    <h3 class="text-xl font-bold text-gray-100 mb-2">{{ project.name }}</h3>
                    <p class="text-gray-400 text-sm mb-6 line-clamp-2">{{ project.description || 'No description provided.' }}</p>
                    <button [routerLink]="['/editor', project.id]" class="text-blue-400 hover:text-blue-300 text-sm font-medium">
                        Open Editor →
                    </button>
                </div>

                <div *ngIf="projects.length === 0" class="col-span-full text-center py-20 bg-gray-800/30 rounded-xl border border-gray-700 border-dashed">
                    <p class="text-gray-500 text-lg">No projects found.</p>
                    <p class="text-gray-600 text-sm mt-2">Create your first project to get started!</p>
                </div>
            </div>
        </ng-container>

        <ng-container *ngIf="activeTab === 'INVITATIONS'">
            <header class="mb-8">
                <h2 class="text-3xl font-bold text-gray-100">Pending Invitations</h2>
                <p class="text-gray-400 mt-2">Join projects you have been invited to.</p>
            </header>

            <div class="space-y-4">
                <div *ngFor="let invite of invitations" class="bg-gray-800 border border-gray-700 rounded-xl p-6 flex justify-between items-center hover:border-blue-500/30 transition">
                    <div>
                        <h3 class="text-lg font-bold text-white mb-1">Project: {{ invite.projectName || 'Unknown Project' }}</h3>
                        <p class="text-sm text-gray-400">Invited by: <span class="text-blue-300">{{ invite.inviterName || 'Someone' }}</span></p>
                    </div>
                    <div class="flex gap-3">
                        <button (click)="respond(invite.id, 'ACCEPTED')" class="bg-green-600 hover:bg-green-500 text-white px-4 py-2 rounded-lg font-medium shadow-lg transition">
                            Accept
                        </button>
                        <button (click)="respond(invite.id, 'DECLINED')" class="bg-gray-700 hover:bg-red-600/80 text-gray-300 hover:text-white px-4 py-2 rounded-lg font-medium transition">
                            Decline
                        </button>
                    </div>
                </div>

                <div *ngIf="invitations.length === 0" class="text-center py-20 bg-gray-800/30 rounded-xl border border-gray-700 border-dashed">
                    <p class="text-gray-500">No pending invitations.</p>
                </div>
            </div>
        </ng-container>

      </main>
    </div>
  `
})
export class DashboardComponent implements OnInit {
  projects: any[] = [];
  invitations: any[] = [];
  userEmail: string = 'Loading...';
  activeTab: 'PROJECTS' | 'INVITATIONS' = 'PROJECTS';

  constructor(
    private projectService: ProjectService,
    private collabService: CollaborationService,
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    this.userEmail = this.authService.getEmail();
    const userId = this.authService.getUserId();
    
    if (userId) {
      this.loadProjects(Number(userId));
      this.loadInvitations(); // No ID needed, backend uses token
    } else {
      console.warn('No User ID found');
    }
  }

  loadProjects(userId: number) {
    this.projectService.getProjects(userId).subscribe({
      next: (data) => { this.projects = data; },
      error: (err) => console.error('Failed to load projects', err)
    });
  }

  loadInvitations() {
    this.collabService.getPendingInvitations().subscribe({
        next: (data) => { this.invitations = data; },
        error: (err) => console.error('Failed to load invitations', err)
    });
  }

  respond(inviteId: number, status: 'ACCEPTED' | 'DECLINED') {
      if(!confirm(`Are you sure you want to ${status.toLowerCase()}?`)) return;
      
      // Convert string status to boolean for the API
      const isAccepted = status === 'ACCEPTED';

      this.collabService.respondToInvitation(inviteId, isAccepted).subscribe({
          next: () => {
              // Remove the processed invitation from the list
              this.invitations = this.invitations.filter(i => i.id !== inviteId);
              
              if (isAccepted) {
                  const userId = Number(this.authService.getUserId());
                  this.loadProjects(userId);
                  alert('Project joined successfully!');
                  this.activeTab = 'PROJECTS'; // Switch back to projects tab
              }
          },
          error: (err) => {
              console.error(err);
              alert('Failed to respond to invitation.');
          }
      });
  }

  createProject() {
    const name = prompt('Enter project name:');
    if (name) {
      const userId = this.authService.getUserId();
      if(userId) {
          this.projectService.createProject(name, 'My new project', Number(userId)).subscribe(() => {
            this.loadProjects(Number(userId));
          });
      }
    }
  }

  logout() {
    this.authService.logout();
  }
}