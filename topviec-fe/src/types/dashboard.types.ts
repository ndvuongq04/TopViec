export interface ResSuperAdminDashboardDTO {
  totalActiveUsers: number;
  totalActiveCompanies: number;
  totalPublishedJobs: number;
  monthlyRevenue: number;
  userGrowth: DailyUserGrowth[];
  revenueByMonth: MonthlyRevenue[];
  recentActivities: RecentAdminActivity[];
}

export interface DailyUserGrowth {
  date: string;
  candidateCount: number;
  employerCount: number;
}

export interface MonthlyRevenue {
  month: string; // YYYY-MM
  totalAmount: number;
}

export interface RecentAdminActivity {
  id?: number;
  adminName: string;
  action: string;
  targetEntity: string | null;
  createdAt: string;
}

export interface ResContentModeratorDashboardDTO {
  pendingApprovalJobs: number;
  pendingVerifyCompanies: number;
  rejectedJobsThisMonth: number;
  moderationStats: DailyModerationStat[];
  oldestPendingJobs: OldestPendingJob[];
}

export interface DailyModerationStat {
  date: string;
  approvedCount: number;
  rejectedCount: number;
}

export interface OldestPendingJob {
  jobId: number;
  title: string;
  companyName: string | null;
  createdAt: string;
  waitingDays: number;
}

export interface ResSupportAdminDashboardDTO {
  pendingComplaints: number;
  pendingAppeals: number;
  restrictedEmployers: number;
  complaintsByStatus: ComplaintStatusCounts;
  urgentComplaints: UrgentComplaint[];
}

export interface ComplaintStatusCounts {
  pending: number;
  processing: number;
  waiting_employer: number;
  resolved: number;
  rejected: number;
  auto_closed: number;
}

export interface UrgentComplaint {
  complaintId: number;
  reportCode: string;
  priority: string;
  complaintType: string;
  companyName: string | null;
  waitingHours: number;
  createdAt: string;
}

export interface ResFinanceAdminDashboardDTO {
  monthlyRevenue: number;
  pendingOrders: number;
  refundRequests: number;
  revenueByMonth: MonthlyRevenue[];
  actionableOrders: ActionableOrder[];
}

export interface ActionableOrder {
  orderId: number;
  orderCode: string;
  companyName: string | null;
  totalAmount: number;
  status: 'pending' | 'refund_requested';
  createdAt: string;
}
