<template>
  <div class="category-tree">
    <div v-for="(node, idx) in data" :key="idx" class="tree-node">
      <div class="node-content" :class="{ highlight: isHighlighted(node) }">
        <div class="flex items-center gap-2">
          <svg v-if="node.children?.length" class="w-4 h-4 text-blue-400" fill="currentColor" viewBox="0 0 24 24">
            <path d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z"/>
          </svg>
          <svg v-else class="w-4 h-4 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z"/>
          </svg>
          <span class="text-white font-medium">{{ node.name }}</span>
        </div>
        <div class="flex gap-2">
          <el-button link size="small" @click="$emit('add', node)">添加</el-button>
          <el-button link size="small" @click="$emit('edit', node)">编辑</el-button>
          <el-button link type="danger" size="small" @click="$emit('delete', node)">删除</el-button>
        </div>
      </div>
      <div v-if="node.children?.length" class="tree-children">
        <CategoryTree :data="node.children" :search-keyword="searchKeyword" @add="$emit('add', $event)" @edit="$emit('edit', $event)" @delete="$emit('delete', $event)" />
      </div>
    </div>
  </div>
</template>

<script setup>
const props = defineProps({
  data: Array,
  searchKeyword: { type: String, default: '' }
})
defineEmits(['add', 'edit', 'delete'])

const isHighlighted = (node) => {
  if (!props.searchKeyword) return false
  return node.name?.includes(props.searchKeyword)
}
</script>

<style scoped>
.tree-node {
  position: relative;
  margin-bottom: 0.5rem;
}

.tree-node::before {
  content: '';
  position: absolute;
  left: -1.25rem;
  top: 0;
  bottom: 0.5rem;
  width: 1px;
  background: rgba(94, 129, 172, 0.3);
}

.tree-node::after {
  content: '';
  position: absolute;
  left: -1.25rem;
  top: 1.25rem;
  width: 1rem;
  height: 1px;
  background: rgba(94, 129, 172, 0.3);
}

.tree-node:last-child::before {
  bottom: calc(100% - 1.25rem);
}

.node-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.75rem 1rem;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 4px;
  transition: all 0.3s;
}

.node-content:hover {
  background: rgba(255, 255, 255, 0.06);
  border-color: rgba(94, 129, 172, 0.5);
}

.node-content.highlight {
  border-color: #FFD700;
  box-shadow: 0 0 10px rgba(255, 215, 0, 0.3);
}

.tree-children {
  margin-left: 2rem;
  margin-top: 0.5rem;
}
</style>
