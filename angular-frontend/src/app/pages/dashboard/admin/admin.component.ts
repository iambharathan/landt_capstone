import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AdminService } from '../../../services/admin.service';
import { AuthService } from '../../../services/auth.service';
import { User } from '../../../models/auth.models';
import { ComprehensiveReport } from '../../../models/domain.models';

@Component({
  selector: 'app-admin',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './admin.component.html'
})
export class AdminComponent implements OnInit {
  users: User[] = [];
  managers: User[] = [];
  report: ComprehensiveReport | null = null;
  newUser: any = { username: '', email: '', password: '', fullName: '', role: 'EMPLOYEE', managerId: null };
  message = '';

  constructor(private adminService: AdminService, public authService: AuthService) { }

  ngOnInit() {
    this.refreshData();
  }

  refreshData() {
    this.adminService.getAllUsers().subscribe(data => {
      this.users = data;
      this.managers = data.filter(u => u.role === 'MANAGER');
    });
    this.adminService.getComprehensiveReport().subscribe(data => this.report = data);
  }

  createUser() {
    this.adminService.createUser(this.newUser).subscribe({
      next: () => {
        this.message = 'User created successfully!';
        this.newUser = { username: '', email: '', password: '', fullName: '', role: 'EMPLOYEE', managerId: null };
        this.refreshData();
      },
      error: (err: any) => this.message = err.error?.message || 'Error creating user'
    });
  }

  updateManager(user: User) {
    this.adminService.updateUser(user.id, user).subscribe({
      next: () => this.message = `Manager updated for ${user.fullName}`,
      error: (err: any) => this.message = err.error?.message || 'Error updating manager'
    });
  }

  logout() {
    this.authService.logout();
  }
}
