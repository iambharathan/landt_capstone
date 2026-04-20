export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  username: string;
  email: string;
  password: string;
  fullName: string;
  role: 'ADMIN' | 'MANAGER' | 'EMPLOYEE';
}

export interface LoginResponse {
  token: string;
  tokenType: string;
  id: number;
  email: string;
  username: string;
  fullName: string;
  role: string;
}

export interface User {
  id: number;
  username: string;
  email: string;
  fullName: string;
  role: string;
  isActive: boolean;
}
