import { Component, OnInit } from '@angular/core';
import { environment } from '../../../../environments/environment';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { AdminService } from '../../../services/admin.service';
import { AuthService } from '../../../services/auth.service';
import { User } from '../../../models/auth.models';
import { ComprehensiveReport, AttendanceRecord, LeaveRequest } from '../../../models/domain.models';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.component.html'
})
export class AdminComponent implements OnInit {
  private readonly API = environment.apiUrl + '/admin';

  users: User[] = [];
  managers: User[] = [];
  report: ComprehensiveReport | null = null;
  newUser: any = { username: '', email: '', password: '', fullName: '', role: 'EMPLOYEE', managerId: null };
  message = '';
  loading = false;
  activeTab: 'users' | 'create' | 'detail' = 'users';

  // Deep-dive state
  selectedUser: User | null = null;
  userAttendance: AttendanceRecord[] = [];
  userLeaves: LeaveRequest[] = [];
  detailLoading = false;
  overrideReason = '';

  constructor(private adminService: AdminService, public authService: AuthService, private http: HttpClient) { }

  private headers(): HttpHeaders { return new HttpHeaders({ Authorization: `Bearer ${this.authService.getToken()}` }); }

  ngOnInit() { this.refreshData(); }

  refreshData() {
    this.adminService.getAllUsers().subscribe(data => { this.users = data; this.managers = data.filter((u: any) => u.role === 'MANAGER'); });
    this.adminService.getComprehensiveReport().subscribe(data => this.report = data);
  }

  activeUsersCount(): number {
    return this.users.filter(u => u.isActive).length;
  }

  createUser() {
    this.loading = true;
    this.adminService.createUser(this.newUser).subscribe({
      next: () => { this.loading = false; this.showMsg('✅ User created!'); this.newUser = { username: '', email: '', password: '', fullName: '', role: 'EMPLOYEE', managerId: null }; this.refreshData(); this.activeTab = 'users'; },
      error: (err: any) => { this.loading = false; this.showMsg(err.error?.message || 'Error'); }
    });
  }

  updateManager(user: User) {
    this.adminService.updateUser(user.id, user).subscribe({
      next: () => this.showMsg(`✅ Manager updated for ${user.fullName}`),
      error: (err: any) => this.showMsg(err.error?.message || 'Error updating manager')
    });
  }

  toggleActive(user: any) {
    this.http.put<any>(`${this.API}/users/${user.id}/toggle`, {}, { headers: this.headers() }).subscribe({
      next: (u) => { user.isActive = u.isActive; this.showMsg(`${user.fullName} ${u.isActive ? 'activated' : 'deactivated'}.`); },
      error: () => this.showMsg('Toggle failed')
    });
  }

  openUserDetail(user: User) {
    this.selectedUser = user; this.userAttendance = []; this.userLeaves = []; this.detailLoading = true; this.activeTab = 'detail';
    this.http.get<AttendanceRecord[]>(`${this.API}/users/${user.id}/attendance`, { headers: this.headers() }).subscribe(d => this.userAttendance = d);
    this.http.get<LeaveRequest[]>(`${this.API}/users/${user.id}/leaves`, { headers: this.headers() }).subscribe(d => { this.userLeaves = d; this.detailLoading = false; });
  }

  closeDetail() { this.selectedUser = null; this.activeTab = 'users'; }

  overrideLeave(leaveId: number, status: string) {
    const reason = this.overrideReason || 'Admin override';
    this.http.put(`${this.API}/leaves/${leaveId}/override?status=${status}&reason=${encodeURIComponent(reason)}`, {}, { headers: this.headers() }).subscribe({
      next: () => { this.showMsg(`✅ Leave ${status.toLowerCase()}d`); this.openUserDetail(this.selectedUser!); this.overrideReason = ''; },
      error: () => this.showMsg('Override failed')
    });
  }

  showMsg(m: string) { this.message = m; setTimeout(() => this.message = '', 3000); }
  logout() { this.authService.logout(); }
}
