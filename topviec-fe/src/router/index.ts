// src/router/index.ts
import { createRouter, createWebHistory } from 'vue-router'
import { authRoutes } from './routes/auth.routes'
import { candidateRoutes } from './routes/candidate.routes'
import { recruiterRoutes } from './routes/recruiter.routes'
import { adminRoutes } from './routes/admin.routes'
import { setupGuards } from './guards'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    ...authRoutes,
    ...candidateRoutes,
    ...recruiterRoutes,
    ...adminRoutes,
    {
      path: '/forbidden',
      name: 'forbidden',
      component: () => import('@/pages/error/ForbiddenPage.vue'),
    },
    {
      path: '/:pathMatch(.*)*',
      name: 'not-found',
      component: () => import('@/pages/error/NotFoundPage.vue'),
    },
  ],
})

setupGuards(router)

export default router