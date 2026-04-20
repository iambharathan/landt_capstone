import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router, RouterModule } from '@angular/router';
import { AuthService } from '../../services/auth.service';
import { RegisterRequest } from '../../models/auth.models';

@Component({
    selector: 'app-register',
    standalone: true,
    imports: [CommonModule, FormsModule, RouterModule],
    templateUrl: './register.component.html'
})
export class RegisterComponent {
    user: RegisterRequest = {
        username: '',
        email: '',
        password: '',
        fullName: '',
        role: 'EMPLOYEE'
    };

    message = '';
    success = false;
    loading = false;

    constructor(private authService: AuthService, private router: Router) { }

    onRegister() {
        this.loading = true;
        this.message = '';
        this.success = false;

        this.authService.register(this.user).subscribe({
            next: () => {
                this.success = true;
                this.message = 'Account created! Redirecting securely...';
                setTimeout(() => {
                    this.loading = false;
                    this.router.navigate(['/login']);
                }, 1500);
            },
            error: (err: any) => {
                this.loading = false;
                this.success = false;
                this.message = err.error?.message || 'Registration failed. Please try again.';
            }
        });
    }
}
