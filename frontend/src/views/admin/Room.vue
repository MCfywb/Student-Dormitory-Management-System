<template>
  <div class="room-page">
    <div class="page-header">
      <h3 class="page-title">宿舍房间管理</h3>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增房间
      </el-button>
    </div>
    
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="房间号">
          <el-input v-model="searchForm.roomNumber" placeholder="请输入房间号" clearable />
        </el-form-item>
        <el-form-item label="所属楼栋">
          <el-select v-model="searchForm.buildingId" placeholder="请选择" clearable style="width: 200px">
            <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 200px">
            <el-option label="可入住" value="available" />
            <el-option label="已满" value="full" />
            <el-option label="维修中" value="maintenance" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="roomNumber" label="房间号" width="100" />
      <el-table-column prop="buildingName" label="所属楼栋" />
      <el-table-column prop="floor" label="楼层" width="80" />
      <el-table-column prop="capacity" label="床位容量" width="100" />
      <el-table-column prop="currentCount" label="已入住" width="80" />
      <el-table-column label="入住率" width="150">
        <template #default="{ row }">
          <el-progress :percentage="Math.round(row.currentCount / row.capacity * 100)" :stroke-width="10" />
        </template>
      </el-table-column>
      <el-table-column prop="roomType" label="房间类型">
        <template #default="{ row }">
          <el-tag :type="row.roomType === 'superior' ? 'warning' : ''">
            {{ row.roomType === 'superior' ? '优越间' : '标准间' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="price" label="费用(元/学期)" width="120" />
      <el-table-column label="操作" width="200" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleView(row)">详情</el-button>
          <el-button type="warning" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="danger" link @click="handleDelete(row)">删除</el-button>
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
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="房间号" prop="roomNumber">
          <el-input v-model="form.roomNumber" placeholder="请输入房间号" />
        </el-form-item>
        <el-form-item label="所属楼栋" prop="buildingId">
          <el-select v-model="form.buildingId" placeholder="请选择" style="width: 100%" @change="handleBuildingChange">
            <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层" prop="floor">
          <el-input-number v-model="form.floor" :min="1" :max="20" />
        </el-form-item>
        <el-form-item label="床位容量" prop="capacity">
          <el-input-number v-model="form.capacity" :min="1" :max="8" />
        </el-form-item>
        <el-form-item label="房间类型" prop="roomType">
          <el-select v-model="form.roomType" placeholder="请选择" style="width: 100%">
            <el-option label="标准间" value="standard" />
            <el-option label="优越间" value="superior" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择" style="width: 100%">
            <el-option label="可入住" value="available" />
            <el-option label="已满" value="full" />
            <el-option label="维修中" value="maintenance" />
          </el-select>
        </el-form-item>
        <el-form-item label="费用(元/学期)" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="detailVisible" title="房间详情" width="700px">
      <div class="detail-content">
        <div class="detail-section">
          <h4 class="section-title">房间信息</h4>
          <el-descriptions :column="3" border>
            <el-descriptions-item label="房间号">{{ detailRoom?.roomNumber }}</el-descriptions-item>
            <el-descriptions-item label="所属楼栋">{{ detailRoom?.buildingName }}</el-descriptions-item>
            <el-descriptions-item label="楼层">{{ detailRoom?.floor }}层</el-descriptions-item>
            <el-descriptions-item label="房间类型">
              <el-tag :type="detailRoom?.roomType === 'superior' ? 'warning' : ''">
                {{ detailRoom?.roomType === 'superior' ? '优越间' : '标准间' }}
              </el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="床位容量">{{ detailRoom?.capacity }}人</el-descriptions-item>
            <el-descriptions-item label="已入住">{{ detailRoom?.currentCount }}人</el-descriptions-item>
            <el-descriptions-item label="状态">
              <el-tag :type="getStatusType(detailRoom?.status)">{{ getStatusText(detailRoom?.status) }}</el-tag>
            </el-descriptions-item>
            <el-descriptions-item label="住宿费">{{ detailRoom?.price }}元/学期</el-descriptions-item>
            <el-descriptions-item label="入住率">
              <el-progress :percentage="Math.round(detailRoom?.currentCount / detailRoom?.capacity * 100)" :stroke-width="8" style="width: 100px" />
            </el-descriptions-item>
          </el-descriptions>
        </div>
        
        <div class="detail-section">
          <h4 class="section-title">入住学生</h4>
          <el-table :data="detailStudents" border stripe v-if="detailStudents.length > 0">
            <el-table-column prop="bedNumber" label="床位" width="80">
              <template #default="{ row }">
                <el-tag type="primary">{{ row.bedNumber }}号床</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="studentNo" label="学号" width="120" />
            <el-table-column prop="realName" label="姓名" width="100" />
            <el-table-column prop="gender" label="性别" width="60">
              <template #default="{ row }">
                <el-tag :type="row.gender === '男' ? 'primary' : 'danger'" size="small">{{ row.gender }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="className" label="班级" />
            <el-table-column prop="phone" label="联系电话" width="120" />
            <el-table-column label="入住时间" width="110">
              <template #default="{ row }">
                {{ formatDate(row.checkInTime) }}
              </template>
            </el-table-column>
            <el-table-column label="操作" width="80" fixed="right">
              <template #default="{ row }">
                <el-button type="danger" link @click="handleLeave(row)">退宿</el-button>
              </template>
            </el-table-column>
          </el-table>
          <el-empty v-else description="暂无入住学生" />
        </div>
        
        <div class="detail-section" v-if="detailRoom?.capacity > detailStudents.length">
          <h4 class="section-title">床位分布</h4>
          <div class="bed-grid">
            <div 
              v-for="bed in bedList" 
              :key="bed.number" 
              :class="['bed-item', bed.occupied ? 'occupied' : 'empty']"
            >
              <div class="bed-number">{{ bed.number }}号床</div>
              <div class="bed-status">
                <template v-if="bed.student">
                  <span class="student-name">{{ bed.student.realName }}</span>
                </template>
                <template v-else>
                  <span class="empty-text">空床位</span>
                </template>
              </div>
            </div>
          </div>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getRoomPage, saveRoom, updateRoom, deleteRoom, leaveRoom } from '@/api/room'
import { getBuildingList } from '@/api/building'
import { getRoommates } from '@/api/student'

const loading = ref(false)
const tableData = ref([])
const buildingList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const detailVisible = ref(false)
const detailRoom = ref(null)
const detailStudents = ref([])

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  roomNumber: '',
  buildingId: null,
  status: ''
})

