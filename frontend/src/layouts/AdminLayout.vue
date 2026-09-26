<template>
  <div class="admin-layout">
    <el-container>
      <el-aside :width="isCollapse ? '64px' : '220px'" class="sidebar">
        <div class="logo">
          <img src="/public/favicon.svg" alt="logo" />
          <span v-show="!isCollapse">宿舍事务管理系统</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          :collapse="isCollapse"
          :collapse-transition="false"
          background-color="#304156"
          text-color="#bfcbd9"
          active-text-color="#409EFF"
          router
        >
          <el-menu-item index="/admin/dashboard">
            <el-icon><DataAnalysis /></el-icon>
            <template #title>首页</template>
          </el-menu-item>
          
          <el-sub-menu index="dormitory">
            <template #title>
              <el-icon><OfficeBuilding /></el-icon>
              <span>宿舍管理</span>
            </template>
            <el-menu-item index="/admin/building">宿舍楼管理</el-menu-item>
            <el-menu-item index="/admin/room">房间管理</el-menu-item>
          </el-sub-menu>
          
          <el-sub-menu index="personnel">
            <template #title>
              <el-icon><User /></el-icon>
              <span>人员管理</span>
            </template>
            <el-menu-item index="/admin/manager" v-if="userStore.userInfo.value?.role === 'admin'">宿管管理</el-menu-item>
            <el-menu-item index="/admin/student">学生管理</el-menu-item>
          </el-sub-menu>
          
          <el-menu-item index="/admin/accommodation">
            <el-icon><House /></el-icon>
            <template #title>住宿管理</template>
          </el-menu-item>
          
          <el-menu-item index="/admin/hygiene">
            <el-icon><Brush /></el-icon>
            <template #title>卫生管理</template>
          </el-menu-item>
          
          <el-sub-menu index="facility">
            <template #title>
              <el-icon><Tools /></el-icon>
              <span>设施管理</span>
            </template>
            <el-menu-item index="/admin/facility">设施列表</el-menu-item>
            <el-menu-item index="/admin/repair">维修管理</el-menu-item>
          </el-sub-menu>
          
          <el-menu-item index="/admin/fee">
            <el-icon><Money /></el-icon>
            <template #title>费用管理</template>
          </el-menu-item>
          
          <el-menu-item index="/admin/notice">
            <el-icon><Bell /></el-icon>
            <template #title>公告管理</template>
          </el-menu-item>
          
          <el-menu-item index="/admin/visitor">
            <el-icon><Avatar /></el-icon>
            <template #title>来访登记</template>
          </el-menu-item>
        </el-menu>
      </el-aside>
      
      <el-container>
        <el-header class="header">
          <div class="header-left">
            <el-icon class="collapse-btn" @click="toggleCollapse">
              <Fold v-if="!isCollapse" />
              <Expand v-else />
            </el-icon>
            <el-breadcrumb separator="/">
              <el-breadcrumb-item :to="{ path: '/admin/dashboard' }">首页</el-breadcrumb-item>
              <el-breadcrumb-item v-if="$route.meta.title !== '首页'">{{ $route.meta.title }}</el-breadcrumb-item>
            </el-breadcrumb>
          </div>
          <div class="header-right">
            <el-dropdown @command="handleCommand">
              <span class="user-info">
                <el-avatar :size="32" :src="avatarUrl" />
                <span class="username">{{ userStore.userInfo.value?.realName }}</span>
                <el-icon><ArrowDown /></el-icon>
              </span>
              <template #dropdown>
                <el-dropdown-menu>
                  <el-dropdown-item command="profile">个人中心</el-dropdown-item>
                  <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
                </el-dropdown-menu>
              </template>
            </el-dropdown>
          </div>
        </el-header>
        
        <el-main class="main">
          <router-view v-slot="{ Component }">
            <transition name="fade-transform" mode="out-in">
              <component :is="Component" />
            </transition>
          </router-view>
        </el-main>
      </el-container>
    </el-container>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getAvatarUrl } from '@/api/auth'
import { useUserStore } from '@/stores/userStore'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const isCollapse = ref(false)

const avatarUrl = computed(() => {
  return getAvatarUrl(userStore.userInfo.value?.avatar)
})

const activeMenu = computed(() => route.path)

const toggleCollapse = () => {
  isCollapse.value = !isCollapse.value
}

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/admin/profile')
  } else if (command === 'logout') {
    ElMessageBox.confirm('确定要退出登录吗？', '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }).then(() => {
      userStore.clearUserInfo()
      ElMessage.success('退出成功')
      router.push('/login')
    }).catch(() => {})
  }
}
</script>

<style scoped>
.admin-layout {
  height: 100vh;
}

.admin-layout .el-container {
  height: 100%;
}

.sidebar {
  background-color: #304156;
  overflow: hidden;
  transition: width 0.3s;
}

.logo {
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 0 15px;
  background-color: #263445;
}

.logo img {
  width: 32px;
  height: 32px;
}

.logo span {
  color: #fff;
  font-size: 16px;
  font-weight: bold;
  margin-left: 10px;
  white-space: nowrap;
}

.el-menu {
  border-right: none;
}

.header {
  background: #fff;
  box-shadow: 0 1px 4px rgba(0, 21, 41, 0.08);
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0 20px;
}

.header-left {
  display: flex;
  align-items: center;
}

.collapse-btn {
  font-size: 20px;
  cursor: pointer;
  margin-right: 15px;
}

.header-right {
  display: flex;
  align-items: center;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.username {
  margin: 0 8px;
  color: #333;
}

.main {
  background: #f0f2f5;
  padding: 20px;
  overflow-y: auto;
}
</style>
