import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ManagerService } from '../../../services/manager.service';
import { AuthService } from '../../../services/auth.service';
import { EmployeeService } from '../../../services/employee.service';
import { LeaveRequest, AttendanceRecord } from '../../../models/domain.models';

@Component({
  selector: 'app-manager',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './manager.component.html'
})
export class ManagerComponent implements OnInit {
  pendingLeaves: LeaveRequest[] = [];
  todayAttendance: AttendanceRecord | null = null;
  attendanceHistory: AttendanceRecord[] = [];

  // Stats
  teamMembersCount = 0;
  totalPendingRequests = 0;

  selectedLeaveId: number | null = null;
  rejectionReason = '';

  message = '';
  loading = false;
  attendanceLoading = false;

  constructor(
    private managerService: ManagerService,
    public authService: AuthService,
    private employeeService: EmployeeService
  ) { }

  ngOnInit() {
    this.refreshData();
  }

  refreshData() {
    this.managerService.getPendingLeaves().subscribe(data => {
      this.pendingLeaves = data;
      this.totalPendingRequests = data.length;
    });

    this.employeeService.getTodayAttendance().subscribe({
      next: (data) => this.todayAttendance = data,
      error: () => this.todayAttendance = null
    });

    this.employeeService.getAttendanceHistory().subscribe(data => {
      this.attendanceHistory = data;
    });
  }

  checkIn() {
    this.attendanceLoading = true;
    this.employeeService.checkIn().subscribe({
      next: () => { this.showToast('Checked in successfully!'); this.refreshData(); this.attendanceLoading = false; },
      error: (err: any) => { this.showToast(err.error?.message || 'Error checking in'); this.attendanceLoading = false; }
    });
  }

  checkOut() {
    this.attendanceLoading = true;
    this.employeeService.checkOut().subscribe({
      next: () => { this.showToast('Checked out successfully!'); this.refreshData(); this.attendanceLoading = false; },
      error: (err: any) => { this.showToast(err.error?.message || 'Error checking out'); this.attendanceLoading = false; }
    });
  }

  approve(id: number) {
    this.loading = true;
    this.managerService.approveLeave(id).subscribe({
      next: () => {
        this.showToast('Leave approved!');
        this.refreshData();
        this.loading = false;
      },
      error: (err: any) => { this.showToast(err.error?.message || 'Error approving leave'); this.loading = false; }
    });
  }

  startReject(id: number) {
    this.selectedLeaveId = id;
    this.rejectionReason = '';
  }

  cancelReject() {
    this.selectedLeaveId = null;
    this.rejectionReason = '';
  }

  confirmReject() {
    if (!this.rejectionReason.trim()) {
      this.showToast('Rejection reason is required.');
      return;
    }
    this.loading = true;
    this.managerService.rejectLeave(this.selectedLeaveId!, this.rejectionReason).subscribe({
      next: () => {
        this.showToast('Leave rejected!');
        this.selectedLeaveId = null;
        this.refreshData();
        this.loading = false;
      },
      error: (err: any) => { this.showToast(err.error?.message || 'Error rejecting leave'); this.loading = false; }
    });
  }

  showToast(msg: string) {
    this.message = msg;
    setTimeout(() => this.message = '', 4000);
  }

  logout() {
    this.authService.logout();
  }
}
