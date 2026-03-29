<template>
  <aside class="sidebar" :class="{ 'is-bright-theme': isBrightTheme }">
    <div class="brand">IMS Engine</div>

    <nav class="nav-group custom-scrollbar">
      <section v-for="section in navSections" :key="section.label">
        <div class="nav-label">{{ section.label }}</div>
        <router-link
          v-for="item in section.items"
          :key="item.to"
          :to="item.to"
          class="nav-item"
          :class="{ active: isActive(item) }"
        >
          <svg class="nav-icon" fill="none" stroke="currentColor" viewBox="0 0 24 24" v-html="item.icon"></svg>
          <span>{{ item.label }}</span>
        </router-link>
      </section>
    </nav>

    <div class="sidebar-footer">
      <button type="button" class="theme-toggle" :class="{ bright: isBrightTheme }" @click="emit('toggle-bright-theme')">
        {{ isBrightTheme ? '切回暗色' : '切换亮色' }}
      </button>

      <div class="user-card">
        <div class="user-avatar">AD</div>
        <div class="user-meta">
          <p>Admin</p>
          <p>Enterprise Plan</p>
        </div>
      </div>
    </div>
  </aside>
</template>

<script setup>
import { useRoute } from 'vue-router'

defineProps({
  isBrightTheme: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['toggle-bright-theme'])
const route = useRoute()

const navSections = [
  {
    label: 'OPERATIONS',
    items: [
      {
        to: '/',
        label: '控制台',
        exact: true,
        icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.9" d="M4 4h7v7H4zm9 0h7v7h-7zM4 13h7v7H4zm9 0h7v7h-7z" />'
      },
      {
        to: '/parts',
        label: '物料管理',
        icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />'
      },
      {
        to: '/equipments',
        label: '智能资产',
        icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />'
      }
    ]
  },
  {
    label: 'MANUFACTURING',
    items: [
      {
        to: '/working-plans',
        label: '工艺路线',
        icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />'
      },
      {
        to: '/procedures',
        label: '工序配置',
        icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M11 4a2 2 0 114 0v1a1 1 0 001 1h3a1 1 0 011 1v3a1 1 0 01-1 1h-1a2 2 0 100 4h1a1 1 0 011 1v3a1 1 0 01-1 1h-3a1 1 0 01-1-1v-1a2 2 0 10-4 0v1a1 1 0 01-1 1H7a1 1 0 01-1-1v-3a1 1 0 011-1h1a2 2 0 100-4H7a1 1 0 01-1-1V7a1 1 0 011-1h3a1 1 0 001-1V4z" />'
      }
    ]
  }
]

const isActive = (item) => {
  if (item.exact) {
    return route.path === item.to
  }

  return route.path === item.to || route.path.startsWith(`${item.to}/`)
}
</script>

<style scoped>
.sidebar {
  width: 240px;
  background: #11141a;
  border-right: 1px solid #262c3a;
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
}

.sidebar.is-bright-theme {
  background: #ffffff;
  border-right-color: #e2e8f0;
}

.brand {
  padding: 30px 24px;
  font-size: 20px;
  font-weight: 800;
  color: #f43f5e;
}

.nav-group {
  flex: 1;
  padding: 0 12px;
  overflow-y: auto;
}

.nav-label {
  padding: 15px 12px;
  font-size: 11px;
  font-weight: 700;
  color: #94a3b8;
  text-transform: uppercase;
}

.sidebar.is-bright-theme .nav-label {
  color: #94a3b8;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 4px;
  padding: 12px;
  border-radius: 10px;
  color: #e2e8f0;
  font-size: 14px;
  text-decoration: none;
  transition: background-color 0.2s ease, color 0.2s ease, transform 0.2s ease;
}

.sidebar.is-bright-theme .nav-item {
  color: #0f172a;
}

.nav-item:hover {
  background: rgba(255, 255, 255, 0.04);
  transform: translateX(2px);
}

.sidebar.is-bright-theme .nav-item:hover {
  background: #f8fafc;
}

.nav-item.active {
  background: #f43f5e;
  color: #ffffff;
  font-weight: 600;
}

.sidebar.is-bright-theme .nav-item.active {
  margin-right: 15px;
  border-radius: 0 100px 100px 0;
}

.nav-icon {
  width: 18px;
  height: 18px;
  flex-shrink: 0;
}

.sidebar-footer {
  padding: 16px 20px 20px;
  border-top: 1px solid #262c3a;
}

.sidebar.is-bright-theme .sidebar-footer {
  border-top-color: #e2e8f0;
}

.theme-toggle {
  width: 100%;
  margin-bottom: 12px;
  border: 1px solid rgba(244, 63, 94, 0.24);
  border-radius: 10px;
  padding: 10px 12px;
  background: rgba(244, 63, 94, 0.08);
  color: #fecdd3;
  font-size: 12px;
  font-weight: 700;
  cursor: pointer;
}

.theme-toggle.bright {
  background: #fee2e2;
  color: #be123c;
}

.user-card {
  display: flex;
  align-items: center;
  gap: 12px;
}

.user-avatar {
  display: flex;
  width: 32px;
  height: 32px;
  align-items: center;
  justify-content: center;
  border-radius: 8px;
  background: #f43f5e;
  color: #ffffff;
  font-size: 12px;
  font-weight: 800;
}

.user-meta p {
  margin: 0;
}

.user-meta p:first-child {
  font-size: 13px;
  font-weight: 700;
  color: #f8fafc;
}

.sidebar.is-bright-theme .user-meta p:first-child {
  color: #0f172a;
}

.user-meta p:last-child {
  margin-top: 2px;
  font-size: 10px;
  color: #94a3b8;
}

.sidebar.is-bright-theme .user-meta p:last-child {
  color: #475569;
}

@media (max-width: 960px) {
  .sidebar {
    width: 88px;
  }

  .brand,
  .nav-label,
  .user-meta,
  .theme-toggle {
    display: none;
  }

  .nav-group {
    padding-top: 16px;
  }

  .nav-item,
  .sidebar-footer {
    justify-content: center;
  }
}
</style>
