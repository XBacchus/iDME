<template>
  <FloatingCard :custom-class="`operation-panel ${panelStateClass}`">
    <span class="operation-panel__code">{{ procedure.code }}</span>
    <span :class="statusClass" class="operation-panel__badge">{{ procedure.statusText }}</span>
    <h4 class="operation-panel__title">{{ procedure.name }}</h4>
    <p class="operation-panel__desc">{{ procedure.description }}</p>
    <div class="operation-panel__footer">
      <div class="operation-panel__owner">
        <div class="operation-panel__avatar">{{ procedure.operator }}</div>
        <span>责任人：{{ procedure.operatorName }}</span>
      </div>
      <span>{{ procedure.time }}</span>
    </div>
  </FloatingCard>
</template>

<script setup>
import { computed } from 'vue'
import FloatingCard from '../common/FloatingCard.vue'

const props = defineProps({
  procedure: { type: Object, required: true }
})

const panelStateClass = computed(() => {
  const classes = {
    1: 'is-running',
    0: 'is-waiting',
    2: 'is-complete'
  }
  return classes[props.procedure.status] || 'is-waiting'
})

const statusClass = computed(() => {
  const classes = {
    1: 'is-running',
    0: 'is-waiting',
    2: 'is-complete'
  }
  return classes[props.procedure.status] || 'is-waiting'
})
</script>

<style scoped>
.operation-panel {
  position: relative;
  padding: 24px;
  border-left: 3px solid rgba(255, 255, 255, 0.12);
}

.operation-panel.is-running {
  border-left-color: #10b981;
}

.operation-panel.is-complete {
  border-left-color: #3b82f6;
}

.operation-panel__code {
  position: absolute;
  top: 20px;
  right: 20px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.32);
}

.operation-panel__badge {
  display: inline-flex;
  border-radius: 999px;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: 700;
}

.operation-panel__badge.is-running {
  background: rgba(16, 185, 129, 0.12);
  color: #6ee7b7;
}

.operation-panel__badge.is-waiting {
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.72);
}

.operation-panel__badge.is-complete {
  background: rgba(59, 130, 246, 0.12);
  color: #93c5fd;
}

.operation-panel__title {
  margin: 16px 0 0;
  font-size: 18px;
  color: #f8fafc;
}

.operation-panel__desc {
  margin: 12px 0 0;
  min-height: 88px;
  font-size: 13px;
  line-height: 1.75;
  color: rgba(255, 255, 255, 0.62);
}

.operation-panel__footer {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  font-size: 12px;
  color: rgba(255, 255, 255, 0.54);
}

.operation-panel__owner {
  display: flex;
  align-items: center;
  gap: 8px;
}

.operation-panel__avatar {
  display: inline-flex;
  width: 24px;
  height: 24px;
  align-items: center;
  justify-content: center;
  border-radius: 999px;
  background: rgba(59, 130, 246, 0.16);
  color: #93c5fd;
  font-size: 10px;
  font-weight: 700;
}

:global(.app-shell.theme-bright .operation-panel) {
  border-left-color: #e2e8f0;
}

:global(.app-shell.theme-bright .operation-panel.is-running) {
  border-left-color: #f43f5e;
}

:global(.app-shell.theme-bright .operation-panel.is-complete) {
  border-left-color: #0f766e;
}

:global(.app-shell.theme-bright .operation-panel__code) {
  color: #94a3b8;
}

:global(.app-shell.theme-bright .operation-panel__badge.is-running) {
  background: #f8fafc;
  color: #f43f5e;
  border: 1px solid #e2e8f0;
}

:global(.app-shell.theme-bright .operation-panel__badge.is-waiting) {
  background: #f8fafc;
  color: #475569;
  border: 1px solid #e2e8f0;
}

:global(.app-shell.theme-bright .operation-panel__badge.is-complete) {
  background: #ecfdf5;
  color: #059669;
  border: 1px solid #d1fae5;
}

:global(.app-shell.theme-bright .operation-panel__title) {
  color: #0f172a;
}

:global(.app-shell.theme-bright .operation-panel__desc) {
  color: #475569;
}

:global(.app-shell.theme-bright .operation-panel__footer) {
  border-top-color: #e2e8f0;
  color: #64748b;
}

:global(.app-shell.theme-bright .operation-panel__avatar) {
  background: #fff1f2;
  color: #e11d48;
}

@media (max-width: 768px) {
  .operation-panel__footer {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>
