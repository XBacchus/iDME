<template>
  <div class="console-page">
    <section class="console-overview">
      <div class="overview-grid">
        <div>
          <p class="eyebrow">Control Center</p>
          <h1 class="page-heading">
            IMS Engine <span class="console-heading-light">控制中枢</span>
          </h1>
          <p class="page-subtitle">
            实时扫描制造现场的追溯、节拍与执行信息，确保全业务域数据闭环。
          </p>
        </div>

        <div class="overview-metrics">
          <article v-for="metric in metrics" :key="metric.label" class="metric-panel">
            <p class="metric-panel__value">{{ metric.value }}</p>
            <p class="metric-panel__label">{{ metric.label }}</p>
          </article>
        </div>
      </div>
    </section>

    <section class="quick-nav-grid">
      <button
        v-for="module in modules"
        :key="module.name"
        type="button"
        class="nav-card"
        @click="router.push(module.path)"
      >
        <div>
          <div :class="module.color" class="nav-card__icon">
            <svg class="h-6 w-6" fill="none" stroke="currentColor" viewBox="0 0 24 24" v-html="module.icon" />
          </div>
          <h3>{{ module.name }}</h3>
          <p>{{ module.desc }}</p>
        </div>
        <div class="nav-card__enter">{{ module.enterLabel }} <span>→</span></div>
      </button>
    </section>

    <section class="section-shell">
      <div class="section-heading">
        <div>
          <h2 class="section-title">物料库存列表</h2>
          <p class="section-subtitle">关键库存按当前制造任务优先排序，便于快速识别紧张物料。</p>
        </div>
        <span class="data-pill">实时快照</span>
      </div>

      <div class="table-shell">
        <table class="w-full border-collapse text-left">
          <thead>
            <tr>
              <th>编号</th>
              <th>物料名称</th>
              <th>规格型号</th>
              <th>库存</th>
              <th>供应商</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="item in materials" :key="item.id" class="inventory-row">
              <td class="font-mono text-xs">{{ item.code }}</td>
              <td class="font-medium">{{ item.name }}</td>
              <td>{{ item.spec }}</td>
              <td>
                <span :class="item.stockClass" class="stock-tag">{{ item.stock }}</span>
              </td>
              <td>{{ item.supplier }}</td>
            </tr>
          </tbody>
        </table>
      </div>
    </section>

    <section class="section-shell">
      <div class="section-heading">
        <div>
          <h2 class="section-title">工艺路线可视化</h2>
          <p class="section-subtitle">中心轮零件加工正在执行标准五段流程，当前进度停留在首道工序。</p>
        </div>
        <span class="data-pill accent">Route 1.0</span>
      </div>

      <div class="process-shell custom-scrollbar">
        <div class="process-flow">
          <div class="process-track">
            <div class="process-progress" :style="{ width: processProgressWidth }"></div>
          </div>

          <div
            v-for="step in processSteps"
            :key="step.num"
            class="process-step"
            :class="step.stateClass"
          >
            <div class="process-step__circle">{{ step.num }}</div>
            <div class="process-step__label">{{ step.name }}</div>
            <div class="process-step__sub">{{ step.location }}</div>
          </div>
        </div>
      </div>
    </section>

    <section class="operation-grid">
      <article
        v-for="operation in operations"
        :key="operation.id"
        class="operation-card"
        :class="operation.cardClass"
      >
        <span class="operation-card__code">{{ operation.code }}</span>
        <span :class="operation.statusClass" class="operation-card__badge">{{ operation.status }}</span>
        <h3>{{ operation.title }}</h3>
        <p class="operation-card__desc">{{ operation.desc }}</p>
        <div class="operation-card__footer">
          <span>负责人: {{ operation.owner }}</span>
          <span>{{ operation.time }}</span>
        </div>
      </article>
    </section>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const router = useRouter()

const metrics = [
  { label: '在线模块', value: '04' },
  { label: '预警项', value: '02' },
  { label: '在制工序', value: '01' },
  { label: '设备在线', value: '98.6%' }
]

