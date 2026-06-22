<template>
  <section class="admin-shell">
    <AppSidebar />
    <main class="app-main">
      <AppHeader />
      <section class="content-wrap">
        <router-view />
      </section>
    </main>
  </section>
</template>

<script setup>
import AppHeader from '@/components/AppHeader.vue'
import AppSidebar from '@/components/AppSidebar.vue'
</script>