export interface EditorChangeMessage {
  type: 'EDIT';
  fileId: number;
  content: string;
}

export interface DocumentJoinMessage {
  type: 'JOIN';
  fileId: number;
}

export interface CursorUpdateMessage {
  type: 'CURSOR';
  fileId: number;
  line: number;
  column: number;
  user: string;
}

export type WebSocketMessage = EditorChangeMessage | DocumentJoinMessage | CursorUpdateMessage;
