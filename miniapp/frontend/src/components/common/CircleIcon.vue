<template>
  <div class="circle-icon" :class="colorClass" :style="{ width: size, height: size }">
    <slot />
  </div>
</template>

<script setup>
defineProps({
  size: { type: String, default: '40px' },
  colorClass: { type: String, default: 'bg-blue-500/10 text-blue-400' }
})
</script>
