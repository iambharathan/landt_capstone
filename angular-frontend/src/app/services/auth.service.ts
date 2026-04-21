import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequest, LoginResponse, RegisterRequest } from '../models/auth.models';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {

    private readonly API = environment.apiUrl + '/auth';

    private readonly TOKEN_KEY = 'auth_token';
    private readonly USER_KEY = 'auth_user';

    constructor(private http: HttpClient, private router: Router) {
        console.log('API BASE:', this.API); // 🔥 DEBUG LINE
    }

    login(req: LoginRequest): Observable<LoginResponse> {
        return this.http.post<LoginResponse>(`${this.API}/login`, req).pipe(
            tap(res => {
                localStorage.setItem(this.TOKEN_KEY, res.token);
                localStorage.setItem(this.USER_KEY, JSON.stringify(res));
            })
        );
    }

    register(req: RegisterRequest): Observable<any> {
        return this.http.post<any>(`${this.API}/register`, req);
    }

    logout(): void {
        localStorage.removeItem(this.TOKEN_KEY);
        localStorage.removeItem(this.USER_KEY);
        this.router.navigate(['/login']);
    }

    getToken(): string | null {
        return localStorage.getItem(this.TOKEN_KEY);
    }

    isLoggedIn(): boolean {
        return !!this.getToken();
    }

    getCurrentUser(): LoginResponse | null {
        const user = localStorage.getItem(this.USER_KEY);
        return user ? JSON.parse(user) : null;
    }

    getRole(): string {
        return this.getCurrentUser()?.role ?? '';
    }
}