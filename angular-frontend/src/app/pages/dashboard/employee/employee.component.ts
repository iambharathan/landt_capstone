import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmployeeService } from '../../../services/employee.service';
import { AuthService } from '../../../services/auth.service';
import { AttendanceRecord, LeaveRequest, ApplyLeaveRequest } from '../../../models/domain.models';

@Component({
  selector: 'app-employee',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './employee.component.html'
})
export class EmployeeComponent implements OnInit {
  attendanceHistory: AttendanceRecord[] = [];
  todayAttendance: AttendanceRecord | null = null;
  myLeaves: LeaveRequest[] = [];

  leaveRequest: ApplyLeaveRequest = { startDate: '', endDate: '', leaveType: 'SICK', reason: '' };

  // Statistics
  totalLeavesTaken = 0;
  pendingLeavesCount = 0;
  approvedLeavesCount = 0;

  // Loading States
  attendanceLoading = false;
  leaveLoading = false;

  message = '';

  constructor(private employeeService: EmployeeService, public authService: AuthService) { }

  ngOnInit() {
    this.refreshData();
  }

  refreshData() {
    this.getTodayAttendance();
    this.getHistory();
    this.getLeaves();
  }

  getTodayAttendance() {
    this.employeeService.getTodayAttendance().subscribe({
      next: (data) => this.todayAttendance = data,
      error: () => this.todayAttendance = null
    });
  }

  getHistory() {
    this.employeeService.getAttendanceHistory().subscribe(data => this.attendanceHistory = data);
  }

  getLeaves() {
    this.employeeService.getMyLeaves().subscribe(data => {
      this.myLeaves = data;
      this.totalLeavesTaken = data.length;
      this.pendingLeavesCount = data.filter(l => l.status === 'PENDING').length;
      this.approvedLeavesCount = data.filter(l => l.status === 'APPROVED').length;
    });
  }

  checkIn() {
    this.attendanceLoading = true;
    this.employeeService.checkIn().subscribe({
      next: () => {
        this.showToast('Checked in successfully!');
        this.refreshData();
        this.attendanceLoading = false;
      },
      error: (err: any) => {
        this.showToast(err.error?.message || 'Error checking in');
        this.attendanceLoading = false;
      }
    });
  }

  checkOut() {
    this.attendanceLoading = true;
    this.employeeService.checkOut().subscribe({
      next: () => {
        this.showToast('Checked out successfully!');
        this.refreshData();
        this.attendanceLoading = false;
      },
      error: (err: any) => {
        this.showToast(err.error?.message || 'Error checking out');
        this.attendanceLoading = false;
      }
    });
  }

  applyLeave() {
    this.leaveLoading = true;
    this.employeeService.applyLeave(this.leaveRequest).subscribe({
      next: () => {
        this.showToast('Leave applied successfully!');
        this.leaveRequest = { startDate: '', endDate: '', leaveType: 'SICK', reason: '' };
        this.getLeaves();
        this.leaveLoading = false;
      },
      error: (err: any) => {
        this.showToast(err.error?.message || 'Error applying leave');
        this.leaveLoading = false;
      }
    });
  }

  showToast(msg: string) {
    this.message = msg;
    setTimeout(() => this.message = '', 4000); // hide after 4s
  }

  logout() {
    this.authService.logout();
  }
}
