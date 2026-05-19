import type { RouteRecordRaw } from 'vue-router'

export const candidateRoutes: RouteRecordRaw[] = [
    {
        path: '/',
        component: () => import('@/layouts/candidate/CandidateLayout.vue'),
        children: [
            {
                path: '',
                name: 'home',
                component: () => import('@/pages/candidate/HomePage.vue'),
            },
            {
                path: 'profile',
                name: 'CandidateProfile',
                component: () => import('@/pages/candidate/ProfilePage.vue'),
                meta: { requiresAuth: true, role: 'CANDIDATE' },
            },
            {
                path: 'applied-jobs',
                name: 'AppliedJobs',
                component: () => import('@/pages/candidate/AppliedJobsPage.vue'),
                meta: { requiresAuth: true, role: 'CANDIDATE' },
            },
            {
                path: 'saved-jobs',
                name: 'SavedJobs',
                component: () => import('@/pages/candidate/SavedJobsPage.vue'),
                meta: { requiresAuth: true, role: 'CANDIDATE' },
            },
            {
                path: 'search',
                name: 'JobSearch',
                component: () => import('@/pages/candidate/SearchPage.vue'),
            },
            {
                path: 'jobs/:id',
                name: 'JobDetail',
                component: () => import('@/pages/candidate/JobDetailPage.vue'),
            },
            {
                path: 'companies/:slug',
                name: 'CompanyDetail',
                component: () => import('@/pages/candidate/CompanyDetailPage.vue'),
            },
            {
                path: 'interviews',
                name: 'CandidateInterviews',
                component: () => import('@/pages/candidate/InterviewsPage.vue'),
                meta: { requiresAuth: true, role: 'CANDIDATE' },
            },
            {
                path: 'interviews/detail/:id',
                name: 'CandidateInterviewDetail',
                component: () => import('@/pages/candidate/InterviewDetailPage.vue'),
                meta: { requiresAuth: true, role: 'CANDIDATE' },
            },
            {
                path: 'messages',
                name: 'CandidateMessages',
                component: () => import('@/pages/candidate/MessagingPage.vue'),
                meta: { requiresAuth: true, role: 'CANDIDATE' },
            },
            {
                path: 'change-password',
                name: 'CandidateChangePassword',
                component: () => import('@/pages/candidate/ChangePasswordPage.vue'),
                meta: { requiresAuth: true, role: 'CANDIDATE' },
            },
            {
                path: 'my-complaints',
                name: 'CandidateComplaints',
                component: () => import('@/pages/candidate/ComplaintsPage.vue'),
                meta: { requiresAuth: true, role: 'CANDIDATE' },
            },
            {
                path: 'cv-templates',
                name: 'CvTemplateList',
                component: () => import('@/pages/candidate/CvTemplateListPage.vue'),
            },
            {
                path: 'cv-online/draft/:localDraftId',
                name: 'CvOnlineEditor',
                component: () => import('@/pages/candidate/CvOnlineEditorPage.vue'),
                meta: { requiresAuth: true, role: 'CANDIDATE' },
            },
            {
                path: 'cv-online/:id/edit',
                name: 'CvOnlineEditorLegacy',
                component: () => import('@/pages/candidate/CvOnlineEditorPage.vue'),
                meta: { requiresAuth: true, role: 'CANDIDATE' },
            },
        ],
    },
]
