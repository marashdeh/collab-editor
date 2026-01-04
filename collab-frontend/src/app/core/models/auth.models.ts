export interface LoginRequest {
  email?: string | null;
  password?: string | null;
}

export interface RegisterRequest {
  username?: string | null;
  email?: string | null;
  password?: string | null;
}

export interface UserDto {
  id: number;
  username: string;
  email: string;
}
