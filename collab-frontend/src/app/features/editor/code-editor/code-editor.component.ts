import { Component, Input, OnChanges, SimpleChanges, OnDestroy, ElementRef, ViewChild, AfterViewInit, ViewEncapsulation } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { ProjectService } from '../../../core/services/project.service';
import { MonacoService } from '../../../core/services/monaco.service';
import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';

declare const monaco: any;

@Component({
  selector: 'app-code-editor',
  standalone: true,
  imports: [CommonModule, FormsModule],
  encapsulation: ViewEncapsulation.None, // Allows styles to apply to Monaco widgets
  styles: [`
    /* Container for the widget */
    .remote-cursor-widget {
      z-index: 100;
      pointer-events: none;
    }
    /* The vertical bar */
    .cursor-line {
      width: 2px;
      height: 20px;
      background: #ff0055;
      position: absolute;
    }
    /* The name flag */
    .cursor-label {
      position: absolute;
      top: -20px;
      left: 0;
      background: #ff0055;
      color: white;
      font-size: 10px;
      font-weight: bold;
      padding: 1px 6px;
      border-radius: 4px;
      white-space: nowrap;
      font-family: sans-serif;
      box-shadow: 0 2px 4px rgba(0,0,0,0.2);
    }
  `],
  template: `
    <div class="relative w-full h-full bg-gray-900 overflow-hidden flex flex-col">
      <div *ngIf="loading" class="absolute inset-0 z-50 flex items-center justify-center bg-gray-900 text-blue-400 font-mono">
        Initializing Editor...
      </div>
      <div #editorContainer class="flex-grow w-full h-full"></div>
    </div>
  `
})
export class CodeEditorComponent implements OnChanges, OnDestroy, AfterViewInit {
  @Input() fileId: number | null = null;
  @Input() fileName: string = '';
  @ViewChild('editorContainer') editorContainer!: ElementRef;

  private editor: any;
  private stompClient: Client | null = null;
  loading = false;
  private isRemoteUpdate = false;
  
  // Track widgets by userId
  private cursorWidgets = new Map<number, any>();

  constructor(
    private authService: AuthService,
    private projectService: ProjectService,
    private monacoService: MonacoService
  ) {}

