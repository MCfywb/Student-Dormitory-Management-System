<template>
  <div class="student-home">
    <el-row :gutter="20">
      <el-col :span="16">
        <div class="welcome-card">
          <div class="welcome-content">
            <h2>欢迎回来，{{ userInfo?.realName }}！</h2>
            <p>今天是 {{ currentDate }}，祝您学习愉快！</p>
          </div>
          <div class="welcome-icon">
            <el-icon :size="80"><Sunny /></el-icon>
          </div>
        </div>
        
        <div class="info-cards">
          <el-row :gutter="20">
            <el-col :span="8">
              <div class="info-card">
                <div class="info-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
                  <el-icon :size="30"><OfficeBuilding /></el-icon>
                </div>
                <div class="info-content">
                  <div class="info-value">{{ studentInfo?.buildingName || '未分配' }}</div>
                  <div class="info-label">楼栋</div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-card">
                <div class="info-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
                  <el-icon :size="30"><House /></el-icon>
                </div>
                <div class="info-content">
                  <div class="info-value">{{ studentInfo?.roomNumber || '未分配' }}</div>
                  <div class="info-label">房间</div>
                </div>
              </div>
            </el-col>
            <el-col :span="8">
              <div class="info-card">
                <div class="info-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
                  <el-icon :size="30"><Avatar /></el-icon>
                </div>
                <div class="info-content">
                  <div class="info-value">{{ studentInfo?.bedNumber || '-' }}</div>
                  <div class="info-label">床位</div>
                </div>
              </div>
            </el-col>
          </el-row>
        </div>
        
        <div class="notice-section">
          <div class="section-header">
            <h3>最新公告</h3>
            <el-button type="primary" link @click="$router.push('/student/notice')">查看更多</el-button>
          </div>
          <el-table :data="noticeList" size="small" @row-click="handleNoticeClick">
            <el-table-column prop="title" label="标题" />
            <el-table-column prop="noticeType" label="类型" width="80">
              <template #default="{ row }">
                <el-tag :type="getTypeColor(row.noticeType)" size="small">{{ getTypeText(row.noticeType) }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="publishTime" label="发布时间" width="180">
              <template #default="{ row }">
                {{ formatDate(row.publishTime) }}
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
      
      <el-col :span="8">
        <div class="user-card">
          <div class="user-avatar">
            <el-avatar :size="80" :src="avatarUrl" />
          </div>
          <div class="user-info">
            <h3>{{ userInfo?.realName }}</h3>
            <p>{{ studentInfo?.college }} · {{ studentInfo?.className }}</p>
          </div>
          <el-button type="primary" @click="$router.push('/student/profile')">个人中心</el-button>
        </div>
        
        <div class="quick-actions">
          <h3>快捷操作</h3>
          <div class="action-grid">
            <div class="action-item" @click="$router.push('/student/myroom')">
              <el-icon :size="24"><House /></el-icon>
              <span>我的宿舍</span>
            </div>
            <div class="action-item" @click="$router.push('/student/hygiene')">
              <el-icon :size="24"><Brush /></el-icon>
              <span>卫生检查</span>
            </div>
            <div class="action-item" @click="$router.push('/student/repair')">
              <el-icon :size="24"><Tools /></el-icon>
              <span>报修申请</span>
            </div>
            <div class="action-item" @click="$router.push('/student/fee')">
              <el-icon :size="24"><Money /></el-icon>
              <span>费用查询</span>
            </div>
          </div>
        </div>
        
        <div class="fee-reminder">
          <h3>费用提醒</h3>
          <div class="fee-item" v-for="fee in unpaidFees" :key="fee.id">
            <div class="fee-info">
              <span class="fee-name">{{ fee.feeTypeName }}</span>
              <span class="fee-semester">{{ fee.academicYear }} {{ fee.semester }}</span>
            </div>
            <div class="fee-amount">¥{{ fee.amount }}</div>
          </div>
          <el-empty v-if="unpaidFees.length === 0" description="暂无待缴费用" :image-size="60" />
        </div>
      </el-col>
    </el-row>
    
    <el-dialog v-model="noticeDetailVisible" title="公告详情" width="600px">
      <div class="notice-detail">
        <h2>{{ currentNotice.title }}</h2>
        <div class="meta">
          <span>发布时间：{{ formatDate(currentNotice.publishTime) }}</span>
        </div>
        <el-divider />
        <div class="content" v-html="currentNotice.content?.replace(/\n/g, '<br>')"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getNoticeList } from '@/api/notice'
