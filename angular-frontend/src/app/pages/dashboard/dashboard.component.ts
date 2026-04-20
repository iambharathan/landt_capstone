import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-dashboard',
    standalone: true,
    template: `<div class="container mt-5 text-center"><h4>Redirecting to your dashboard...</h4></div>`
})
export class DashboardComponent implements OnInit {
    constructor(private authService: AuthService, private router: Router) { }

    ngOnInit() {
        const role = this.authService.getRole();
        if (role === 'ADMIN') {
            this.router.navigate(['/dashboard/admin']);
        } else if (role === 'MANAGER') {
            this.router.navigate(['/dashboard/manager']);
        } else if (role === 'EMPLOYEE') {
            this.router.navigate(['/dashboard/employee']);
        } else {
            this.authService.logout();
        }
    }
}