const modules = [
  {
    name: '物料管理',
    desc: '管理全流程库存变动，实时监控 BOM 关联与物料追溯链条。',
    path: '/parts',
    color: 'module-icon material',
    enterLabel: '进入管理系统',
    icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4" />'
  },
  {
    name: '设备管理',
    desc: '监控关键资产运行状态，自动化调度预防性维护任务。',
    path: '/equipments',
    color: 'module-icon equipment',
    enterLabel: '进入管理系统',
    icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z" />'
  },
  {
    name: '工艺路线',
    desc: '定义标准化生产流程，可视化管理零件加工的每一道路径。',
    path: '/working-plans',
    color: 'module-icon route',
    enterLabel: '进入管理系统',
    icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />'
  },
  {
    name: '工序配置',
    desc: '精准下达工位参数，实时派发工单至操作员终端。',
    path: '/procedures',
    color: 'module-icon config',
    enterLabel: '进入管理系统',
    icon: '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.8" d="M11 4a2 2 0 114 0v1a1 1 0 001 1h3a1 1 0 011 1v3a1 1 0 01-1 1h-1a2 2 0 100 4h1a1 1 0 011 1v3a1 1 0 01-1 1h-3a1 1 0 01-1-1v-1a2 2 0 10-4 0v1a1 1 0 01-1 1H7a1 1 0 01-1-1v-3a1 1 0 011-1h1a2 2 0 100-4H7a1 1 0 01-1-1V7a1 1 0 011-1h3a1 1 0 001-1V4z" />'
  }
]

const materials = [
  { id: 1, code: 'MTR-2023-001', name: '中心轮轴承单元', spec: 'SKF-6205-2RS', stock: '1,240', stockClass: 'stock-tag ok', supplier: 'SKF Group' },
  { id: 2, code: 'MTR-2023-042', name: '精密硬化齿轮', spec: 'MOD-2.5-42T', stock: '15', stockClass: 'stock-tag warn', supplier: '德国精工部件' },
  { id: 3, code: 'MTR-2023-083', name: '定位销组件', spec: 'PIN-08-SS', stock: '428', stockClass: 'stock-tag info', supplier: '华东精密五金' }
]

const processSteps = [
  { num: '01', name: '毛坯制造', location: '铸造车间', stateClass: 'is-active' },
  { num: '02', name: '粗加工', location: 'CNC-01 工位', stateClass: 'is-idle' },
  { num: '03', name: '精加工', location: 'CNC-05 工位', stateClass: 'is-idle' },
  { num: '04', name: '检测', location: 'CMM 三坐标', stateClass: 'is-review' },
  { num: '05', name: '入库', location: '成品仓', stateClass: 'is-complete' }
]

const operations = [
  {
    id: 1,
    code: 'OP-2023-A1',
    status: '进行中',
    statusClass: 'badge running',
    title: '毛坯制造',
    desc: '对铝合金压铸件进行清理、去毛刺及固溶时效处理，确保内部应力消除并为后续切削预留稳定基准。',
    owner: '王力',
    time: '2023-10-24',
    cardClass: 'is-running'
  },
  {
    id: 2,
    code: 'OP-2023-A2',
    status: '待开始',
    statusClass: 'badge idle',
    title: '粗加工',
    desc: '多轴加工中心对主要配合面执行粗铣，预留 0.5mm 余量用于精修，并校正基准面一致性。',
    owner: '张顺',
    time: '预计 10-25',
    cardClass: 'is-waiting'
  },
  {
    id: 3,
    code: 'OP-2023-A3',
    status: '待开始',
    statusClass: 'badge idle',
    title: '精加工',
    desc: '内外圆磨床执行精密加工，结合 CMM 实时反馈补偿，目标精度等级 IT6，确保尺寸与同轴度稳定。',
    owner: '李晨',
    time: '预计 10-27',
    cardClass: 'is-waiting'
  }
]

const processProgressWidth = computed(() => '25%')
</script>

<style scoped>
.console-page {
  display: flex;
  flex-direction: column;
  gap: 32px;
  padding-bottom: 40px;
}

