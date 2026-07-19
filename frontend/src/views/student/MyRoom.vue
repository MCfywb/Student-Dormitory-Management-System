<template>
  <div class="my-room">
    <div class="page-header">
      <h3 class="page-title">我的宿舍</h3>
    </div>
    
    <div v-if="isCheckIn" class="room-content">
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="room-card">
            <div class="room-header">
              <h3>{{ roomInfo?.buildingName }} - {{ roomInfo?.roomNumber }}</h3>
              <el-tag :type="roomInfo?.status === 'full' ? 'danger' : 'success'">
                {{ roomInfo?.status === 'full' ? '已满' : '可入住' }}
              </el-tag>
            </div>
            <el-divider />
            <el-descriptions :column="2" border>
              <el-descriptions-item label="楼栋">{{ roomInfo?.buildingName }}</el-descriptions-item>
              <el-descriptions-item label="楼层">{{ roomInfo?.floor }}层</el-descriptions-item>
              <el-descriptions-item label="房间类型">
                <el-tag :type="roomInfo?.roomType === 'superior' ? 'warning' : ''">
                  {{ roomInfo?.roomType === 'superior' ? '优越间' : '标准间' }}
                </el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="床位容量">{{ roomInfo?.capacity }}人</el-descriptions-item>
              <el-descriptions-item label="已入住">{{ roomInfo?.currentCount }}人</el-descriptions-item>
              <el-descriptions-item label="住宿费">{{ roomInfo?.price }}元/学期</el-descriptions-item>
              <el-descriptions-item label="我的床位">
                <el-tag type="primary">{{ studentInfo?.bedNumber }}号床</el-tag>
              </el-descriptions-item>
              <el-descriptions-item label="入住时间">{{ formatDate(studentInfo?.checkInTime) }}</el-descriptions-item>
            </el-descriptions>
          </div>
        </el-col>
        
        <el-col :span="12">
          <div class="roommates-card" v-if="roommates.length > 0">
            <h3>室友信息</h3>
            <el-divider />
            <div class="roommate-list">
              <div class="roommate-item" v-for="mate in roommates" :key="mate.id">
                <el-avatar :size="50">{{ mate.realName?.charAt(0) }}</el-avatar>
                <div class="roommate-info">
                  <div class="roommate-name">{{ mate.realName }}</div>
                  <div class="roommate-detail">{{ mate.className }}</div>
                </div>
                <div class="roommate-bed">床位 {{ mate.bedNumber }}</div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
      
      <div class="facility-section" v-if="facilities.length > 0">
        <h3>房间设施</h3>
        <el-divider />
        <el-table :data="facilities" border>
          <el-table-column prop="facilityName" label="设施名称" />
          <el-table-column prop="facilityType" label="类型">
            <template #default="{ row }">
              <el-tag :type="getTypeColor(row.facilityType)">{{ getTypeText(row.facilityType) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="purchaseDate" label="购买日期" />
          <el-table-column prop="price" label="价格(元)" />
          <el-table-column prop="status" label="状态">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="remark" label="备注" show-overflow-tooltip />
        </el-table>
      </div>
    </div>
    
    <div v-else class="empty-content">
      <div class="empty-card">
        <el-icon class="empty-icon"><House /></el-icon>
        <h3 class="empty-title">暂未分配宿舍</h3>
        <p class="empty-desc">您目前还没有入住宿舍，请联系宿管老师进行宿舍分配</p>
        <div class="empty-info">
          <el-descriptions :column="1" border size="small">
            <el-descriptions-item label="学生姓名">{{ studentInfo?.realName }}</el-descriptions-item>
            <el-descriptions-item label="学号">{{ studentInfo?.studentNo }}</el-descriptions-item>
            <el-descriptions-item label="学院">{{ studentInfo?.college }}</el-descriptions-item>
            <el-descriptions-item label="班级">{{ studentInfo?.className }}</el-descriptions-item>
          </el-descriptions>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { House } from '@element-plus/icons-vue'
import { getRoomById } from '@/api/room'
import { getRoommates, getStudentByUserId } from '@/api/student'
import { getFacilityByRoom } from '@/api/facility'

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')
const studentInfo = ref(null)
const roomInfo = ref(null)
const roommates = ref([])
const facilities = ref([])

const isCheckIn = computed(() => {
  return studentInfo.value?.status === 'checked_in'
})

const getTypeColor = (type) => {
  const colors = { furniture: 'primary', appliance: 'warning', other: 'info' }
  return colors[type] || ''
}

const getTypeText = (type) => {
  const texts = { furniture: '家具', appliance: '电器', other: '其他' }
  return texts[type] || type
}

const getStatusType = (status) => {
  const types = { normal: 'success', damaged: 'danger', repairing: 'warning', scrapped: 'info' }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { normal: '正常', damaged: '损坏', repairing: '维修中', scrapped: '已报废' }
  return texts[status] || status
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

const loadData = async () => {
  if (!userInfo.id) return
  
  try {
    const studentRes = await getStudentByUserId(userInfo.id)
    studentInfo.value = studentRes.data
    
    if (studentInfo.value?.status === 'checked_in' && studentInfo.value?.roomId) {
      const roomRes = await getRoomById(studentInfo.value.roomId)
      roomInfo.value = roomRes.data
      
      const roommateRes = await getRoommates(studentInfo.value.roomId)
      roommates.value = roommateRes.data
      
      const facilityRes = await getFacilityByRoom(studentInfo.value.roomId)
      facilities.value = facilityRes.data
    }
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.my-room {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
  min-height: calc(100vh - 150px);
}

.room-content {
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.room-card, .roommates-card, .facility-section {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
  margin-bottom: 20px;
}

.room-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.room-header h3 {
  margin: 0;
  font-size: 18px;
}

.roommates-card h3, .facility-section h3 {
  margin: 0;
  font-size: 16px;
}

.roommate-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.roommate-item {
  display: flex;
  align-items: center;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 8px;
}

.roommate-info {
  flex: 1;
  margin-left: 15px;
}

.roommate-name {
  font-size: 14px;
  font-weight: 500;
}

.roommate-detail {
  font-size: 12px;
  color: #909399;
}

.roommate-bed {
  font-size: 13px;
  color: #409EFF;
}

.empty-content {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 500px;
}

.empty-card {
  text-align: center;
  padding: 60px 80px;
  background: linear-gradient(135deg, #f5f7fa 0%, #e4e7ed 100%);
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.08);
}

.empty-icon {
  font-size: 80px;
  color: #c0c4cc;
  margin-bottom: 20px;
}

.empty-title {
  font-size: 24px;
  color: #303133;
  margin: 0 0 12px 0;
  font-weight: 500;
}

.empty-desc {
  font-size: 14px;
  color: #909399;
  margin: 0 0 30px 0;
}

.empty-info {
  max-width: 400px;
  margin: 0 auto;
  text-align: left;
}
</style>
