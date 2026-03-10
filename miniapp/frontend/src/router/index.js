import { createRouter, createWebHistory } from 'vue-router'
import AppLayout from '../components/layout/AppLayout.vue'

const routes = [
  {
    path: '/',
    component: AppLayout,
    children: [
      { path: '', name: 'Home', component: () => import('../views/Home.vue') },
      { path: 'parts', name: 'Parts', component: () => import('../views/parts/PartList.vue') },
      { path: 'parts/add', name: 'PartAdd', component: () => import('../views/parts/PartForm.vue') },
      { path: 'parts/edit/:id', name: 'PartEdit', component: () => import('../views/parts/PartForm.vue') },
      { path: 'parts/bom/:id', name: 'PartBOM', component: () => import('../views/parts/PartBOM.vue') },
      { path: 'equipments', name: 'Equipments', component: () => import('../views/equipments/EquipmentList.vue') },
      { path: 'equipments/add', name: 'EquipmentAdd', component: () => import('../views/equipments/EquipmentForm.vue') },
      { path: 'equipments/edit/:id', name: 'EquipmentEdit', component: () => import('../views/equipments/EquipmentForm.vue') },
      { path: 'working-plans', name: 'WorkingPlans', component: () => import('../views/working-plans/WorkingPlanList.vue') },
      { path: 'working-plans/new', name: 'WorkingPlanAdd', component: () => import('../views/working-plans/WorkingPlanForm.vue') },
      { path: 'working-plans/:id/edit', name: 'WorkingPlanEdit', component: () => import('../views/working-plans/WorkingPlanForm.vue') },
      { path: 'working-plans/:id', name: 'WorkingPlanDetail', component: () => import('../views/working-plans/WorkingPlanDetail.vue') },
      { path: 'procedures', name: 'Procedures', component: () => import('../views/procedures/ProcedureList.vue') }
    ]
  }
]

export default createRouter({
  history: createWebHistory(),
  routes
})
