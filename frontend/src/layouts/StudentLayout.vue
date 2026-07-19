<template>
  <div class="student-layout">
    <el-container>
      <el-header class="header">
        <div class="logo">
          <img src="@/assets/logo.svg" alt="logo" />
          <span>宿舍事务管理系统</span>
        </div>
        <el-menu
          :default-active="activeMenu"
          mode="horizontal"
          background-color="#409EFF"
          text-color="#fff"
          active-text-color="#ffd04b"
          router
        >
          <el-menu-item index="/student/home">首页</el-menu-item>
          <el-menu-item index="/student/myroom">我的宿舍</el-menu-item>
          <el-menu-item index="/student/hygiene">卫生检查</el-menu-item>
          <el-menu-item index="/student/repair">报修申请</el-menu-item>
          <el-menu-item index="/student/fee">费用查询</el-menu-item>
          <el-menu-item index="/student/notice">公告通知</el-menu-item>
        </el-menu>
        <div class="user-area">
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
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessageBox, ElMessage } from 'element-plus'
import { getAvatarUrl } from '@/api/auth'
import { useUserStore } from '@/stores/userStore'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const avatarUrl = computed(() => {
  return getAvatarUrl(userStore.userInfo.value?.avatar)
})

const activeMenu = computed(() => route.path)

const handleCommand = (command) => {
  if (command === 'profile') {
    router.push('/student/profile')
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
.student-layout {
  height: 100vh;
  background: #f0f2f5;
}

.student-layout .el-container {
  height: 100%;
}

.header {
  background: #409EFF;
  display: flex;
  align-items: center;
  padding: 0 20px;
  height: 60px !important;
}

.logo {
  display: flex;
  align-items: center;
  margin-right: 40px;
}

.logo img {
  width: 32px;
  height: 32px;
}

.logo span {
  color: #fff;
  font-size: 18px;
  font-weight: bold;
  margin-left: 10px;
  white-space: nowrap;
}

.el-menu--horizontal {
  border-bottom: none;
  flex: 1;
}

.el-menu--horizontal > .el-menu-item {
  height: 60px;
  line-height: 60px;
}

.user-area {
  margin-left: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  color: #fff;
}

.username {
  margin: 0 8px;
}

.main {
  padding: 20px;
  overflow-y: auto;
}
</style>
