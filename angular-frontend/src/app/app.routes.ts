import { Routes } from '@angular/router';
import { LoginComponent } from './pages/login/login.component';
import { RegisterComponent } from './pages/register/register.component';
import { authGuard, roleGuard } from './guards/auth.guard';

export const routes: Routes = [
    { path: 'login', component: LoginComponent },
    { path: 'register', component: RegisterComponent },
    {
        path: 'dashboard',
        loadComponent: () => import('./pages/dashboard/dashboard.component').then(m => m.DashboardComponent),
        canActivate: [authGuard]
    },
    {
        path: 'dashboard/employee',
        loadComponent: () => import('./pages/dashboard/employee/employee.component').then(m => m.EmployeeComponent),
        canActivate: [roleGuard('EMPLOYEE')]
    },
    {
        path: 'dashboard/manager',
        loadComponent: () => import('./pages/dashboard/manager/manager.component').then(m => m.ManagerComponent),
        canActivate: [roleGuard('MANAGER')]
    },
    {
        path: 'dashboard/admin',
        loadComponent: () => import('./pages/dashboard/admin/admin.component').then(m => m.AdminComponent),
        canActivate: [roleGuard('ADMIN')]
    },
    { path: '', redirectTo: 'login', pathMatch: 'full' },
    { path: '**', redirectTo: 'login' }
];
