<template>
  <div class="forgot-container">
    <div class="forgot-bg">
      <div class="bg-shape shape1"></div>
      <div class="bg-shape shape2"></div>
      <div class="bg-shape shape3"></div>
    </div>
    <div class="forgot-box">
      <div class="forgot-header">
        <img src="@/assets/logo.svg" alt="logo" class="logo" />
        <h1>忘记密码</h1>
        <p>重置您的账户密码</p>
      </div>

      <el-form ref="formRef" :model="form" :rules="rules" class="forgot-form">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" prefix-icon="User" size="large" />
        </el-form-item>

        <el-form-item prop="studentNo">
          <el-input v-model="form.studentNo" placeholder="请输入学号" prefix-icon="Postcard" size="large" />
        </el-form-item>

        <el-form-item prop="phone">
          <el-input v-model="form.phone" placeholder="请输入注册手机号" prefix-icon="Phone" size="large" />
        </el-form-item>

        <el-form-item prop="newPassword">
          <el-input v-model="form.newPassword" type="password" placeholder="请输入新密码" prefix-icon="Lock"
            size="large" show-password />
        </el-form-item>

        <el-form-item prop="confirmPassword">
          <el-input v-model="form.confirmPassword" type="password" placeholder="请确认新密码" prefix-icon="Lock"
            size="large" show-password />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" size="large" :loading="loading" class="forgot-btn" @click="handleReset">
            重置密码
          </el-button>
        </el-form-item>

        <div class="forgot-footer">
          <router-link to="/login" class="back-link">返回登录</router-link>
        </div>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { resetPassword } from '@/api/auth'

const router = useRouter()
const formRef = ref(null)
const loading = ref(false)

const form = reactive({
  username: '',
  studentNo: '',
  phone: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule, value, callback) => {
  if (value !== form.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const rules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' }
  ],
  studentNo: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  phone: [
    { required: true, message: '请输入注册手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleReset = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  loading.value = true
  try {
    await resetPassword({
      username: form.username,
      studentNo: form.studentNo,
      phone: form.phone,
      newPassword: form.newPassword
    })
    ElMessage.success('密码重置成功，请登录')
    router.push('/login')
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}
</script>

<style scoped>
.forgot-container {
  min-height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  position: relative;
  overflow: hidden;
}

.forgot-bg {
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
  background: #fff;
}

.shape1 {
  width: 400px;
  height: 400px;
  top: -100px;
  left: -100px;
  animation: float 8s ease-in-out infinite;
}

.shape2 {
  width: 300px;
  height: 300px;
  bottom: -50px;
  right: -50px;
  animation: float 6s ease-in-out infinite reverse;
}

.shape3 {
  width: 200px;
  height: 200px;
  top: 50%;
  left: 10%;
  animation: float 7s ease-in-out infinite;
}

@keyframes float {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
  }
}

.forgot-box {
  width: 420px;
  padding: 40px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 16px;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
  position: relative;
  z-index: 1;
}

.forgot-header {
  text-align: center;
  margin-bottom: 30px;
}

.forgot-header .logo {
  width: 60px;
  height: 60px;
  margin-bottom: 15px;
}

.forgot-header h1 {
  font-size: 24px;
  color: #333;
  margin: 0 0 8px 0;
}

.forgot-header p {
  font-size: 14px;
  color: #999;
  margin: 0;
}

.forgot-form {
  margin-top: 20px;
}

.forgot-btn {
  width: 100%;
  height: 44px;
  font-size: 16px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border: none;
}

.forgot-btn:hover {
  background: linear-gradient(135deg, #5a6fd6 0%, #6a4190 100%);
}

.forgot-footer {
  text-align: center;
  margin-top: 20px;
}

.back-link {
  color: #667eea;
  font-weight: 500;
}

.back-link:hover {
  text-decoration: underline;
}
</style>
