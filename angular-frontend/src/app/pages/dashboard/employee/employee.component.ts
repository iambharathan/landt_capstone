import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AuthService } from '../../../services/auth.service';
import { AttendanceRecord, LeaveRequest } from '../../../models/domain.models';

@Component({
  selector: 'app-employee',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './employee.component.html'
})
export class EmployeeComponent implements OnInit {
  private API = 'http://localhost:8080/api/employee';

  message = '';
  activeTab: 'dashboard' | 'leaves' | 'calendar' | 'profile' = 'dashboard';

  // Attendance
  todayAttendance: AttendanceRecord | null = null;
  attendanceHistory: AttendanceRecord[] = [];
  attendanceLoading = false;

  // Leaves
  myLeaves: LeaveRequest[] = [];
  leaveLoading = false;
  leaveRequest: any = { startDate: '', endDate: '', leaveType: 'ANNUAL', reason: '' };

  // Leave Balance
  leaveBalance: any = null;

  // Stats
  totalLeavesTaken = 0;
  pendingLeavesCount = 0;
  approvedLeavesCount = 0;

  // Profile
  profile: any = null;
  editName = '';
  profileLoading = false;

  // Calendar
  calendarYear = new Date().getFullYear();
  calendarMonth = new Date().getMonth();
  calendarDays: any[] = [];

  constructor(public authService: AuthService, private http: HttpClient) { }

  private headers(): HttpHeaders {
    return new HttpHeaders({ Authorization: `Bearer ${this.authService.getToken()}` });
  }

  ngOnInit() { this.loadAll(); }

  loadAll() {
    this.http.get<AttendanceRecord>(this.API + '/attendance/today', { headers: this.headers() })
      .subscribe({ next: d => this.todayAttendance = d, error: () => this.todayAttendance = null });

    this.http.get<AttendanceRecord[]>(this.API + '/attendance', { headers: this.headers() })
      .subscribe(d => { this.attendanceHistory = d; this.buildCalendar(); });

    this.http.get<LeaveRequest[]>(this.API + '/leaves', { headers: this.headers() })
      .subscribe(d => {
        this.myLeaves = d;
        this.totalLeavesTaken = d.filter(l => l.status === 'APPROVED').length;
        this.pendingLeavesCount = d.filter(l => l.status === 'PENDING').length;
        this.approvedLeavesCount = d.filter(l => l.status === 'APPROVED').length;
      });

    this.http.get<any>(this.API + '/leave-balance', { headers: this.headers() })
      .subscribe(d => this.leaveBalance = d);

    this.http.get<any>(this.API + '/profile', { headers: this.headers() })
      .subscribe(d => { this.profile = d; this.editName = d.fullName; });
  }

  checkIn() {
    this.attendanceLoading = true;
    this.http.post<any>(this.API + '/check-in', {}, { headers: this.headers() })
      .subscribe({
        next: d => { this.todayAttendance = d; this.attendanceLoading = false; this.showMsg('✅ Checked in successfully!'); this.loadAll(); },
        error: e => { this.attendanceLoading = false; this.showMsg(e.error?.message || 'Check-in failed'); }
      });
  }

  checkOut() {
    this.attendanceLoading = true;
    this.http.post<any>(this.API + '/check-out', {}, { headers: this.headers() })
      .subscribe({
        next: d => { this.todayAttendance = d; this.attendanceLoading = false; this.showMsg('✅ Checked out successfully!'); this.loadAll(); },
        error: e => { this.attendanceLoading = false; this.showMsg(e.error?.message || 'Check-out failed'); }
      });
  }

  applyLeave() {
    this.leaveLoading = true;
    this.http.post<any>(this.API + '/leave', this.leaveRequest, { headers: this.headers() })
      .subscribe({
        next: () => { this.leaveLoading = false; this.showMsg('✅ Leave applied!'); this.leaveRequest = { startDate: '', endDate: '', leaveType: 'ANNUAL', reason: '' }; this.loadAll(); },
        error: e => { this.leaveLoading = false; this.showMsg(e.error?.message || 'Failed to apply leave'); }
      });
  }

  saveProfile() {
    this.profileLoading = true;
    this.http.put<any>(this.API + '/profile', { fullName: this.editName }, { headers: this.headers() })
      .subscribe({
        next: d => { this.profile = d; this.profileLoading = false; this.showMsg('✅ Profile updated!'); },
        error: () => { this.profileLoading = false; this.showMsg('Failed to update profile'); }
      });
  }

  // Feature 7: Leave Calendar
  buildCalendar() {
    const year = this.calendarYear, month = this.calendarMonth;
    const firstDay = new Date(year, month, 1).getDay();
    const daysInMonth = new Date(year, month + 1, 0).getDate();
    const days = [];
    for (let i = 0; i < firstDay; i++) days.push(null);
    for (let d = 1; d <= daysInMonth; d++) {
      const dateStr = `${year}-${String(month + 1).padStart(2, '0')}-${String(d).padStart(2, '0')}`;
      const onLeave = this.myLeaves.some(l => l.status === 'APPROVED' && dateStr >= l.startDate && dateStr <= l.endDate);
      const isCheckedIn = this.attendanceHistory.some(a => a.date === dateStr);
      days.push({ day: d, dateStr, onLeave, isCheckedIn });
    }
    this.calendarDays = days;
  }

  prevMonth() { this.calendarMonth--; if (this.calendarMonth < 0) { this.calendarMonth = 11; this.calendarYear--; } this.buildCalendar(); }
  nextMonth() { this.calendarMonth++; if (this.calendarMonth > 11) { this.calendarMonth = 0; this.calendarYear++; } this.buildCalendar(); }

  monthLabel() { return new Date(this.calendarYear, this.calendarMonth).toLocaleString('default', { month: 'long', year: 'numeric' }); }

  // Feature 8: Work Hours Calc
  calcHours(a: AttendanceRecord): string {
    if (!a.checkInTime || !a.checkOutTime) return '—';
    const [ih, im, is2] = a.checkInTime.split(':').map(Number);
    const [oh, om, os] = a.checkOutTime.split(':').map(Number);
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
