<template>
  <div class="accommodation-page">
    <div class="page-header">
      <h3 class="page-title">住宿管理</h3>
    </div>
    
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="学号">
          <el-input v-model="searchForm.studentNo" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.realName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="楼栋">
          <el-select v-model="searchForm.buildingId" placeholder="请选择" clearable style="width: 200px">
            <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="studentNo" label="学号" width="120" />
      <el-table-column prop="realName" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="60">
        <template #default="{ row }">
          <el-tag :type="row.gender === '男' ? 'primary' : 'danger'" size="small">{{ row.gender }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="college" label="学院" />
      <el-table-column prop="major" label="专业" />
      <el-table-column prop="className" label="班级" />
      <el-table-column prop="buildingName" label="楼栋" width="80" />
      <el-table-column prop="roomNumber" label="房间" width="80" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="row.status === 'checked_in' ? 'success' : 'info'" size="small">
            {{ row.status === 'checked_in' ? '已入住' : '未入住' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">详情</el-button>
          <el-button type="success" link @click="handleAssign(row)" v-if="row.status !== 'checked_in'">分配</el-button>
          <el-button type="danger" link @click="handleLeave(row)" v-if="row.status === 'checked_in'">退宿</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <el-pagination
      v-model:current-page="pagination.current"
      v-model:page-size="pagination.size"
      :total="pagination.total"
      :page-sizes="[10, 20, 50]"
      layout="total, sizes, prev, pager, next, jumper"
      @size-change="loadData"
      @current-change="loadData"
      style="margin-top: 20px; display: flex; justify-content: flex-end;"
    />
    
    <el-dialog v-model="detailVisible" title="学生详情" width="700px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="学号">{{ detailData.studentNo }}</el-descriptions-item>
        <el-descriptions-item label="姓名">{{ detailData.realName }}</el-descriptions-item>
        <el-descriptions-item label="性别">{{ detailData.gender }}</el-descriptions-item>
        <el-descriptions-item label="手机号">{{ detailData.phone }}</el-descriptions-item>
        <el-descriptions-item label="邮箱">{{ detailData.email }}</el-descriptions-item>
        <el-descriptions-item label="学院">{{ detailData.college }}</el-descriptions-item>
        <el-descriptions-item label="专业">{{ detailData.major }}</el-descriptions-item>
        <el-descriptions-item label="班级">{{ detailData.className }}</el-descriptions-item>
        <el-descriptions-item label="年级">{{ detailData.grade }}</el-descriptions-item>
        <el-descriptions-item label="楼栋">{{ detailData.buildingName || '未分配' }}</el-descriptions-item>
        <el-descriptions-item label="房间">{{ detailData.roomNumber || '未分配' }}</el-descriptions-item>
        <el-descriptions-item label="床位号">{{ detailData.bedNumber || '-' }}</el-descriptions-item>
        <el-descriptions-item label="入住时间">{{ formatDate(detailData.checkInTime) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="detailData.status === 'checked_in' ? 'success' : 'info'" size="small">
            {{ detailData.status === 'checked_in' ? '已入住' : '未入住' }}
          </el-tag>
        </el-descriptions-item>
      </el-descriptions>
    </el-dialog>
    
    <el-dialog v-model="assignVisible" title="分配房间" width="500px">
      <el-form :model="assignForm" label-width="80px">
        <el-form-item label="学生">
          <el-input :value="assignForm.studentName" disabled />
        </el-form-item>
        <el-form-item label="楼栋" required>
          <el-select v-model="assignForm.buildingId" placeholder="请选择楼栋" @change="handleBuildingChange" style="width: 100%">
            <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="房间" required>
          <el-select v-model="assignForm.roomId" placeholder="请选择房间" @change="handleRoomChange" style="width: 100%">
            <el-option v-for="item in roomList" :key="item.id" :label="item.roomNumber" :value="item.id" :disabled="item.availableBeds === 0">
              <span>{{ item.roomNumber }}</span>
              <span style="float: right; color: #999; font-size: 12px;">剩余{{ item.availableBeds }}床位</span>
            </el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="床位" required>
          <el-select v-model="assignForm.bedNumber" placeholder="请选择床位" style="width: 100%">
            <el-option 
              v-for="bed in allBeds" 
              :key="bed.number" 
              :label="`${bed.number}号床`" 
              :value="bed.number"
              :disabled="bed.occupied"
            >
              <span>{{ bed.number }}号床</span>
              <span v-if="bed.occupied" style="float: right; color: #f56c6c; font-size: 12px;">已占用</span>
            </el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="assignVisible = false">取消</el-button>
        <el-button type="primary" @click="handleAssignSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getStudentPage, getStudentById } from '@/api/student'
import { getBuildingList } from '@/api/building'
import { leaveRoom, getRoomPage, assignRoom } from '@/api/room'

const loading = ref(false)
const tableData = ref([])
const buildingList = ref([])
const detailVisible = ref(false)
const detailData = ref({})
const assignVisible = ref(false)
const roomList = ref([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  studentNo: '',
  realName: '',
  buildingId: null
})

const assignForm = reactive({
  studentId: null,
  studentName: '',
  buildingId: null,
  roomId: null,
  bedNumber: null
})

const allBeds = computed(() => {
  const room = roomList.value.find(r => r.id === assignForm.roomId)
  if (!room) return []
  const occupiedBeds = room.occupiedBeds || []
  const beds = []
  for (let i = 1; i <= room.capacity; i++) {
    beds.push({
      number: i,
      occupied: occupiedBeds.includes(i)
    })
  }
  return beds
})

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getStudentPage({
      current: pagination.current,
      size: pagination.size,
      ...searchForm
    })
    tableData.value = res.data.records
    pagination.total = res.data.total
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadBuildings = async () => {
  try {
    const res = await getBuildingList()
    buildingList.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.studentNo = ''
  searchForm.realName = ''
  searchForm.buildingId = null
  handleSearch()
}

const handleView = async (row) => {
  try {
    const res = await getStudentById(row.id)
    detailData.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const handleLeave = (row) => {
  ElMessageBox.confirm(`确定让 ${row.realName} 退宿吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await leaveRoom(row.id)
      ElMessage.success('退宿成功')
      loadData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleAssign = (row) => {
  assignForm.studentId = row.id
  assignForm.studentName = row.realName
  assignForm.buildingId = null
  assignForm.roomId = null
  assignForm.bedNumber = null
  roomList.value = []
  assignVisible.value = true
}

const handleBuildingChange = async () => {
  assignForm.roomId = null
  assignForm.bedNumber = null
  if (!assignForm.buildingId) {
    roomList.value = []
    return
  }
  try {
    const res = await getRoomPage({ current: 1, size: 100, buildingId: assignForm.buildingId })
    roomList.value = res.data.records
  } catch (error) {
    console.error(error)
  }
}

const handleRoomChange = () => {
  assignForm.bedNumber = null
}

const handleAssignSubmit = async () => {
  if (!assignForm.buildingId || !assignForm.roomId || !assignForm.bedNumber) {
    ElMessage.warning('请选择楼栋、房间和床位')
    return
  }
  try {
    await assignRoom({
      studentId: assignForm.studentId,
      roomId: assignForm.roomId,
      bedNumber: assignForm.bedNumber
    })
    ElMessage.success('分配成功')
    assignVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
  loadBuildings()
})
</script>

<style scoped>
.accommodation-page {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}
</style>
