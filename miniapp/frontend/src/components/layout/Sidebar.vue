<template>
  <aside class="arc-sidebar flex flex-col h-full shrink-0 w-60">
    <div class="p-6">
      <div class="flex items-center gap-3">
        <div class="w-8 h-8 bg-blue-500 rounded-sm flex items-center justify-center font-bold text-lg">I</div>
        <span class="font-semibold tracking-tight text-lg">IMS Engine</span>
      </div>
    </div>

    <nav class="flex-1 px-3 space-y-1 custom-scrollbar overflow-y-auto">
      <p class="px-3 py-2 text-xs font-bold text-gray-500 uppercase tracking-widest">工作区</p>
      <router-link to="/" class="nav-item flex items-center px-3 py-2.5 text-sm transition-all group" active-class="active-nav">
        <CircleIcon size="24px" color-class="bg-gray-700 mr-3 group-hover:scale-110 transition-transform">
          <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 6h16M4 12h16m-7 6H7"/></svg>
        </CircleIcon>
        首页控制台
      </router-link>
      <router-link to="/parts" class="nav-item flex items-center px-3 py-2.5 text-sm transition-all group" active-class="active-nav">
        <CircleIcon size="24px" color-class="bg-gray-700 mr-3 group-hover:scale-110 transition-transform text-green-400">
          <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/></svg>
        </CircleIcon>
        物料管理
      </router-link>
      <router-link to="/equipments" class="nav-item flex items-center px-3 py-2.5 text-sm transition-all group" active-class="active-nav">
        <CircleIcon size="24px" color-class="bg-gray-700 mr-3 group-hover:scale-110 transition-transform text-blue-400">
          <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z"/></svg>
        </CircleIcon>
        设备管理
      </router-link>
      <router-link to="/working-plans" class="nav-item flex items-center px-3 py-2.5 text-sm transition-all group" active-class="active-nav">
        <CircleIcon size="24px" color-class="bg-gray-700 mr-3 group-hover:scale-110 transition-transform text-cyan-400">
          <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2m-3 7h3m-3 4h3m-6-4h.01M9 16h.01"/></svg>
        </CircleIcon>
        工艺路线
      </router-link>
      <router-link to="/procedures" class="nav-item flex items-center px-3 py-2.5 text-sm transition-all group" active-class="active-nav">
        <CircleIcon size="24px" color-class="bg-gray-700 mr-3 group-hover:scale-110 transition-transform text-purple-400">
          <svg class="w-3 h-3" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M11 4a2 2 0 114 0v1a1 1 0 001 1h3a1 1 0 011 1v3a1 1 0 01-1 1h-1a2 2 0 100 4h1a1 1 0 011 1v3a1 1 0 01-1 1h-3a1 1 0 01-1-1v-1a2 2 0 10-4 0v1a1 1 0 01-1 1H7a1 1 0 01-1-1v-3a1 1 0 011-1h1a2 2 0 100-4H7a1 1 0 01-1-1V7a1 1 0 011-1h3a1 1 0 001-1V4z"/></svg>
        </CircleIcon>
        工序配置
      </router-link>
    </nav>

    <div class="p-4 border-t border-color mt-auto">
      <div class="flex items-center gap-3 px-2 py-3 bg-white/5 rounded-lg">
        <div class="w-8 h-8 rounded-full bg-indigo-500 flex items-center justify-center text-xs font-bold">U</div>
        <div class="flex-1 overflow-hidden">
          <p class="text-xs font-medium truncate">用户</p>
          <p class="text-[10px] text-gray-500">管理员</p>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
import CircleIcon from '../common/CircleIcon.vue'
</script>