.console-overview,
.section-shell {
  position: relative;
  overflow: hidden;
  border-radius: 28px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: linear-gradient(180deg, rgba(23, 28, 38, 0.92), rgba(14, 17, 24, 0.72));
  box-shadow: 0 24px 48px rgba(0, 0, 0, 0.28);
}

.console-overview {
  padding: 32px;
}

.section-shell {
  padding: 24px;
}

.overview-grid {
  display: grid;
  grid-template-columns: minmax(0, 1.2fr) minmax(260px, 0.8fr);
  gap: 24px;
  align-items: center;
}

.eyebrow {
  margin: 0 0 8px;
  font-size: 12px;
  font-weight: 700;
  letter-spacing: 0.2em;
  color: #94a3b8;
  text-transform: uppercase;
}

.page-heading {
  margin: 0;
  font-size: clamp(2rem, 4vw, 3.1rem);
  font-weight: 800;
  color: #f8fafc;
  line-height: 1;
}

.console-heading-light {
  font-weight: 300;
  opacity: 0.72;
}

.page-subtitle {
  margin: 16px 0 0;
  font-size: 14px;
  line-height: 1.75;
  color: rgba(255, 255, 255, 0.68);
}

.overview-metrics {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 16px;
}

.metric-panel {
  border-radius: 18px;
  padding: 18px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.06);
}

.metric-panel__value {
  margin: 0;
  font-size: 30px;
  font-weight: 800;
  color: #f8fafc;
}

.metric-panel__label {
  margin: 8px 0 0;
  font-size: 12px;
  color: #94a3b8;
}

.quick-nav-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 20px;
}

.nav-card {
  display: flex;
  min-height: 230px;
  flex-direction: column;
  justify-content: space-between;
  padding: 24px;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: linear-gradient(180deg, rgba(23, 28, 38, 0.92), rgba(14, 17, 24, 0.72));
  box-shadow: 0 20px 42px rgba(0, 0, 0, 0.24);
  text-align: left;
  cursor: pointer;
  transition: transform 0.25s ease, border-color 0.25s ease;
}

.nav-card:hover {
  transform: translateY(-6px);
  border-color: rgba(244, 63, 94, 0.4);
}

.nav-card__icon {
  display: inline-flex;
  width: 52px;
  height: 52px;
  align-items: center;
  justify-content: center;
  margin-bottom: 18px;
  border-radius: 16px;
}

.module-icon.material {
  background: rgba(59, 130, 246, 0.12);
  color: #93c5fd;
}

.module-icon.equipment {
  background: rgba(16, 185, 129, 0.12);
  color: #6ee7b7;
}

.module-icon.route {
  background: rgba(245, 158, 11, 0.12);
  color: #fcd34d;
}

.module-icon.config {
  background: rgba(139, 92, 246, 0.12);
  color: #c4b5fd;
}

.nav-card h3 {
  margin: 0 0 10px;
  font-size: 18px;
  font-weight: 700;
  color: #f8fafc;
}

.nav-card p {
  margin: 0;
  font-size: 13px;
  line-height: 1.7;
  color: rgba(255, 255, 255, 0.68);
}

.nav-card__enter {
  font-size: 13px;
  font-weight: 700;
  color: #f43f5e;
}

.section-heading {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 20px;
}

.section-title {
  margin: 0;
  padding-left: 12px;
  border-left: 4px solid #f43f5e;
  font-size: 16px;
  font-weight: 800;
  color: #f8fafc;
}

.section-subtitle {
  margin: 8px 0 0;
  font-size: 13px;
  color: rgba(255, 255, 255, 0.62);
}

.data-pill {
  display: inline-flex;
  align-items: center;
  border-radius: 999px;
  padding: 6px 10px;
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.72);
  font-size: 12px;
  font-weight: 700;
}

.data-pill.accent {
  background: rgba(59, 130, 246, 0.12);
  color: #93c5fd;
}

.table-shell {
  overflow: hidden;
  border-radius: 18px;
  border: 1px solid rgba(255, 255, 255, 0.06);
}

