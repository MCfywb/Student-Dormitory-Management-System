<template>
  <div class="visitor-page">
    <div class="page-header">
      <h3 class="page-title">来访登记</h3>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>登记来访
      </el-button>
    </div>
    
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="来访人">
          <el-input v-model="searchForm.visitorName" placeholder="请输入来访人姓名" clearable />
        </el-form-item>
        <el-form-item label="楼栋">
          <el-select v-model="searchForm.buildingName" placeholder="请选择" clearable style="width: 200px">
            <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.buildingName" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 200px">
            <el-option label="访问中" value="visiting" />
            <el-option label="已离开" value="left" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="visitorName" label="来访人" width="100" />
      <el-table-column prop="visitorPhone" label="联系电话" width="120" />
      <el-table-column prop="buildingName" label="访问楼栋" />
      <el-table-column prop="roomNumber" label="访问房间" width="100" />
      <el-table-column prop="visitedStudentName" label="被访学生" width="100" />
      <el-table-column prop="visitReason" label="来访事由" show-overflow-tooltip />
      <el-table-column prop="visitTime" label="来访时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.visitTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="leaveTime" label="离开时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.leaveTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 'visiting' ? 'warning' : 'success'">
            {{ row.status === 'visiting' ? '访问中' : '已离开' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="registerName" label="登记人" width="100" />
      <el-table-column label="操作" width="120" fixed="right">
        <template #default="{ row }">
          <el-button type="success" link @click="handleLeave(row)" v-if="row.status === 'visiting'">离开</el-button>
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
    
    <el-dialog v-model="dialogVisible" title="登记来访" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="来访人姓名" prop="visitorName">
              <el-input v-model="form.visitorName" placeholder="请输入来访人姓名" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="联系电话" prop="visitorPhone">
              <el-input v-model="form.visitorPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="身份证号" prop="visitorIdCard">
          <el-input v-model="form.visitorIdCard" placeholder="请输入身份证号" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="访问楼栋" prop="buildingId">
              <el-select v-model="form.buildingId" placeholder="请选择" style="width: 100%" @change="handleBuildingChange">
                <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="访问房间" prop="roomId">
              <el-select v-model="form.roomId" placeholder="请选择" style="width: 100%" @change="handleRoomChange">
                <el-option v-for="item in roomList" :key="item.id" :label="item.roomNumber" :value="item.id" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="被访学生" prop="visitedStudentId">
          <el-select v-model="form.visitedStudentId" placeholder="请选择" style="width: 100%" @change="handleStudentChange">
            <el-option v-for="item in studentList" :key="item.id" :label="item.realName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="来访事由" prop="visitReason">
          <el-input v-model="form.visitReason" type="textarea" :rows="2" placeholder="请输入来访事由" />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getVisitorPage, saveVisitor, leaveVisitor, deleteVisitor } from '@/api/visitor'
import { getBuildingList } from '@/api/building'
import { getRoomsByBuilding } from '@/api/room'
import { getStudentPage } from '@/api/student'

const loading = ref(false)
const tableData = ref([])
const buildingList = ref([])
const roomList = ref([])
const studentList = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  visitorName: '',
  buildingName: '',
  status: ''
})

const form = reactive({
  visitorName: '',
  visitorPhone: '',
  visitorIdCard: '',
  buildingId: null,
  buildingName: '',
  roomId: null,
  roomNumber: '',
  visitedStudentId: null,
  visitedStudentName: '',
  visitReason: '',
  registerId: userInfo.id,
  registerName: userInfo.realName,
  remark: ''
})

const rules = {
  visitorName: [{ required: true, message: '请输入来访人姓名', trigger: 'blur' }],
  visitorPhone: [{ required: true, message: '请输入联系电话', trigger: 'blur' }],
  buildingId: [{ required: true, message: '请选择访问楼栋', trigger: 'change' }],
  visitReason: [{ required: true, message: '请输入来访事由', trigger: 'blur' }]
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getVisitorPage({
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
  searchForm.visitorName = ''
  searchForm.buildingName = ''
  searchForm.status = ''
  handleSearch()
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handleLeave = (row) => {
  ElMessageBox.confirm('确定该访客已离开吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'info'
  }).then(async () => {
    try {
      await leaveVisitor(row.id)
      ElMessage.success('已登记离开')
      loadData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteVisitor(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleBuildingChange = async (val) => {
  const building = buildingList.value.find(b => b.id === val)
  if (building) {
    form.buildingName = building.buildingName
  }
  try {
    const res = await getRoomsByBuilding(val)
    roomList.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleRoomChange = async (val) => {
  const room = roomList.value.find(r => r.id === val)
  if (room) {
    form.roomNumber = room.roomNumber
  }
  try {
    const res = await getStudentPage({ current: 1, size: 100, buildingId: form.buildingId })
    studentList.value = res.data.records.filter(s => s.roomNumber === form.roomNumber)
  } catch (error) {
    console.error(error)
  }
}

const handleStudentChange = (val) => {
  const student = studentList.value.find(s => s.id === val)
  if (student) {
    form.visitedStudentName = student.realName
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    await saveVisitor(form)
    ElMessage.success('登记成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const resetForm = () => {
  form.visitorName = ''
  form.visitorPhone = ''
  form.visitorIdCard = ''
  form.buildingId = null
  form.buildingName = ''
  form.roomId = null
  form.roomNumber = ''
  form.visitedStudentId = null
  form.visitedStudentName = ''
  form.visitReason = ''
  form.registerId = userInfo.id
  form.registerName = userInfo.realName
  form.remark = ''
  roomList.value = []
  studentList.value = []
}

onMounted(() => {
  loadData()
  loadBuildings()
})
</script>

<style scoped>
.visitor-page {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}
</style>
