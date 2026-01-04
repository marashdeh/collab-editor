export interface CommentDto {
  id: number;
  content: string;
  authorName: string;
  createdAt: string;
}

export interface DiscussionDto {
  id: number;
  topic: string;
  creatorName: string;
  createdAt: string;
}
