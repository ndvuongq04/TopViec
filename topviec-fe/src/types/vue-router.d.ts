// src/types/vue-router.d.ts
// Mở rộng RouteMeta của vue-router để TypeScript nhận diện requiresAuth, requiresGuest, roles.

import type { CustomRouteMeta } from './router.types'

declare module 'vue-router' {
  interface RouteMeta extends CustomRouteMeta {}
}