th {
  padding: 14px 16px;
  font-size: 12px;
  font-weight: 600;
  color: rgba(255, 255, 255, 0.54);
  background: rgba(255, 255, 255, 0.03);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

td {
  padding: 16px;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.82);
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

.inventory-row:hover {
  background: rgba(255, 255, 255, 0.03);
}

.stock-tag {
  display: inline-flex;
  min-width: 56px;
  justify-content: center;
  border-radius: 8px;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: 700;
}

.stock-tag.ok {
  background: rgba(16, 185, 129, 0.12);
  color: #6ee7b7;
}

.stock-tag.warn {
  background: rgba(245, 158, 11, 0.12);
  color: #fcd34d;
}

.stock-tag.info {
  background: rgba(59, 130, 246, 0.12);
  color: #93c5fd;
}

.process-shell {
  overflow-x: auto;
}

.process-flow {
  position: relative;
  display: flex;
  justify-content: space-between;
  gap: 16px;
  min-width: 760px;
  padding: 16px 0 4px;
}

.process-track {
  position: absolute;
  top: 48px;
  left: 9%;
  right: 9%;
  height: 2px;
  background: rgba(255, 255, 255, 0.08);
}

.process-progress {
  height: 100%;
  background: #f43f5e;
}

.process-step {
  position: relative;
  z-index: 1;
  display: flex;
  width: 110px;
  flex-direction: column;
  align-items: center;
  text-align: center;
}

.process-step__circle {
  display: flex;
  width: 64px;
  height: 64px;
  align-items: center;
  justify-content: center;
  border-radius: 20px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: #11141a;
  color: #f8fafc;
  font-weight: 800;
}

.process-step__label {
  margin-top: 12px;
  font-size: 14px;
  font-weight: 600;
  color: #f8fafc;
}

.process-step__sub {
  margin-top: 6px;
  font-size: 12px;
  color: rgba(255, 255, 255, 0.54);
}

.process-step.is-active .process-step__circle {
  background: #f43f5e;
  border-color: #f43f5e;
  box-shadow: 0 10px 24px rgba(244, 63, 94, 0.22);
}

.process-step.is-review .process-step__circle {
  background: rgba(245, 158, 11, 0.12);
  color: #fcd34d;
}

.process-step.is-complete .process-step__circle {
  background: rgba(16, 185, 129, 0.12);
  color: #6ee7b7;
}

.operation-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 20px;
}

.operation-card {
  position: relative;
  padding: 24px;
  border-radius: 24px;
  border: 1px solid rgba(255, 255, 255, 0.08);
  background: linear-gradient(180deg, rgba(23, 28, 38, 0.92), rgba(14, 17, 24, 0.72));
}

.operation-card.is-running {
  border-left: 3px solid #10b981;
}

.operation-card.is-waiting {
  border-left: 3px solid rgba(255, 255, 255, 0.12);
}

.operation-card__code {
  position: absolute;
  top: 20px;
  right: 20px;
  font-size: 11px;
  color: rgba(255, 255, 255, 0.32);
}

.badge {
  display: inline-flex;
  border-radius: 999px;
  padding: 4px 10px;
  font-size: 12px;
  font-weight: 700;
}

.badge.running {
  background: rgba(16, 185, 129, 0.12);
  color: #6ee7b7;
}

.badge.idle {
  background: rgba(255, 255, 255, 0.08);
  color: rgba(255, 255, 255, 0.72);
}

.operation-card h3 {
  margin: 16px 0 0;
  font-size: 18px;
  color: #f8fafc;
}

.operation-card__desc {
  margin: 12px 0 0;
  min-height: 88px;
  font-size: 13px;
  line-height: 1.75;
  color: rgba(255, 255, 255, 0.62);
}

.operation-card__footer {
  display: flex;
  justify-content: space-between;
  gap: 16px;
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
  font-size: 12px;
  color: rgba(255, 255, 255, 0.54);
}

@media (max-width: 1280px) {
  .quick-nav-grid,
  .operation-grid {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }
}

