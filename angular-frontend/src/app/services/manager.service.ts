import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LeaveRequest } from '../models/domain.models';

import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ManagerService {
    private readonly API = environment.apiUrl + '/manager';

    constructor(private http: HttpClient) { }

    getPendingLeaves(): Observable<LeaveRequest[]> {
        return this.http.get<LeaveRequest[]>(`${this.API}/leaves`);
    }

    approveLeave(id: number): Observable<LeaveRequest> {
        return this.http.put<LeaveRequest>(`${this.API}/leave/${id}/approve`, {});
    }

    rejectLeave(id: number, reason: string): Observable<LeaveRequest> {
        return this.http.put<LeaveRequest>(`${this.API}/leave/${id}/reject?reason=${encodeURIComponent(reason)}`, {});
    }
}
