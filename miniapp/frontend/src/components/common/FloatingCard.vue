<template>
  <div class="floating-island" :class="customClass">
    <slot />
  </div>
</template>

<script setup>
defineProps({
  customClass: { type: String, default: '' }
})
</script>