import { getMyFees } from '@/api/fee'
import { getAvatarUrl } from '@/api/auth'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()

const userInfo = computed(() => userStore.userInfo.value)

const studentInfo = computed(() => {
  const info = localStorage.getItem('studentInfo')
  return info ? JSON.parse(info) : null
})

const avatarUrl = computed(() => {
  return getAvatarUrl(userInfo.value?.avatar)
})

const currentDate = computed(() => {
  const now = new Date()
  const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六']
  return `${now.getFullYear()}年${now.getMonth() + 1}月${now.getDate()}日 ${weekDays[now.getDay()]}`
})

const noticeList = ref([])
const unpaidFees = ref([])
const noticeDetailVisible = ref(false)
const currentNotice = ref({})

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const getTypeColor = (type) => {
  const colors = { notice: 'primary', announcement: 'success', warning: 'danger' }
  return colors[type] || ''
}

const getTypeText = (type) => {
  const texts = { notice: '通知', announcement: '公告', warning: '警告' }
  return texts[type] || type
}

const loadNotices = async () => {
  try {
    const res = await getNoticeList(5)
    noticeList.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const loadFees = async () => {
  if (!studentInfo.value?.id) return
  try {
    const res = await getMyFees(studentInfo.value.id)
    unpaidFees.value = res.data.filter(f => f.payStatus !== 'paid').slice(0, 3)
  } catch (error) {
    console.error(error)
  }
}

const handleNoticeClick = (row) => {
  currentNotice.value = row
  noticeDetailVisible.value = true
}

onMounted(() => {
  loadNotices()
  loadFees()
})
</script>

<style scoped>
.student-home {
  padding: 0;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 30px;
  color: #fff;
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.welcome-content h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
}

.welcome-content p {
  margin: 0;
  opacity: 0.9;
}

.welcome-icon {
  opacity: 0.3;
}

.info-cards {
  margin-bottom: 20px;
}

.info-card {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  display: flex;
  align-items: center;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.info-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  margin-right: 15px;
}

.info-value {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
}

.info-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

.notice-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.section-header h3 {
  margin: 0;
  font-size: 16px;
}

.user-card {
  background: #fff;
  border-radius: 12px;
  padding: 30px;
  text-align: center;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.user-avatar {
  margin-bottom: 15px;
}

.user-info h3 {
  margin: 0 0 5px 0;
  font-size: 18px;
}

.user-info p {
  margin: 0 0 20px 0;
  color: #909399;
  font-size: 14px;
}

.quick-actions {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.quick-actions h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.action-item:hover {
  background: #409EFF;
  color: #fff;
}

.action-item span {
  margin-top: 8px;
  font-size: 13px;
}

.fee-reminder {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.fee-reminder h3 {
  margin: 0 0 15px 0;
  font-size: 16px;
}

.fee-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  border-bottom: 1px solid #ebeef5;
}

.fee-item:last-child {
  border-bottom: none;
}

.fee-info {
  display: flex;
  flex-direction: column;
}

.fee-name {
  font-size: 14px;
  color: #303133;
}

.fee-semester {
  font-size: 12px;
  color: #909399;
}

.fee-amount {
  font-size: 16px;
  font-weight: bold;
  color: #f56c6c;
}

.notice-detail h2 {
  margin: 0 0 10px 0;
  font-size: 18px;
}

.notice-detail .meta {
  color: #909399;
  font-size: 14px;
}

.notice-detail .content {
  line-height: 1.8;
  color: #606266;
}
</style>
