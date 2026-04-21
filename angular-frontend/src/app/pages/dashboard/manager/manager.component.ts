import { Component, OnInit } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../../services/auth.service';
import { LeaveRequest, AttendanceRecord } from '../../../models/domain.models';

@Component({
  selector: 'app-manager',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manager.component.html'
})
export class ManagerComponent implements OnInit {
  private BASE = environment.apiUrl;

  message = '';
  activeTab: 'team' | 'leaves' | 'checkin' = 'team';

  // Leave Management
  pendingLeaves: LeaveRequest[] = [];
  totalPendingRequests = 0;
  loading = false;
  selectedLeaveId: number | null = null;
  rejectionReason = '';

  // Team View
  team: any[] = [];
  selectedMember: any = null;
  memberAttendance: AttendanceRecord[] = [];
  teamLoading = false;

  // Own Check-in
  todayAttendance: AttendanceRecord | null = null;
  attendanceLoading = false;

  constructor(public authService: AuthService, private http: HttpClient) { }

  private headers(): HttpHeaders { return new HttpHeaders({ Authorization: `Bearer ${this.authService.getToken()}` }); }

  ngOnInit() { this.loadAll(); }

  loadAll() {
    this.http.get<LeaveRequest[]>(this.BASE + '/manager/leaves', { headers: this.headers() })
      .subscribe(d => { this.pendingLeaves = d; this.totalPendingRequests = d.length; });

    this.http.get<any[]>(this.BASE + '/manager/team', { headers: this.headers() })
      .subscribe(d => this.team = d);

    this.http.get<AttendanceRecord>(this.BASE + '/employee/attendance/today', { headers: this.headers() })
      .subscribe({ next: d => this.todayAttendance = d, error: () => this.todayAttendance = null });
  }

  approve(leaveId: number) {
    this.loading = true;
    this.http.put(this.BASE + `/manager/leave/${leaveId}/approve`, {}, { headers: this.headers() })
      .subscribe({
        next: () => { this.loading = false; this.showMsg('✅ Leave approved!'); this.loadAll(); },
        error: e => { this.loading = false; this.showMsg(e.error?.message || 'Error'); }
      });
  }

  startReject(id: number) { this.selectedLeaveId = id; }
  cancelReject() { this.selectedLeaveId = null; this.rejectionReason = ''; }

  confirmReject() {
    if (!this.selectedLeaveId) return;
    this.loading = true;
    this.http.put(this.BASE + `/manager/leave/${this.selectedLeaveId}/reject?reason=${encodeURIComponent(this.rejectionReason)}`, {}, { headers: this.headers() })
      .subscribe({
        next: () => { this.loading = false; this.selectedLeaveId = null; this.rejectionReason = ''; this.showMsg('Leave rejected.'); this.loadAll(); },
        error: e => { this.loading = false; this.showMsg(e.error?.message || 'Error'); }
      });
  }

  checkIn() {
    this.attendanceLoading = true;
    this.http.post<any>(this.BASE + '/employee/check-in', {}, { headers: this.headers() })
      .subscribe({
        next: d => { this.todayAttendance = d; this.attendanceLoading = false; this.showMsg('✅ Checked in!'); },
        error: e => { this.attendanceLoading = false; this.showMsg(e.error?.message || 'Error'); }
      });
  }

  checkOut() {
    this.attendanceLoading = true;
    this.http.post<any>(this.BASE + '/employee/check-out', {}, { headers: this.headers() })
      .subscribe({
        next: d => { this.todayAttendance = d; this.attendanceLoading = false; this.showMsg('✅ Checked out!'); },
        error: e => { this.attendanceLoading = false; this.showMsg(e.error?.message || 'Error'); }
      });
  }

  viewMember(member: any) {
    this.selectedMember = member;
    this.teamLoading = true;
    this.http.get<AttendanceRecord[]>(this.BASE + `/manager/team/${member.id}/attendance`, { headers: this.headers() })
      .subscribe({ next: d => { this.memberAttendance = d; this.teamLoading = false; }, error: () => this.teamLoading = false });
  }

  closeMember() { this.selectedMember = null; this.memberAttendance = []; }

  calcHours(a: AttendanceRecord): string {
    if (!a.checkInTime || !a.checkOutTime) return '—';
    const [ih, im] = a.checkInTime.split(':').map(Number);
    const [oh, om] = a.checkOutTime.split(':').map(Number);
    const mins = (oh * 60 + om) - (ih * 60 + im);
    return `${Math.floor(mins / 60)}h ${mins % 60}m`;
  }

  showMsg(m: string) { this.message = m; setTimeout(() => this.message = '', 3000); }
  logout() { this.authService.logout(); }

  isToday(dateStr: string): boolean {
    const d = new Date(dateStr);
    const today = new Date();
    return d.toDateString() === today.toDateString();
  }

  formatCheckOut(record: any): string {
    if (record.checkOutTime) {
      const [h, m] = record.checkOutTime.split(':');
      let hh = parseInt(h, 10);
      const ampm = hh >= 12 ? 'PM' : 'AM';
      hh = hh % 12 || 12;
      return `${hh}:${m} ${ampm}`;
    }
    return this.isToday(record.date) ? '— Ongoing —' : 'Auto Check-Out (EOD)';
  }
}
