import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from '../models/auth.models';
import { Policy, ComprehensiveReport, AttendanceReport, LeaveReport } from '../models/domain.models';

@Injectable({ providedIn: 'root' })
export class AdminService {
    private readonly API = 'http://localhost:8080/api/admin';

    constructor(private http: HttpClient) { }

    getAllUsers(): Observable<User[]> {
        return this.http.get<User[]>(`${this.API}/users`);
    }

    getUserById(id: number): Observable<User> {
        return this.http.get<User>(`${this.API}/users/${id}`);
    }

    createUser(user: any): Observable<User> {
        return this.http.post<User>(`${this.API}/users`, user);
    }

    deleteUser(id: number): Observable<any> {
        return this.http.delete(`${this.API}/users/${id}`);
    }

    getAllPolicies(): Observable<Policy[]> {
        return this.http.get<Policy[]>(`${this.API}/policies`);
    }

    createPolicy(policy: any): Observable<Policy> {
        return this.http.post<Policy>(`${this.API}/policies`, policy);
    }

    getAttendanceReport(): Observable<AttendanceReport> {
        return this.http.get<AttendanceReport>(`${this.API}/reports/attendance`);
    }

    getLeaveReport(): Observable<LeaveReport> {
        return this.http.get<LeaveReport>(`${this.API}/reports/leave`);
    }

    getComprehensiveReport(): Observable<ComprehensiveReport> {
        return this.http.get<ComprehensiveReport>(`${this.API}/reports/comprehensive`);
    }
}
