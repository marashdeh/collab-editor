import { Component, OnInit, ViewChild } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { FileService } from '../../../core/services/file.service';
import { CollaborationService } from '../../../core/services/collaboration.service';
import { DiscussionService } from '../../../core/services/discussion.service';
import { AuthService } from '../../../core/services/auth.service';
import { CompilerService, ExecuteResponse } from '../../../core/services/compiler.service';
import { ProjectFileDto, FileVersionDto } from '../../../core/models/file.models';
import { CollaboratorDto } from '../../../core/models/collaboration.models';
import { DiscussionDto, CommentDto } from '../../../core/models/discussion.models';
import { CodeEditorComponent } from '../../editor/code-editor/code-editor.component';

@Component({
  selector: 'app-workspace',
  standalone: true,
  imports: [CommonModule, FormsModule, CodeEditorComponent, RouterLink],
  template: `
    <div class="flex h-screen w-full overflow-hidden bg-gray-900 text-white">
      
      <div class="w-80 bg-gray-800 border-r border-gray-700 flex flex-col flex-shrink-0">
        <div class="p-4 border-b border-gray-700">
            <h2 class="font-bold text-blue-400">Project #{{ projectId }}</h2>
            <a routerLink="/dashboard" class="text-xs text-gray-400 hover:text-white cursor-pointer">← Back to Dashboard</a>
        </div>

        <div class="flex border-b border-gray-700">
            <button (click)="switchTab('FILES')" class="flex-1 py-2 text-[10px] font-bold hover:bg-gray-700" [class.text-blue-400]="activeTab==='FILES'">FILES</button>
            <button (click)="switchTab('DISCUSSIONS')" class="flex-1 py-2 text-[10px] font-bold hover:bg-gray-700" [class.text-blue-400]="activeTab==='DISCUSSIONS'">CHAT</button>
            <button (click)="switchTab('HISTORY')" class="flex-1 py-2 text-[10px] font-bold hover:bg-gray-700" [class.text-blue-400]="activeTab==='HISTORY'">HISTORY</button>
        </div>

        <div *ngIf="activeTab === 'FILES'" class="flex-grow flex flex-col min-h-0">
            <div class="p-2 flex justify-end">
                <button (click)="toggleTrash()" class="text-xs px-2 py-1 rounded border border-gray-600" [class.bg-red-900]="showTrash">
                    {{ showTrash ? '📂 Active' : '🗑️ Trash' }}
                </button>
            </div>
            <div class="flex-grow overflow-y-auto p-2">
                <ul *ngIf="!showTrash" class="space-y-1">
                    <li *ngFor="let file of files" (click)="selectFile(file)" 
                        [class.bg-blue-600]="selectedFile?.id === file.id"
                        class="cursor-pointer p-2 rounded hover:bg-gray-700 text-sm flex items-center justify-between group">
                        <div class="flex items-center gap-2 truncate"><span class="text-gray-400">📄</span> {{ file.name }}</div>
                        <button (click)="deleteFile(file.id!, $event)" class="text-gray-500 hover:text-red-500 opacity-0 group-hover:opacity-100 font-bold">×</button>
                    </li>
                </ul>
                <ul *ngIf="showTrash" class="space-y-1">
                    <li *ngFor="let file of deletedFiles" class="p-2 rounded bg-gray-900/50 border border-red-900/30 text-sm flex justify-between">
                        <span class="line-through text-gray-500">{{ file.name }}</span>
                        <button (click)="restoreFile(file.id!)" class="text-green-500 font-bold" title="Restore">↺</button>
                    </li>
                    <li *ngIf="deletedFiles.length === 0" class="text-center text-gray-500 text-xs mt-4">Trash is empty.</li>
                </ul>
            </div>
            <div *ngIf="!showTrash" class="p-2 border-t border-gray-700 flex gap-1">
                  <input [(ngModel)]="newFileName" placeholder="New file..." class="w-full p-1 text-sm bg-gray-700 text-white border border-gray-600 rounded">
                  <button (click)="createFile()" class="text-green-500 font-bold px-2 border border-green-500 rounded">+</button>
            </div>
            <div *ngIf="!showTrash" class="p-3 border-t border-gray-700 bg-gray-900/30">
                <div class="text-xs font-bold text-gray-500 uppercase mb-2">Invite</div>
                <div class="flex gap-1">
                    <input [(ngModel)]="inviteEmail" placeholder="Email..." class="w-full p-1 text-xs bg-gray-700 text-white border border-gray-600 rounded">
                    <button (click)="sendInvite()" class="bg-blue-600 text-white text-xs px-2 rounded hover:bg-blue-500">></button>
                </div>
                <p *ngIf="inviteMessage" class="text-xs mt-1 text-green-400">{{ inviteMessage }}</p>
            </div>
        </div>

        <div *ngIf="activeTab === 'DISCUSSIONS'" class="flex-grow flex flex-col min-h-0">
             <div *ngIf="!selectedFile" class="p-4 text-center text-gray-500 text-sm mt-10">Select a file.</div>
             <div *ngIf="selectedFile" class="flex-grow flex flex-col">
                <ng-container *ngIf="!activeDiscussion; else commentView">
                    <div class="p-2 bg-gray-700 text-xs text-center font-bold shadow">Topics: {{ selectedFile.name }}</div>
                    <div class="flex-grow overflow-y-auto p-2 space-y-3">
                        <div *ngFor="let disc of discussions" (click)="openDiscussion(disc)" class="bg-gray-700/50 p-3 rounded border border-gray-600 cursor-pointer hover:bg-gray-600">
                            <div class="text-blue-300 font-bold text-sm mb-1">{{ disc.topic }}</div>
                            <div class="text-xs text-gray-400">{{ disc.creatorName }}</div>
                        </div>
                    </div>
                    <div class="p-3 border-t border-gray-700">
                        <textarea [(ngModel)]="newTopic" placeholder="Topic..." rows="2" class="w-full p-2 text-sm bg-gray-700 text-white"></textarea>
                        <button (click)="createDiscussion()" class="w-full mt-2 bg-blue-600 text-white text-sm py-1 rounded font-bold">Post</button>
                    </div>
                </ng-container>
                <ng-template #commentView>
                    <div class="p-2 bg-gray-700 flex items-center gap-2 shadow">
                        <button (click)="activeDiscussion = null" class="text-gray-300 hover:text-white font-bold px-2">←</button>
                        <div class="text-xs font-bold truncate text-white">{{ activeDiscussion?.topic }}</div>
                    </div>
                    <div class="flex-grow overflow-y-auto p-2 space-y-2 bg-gray-900/50">
                        <div *ngFor="let comment of comments" class="bg-gray-800 p-2 rounded text-sm border-l-4 border-blue-500">
                            <div class="flex justify-between text-xs text-gray-400 mb-1"><span class="font-bold text-blue-300">{{ comment.authorName }}</span><span>{{ comment.createdAt | date:'shortTime' }}</span></div>
                            <div class="text-gray-200 break-words">{{ comment.content }}</div>
                        </div>
                    </div>
                    <div class="p-3 border-t border-gray-700 flex gap-2">
                        <input [(ngModel)]="newComment" (keyup.enter)="postComment()" placeholder="Reply..." class="w-full p-2 text-sm bg-gray-700 text-white">
                        <button (click)="postComment()" class="bg-blue-600 px-3 rounded text-white">➤</button>
                    </div>
                </ng-template>
             </div>
        </div>

        <div *ngIf="activeTab === 'HISTORY'" class="flex-grow flex flex-col min-h-0">
             <div *ngIf="!selectedFile" class="p-4 text-center text-gray-500 text-sm mt-10">Select a file to view history.</div>
             <div *ngIf="selectedFile" class="flex-grow flex flex-col">
                 <div class="p-2 bg-gray-700 text-xs text-center font-bold shadow">History: {{ selectedFile.name }}</div>
                 <div class="flex-grow overflow-y-auto p-2 space-y-2">
                     <div *ngFor="let ver of versions" class="bg-gray-800 p-3 rounded border border-gray-600 flex justify-between items-center">
                         <div>
                             <div class="font-bold text-yellow-500 text-sm">v{{ ver.versionNumber }}</div>
                             <div class="text-xs text-gray-400">{{ ver.editedAt | date:'medium' }}</div>
                         </div>
                         <button (click)="restoreVersion(ver.id)" class="bg-blue-900 hover:bg-blue-700 text-blue-200 text-xs px-2 py-1 rounded border border-blue-700">Restore</button>
                     </div>
                     <div *ngIf="versions.length === 0" class="text-center text-gray-500 text-xs mt-4">No history yet.</div>
                 </div>
             </div>
        </div>

      </div>

      <div class="flex-grow h-full flex flex-col relative">
        <div class="h-12 bg-gray-800 border-b border-gray-700 flex items-center justify-between px-4">
            <span class="font-mono text-sm text-gray-300">{{ selectedFile?.name || 'No file selected' }}</span>
            <button (click)="runCode()" [disabled]="isRunning || !selectedFile" class="bg-green-600 hover:bg-green-500 text-white px-4 py-1 rounded font-bold shadow text-sm">
                <span *ngIf="!isRunning">▶ Run Code</span><span *ngIf="isRunning">⏳ Running...</span>
            </button>
        </div>
        <div class="flex-grow relative">
            <app-code-editor #editorRef [fileId]="selectedFile?.id || null" [fileName]="selectedFile?.name || ''"></app-code-editor>
        </div>
        <div class="h-48 bg-black border-t border-gray-700 flex flex-col font-mono">
            <div class="bg-gray-800 px-3 py-1 text-xs font-bold text-gray-400 border-b border-gray-700 flex justify-between"><span>TERMINAL</span><button (click)="output=''" class="hover:text-white">Clear</button></div>
            <div class="flex-grow p-3 text-sm overflow-auto whitespace-pre-wrap">
                <span *ngIf="!output && !isRunning" class="text-gray-600">Output will appear here...</span>
                <span *ngIf="isRunning" class="text-yellow-500">Compiling...</span>
                <span *ngIf="output" [class.text-red-400]="isOutputError" [class.text-green-400]="!isOutputError">{{ output }}</span>
            </div>
        </div>
      </div>
    </div>
  `
})
export class WorkspaceComponent implements OnInit {
  @ViewChild('editorRef') editorRef!: CodeEditorComponent; 