const form = reactive({
  id: null,
  roomNumber: '',
  buildingId: null,
  buildingName: '',
  floor: 1,
  capacity: 4,
  currentCount: 0,
  roomType: 'standard',
  status: 'available',
  price: 1200
})

const rules = {
  roomNumber: [{ required: true, message: '请输入房间号', trigger: 'blur' }],
  buildingId: [{ required: true, message: '请选择所属楼栋', trigger: 'change' }],
  floor: [{ required: true, message: '请输入楼层', trigger: 'blur' }],
  capacity: [{ required: true, message: '请输入床位容量', trigger: 'blur' }]
}

const bedList = computed(() => {
  if (!detailRoom.value) return []
  const beds = []
  for (let i = 1; i <= detailRoom.value.capacity; i++) {
    const student = detailStudents.value.find(s => s.bedNumber === i)
    beds.push({
      number: i,
      occupied: !!student,
      student: student || null
    })
  }
  return beds
})

const getStatusType = (status) => {
  const types = { available: 'success', full: 'danger', maintenance: 'warning' }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = { available: '可入住', full: '已满', maintenance: '维修中' }
  return texts[status] || status
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleDateString('zh-CN')
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRoomPage({
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
  searchForm.roomNumber = ''
  searchForm.buildingId = null
  searchForm.status = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增房间'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑房间'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleView = async (row) => {
  detailRoom.value = row
  try {
    const res = await getRoommates(row.id)
    detailStudents.value = res.data
  } catch (error) {
    console.error(error)
  }
  detailVisible.value = true
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
      const res = await getRoommates(detailRoom.value.id)
      detailStudents.value = res.data
      loadData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该房间吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteRoom(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleBuildingChange = (val) => {
  const building = buildingList.value.find(item => item.id === val)
  if (building) {
    form.buildingName = building.buildingName
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (form.id) {
      await updateRoom(form)
      ElMessage.success('修改成功')
    } else {
      await saveRoom(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const resetForm = () => {
  form.id = null
  form.roomNumber = ''
  form.buildingId = null
  form.buildingName = ''
  form.floor = 1
  form.capacity = 4
  form.currentCount = 0
  form.roomType = 'standard'
  form.status = 'available'
  form.price = 1200
}

onMounted(() => {
  loadData()
  loadBuildings()
})
</script>

<style scoped>
.room-page {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}

.detail-content {
  max-height: 60vh;
  overflow-y: auto;
}

.detail-section {
  margin-bottom: 24px;
}

.detail-section:last-child {
  margin-bottom: 0;
}

.section-title {
  font-size: 16px;
  font-weight: 500;
  color: #303133;
  margin: 0 0 16px 0;
  padding-left: 10px;
  border-left: 3px solid #409EFF;
}

.bed-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.bed-item {
  padding: 16px;
  border-radius: 8px;
  text-align: center;
  transition: all 0.3s;
}

.bed-item.occupied {
  background: linear-gradient(135deg, #e6f7ff 0%, #bae7ff 100%);
  border: 1px solid #91d5ff;
}

.bed-item.empty {
  background: #f5f7fa;
  border: 1px dashed #dcdfe6;
}

.bed-number {
  font-size: 14px;
  font-weight: 500;
  color: #606266;
  margin-bottom: 8px;
}

.bed-status {
  font-size: 13px;
}

.student-name {
  color: #409EFF;
  font-weight: 500;
}

.empty-text {
  color: #c0c4cc;
}
</style>
