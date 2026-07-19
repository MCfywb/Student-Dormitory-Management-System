<template>
  <div class="hygiene-page">
    <div class="page-header">
      <h3 class="page-title">卫生检查管理</h3>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增检查记录
      </el-button>
    </div>
    
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="房间号">
          <el-input v-model="searchForm.roomNumber" placeholder="请输入房间号" clearable />
        </el-form-item>
        <el-form-item label="楼栋">
          <el-select v-model="searchForm.buildingId" placeholder="请选择" clearable style="width: 200px">
            <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="等级">
          <el-select v-model="searchForm.level" placeholder="请选择" clearable style="width: 200px">
            <el-option label="优秀" value="excellent" />
            <el-option label="良好" value="good" />
            <el-option label="合格" value="qualified" />
            <el-option label="不合格" value="unqualified" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="roomNumber" label="房间号" width="100" />
      <el-table-column prop="buildingName" label="楼栋" />
      <el-table-column prop="checkDate" label="检查日期" width="120" />
      <el-table-column prop="score" label="分数" width="80">
        <template #default="{ row }">
          <el-tag :type="getScoreType(row.score)">{{ row.score }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="level" label="等级">
        <template #default="{ row }">
          <el-tag :type="getLevelType(row.level)">{{ getLevelText(row.level) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="checkerName" label="检查人" />
      <el-table-column prop="remark" label="备注" show-overflow-tooltip />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
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
        <el-form-item label="房间" prop="roomId">
          <el-select v-model="form.roomId" placeholder="请选择房间" style="width: 100%" @change="handleRoomChange">
            <el-option v-for="item in roomList" :key="item.id" :label="`${item.buildingName} - ${item.roomNumber}`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="检查日期" prop="checkDate">
          <el-date-picker v-model="form.checkDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="分数" prop="score">
          <el-slider v-model="form.score" :min="0" :max="100" show-input />
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="请输入检查备注" />
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
import { getHygienePage, saveHygiene, updateHygiene, deleteHygiene } from '@/api/hygiene'
import { getBuildingList } from '@/api/building'
import { getRoomsByBuilding } from '@/api/room'

const loading = ref(false)
const tableData = ref([])
const buildingList = ref([])
const roomList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  roomNumber: '',
  buildingId: null,
  level: ''
})

const form = reactive({
  id: null,
  roomId: null,
  roomNumber: '',
  buildingId: null,
  buildingName: '',
  checkDate: '',
  score: 80,
  level: '',
  checkerId: userInfo.id,
  checkerName: userInfo.realName,
  remark: ''
})

const rules = {
  roomId: [{ required: true, message: '请选择房间', trigger: 'change' }],
  checkDate: [{ required: true, message: '请选择检查日期', trigger: 'change' }],
  score: [{ required: true, message: '请输入分数', trigger: 'blur' }]
}

const getScoreType = (score) => {
  if (score >= 90) return 'success'
  if (score >= 80) return 'primary'
  if (score >= 60) return 'warning'
  return 'danger'
}

const getLevelType = (level) => {
  const types = { excellent: 'success', good: 'primary', qualified: 'warning', unqualified: 'danger' }
  return types[level] || 'info'
}

const getLevelText = (level) => {
  const texts = { excellent: '优秀', good: '良好', qualified: '合格', unqualified: '不合格' }
  return texts[level] || level
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getHygienePage({
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
    if (buildingList.value.length > 0) {
      loadRooms(buildingList.value[0].id)
    }
  } catch (error) {
    console.error(error)
  }
}

const loadRooms = async (buildingId) => {
  try {
    const res = await getRoomsByBuilding(buildingId)
    roomList.value = res.data
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
  searchForm.level = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增检查记录'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑检查记录'
  Object.assign(form, row)
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteHygiene(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleRoomChange = (val) => {
  const room = roomList.value.find(item => item.id === val)
  if (room) {
    form.roomNumber = room.roomNumber
    form.buildingId = room.buildingId
    form.buildingName = room.buildingName
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (form.id) {
      await updateHygiene(form)
      ElMessage.success('修改成功')
    } else {
      await saveHygiene(form)
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
  form.roomId = null
  form.roomNumber = ''
  form.buildingId = null
  form.buildingName = ''
  form.checkDate = ''
  form.score = 80
  form.level = ''
  form.checkerId = userInfo.id
  form.checkerName = userInfo.realName
  form.remark = ''
}

onMounted(() => {
  loadData()
  loadBuildings()
})
</script>

<style scoped>
.hygiene-page {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}
</style>
