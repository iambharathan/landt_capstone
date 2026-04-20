export interface AttendanceRecord {
    id: number;
    userId: number;
    date: string;
    checkInTime: string;
    checkOutTime: string | null;
    status: string;
}

export interface LeaveRequest {
    id: number;
    userId?: number;
    user?: any;
    startDate: string;
    endDate: string;
    leaveType: string;
    reason: string;
    status: 'PENDING' | 'APPROVED' | 'REJECTED';
    rejectionReason?: string;
}

export interface ApplyLeaveRequest {
    startDate: string;
    endDate: string;
    leaveType: string;
    reason: string;
}

export interface Policy {
    id: number;
    leaveType: string;
    maxDays: number;
    rules: string;
    isActive: boolean;
}

export interface AttendanceReport {
    date: string;
    checkedInToday: number;
    presentTotal: number;
    absentTotal: number;
    lateTotal: number;
}

export interface LeaveReport {
    pending: number;
    approved: number;
    rejected: number;
    total: number;
}

export interface ComprehensiveReport {
    totalUsers: number;
    attendance: AttendanceReport;
    leaves: LeaveReport;
}
