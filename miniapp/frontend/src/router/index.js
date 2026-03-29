import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '../components/layout/AppLayout.vue'

const createRoute = (path, name, component, meta) => ({
  path,
  name,
  component,
  meta
})

const routes = [
  {
    path: '/',
    component: AppLayout,
    children: [
      createRoute('', 'Home', () => import('../views/Home.vue'), {
        section: 'Workspace',
        title: 'Control Center',
        titleCn: '首页控制台',
        searchPlaceholder: '搜索功能...'
      }),
      createRoute('parts', 'Parts', () => import('../views/parts/PartList.vue'), {
        section: 'Warehouse',
        title: 'Material List',
        titleCn: '物料管理',
        searchPlaceholder: '搜索物料名称、编号、供应商...'
      }),
      createRoute('parts/add', 'PartAdd', () => import('../views/parts/PartForm.vue'), {
        section: 'Warehouse',
        title: 'Create Material',
        titleCn: '新增物料',
        searchPlaceholder: '搜索分类或字段...'
      }),
      createRoute('parts/edit/:id', 'PartEdit', () => import('../views/parts/PartForm.vue'), {
        section: 'Warehouse',
        title: 'Edit Material',
        titleCn: '编辑物料',
        searchPlaceholder: '搜索分类或字段...'
      }),
      createRoute('parts/bom/:id', 'PartBOM', () => import('../views/parts/PartBOM.vue'), {
        section: 'Warehouse',
        title: 'BOM Explorer',
        titleCn: 'BOM 管理',
        searchPlaceholder: '搜索组件或层级...'
      }),
      createRoute('parts/versions/:id', 'PartVersions', () => import('../views/parts/VersionHistory.vue'), {
        section: 'Warehouse',
        title: 'Version History',
        titleCn: '版本管理',
        searchPlaceholder: '搜索版本记录...'
      }),
      createRoute('parts/categories', 'PartCategories', () => import('../views/parts/CategoryManage.vue'), {
        section: 'Warehouse',
        title: 'Category Matrix',
        titleCn: '分类管理',
        searchPlaceholder: '搜索分类节点...'
      }),
      createRoute('equipments', 'Equipments', () => import('../views/equipments/EquipmentList.vue'), {
        section: 'Assets',
        title: 'Equipment Ledger',
        titleCn: '设备管理',
        searchPlaceholder: '搜索设备编码、名称、厂商...'
      }),
      createRoute('equipments/add', 'EquipmentAdd', () => import('../views/equipments/EquipmentForm.vue'), {
        section: 'Assets',
        title: 'Create Equipment',
        titleCn: '新增设备',
        searchPlaceholder: '搜索设备字段...'
      }),
      createRoute('equipments/edit/:id', 'EquipmentEdit', () => import('../views/equipments/EquipmentForm.vue'), {
        section: 'Assets',
        title: 'Edit Equipment',
        titleCn: '编辑设备',
        searchPlaceholder: '搜索设备字段...'
      }),
      createRoute('working-plans', 'WorkingPlans', () => import('../views/working-plans/WorkingPlanList.vue'), {
        section: 'Manufacturing',
        title: 'Process Routes',
        titleCn: '工艺路线管理',
        searchPlaceholder: '搜索工艺名称、版本、产品...'
      }),
      createRoute('working-plans/new', 'WorkingPlanAdd', () => import('../views/working-plans/WorkingPlanForm.vue'), {
        section: 'Manufacturing',
        title: 'Create Route',
        titleCn: '新增工艺路线',
        searchPlaceholder: '搜索工序或工艺字段...'
      }),
      createRoute('working-plans/:id/edit', 'WorkingPlanEdit', () => import('../views/working-plans/WorkingPlanForm.vue'), {
        section: 'Manufacturing',
        title: 'Edit Route',
        titleCn: '编辑工艺路线',
        searchPlaceholder: '搜索工序或工艺字段...'
      }),
      createRoute('working-plans/:id', 'WorkingPlanDetail', () => import('../views/working-plans/WorkingPlanDetail.vue'), {
        section: 'Manufacturing',
        title: 'Route Detail',
        titleCn: '工艺路线详情',
        searchPlaceholder: '搜索工序节点...'
      }),
      createRoute('procedures', 'Procedures', () => import('../views/procedures/ProcedureList.vue'), {
        section: 'Manufacturing',
        title: 'Procedure Config',
        titleCn: '工序配置',
        searchPlaceholder: '搜索工序名称...'
      })
    ]
  }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
