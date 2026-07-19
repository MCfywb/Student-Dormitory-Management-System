<template>
  <div class="profile-page">
    <div class="page-header">
      <h3 class="page-title">个人中心</h3>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="8">
        <div class="avatar-card">
          <el-avatar :size="100" :src="avatarUrl" />
          <h3>{{ userStore.userInfo.value?.realName }}</h3>
          <p>{{ userStore.userInfo.value?.role === 'student' ? '学生' : userStore.userInfo.value?.role === 'manager' ? '宿管' : '管理员' }}</p>
          <el-upload
            :show-file-list="false"
            :before-upload="beforeUpload"
            :http-request="handleUpload"
          >
            <el-button type="primary">更换头像</el-button>
          </el-upload>
        </div>
      </el-col>
      
      <el-col :span="16">
        <el-tabs v-model="activeTab">
          <el-tab-pane label="基本信息" name="info">
            <el-form ref="infoFormRef" :model="infoForm" :rules="infoRules" label-width="100px">
              <el-form-item label="用户名">
                <el-input :model-value="userStore.userInfo.value?.username" disabled />
              </el-form-item>
              <el-form-item label="姓名" prop="realName">
                <el-input v-model="infoForm.realName" />
              </el-form-item>
              <el-form-item label="性别" prop="gender">
                <el-radio-group v-model="infoForm.gender">
                  <el-radio label="男">男</el-radio>
                  <el-radio label="女">女</el-radio>
                </el-radio-group>
              </el-form-item>
              <el-form-item label="手机号" prop="phone">
                <el-input v-model="infoForm.phone" />
              </el-form-item>
              <el-form-item label="邮箱" prop="email">
                <el-input v-model="infoForm.email" />
              </el-form-item>
              <el-form-item v-if="userStore.userInfo.value?.role === 'student'">
                <el-descriptions :column="2" border>
                  <el-descriptions-item label="学号">{{ studentInfo?.studentNo }}</el-descriptions-item>
                  <el-descriptions-item label="学院">{{ studentInfo?.college }}</el-descriptions-item>
                  <el-descriptions-item label="专业">{{ studentInfo?.major }}</el-descriptions-item>
                  <el-descriptions-item label="班级">{{ studentInfo?.className }}</el-descriptions-item>
                  <el-descriptions-item label="年级">{{ studentInfo?.grade }}</el-descriptions-item>
                  <el-descriptions-item label="楼栋">{{ studentInfo?.buildingName || '未分配' }}</el-descriptions-item>
                  <el-descriptions-item label="房间">{{ studentInfo?.roomNumber || '未分配' }}</el-descriptions-item>
                </el-descriptions>
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdateInfo">保存修改</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
          
          <el-tab-pane label="修改密码" name="password">
            <el-form ref="pwdFormRef" :model="pwdForm" :rules="pwdRules" label-width="100px">
              <el-form-item label="原密码" prop="oldPassword">
                <el-input v-model="pwdForm.oldPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="新密码" prop="newPassword">
                <el-input v-model="pwdForm.newPassword" type="password" show-password />
              </el-form-item>
              <el-form-item label="确认密码" prop="confirmPassword">
                <el-input v-model="pwdForm.confirmPassword" type="password" show-password />
              </el-form-item>
              <el-form-item>
                <el-button type="primary" @click="handleUpdatePassword">修改密码</el-button>
              </el-form-item>
            </el-form>
          </el-tab-pane>
        </el-tabs>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { updatePassword, uploadAvatar, getAvatarUrl } from '@/api/auth'
import { updateStudent, getStudentByUserId } from '@/api/student'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()
const activeTab = ref('info')
const infoFormRef = ref(null)
const pwdFormRef = ref(null)

const avatarUrl = computed(() => {
  return getAvatarUrl(userStore.userInfo.value?.avatar)
})

const studentInfo = ref(null)

const infoForm = reactive({
  realName: '',
  gender: '',
  phone: '',
  email: ''
})

const infoRules = {
  realName: [{ required: true, message: '请输入姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  email: [
    { required: true, message: '请输入邮箱', trigger: 'blur' },
    { type: 'email', message: '邮箱格式不正确', trigger: 'blur' }
  ]
}

const pwdForm = reactive({
  oldPassword: '',
  newPassword: '',
  confirmPassword: ''
})

const validateConfirm = (rule, value, callback) => {
  if (value !== pwdForm.newPassword) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const pwdRules = {
  oldPassword: [{ required: true, message: '请输入原密码', trigger: 'blur' }],
  newPassword: [
    { required: true, message: '请输入新密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认新密码', trigger: 'blur' },
    { validator: validateConfirm, trigger: 'blur' }
  ]
}

const loadStudentInfo = async () => {
  if (userStore.userInfo.value?.role === 'student') {
    try {
      const res = await getStudentByUserId(userStore.userInfo.value.id)
      studentInfo.value = res.data
    } catch (error) {
      console.error(error)
    }
  }
}

const initForm = () => {
  if (userStore.userInfo.value) {
    infoForm.realName = userStore.userInfo.value.realName
    infoForm.gender = userStore.userInfo.value.gender
    infoForm.phone = userStore.userInfo.value.phone
    infoForm.email = userStore.userInfo.value.email
  }
}

const beforeUpload = (file) => {
  const isImage = file.type.startsWith('image/')
  const isLt2M = file.size / 1024 / 1024 < 2
  
  if (!isImage) {
    ElMessage.error('只能上传图片文件!')
    return false
  }
  if (!isLt2M) {
    ElMessage.error('图片大小不能超过 2MB!')
    return false
  }
  return true
}

const handleUpload = async (options) => {
  try {
    const res = await uploadAvatar(options.file, userStore.userInfo.value.id)
    userStore.updateAvatar(res.data.url)
    ElMessage.success('头像更新成功')
  } catch (error) {
    console.error(error)
  }
}

const handleUpdateInfo = async () => {
  const valid = await infoFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (userStore.userInfo.value.role === 'student' && studentInfo.value) {
      await updateStudent({
        id: studentInfo.value.id,
        realName: infoForm.realName,
        gender: infoForm.gender,
        phone: infoForm.phone,
        email: infoForm.email
      })
    }
    
    userStore.updateUserInfo({
      realName: infoForm.realName,
      gender: infoForm.gender,
      phone: infoForm.phone,
      email: infoForm.email
    })
    
    ElMessage.success('修改成功')
  } catch (error) {
    console.error(error)
  }
}

const handleUpdatePassword = async () => {
  const valid = await pwdFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    await updatePassword({
      userId: userStore.userInfo.value.id,
      oldPassword: pwdForm.oldPassword,
      newPassword: pwdForm.newPassword
    })
    ElMessage.success('密码修改成功')
    pwdForm.oldPassword = ''
    pwdForm.newPassword = ''
    pwdForm.confirmPassword = ''
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  initForm()
  loadStudentInfo()
})
</script>

<style scoped>
.profile-page {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}

.avatar-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 40px 20px;
  text-align: center;
  color: #fff;
}

.avatar-card h3 {
  margin: 15px 0 5px 0;
  font-size: 18px;
}

.avatar-card p {
  margin: 0 0 20px 0;
  opacity: 0.9;
}
</style>
