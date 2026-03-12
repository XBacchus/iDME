<template>
  <div class="space-y-8">
    <!-- 四个模块卡片 -->
    <section class="grid grid-cols-4 gap-6">
      <div v-for="module in modules" :key="module.name"
           class="floating-island p-6 hover:translate-y-[-4px] transition-all cursor-pointer group"
           @click="$router.push(module.path)">
        <div :class="module.color" class="w-10 h-10 circle-icon mb-4 group-hover:scale-110 transition-transform">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" v-html="module.icon"/>
        </div>
        <h3 class="font-semibold mb-1">{{ module.name }}</h3>
        <p class="text-xs text-gray-500">{{ module.desc }}</p>
      </div>
    </section>

    <!-- 物料库存列表 -->
    <section class="floating-island overflow-hidden">
      <div class="p-4 border-b border-color flex justify-between items-center bg-white/5">
        <h2 class="text-sm font-bold tracking-wider uppercase text-gray-400">物料库存列表</h2>
      </div>
      <table class="w-full text-left text-sm">
        <thead>
          <tr class="text-gray-500 border-b border-color">
            <th class="px-6 py-4 font-medium">编号</th>
            <th class="px-6 py-4 font-medium">物料名称</th>
            <th class="px-6 py-4 font-medium">规格型号</th>
            <th class="px-6 py-4 font-medium">库存</th>
            <th class="px-6 py-4 font-medium">供应商</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-white/5">
          <tr v-for="item in materials" :key="item.id" class="hover:bg-white/5 transition-colors">
            <td class="px-6 py-4 font-mono text-xs text-blue-400">{{ item.code }}</td>
            <td class="px-6 py-4">{{ item.name }}</td>
            <td class="px-6 py-4 text-gray-400">{{ item.spec }}</td>
            <td class="px-6 py-4">
              <span :class="item.stockClass" class="px-2 py-0.5 text-xs rounded">{{ item.stock }}</span>
            </td>
            <td class="px-6 py-4 text-gray-400">{{ item.supplier }}</td>
          </tr>
        </tbody>
      </table>
    </section>

    <!-- 工艺路线可视化 -->
    <section class="space-y-4">
      <h2 class="text-lg font-bold">工艺路线可视化：中心轮零件加工</h2>
      <div class="floating-island p-8 overflow-x-auto">
        <div class="flex items-center justify-between min-w-[800px]">
          <div v-for="(step, idx) in processSteps" :key="idx" :class="['process-line relative flex flex-col items-center', idx === processSteps.length - 1 ? '' : 'after:content-[\'\'] after:absolute after:top-7 after:left-full after:w-10 after:h-[1px] after:bg-white/10']">
            <div :class="step.circleClass" class="w-14 h-14 circle-icon z-10 mb-3 font-bold">{{ step.num }}</div>
            <span class="text-sm font-medium">{{ step.name }}</span>
            <span class="text-[10px] text-gray-500 mt-1">{{ step.location }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 工序明细配置 -->
    <section class="space-y-4 pb-20">
      <h2 class="text-sm font-bold uppercase tracking-widest text-gray-500">工序明细配置</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div v-for="op in operations" :key="op.id" :class="['floating-island p-5 relative border-l-4', op.borderClass]">
          <div class="absolute top-4 right-4 text-4xl font-black opacity-5">{{ op.num }}</div>
          <div class="flex justify-between mb-4">
            <span :class="op.statusClass" class="text-xs px-2 py-0.5 rounded">{{ op.status }}</span>
            <span class="text-[10px] text-gray-500">{{ op.code }}</span>
          </div>
          <h4 class="font-bold mb-2">{{ op.title }}</h4>
          <p class="text-[11px] text-gray-400 mb-4 leading-relaxed">{{ op.desc }}</p>
          <div class="flex items-center justify-between text-[10px] pt-4 border-t border-white/5">
            <div class="flex items-center gap-2">
              <div :class="op.avatarClass" class="w-5 h-5 rounded-full flex items-center justify-center text-[8px]">{{ op.avatar }}</div>
              <span>负责人：{{ op.owner }}</span>
            </div>
            <span class="text-gray-500">{{ op.time }}</span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
const modules = [
  { name: '物料管理', desc: '全流程库存与BOM追溯系统', path: '/parts', color: 'bg-blue-500/10 text-blue-400', icon: '<path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>' },
  { name: '设备管理', desc: '关键设备台账与预防性维护', path: '/equipments', color: 'bg-emerald-500/10 text-emerald-400', icon: '<path d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z"/>' },
  { name: '工艺路线', desc: '标准化生产流程与可视化路径', path: '/working-plans', color: 'bg-amber-500/10 text-amber-400', icon: '<path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>' },
  { name: '工序配置', desc: '工位参数设定与操作员派发', path: '/procedures', color: 'bg-purple-500/10 text-purple-400', icon: '<path d="M11 4a2 2 0 114 0v1a1 1 0 001 1h3a1 1 0 011 1v3a1 1 0 01-1 1h-1a2 2 0 100 4h1a1 1 0 011 1v3a1 1 0 01-1 1h-3a1 1 0 01-1-1v-1a2 2 0 10-4 0v1a1 1 0 01-1 1H7a1 1 0 01-1-1v-3a1 1 0 011-1h1a2 2 0 100-4H7a1 1 0 01-1-1V7a1 1 0 011-1h3a1 1 0 001-1V4z"/>' }
]

const materials = [
  { id: 1, code: 'MTR-2023-001', name: '中心轮轴承单元', spec: 'SKF-6205-2RS', stock: '1,240', stockClass: 'bg-emerald-500/20 text-emerald-400', supplier: 'SKF Group' },
  { id: 2, code: 'MTR-2023-042', name: '精密硬化齿轮', spec: 'MOD-2.5-42T', stock: '15', stockClass: 'bg-amber-500/20 text-amber-400', supplier: '德国精工部件' }
]

const processSteps = [
  { num: '01', name: '毛坯制造', location: '铸造车间', circleClass: 'bg-blue-500 text-white shadow-lg shadow-blue-500/20' },
  { num: '02', name: '粗加工', location: 'CNC-01 工位', circleClass: 'bg-gray-700 text-white border border-white/10' },
  { num: '03', name: '精加工', location: 'CNC-05 工位', circleClass: 'bg-gray-700 text-white border border-white/10' },
  { num: '04', name: '检测', location: 'CMM 三坐标', circleClass: 'bg-amber-500/20 text-amber-500 border border-amber-500/30' },
  { num: '05', name: '入库', location: '成品仓', circleClass: 'bg-emerald-500/20 text-emerald-500 border border-emerald-500/30' }
]

const operations = [
  { id: 1, num: '01', status: '进行中', statusClass: 'bg-emerald-500/10 text-emerald-400', code: 'OP-2023-A1', title: '毛坯制造', desc: '对铝合金压铸件进行清理、去毛刺及固溶时效处理，确保内应力消除。', owner: '王力', avatar: 'WL', avatarClass: 'bg-blue-500', time: '2023-10-24 08:00', borderClass: 'border-emerald-500' },
  { id: 2, num: '02', status: '待开始', statusClass: 'bg-gray-500/10 text-gray-400', code: 'OP-2023-A2', title: '粗加工', desc: '多轴加工中心对主要配合面进行粗铣，预留 0.5mm 余量用于精修。', owner: '张顺', avatar: 'ZS', avatarClass: 'bg-indigo-500', time: '预计 10-25', borderClass: 'border-gray-600' },
  { id: 3, num: '03', status: '待开始', statusClass: 'bg-gray-500/10 text-gray-400', code: 'OP-2023-A3', title: '精加工', desc: '内外圆磨床精密加工，配合 CMM 实时反馈补偿，精度等级 IT6。', owner: '李晨', avatar: 'LC', avatarClass: 'bg-purple-500', time: '预计 10-27', borderClass: 'border-gray-600' },
  { id: 4, num: '04', status: '待开始', statusClass: 'bg-gray-500/10 text-gray-400', code: 'OP-2023-A4', title: '检测', desc: '使用三坐标测量仪进行全尺寸检测，确保符合图纸要求。', owner: '赵敏', avatar: 'ZM', avatarClass: 'bg-pink-500', time: '预计 10-28', borderClass: 'border-gray-600' },
  { id: 5, num: '05', status: '待开始', statusClass: 'bg-gray-500/10 text-gray-400', code: 'OP-2023-A5', title: '入库', desc: '质检合格后进行包装、标识，入库登记并更新库存系统。', owner: '王芳', avatar: 'WF', avatarClass: 'bg-teal-500', time: '预计 10-29', borderClass: 'border-gray-600' }
]
</script>