  ngAfterViewInit() {
    this.monacoService.load();
    this.monacoService.loading$.subscribe(() => {
      this.initMonaco();
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['fileId'] && this.fileId && this.editor) {
      this.loadFileContent(this.fileId);
    }
    if (changes['fileName'] && this.fileName && this.editor) {
       this.updateEditorLanguage(this.fileName);
    }
  }

  ngOnDestroy() {
    this.disconnect();
    if (this.editor) {
      this.editor.dispose();
    }
  }

  public getCurrentCode(): string {
    return this.editor ? this.editor.getValue() : '';
  }

  public switchFile(fileId: number) {
    this.fileId = fileId;
    if (this.editor) {
      this.loadFileContent(fileId);
    }
  }

  private initMonaco() {
    if (this.editor) return;

    this.editor = monaco.editor.create(this.editorContainer.nativeElement, {
      value: '// Select a file...',
      language: this.getLanguageFromExtension(this.fileName),
      theme: 'vs-dark',
      automaticLayout: true,
      fontSize: 14,
      minimap: { enabled: true },
      scrollBeyondLastLine: false
    });

    // 1. Send Edits
    this.editor.onDidChangeModelContent(() => {
      if (this.isRemoteUpdate || !this.stompClient?.connected || !this.fileId) return;
      this.stompClient.publish({
        destination: `/app/edit/${this.fileId}`,
        body: JSON.stringify({
          senderId: Number(this.authService.getUserId()),
          content: this.editor.getValue()
        })
      });
    });

    // 2. Send Cursor Position
    this.editor.onDidChangeCursorPosition((e: any) => {
      if (!this.stompClient?.connected || !this.fileId) return;
      this.stompClient.publish({
        destination: `/app/cursor/${this.fileId}`,
        body: JSON.stringify({
          userId: Number(this.authService.getUserId()),
          username: this.authService.getEmail().split('@')[0],
          lineNumber: e.position.lineNumber,
          column: e.position.column
        })
      });
    });

    if (this.fileId) {
        this.loadFileContent(this.fileId);
    }
  }

  private loadFileContent(id: number) {
    this.loading = true;
    this.disconnect();

    this.projectService.getFileContent(id).subscribe({
      next: (response: any) => {
        const content = typeof response === 'string' ? response : (response?.content || '');
        if (this.editor) {
          this.isRemoteUpdate = true;
          this.editor.setValue(content);
          this.updateEditorLanguage(this.fileName);
          this.isRemoteUpdate = false;
        }
        this.loading = false;
        this.connectWebSocket();
      },
      error: (err: any) => {
        console.error('Error loading file', err);
        this.loading = false;
      }
    });
  }

  private connectWebSocket() {
    const socket = new SockJS('http://localhost:8080/ws');
    this.stompClient = new Client({
      webSocketFactory: () => socket,
      debug: (msg) => console.log(msg),
      onConnect: () => {
        this.stompClient?.subscribe(`/topic/files/${this.fileId}`, (message) => {
          const payload = JSON.parse(message.body);
          if (payload.senderId !== Number(this.authService.getUserId())) {
            this.isRemoteUpdate = true;
            const pos = this.editor.getPosition();
            this.editor.setValue(payload.content);
            this.editor.setPosition(pos);
            this.isRemoteUpdate = false;
          }
        });

        this.stompClient?.subscribe(`/topic/cursors/${this.fileId}`, (message) => {
          const cursor = JSON.parse(message.body);
          if (cursor.userId !== Number(this.authService.getUserId())) {
            this.updateRemoteCursor(cursor);
          }
        });
      }
    });
    this.stompClient.activate();
  }

  /**
   * Updated to use Content Widgets instead of Decorations.
   * This prevents the flickering and disappearing issue.
   */
  private updateRemoteCursor(data: any) {
    if (!this.editor || !monaco) return;

    const widgetId = `cursor-${data.userId}`;

    // If widget already exists, we remove it to refresh its position
    if (this.cursorWidgets.has(data.userId)) {
      this.editor.removeContentWidget(this.cursorWidgets.get(data.userId));
    }

    // Create the Widget Object
    const widget = {
      domNode: null as any,
      getId: () => widgetId,
      getDomNode: function() {
        if (!this.domNode) {
          this.domNode = document.createElement('div');
          this.domNode.className = 'remote-cursor-widget';
          
          const line = document.createElement('div');
          line.className = 'cursor-line';
          
          const label = document.createElement('div');
          label.className = 'cursor-label';
          label.innerText = data.username;
          
          this.domNode.appendChild(line);
          this.domNode.appendChild(label);
        }
        return this.domNode;
      },
      getPosition: () => ({
        position: {
          lineNumber: data.lineNumber,
          column: data.column
        },
        preference: [monaco.editor.ContentWidgetPositionPreference.EXACT]
      })
    };

    // Add to editor and store in map
    this.editor.addContentWidget(widget);
    this.cursorWidgets.set(data.userId, widget);
  }

  private updateEditorLanguage(fileName: string) {
     if (!this.editor) return;
     const model = this.editor.getModel();
     const language = this.getLanguageFromExtension(fileName);
     monaco.editor.setModelLanguage(model, language);
  }

  private getLanguageFromExtension(fileName: string): string {
    if (!fileName) return 'plaintext';
    const ext = fileName.split('.').pop()?.toLowerCase();
    switch (ext) {
      case 'java': return 'java';
      case 'py': return 'python';
      case 'ts': return 'typescript';
      case 'js': return 'javascript';
      case 'html': return 'html';
      case 'css': return 'css';
      case 'json': return 'json';
      case 'sql': return 'sql';
      default: return 'plaintext';
    }
  }

  disconnect() {
    if (this.stompClient) {
      this.stompClient.deactivate();
    }
    // Clean up all cursor widgets on disconnect
    this.cursorWidgets.forEach((widget) => {
      if (this.editor) {
        this.editor.removeContentWidget(widget);
      }
    });
    this.cursorWidgets.clear();
  }
}