  projectId!: number;
  files: ProjectFileDto[] = [];
  deletedFiles: ProjectFileDto[] = [];
  collaborators: CollaboratorDto[] = [];
  discussions: DiscussionDto[] = [];
  comments: CommentDto[] = [];
  versions: FileVersionDto[] = [];
  
  activeDiscussion: DiscussionDto | null = null;
  selectedFile: ProjectFileDto | null = null;
  activeTab: 'FILES' | 'DISCUSSIONS' | 'HISTORY' = 'FILES';
  showTrash: boolean = false;
  
  newFileName: string = '';
  newTopic: string = '';
  newComment: string = '';
  inviteEmail: string = '';
  inviteMessage: string = '';
  isRunning: boolean = false;
  output: string = '';
  isOutputError: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private fileService: FileService,
    private collabService: CollaborationService,
    private discussService: DiscussionService,
    private compilerService: CompilerService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.projectId = Number(this.route.snapshot.paramMap.get('projectId'));
    this.loadFiles();
    this.loadCollaborators();
  }

  loadFiles() { this.fileService.getFilesByProject(this.projectId).subscribe((data: any) => this.files = data); }
  loadCollaborators() { this.collabService.getProjectCollaborators(this.projectId).subscribe((data: any) => this.collaborators = data); }

  selectFile(file: ProjectFileDto) {
    this.selectedFile = file;
    this.output = ''; 
    if (this.activeTab === 'DISCUSSIONS') this.loadDiscussions(file.id!);
    if (this.activeTab === 'HISTORY') this.loadVersions(file.id!);
  }
  
  switchTab(tab: 'FILES' | 'DISCUSSIONS' | 'HISTORY') {
      this.activeTab = tab;
      if (this.selectedFile) {
          if (tab === 'DISCUSSIONS') this.loadDiscussions(this.selectedFile.id!);
          if (tab === 'HISTORY') this.loadVersions(this.selectedFile.id!);
      }
  }

  loadVersions(fileId: number) {
      this.fileService.getFileVersions(fileId).subscribe({
          next: (data: any) => this.versions = data.sort((a: any, b: any) => b.versionNumber - a.versionNumber),
          error: (err: any) => console.error(err)
      });
  }

  restoreVersion(versionId: number) {
      if(!this.selectedFile) return;
      if(!confirm('Revert to this version? Current changes will be saved as a new version.')) return;
      
      this.fileService.restoreVersionTo(this.selectedFile.id!, versionId).subscribe(() => {
          alert('Restored!');
          this.editorRef.switchFile(this.selectedFile!.id!); 
          this.loadVersions(this.selectedFile!.id!);
      });
  }

  toggleTrash() {
      this.showTrash = !this.showTrash;
      if (this.showTrash) this.fileService.getDeletedFiles(this.projectId).subscribe((data: any) => this.deletedFiles = data);
      else this.loadFiles();
  }

  restoreFile(id: number) {
      if(!confirm('Restore?')) return;
      this.fileService.restoreFile(id).subscribe({
          next: () => {
              this.deletedFiles = this.deletedFiles.filter(f => f.id !== id);
              if (this.deletedFiles.length === 0) {
                  this.showTrash = false; 
                  this.loadFiles();
              }
          },
          error: (err) => {
              const msg = err.error?.message || err.error || err.statusText || 'Unknown Error';
              alert("❌ Restore Failed: " + msg);
              console.error(err);
          }
      });
  }

  createFile() {
    if (!this.newFileName.trim()) return;
    this.fileService.createFile({ name: this.newFileName, projectId: this.projectId, content: '' }).subscribe((file: any) => {
      this.files.push(file); this.newFileName = ''; this.selectFile(file);
    });
  }
  
  deleteFile(id: number, event: Event) {
      event.stopPropagation();
      if(!confirm('Delete?')) return;
      this.fileService.deleteFile(id).subscribe(() => {
          this.files = this.files.filter(f => f.id !== id);
          if (this.selectedFile?.id === id) this.selectedFile = null;
      });
  }
  
  loadDiscussions(fileId: number) {
      this.activeDiscussion = null;
      this.discussService.getDiscussionsByFile(fileId).subscribe((data: any) => this.discussions = data);
  }
  
  // ✅ FIXED: Convert getUserId() string to number
  createDiscussion() {
      if (!this.newTopic.trim() || !this.selectedFile) return;
      const userId = Number(this.authService.getUserId());
      if (!userId) return; 

      this.discussService.createDiscussion(this.selectedFile.id!, userId, this.newTopic).subscribe((dto: any) => {
          this.discussions.push(dto); this.newTopic = '';
      });
  }
  
  openDiscussion(disc: DiscussionDto) {
      this.activeDiscussion = disc;
      this.discussService.getComments(disc.id).subscribe((data: any) => this.comments = data);
  }
  
  // ✅ FIXED: Convert getUserId() string to number
  postComment() {
      if (!this.newComment.trim() || !this.activeDiscussion) return;
      const userId = Number(this.authService.getUserId());
      if (!userId) return;

      this.discussService.addComment(this.activeDiscussion.id, userId, this.newComment).subscribe((dto: any) => {
          this.comments.push(dto); this.newComment = '';
      });
  }
  
  sendInvite() {
    if (!this.inviteEmail.trim()) return;
    this.collabService.sendInvitation(this.projectId, this.inviteEmail).subscribe({
        next: () => { this.inviteMessage = 'Sent!'; setTimeout(() => this.inviteMessage = '', 2000); },
        error: (err: any) => { this.inviteMessage = '❌ ' + (err.error?.message || err.error || 'Failed'); }
    });
  }
  
  runCode() {
    if (!this.selectedFile) return;
    const currentCode = this.editorRef.getCurrentCode();
    this.isRunning = true; this.output = ''; this.isOutputError = false;
    const ext = this.selectedFile.name.split('.').pop() || 'java';
    this.compilerService.run(ext, currentCode).subscribe({
        next: (res: ExecuteResponse) => { this.output = res.output || '(No output)'; this.isOutputError = res.isError; this.isRunning = false; },
        error: (err: any) => { this.output = '❌ Error'; this.isOutputError = true; this.isRunning = false; }
    });
  }
}