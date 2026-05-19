<script setup lang="ts">
import LoginIllustration from "@/components/auth/LoginIllustration.vue";
import LoginForm from "@/components/auth/LoginForm.vue";

function handleGoogleLogin() {
  // TODO: implement Google OAuth flow
}
</script>

<template>
  <div
    class="flex min-h-screen items-center justify-center bg-background-light p-4 dark:bg-background-dark lg:p-0"
  >
    <div
      class="flex w-full max-w-4xl overflow-hidden rounded-xl bg-white shadow-2xl dark:bg-slate-900 lg:min-h-[600px]"
    >
      <LoginIllustration />

      <LoginForm @google-login="handleGoogleLogin" />
    </div>
  </div>
</template>