@media (max-width: 1024px) {
  .overview-grid {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 768px) {
  .quick-nav-grid,
  .operation-grid {
    grid-template-columns: 1fr;
  }

  .section-heading,
  .operation-card__footer {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>

<style>
.app-shell.theme-bright .console-overview,
.app-shell.theme-bright .section-shell,
.app-shell.theme-bright .nav-card,
.app-shell.theme-bright .operation-card {
  border-color: #e2e8f0;
  background: #ffffff;
  background-image: none;
  box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.05);
}

.app-shell.theme-bright .metric-panel {
  background: #ffffff;
  border-color: #e2e8f0;
  box-shadow: inset 0 0 0 1px rgba(226, 232, 240, 0.7);
}

.app-shell.theme-bright .page-heading,
.app-shell.theme-bright .console-heading-light,
.app-shell.theme-bright .nav-card h3,
.app-shell.theme-bright .section-title,
.app-shell.theme-bright .process-step__label,
.app-shell.theme-bright .operation-card h3,
.app-shell.theme-bright .metric-panel__value {
  color: #0f172a;
}

.app-shell.theme-bright .eyebrow,
.app-shell.theme-bright .metric-panel__label,
.app-shell.theme-bright .section-subtitle,
.app-shell.theme-bright .page-subtitle,
.app-shell.theme-bright td,
.app-shell.theme-bright .process-step__sub,
.app-shell.theme-bright .operation-card__desc,
.app-shell.theme-bright .operation-card__footer,
.app-shell.theme-bright .nav-card p {
  color: #475569;
}

.app-shell.theme-bright .nav-card__enter {
  color: #e11d48;
}

.app-shell.theme-bright .module-icon.material {
  background: #fee2e2;
  color: #e11d48;
}

.app-shell.theme-bright .module-icon.equipment {
  background: #fff7ed;
  color: #f97316;
}

.app-shell.theme-bright .module-icon.route {
  background: #fef3c7;
  color: #d97706;
}

.app-shell.theme-bright .module-icon.config {
  background: #fae8ff;
  color: #c026d3;
}

.app-shell.theme-bright .table-shell {
  background: #ffffff;
  border-color: #e2e8f0;
}

.app-shell.theme-bright th {
  color: #475569;
  background: #ffffff;
  border-bottom-color: #e2e8f0;
}

.app-shell.theme-bright td {
  background: #ffffff;
  border-bottom-color: #e2e8f0;
}

.app-shell.theme-bright .inventory-row:hover {
  background: #f8fafc;
}

.app-shell.theme-bright .data-pill {
  background: #f8fafc;
  color: #be123c;
  border: 1px solid #e2e8f0;
}

.app-shell.theme-bright .data-pill.accent {
  background: #f8fafc;
  color: #f43f5e;
}

.app-shell.theme-bright .process-track {
  background: #e2e8f0;
}

.app-shell.theme-bright .process-step__circle {
  background: #ffffff;
  border-color: #e2e8f0;
  color: #0f172a;
}

.app-shell.theme-bright .process-step.is-active .process-step__circle {
  background: #f43f5e;
  border-color: #f43f5e;
  color: #ffffff;
}

.app-shell.theme-bright .process-step.is-review .process-step__circle {
  background: #fff7ed;
  color: #f97316;
}

.app-shell.theme-bright .process-step.is-complete .process-step__circle {
  background: #ecfdf5;
  color: #059669;
}

.app-shell.theme-bright .operation-card__code {
  color: #94a3b8;
}

.app-shell.theme-bright .badge.running {
  background: #f8fafc;
  color: #f43f5e;
  border: 1px solid #e2e8f0;
}

.app-shell.theme-bright .badge.idle {
  background: #f8fafc;
  color: #475569;
  border: 1px solid #e2e8f0;
}

.app-shell.theme-bright .operation-card.is-running {
  border-left-color: #f43f5e;
}

.app-shell.theme-bright .operation-card.is-waiting {
  border-left-color: #e2e8f0;
}
</style>
