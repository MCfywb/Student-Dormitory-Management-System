import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/forgot-password',
    name: 'ForgotPassword',
    component: () => import('@/views/ForgotPassword.vue'),
    meta: { title: '忘记密码' }
  },
  {
    path: '/admin',
    name: 'AdminLayout',
    component: () => import('@/layouts/AdminLayout.vue'),
    redirect: '/admin/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'AdminDashboard',
        component: () => import('@/views/admin/Dashboard.vue'),
        meta: { title: '首页', requiresAuth: true, roles: ['admin', 'manager'] }
      },
      {
        path: 'building',
        name: 'BuildingManage',
        component: () => import('@/views/admin/Building.vue'),
        meta: { title: '宿舍楼管理', requiresAuth: true, roles: ['admin', 'manager'] }
      },
      {
        path: 'room',
        name: 'RoomManage',
        component: () => import('@/views/admin/Room.vue'),
        meta: { title: '宿舍房间管理', requiresAuth: true, roles: ['admin', 'manager'] }
      },
      {
        path: 'manager',
        name: 'ManagerManage',
        component: () => import('@/views/admin/Manager.vue'),
        meta: { title: '宿管管理', requiresAuth: true, roles: ['admin'] }
      },
      {
        path: 'student',
        name: 'StudentManage',
        component: () => import('@/views/admin/StudentInfo.vue'),
        meta: { title: '学生管理', requiresAuth: true, roles: ['admin', 'manager'] }
      },
      {
        path: 'accommodation',
        name: 'AccommodationManage',
        component: () => import('@/views/admin/Accommodation.vue'),
        meta: { title: '住宿管理', requiresAuth: true, roles: ['admin', 'manager'] }
      },
      {
        path: 'hygiene',
        name: 'HygieneManage',
        component: () => import('@/views/admin/Hygiene.vue'),
        meta: { title: '卫生管理', requiresAuth: true, roles: ['admin', 'manager'] }
      },
      {
        path: 'facility',
        name: 'FacilityManage',
        component: () => import('@/views/admin/Facility.vue'),
        meta: { title: '设施管理', requiresAuth: true, roles: ['admin', 'manager'] }
      },
      {
        path: 'repair',
        name: 'RepairManage',
        component: () => import('@/views/admin/Repair.vue'),
        meta: { title: '维修管理', requiresAuth: true, roles: ['admin', 'manager'] }
      },
      {
        path: 'fee',
        name: 'FeeManage',
        component: () => import('@/views/admin/Fee.vue'),
        meta: { title: '费用管理', requiresAuth: true, roles: ['admin', 'manager'] }
      },
      {
        path: 'notice',
        name: 'NoticeManage',
        component: () => import('@/views/admin/Notice.vue'),
        meta: { title: '公告管理', requiresAuth: true, roles: ['admin', 'manager'] }
      },
      {
        path: 'visitor',
        name: 'VisitorManage',
        component: () => import('@/views/admin/Visitor.vue'),
        meta: { title: '来访登记', requiresAuth: true, roles: ['admin', 'manager'] }
      },
      {
        path: 'profile',
        name: 'AdminProfile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      }
    ]
  },
  {
    path: '/student',
    name: 'StudentLayout',
    component: () => import('@/layouts/StudentLayout.vue'),
    redirect: '/student/home',
    children: [
      {
        path: 'home',
        name: 'StudentHome',
        component: () => import('@/views/student/Home.vue'),
        meta: { title: '首页', requiresAuth: true, roles: ['student'] }
      },
      {
        path: 'myroom',
        name: 'MyRoom',
        component: () => import('@/views/student/MyRoom.vue'),
        meta: { title: '我的宿舍', requiresAuth: true, roles: ['student'] }
      },
      {
        path: 'hygiene',
        name: 'StudentHygiene',
        component: () => import('@/views/student/Hygiene.vue'),
        meta: { title: '卫生检查', requiresAuth: true, roles: ['student'] }
      },
      {
        path: 'repair',
        name: 'StudentRepair',
        component: () => import('@/views/student/Repair.vue'),
        meta: { title: '报修申请', requiresAuth: true, roles: ['student'] }
      },
      {
        path: 'fee',
        name: 'StudentFee',
        component: () => import('@/views/student/Fee.vue'),
        meta: { title: '费用查询', requiresAuth: true, roles: ['student'] }
      },
      {
        path: 'notice',
        name: 'StudentNotice',
        component: () => import('@/views/student/Notice.vue'),
        meta: { title: '公告通知', requiresAuth: true, roles: ['student'] }
      },
      {
        path: 'profile',
        name: 'StudentProfile',
        component: () => import('@/views/Profile.vue'),
        meta: { title: '个人中心', requiresAuth: true }
      }
    ]
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = to.meta.title ? `${to.meta.title} - 宿舍事务管理系统` : '宿舍事务管理系统'
  
  const userInfo = localStorage.getItem('userInfo')
  
  if (to.meta.requiresAuth) {
    if (!userInfo) {
      next('/login')
      return
    }
    
    const user = JSON.parse(userInfo)
    if (to.meta.roles && !to.meta.roles.includes(user.role)) {
      if (user.role === 'student') {
        next('/student/home')
      } else {
        next('/admin/dashboard')
      }
      return
    }
  }
  
  next()
})

export default router
