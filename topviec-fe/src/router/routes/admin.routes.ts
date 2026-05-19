import type { RouteRecordRaw } from 'vue-router'

export const adminRoutes: RouteRecordRaw[] = [
    {
        path: '/admin',
        component: () => import('@/layouts/admin/AdminLayout.vue'),
        meta: { requiresAuth: true, role: 'ADMIN' },
        children: [
            {
                path: '',
                name: 'admin-home',
                component: () => import('@/pages/admin/HomePage.vue'),
            },
            {
                path: 'employers',
                name: 'admin-employers',
                component: () => import('@/pages/admin/EmployersPage.vue'),
            },
            {
                path: 'employers/:id',
                name: 'admin-employer-detail',
                component: () => import('@/pages/admin/EmployerDetailPage.vue'),
            },
            {
                path: 'candidates',
                name: 'admin-candidates',
                component: () => import('@/pages/admin/CandidatesPage.vue'),
            },
            {
                path: 'candidates/:id',
                name: 'admin-candidate-detail',
                component: () => import('@/pages/admin/CandidateDetailPage.vue'),
            },
            {
                path: 'admins',
                name: 'admin-manage-admins',
                component: () => import('@/pages/admin/AdminsPage.vue'),
            },
            {
                path: 'admins/:id',
                name: 'admin-detail',
                component: () => import('@/pages/admin/AdminDetailPage.vue'),
            },
            {
                path: 'admins/:id/edit',
                name: 'admin-edit',
                component: () => import('@/pages/admin/AdminEditPage.vue'),
            },
            {
                path: 'moderation',
                name: 'admin-moderation',
                component: () => import('@/pages/admin/AdminJobPostingsPage.vue'),
            },
            {
                path: 'moderation/:id',
                name: 'admin-job-posting-detail',
                component: () => import('@/pages/admin/AdminJobPostingDetailPage.vue'),
            },
            {
                path: 'service-packages',
                name: 'admin-service-packages',
                component: () => import('@/pages/admin/AdminPackagesPage.vue'),
            },
            {
                path: 'individual-services',
                name: 'admin-individual-services',
                component: () => import('@/pages/admin/RetailServicesPage.vue'),
            },
            {
                path: 'services/create',
                name: 'admin-service-create',
                component: () => import('@/pages/admin/ServiceCreatePage.vue'),
            },
            {
                path: 'orders',
                name: 'admin-orders',
                component: () => import('@/pages/admin/OrdersPage.vue'),
            },
            {
                path: 'orders/:id',
                name: 'admin-order-detail',
                component: () => import('@/pages/admin/OrderDetailPage.vue'),
            },
            {
                path: 'employer-monitor',
                name: 'admin-employer-monitor',
                component: () => import('@/pages/admin/SubscriberDetailPage.vue'),
            },
            {
                path: 'reports',
                name: 'admin-complaints',
                component: () => import('@/pages/admin/ComplaintsPage.vue'),
            },
            {
                path: 'reports/:id',
                name: 'admin-complaint-detail',
                component: () => import('@/pages/admin/ComplaintDetailPage.vue'),
            },
            {
                path: 'settings/permissions',
                name: 'admin-settings-permissions',
                component: () => import('@/pages/admin/PermissionConfigPage.vue'),
            },
            {
                path: 'cv-templates',
                name: 'admin-cv-templates',
                component: () => import('@/pages/admin/CvTemplatesPage.vue'),
            },
            {
                path: 'cv-templates/create',
                name: 'admin-cv-template-create',
                component: () => import('@/pages/admin/CvTemplateCreatePage.vue'),
            },
            {
                path: 'cv-templates/:id/edit',
                name: 'admin-cv-template-edit',
                component: () => import('@/pages/admin/CvTemplateCreatePage.vue'),
            },
            {
                path: 'audit-logs',
                name: 'admin-audit-logs',
                component: () => import('@/pages/admin/AuditLogPage.vue'),
            },
            {
                path: 'audit-logs/:type/:id',
                name: 'admin-audit-log-detail',
                component: () => import('@/pages/admin/AuditLogDetailPage.vue'),
            },
        ],
    },
]
