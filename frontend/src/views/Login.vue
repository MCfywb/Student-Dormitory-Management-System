<template>
  <div class="login-container">
    <div class="login-bg">
      <div class="bg-shape shape1"></div>
      <div class="bg-shape shape2"></div>
      <div class="bg-shape shape3"></div>
      <div class="bg-shape shape4"></div>
    </div>
    <div class="login-box">
      <div class="login-header">
        <img src="@/assets/logo.svg" alt="logo" class="logo" />
        <h1>宿舍事务管理系统</h1>
        <p>Dormitory Management System</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" class="login-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
        </el-form-item>

        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码" prefix-icon="Lock" size="large"
            show-password @keyup.enter="handleLogin" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" class="login-btn" @click="handleLogin">
            登 录
          </el-button>
        </el-form-item>

        <div class="login-footer">
          <span>还没有账号？</span>
          <router-link to="/register" class="register-link">立即注册</router-link>
        </div>

        <div class="forgot-password">
          <router-link to="/forgot-password">忘记密码？</router-link>
        </div>
      </el-form>

      <div class="demo-accounts">
        <p>演示账号：</p>
        <div class="accounts">
          <span @click="fillAccount('admin', '123456')">管理员：admin</span>
          <span @click="fillAccount('manager1', '123456')">宿管：manager1</span>
          <span @click="fillAccount('2021001001', '123456')">学生：2021001001</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login } from '@/api/auth'
import { useUserStore } from '@/stores/userStore'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  password: ''
})

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    const res = await login(form)
    userStore.setUserInfo(res.data.user)
    if (res.data.studentInfo) {
      localStorage.setItem('studentInfo', JSON.stringify(res.data.studentInfo))
    }
    ElMessage.success('登录成功')

    if (res.data.user.role === 'student') {
      router.push('/student/home')
    } else {
      router.push('/admin/dashboard')
    }
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const fillAccount = (username, password) => {
  form.username = username
  form.password = password
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background-image: url('/public/mxzab9vh50qd1.webp');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  position: relative;
  overflow: hidden;
}

.login-bg {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.bg-shape {
  position: absolute;
  border-radius: 50%;
  opacity: 0.1;
}

.shape1 {
  width: 400px;
  height: 400px;
  background: #fff;
  top: -100px;
  left: -100px;
  animation: float 8s ease-in-out infinite;
}

.shape2 {
  width: 300px;
  height: 300px;
  background: #fff;
  bottom: -50px;
  right: -50px;
  animation: float 6s ease-in-out infinite reverse;
}

.shape3 {
  width: 200px;
  height: 200px;
  background: #fff;
  top: 50%;
  left: 10%;
  animation: float 7s ease-in-out infinite;
}

.shape4 {
  width: 150px;
  height: 150px;
  background: #fff;
  bottom: 30%;
  right: 15%;
  animation: float 5s ease-in-out infinite reverse;
}

@keyframes float {

  0%,
  100% {
    transform: translateY(0) rotate(0deg);
  }

  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

.login-box {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 30px;
}

.login-header .logo {
  width: 60px;
  height: 60px;
  margin-bottom: 15px;
}

.login-header h1 {
  font-size: 24px;
  color: #333;
  margin: 0 0 8px 0;
}

.login-header p {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.login-form {
  margin-top: 20px;
}

.login-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.login-btn:hover {
  background: linear-gradient(135deg, #5a6fd6 0%, #6a4190 100%);
}

.login-footer {
  text-align: center;
  margin-top: 20px;
  color: #666;
}

.register-link {
  color: #667eea;
  font-weight: 500;
}

.register-link:hover {
  text-decoration: underline;
}

.forgot-password {
  text-align: center;
  margin-top: 12px;
}

.forgot-password a {
  font-size: 13px;
  color: #999;
}

.forgot-password a:hover {
  color: #667eea;
}

.demo-accounts {
  margin-top: 25px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.demo-accounts p {
  font-size: 13px;
  color: #999;
  margin: 0 0 10px 0;
}

.demo-accounts .accounts {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.demo-accounts .accounts span {
  font-size: 12px;
  color: #667eea;
  background: #f0f2ff;
  padding: 5px 12px;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.demo-accounts .accounts span:hover {
  background: #667eea;
  color: #fff;
}
</style>
