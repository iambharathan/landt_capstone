import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AuthService } from '../../services/auth.service';

@Component({
    selector: 'app-login',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './login.component.html'
})
export class LoginComponent {
    loginObj: any = {
        email: '',
        password: ''
    };

    message: string = '';
    loading: boolean = false;

    constructor(private authService: AuthService, private router: Router) { }

    onLogin() {
        this.loading = true;
        this.authService.login(this.loginObj).subscribe({
            next: (res) => {
                this.loading = false;
                if (res.role === 'ADMIN') {
                    this.router.navigate(['/dashboard/admin']);
                } else if (res.role === 'MANAGER') {
                    this.router.navigate(['/dashboard/manager']);
                } else {
                    this.router.navigate(['/dashboard/employee']);
                }
            },
            error: (err) => {
                this.loading = false;
                this.message = err.error?.message || 'Invalid credentials. Please try again.';
            }
        });
    }
}
console.log(environment.debug);