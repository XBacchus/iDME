<template>
  <div class="category-tree">
    <div v-for="(node, idx) in data" :key="idx" class="category-node mb-2">
      <div class="flex items-center justify-between p-3 bg-white/5 rounded hover:bg-white/10 transition-colors">
        <div class="flex items-center gap-2">
          <span class="text-white">{{ node.name }}</span>
        </div>
        <div class="flex gap-2">
          <el-button link type="primary" size="small" @click="$emit('add', node)">添加子分类</el-button>
          <el-button link type="primary" size="small" @click="$emit('edit', node)">编辑</el-button>
          <el-button link type="danger" size="small" @click="$emit('delete', node)">删除</el-button>
        </div>
      </div>
      <div v-if="node.children?.length" class="ml-8 mt-2">
        <CategoryTree :data="node.children" @add="$emit('add', $event)" @edit="$emit('edit', $event)" @delete="$emit('delete', $event)" />
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({ data: Array })
defineEmits(['add', 'edit', 'delete'])
</script>
