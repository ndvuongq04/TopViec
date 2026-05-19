import type { RouteRecordRaw } from 'vue-router'

export const recruiterRoutes: RouteRecordRaw[] = [
  {
    path: '/recruiter/checkout',
    name: 'recruiter-checkout',
    component: () => import('@/pages/recruiter/CheckoutPage.vue'),
    meta: { requiresAuth: true, role: 'EMPLOYER' },
  },
  {
    path: '/payment/result',
    name: 'payment-result',
    component: () => import('@/pages/recruiter/PaymentResultPage.vue'),
    meta: { requiresAuth: true, role: 'EMPLOYER' },
  },
  {
    path: '/recruiter',
    component: () => import('@/layouts/recruiter/RecruiterLayout.vue'),
    meta: { requiresAuth: true, role: 'EMPLOYER' },
    children: [
      {
        path: '',
        name: 'recruiter-dashboard',
        component: () => import('@/pages/recruiter/HomePage.vue'),
      },
      {
        path: 'company-profile',
        name: 'recruiter-company-profile',
        component: () => import('@/pages/recruiter/CompanyProfilePage.vue'),
      },
      {
        path: 'team',
        name: 'recruiter-team',
        component: () => import('@/pages/recruiter/TeamPage.vue'),
      },
      {
        path: 'assignment',
        name: 'recruiter-assignment',
        component: () => import('@/pages/recruiter/AssignmentPage.vue'),
      },
      {
        path: 'permissions',
        name: 'recruiter-permissions',
        component: () => import('@/pages/recruiter/PermissionConfigPage.vue'),
      },
      {
        path: 'permissions/log',
        name: 'recruiter-permissions-log',
        component: () => import('@/pages/recruiter/PermissionLogPage.vue'),
      },
      {
        path: 'jobs',
        name: 'recruiter-jobs',
        component: () => import('@/pages/recruiter/JobPostingsPage.vue'),
      },
      {
        path: 'jobs/create',
        name: 'recruiter-jobs-create',
        component: () => import('@/pages/recruiter/CreateJobPage.vue'),
      },
      {
        path: 'jobs/:id',
        name: 'recruiter-jobs-detail',
        component: () => import('@/pages/recruiter/JobDetailPage.vue'),
      },
      {
        path: 'jobs/:id/edit',
        name: 'recruiter-jobs-edit',
        component: () => import('@/pages/recruiter/EditJobPage.vue'),
      },
      {
        path: 'jobs/:id/applications',
        name: 'recruiter-job-applications',
        component: () => import('@/pages/recruiter/JobApplicationsPage.vue'),
      },
      {
        path: 'jobs/:id/applications/:applicationId',
        name: 'recruiter-application-detail',
        component: () => import('@/pages/recruiter/CandidateApplicationDetailPage.vue'),
      },
      {
        path: 'jobs/:id/interview-setup',
        name: 'recruiter-job-interview-setup',
        component: () => import('@/pages/recruiter/JobInterviewSetupPage.vue'),
      },
      {
        path: 'jobs/:id/interview-stages',
        name: 'recruiter-job-interview-stages',
        component: () => import('@/pages/recruiter/Interviewstagespage.vue'),
      },
      {
        path: 'jobs/:id/interview-candidate/:applicationId',
        name: 'recruiter-job-interview-candidate-detail',
        component: () => import('@/pages/recruiter/Candidateinterviewdetailpage.vue'),
      },
      {
        path: 'interviews',
        name: 'recruiter-interviews',
        component: () => import('@/pages/recruiter/InterviewManagementPage.vue'),
      },
      {
        path: 'interviews/create',
        name: 'recruiter-interview-create',
        component: () => import('@/pages/recruiter/InterviewSchedulePage.vue'),
      },
      {
        path: 'interviews/propose',
        name: 'recruiter-interview-propose',
        component: () => import('@/pages/recruiter/InterviewProposeSlotsPage.vue'),
      },
      {
        path: 'interviews/sent-proposals',
        name: 'recruiter-interview-sent-proposals',
        component: () => import('@/pages/recruiter/SentProposalsPage.vue'),
      },
      {
        path: 'interviews/:id/evaluate',
        name: 'recruiter-interview-evaluate',
        component: () => import('@/pages/recruiter/InterviewEvaluationPage.vue'),
      },
      {
        path: 'interviews/:id/overdue',
        name: 'recruiter-interview-overdue',
        component: () => import('@/pages/recruiter/InterviewOverduePage.vue'),
      },
      {
        path: 'services',
        name: 'recruiter-services',
        component: () => import('@/pages/recruiter/ServicesPage.vue'),
      },
      {
        path: 'services/shop',
        name: 'recruiter-service-shop',
        component: () => import('@/pages/recruiter/ServiceShopPage.vue'),
      },
      {
        path: 'pricing',
        name: 'recruiter-pricing',
        component: () => import('@/pages/recruiter/PricingPage.vue'),
      },
      {
        path: 'billing',
        name: 'recruiter-billing',
        component: () => import('@/pages/recruiter/BillingHistoryPage.vue'),
      },
      {
        path: 'offers',
        name: 'recruiter-offers',
        component: () => import('@/pages/recruiter/OfferManagementPage.vue'),
      },
      {
        path: 'jobs/:id/offers',
        name: 'recruiter-job-offers',
        component: () => import('@/pages/recruiter/OfferManagementPage.vue'),
      },
      {
        path: 'talent-pool',
        name: 'recruiter-talent-pool',
        component: () => import('@/pages/recruiter/TalentPoolPage.vue'),
      },
      {
        path: 'messages',
        name: 'recruiter-messages',
        component: () => import('@/pages/recruiter/MessagesPage.vue'),
        meta: { fullPage: true },
      },
      {
        path: 'complaints',
        name: 'recruiter-complaints',
        component: () => import('@/pages/recruiter/ComplaintsPage.vue'),
      },
      {
        path: 'complaints/:id',
        name: 'recruiter-complaint-detail',
        component: () => import('@/pages/recruiter/ComplaintDetailPage.vue'),
      },
      {
        path: 'activity-log',
        name: 'recruiter-activity-log',
        component: () => import('@/pages/recruiter/ActivityLogPage.vue'),
      },
      {
        path: 'activity-log/:type/:id',
        name: 'recruiter-activity-log-detail',
        component: () => import('@/pages/recruiter/ActivityLogDetailPage.vue'),
      },
    ],
  },
]
