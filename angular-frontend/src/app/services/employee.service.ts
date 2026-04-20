import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AttendanceRecord, LeaveRequest, ApplyLeaveRequest } from '../models/domain.models';

@Injectable({ providedIn: 'root' })
export class EmployeeService {
    private readonly API = 'http://localhost:8080/api/employee';

    constructor(private http: HttpClient) { }

    checkIn(): Observable<any> {
        return this.http.post(`${this.API}/check-in`, {});
    }

    checkOut(): Observable<any> {
        return this.http.post(`${this.API}/check-out`, {});
    }

    getTodayAttendance(): Observable<AttendanceRecord> {
        return this.http.get<AttendanceRecord>(`${this.API}/attendance/today`);
    }

    getAttendanceHistory(): Observable<AttendanceRecord[]> {
        return this.http.get<AttendanceRecord[]>(`${this.API}/attendance`);
    }

    applyLeave(req: ApplyLeaveRequest): Observable<LeaveRequest> {
        return this.http.post<LeaveRequest>(`${this.API}/leave`, req);
    }

    getMyLeaves(): Observable<LeaveRequest[]> {
        return this.http.get<LeaveRequest[]>(`${this.API}/leaves`);
    }
}
