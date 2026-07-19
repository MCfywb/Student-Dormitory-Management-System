<template>
  <div class="building-page">
    <div class="page-header">
      <h3 class="page-title">宿舍楼管理</h3>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增宿舍楼
      </el-button>
    </div>
    
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="楼栋名称">
          <el-input v-model="searchForm.buildingName" placeholder="请输入楼栋名称" clearable />
        </el-form-item>
        <el-form-item label="楼栋类型">
          <el-select v-model="searchForm.buildingType" placeholder="请选择" clearable style="width: 200px">
            <el-option label="男生楼" value="male" />
            <el-option label="女生楼" value="female" />
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
      <el-table-column prop="buildingName" label="楼栋名称" />
      <el-table-column prop="buildingType" label="楼栋类型">
        <template #default="{ row }">
          <el-tag :type="row.buildingType === 'male' ? 'primary' : 'danger'">
            {{ row.buildingType === 'male' ? '男生楼' : '女生楼' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="floors" label="楼层数" />
      <el-table-column prop="roomsPerFloor" label="每层房间数" />
      <el-table-column prop="managerName" label="宿管" />
      <el-table-column prop="description" label="描述" show-overflow-tooltip />
      <el-table-column prop="createTime" label="创建时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link @click="handleView(row)">详情</el-button>
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
        <el-form-item label="楼栋名称" prop="buildingName">
          <el-input v-model="form.buildingName" placeholder="请输入楼栋名称" />
        </el-form-item>
        <el-form-item label="楼栋类型" prop="buildingType">
          <el-select v-model="form.buildingType" placeholder="请选择" style="width: 100%">
            <el-option label="男生楼" value="male" />
            <el-option label="女生楼" value="female" />
          </el-select>
        </el-form-item>
        <el-form-item label="楼层数" prop="floors">
          <el-input-number v-model="form.floors" :min="1" :max="20" />
        </el-form-item>
        <el-form-item label="每层房间数" prop="roomsPerFloor">
          <el-input-number v-model="form.roomsPerFloor" :min="1" :max="50" />
        </el-form-item>
        <el-form-item label="宿管" prop="managerId">
          <el-select v-model="form.managerId" placeholder="请选择宿管" style="width: 100%" @change="handleManagerChange">
            <el-option v-for="item in managerList" :key="item.id" :label="item.realName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="detailVisible" title="宿舍楼详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="楼栋名称">{{ detailData.buildingName }}</el-descriptions-item>
        <el-descriptions-item label="楼栋类型">
          <el-tag :type="detailData.buildingType === 'male' ? 'primary' : 'danger'">
            {{ detailData.buildingType === 'male' ? '男生楼' : '女生楼' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="楼层数">{{ detailData.floors }}</el-descriptions-item>
        <el-descriptions-item label="每层房间数">{{ detailData.roomsPerFloor }}</el-descriptions-item>
        <el-descriptions-item label="宿管">{{ detailData.managerName }}</el-descriptions-item>
        <el-descriptions-item label="描述">{{ detailData.description }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getBuildingPage, saveBuilding, updateBuilding, deleteBuilding, getBuildingById } from '@/api/building'
import { getUserInfo } from '@/api/auth'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const managerList = ref([])
const detailData = ref({})

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  buildingName: '',
  buildingType: ''
})

const form = reactive({
  id: null,
  buildingName: '',
  buildingType: '',
  floors: 6,
  roomsPerFloor: 20,
  managerId: null,
  managerName: '',
  description: ''
})

const rules = {
  buildingName: [{ required: true, message: '请输入楼栋名称', trigger: 'blur' }],
  buildingType: [{ required: true, message: '请选择楼栋类型', trigger: 'change' }],
  floors: [{ required: true, message: '请输入楼层数', trigger: 'blur' }],
  roomsPerFloor: [{ required: true, message: '请输入每层房间数', trigger: 'blur' }]
}

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getBuildingPage({
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

const loadManagers = async () => {
  try {
    const res = await getUserInfo(2)
    if (res.data && res.data.user) {
      managerList.value = [res.data.user]
    }
  } catch (error) {
    managerList.value = [
      { id: 2, realName: '张宿管' },
      { id: 3, realName: '李宿管' },
      { id: 4, realName: '王宿管' }
    ]
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.buildingName = ''
  searchForm.buildingType = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增宿舍楼'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑宿舍楼'
  try {
    const res = await getBuildingById(row.id)
    Object.assign(form, res.data)
    dialogVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const handleView = async (row) => {
  try {
    const res = await getBuildingById(row.id)
    detailData.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该宿舍楼吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteBuilding(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleManagerChange = (val) => {
  const manager = managerList.value.find(item => item.id === val)
  if (manager) {
    form.managerName = manager.realName
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (form.id) {
      await updateBuilding(form)
      ElMessage.success('修改成功')
    } else {
      await saveBuilding(form)
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
  form.buildingName = ''
  form.buildingType = ''
  form.floors = 6
  form.roomsPerFloor = 20
  form.managerId = null
  form.managerName = ''
  form.description = ''
}

onMounted(() => {
  loadData()
  loadManagers()
})
</script>

<style scoped>
.building-page {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}
</style>
