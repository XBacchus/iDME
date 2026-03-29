<template>
  <div class="bom-tree">
    <div v-for="(node, idx) in data" :key="idx" class="bom-node">
      <div class="flex items-center justify-between p-3 border-l-2 border-blue-500 bg-white/5 rounded mb-2">
        <div class="flex items-center gap-3">
          <span class="text-white">{{ node.partName }}</span>
          <span class="text-xs text-gray-400">{{ node.partNo }}</span>
          <span class="text-xs text-blue-400">数量: {{ node.quantity }}</span>
        </div>
        <div class="flex gap-2">
          <el-button link type="primary" size="small" @click="$emit('add', node)">添加子项</el-button>
          <el-button link type="danger" size="small" @click="$emit('delete', node, null)">删除</el-button>
        </div>
      </div>
      <div v-if="node.children?.length" class="ml-8">
        <BOMTree :data="node.children" @add="$emit('add', $event)" @delete="(n) => $emit('delete', n, node)" />
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ data: Array })
defineEmits(['add', 'delete'])
</script>
