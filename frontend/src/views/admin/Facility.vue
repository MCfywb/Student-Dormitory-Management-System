<template>
  <div class="facility-page">
    <div class="page-header">
      <h3 class="page-title">设施管理</h3>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增设施
      </el-button>
    </div>
    
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="设施名称">
          <el-input v-model="searchForm.facilityName" placeholder="请输入设施名称" clearable />
        </el-form-item>
        <el-form-item label="楼栋">
          <el-select v-model="searchForm.buildingId" placeholder="请选择" clearable style="width: 200px">
            <el-option v-for="item in buildingList" :key="item.id" :label="item.buildingName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 200px">
            <el-option label="正常" value="normal" />
            <el-option label="损坏" value="damaged" />
            <el-option label="维修中" value="repairing" />
            <el-option label="已报废" value="scrapped" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
      <el-table-column prop="facilityName" label="设施名称" />
      <el-table-column prop="facilityType" label="类型">
        <template #default="{ row }">
          <el-tag :type="getTypeColor(row.facilityType)">{{ getTypeText(row.facilityType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="buildingName" label="楼栋" />
      <el-table-column prop="roomNumber" label="房间" />
      <el-table-column prop="purchaseDate" label="购买日期" width="120" />
      <el-table-column prop="price" label="价格(元)" width="100" />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
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
        <el-form-item label="设施名称" prop="facilityName">
          <el-input v-model="form.facilityName" placeholder="请输入设施名称" />
        </el-form-item>
        <el-form-item label="设施类型" prop="facilityType">
          <el-select v-model="form.facilityType" placeholder="请选择" style="width: 100%">
            <el-option label="家具" value="furniture" />
            <el-option label="电器" value="appliance" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="所属房间" prop="roomId">
          <el-cascader
            v-model="form.roomPath"
            :options="roomOptions"
            :props="{ value: 'id', label: 'name', children: 'children' }"
            placeholder="请选择楼栋和房间"
            style="width: 100%"
            @change="handleRoomChange"
          />
        </el-form-item>
        <el-form-item label="购买日期" prop="purchaseDate">
          <el-date-picker v-model="form.purchaseDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number v-model="form.price" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-select v-model="form.status" placeholder="请选择" style="width: 100%">
            <el-option label="正常" value="normal" />
            <el-option label="损坏" value="damaged" />
            <el-option label="维修中" value="repairing" />
            <el-option label="已报废" value="scrapped" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" :rows="2" placeholder="请输入备注" />
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
import { ref, reactive, onMounted, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFacilityPage, saveFacility, updateFacility, deleteFacility } from '@/api/facility'
import { getBuildingList } from '@/api/building'
import { getRoomsByBuilding } from '@/api/room'

const loading = ref(false)
const tableData = ref([])
const buildingList = ref([])
const roomMap = ref({})
const dialogVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  facilityName: '',
  buildingId: null,
  status: ''
})

const form = reactive({
  id: null,
  facilityName: '',
  facilityType: '',
  roomId: null,
  roomNumber: '',
  buildingId: null,
  buildingName: '',
  roomPath: [],
  purchaseDate: '',
  price: 0,
  status: 'normal',
  remark: ''
})

const rules = {
  facilityName: [{ required: true, message: '请输入设施名称', trigger: 'blur' }],
  facilityType: [{ required: true, message: '请选择设施类型', trigger: 'change' }]
}

const roomOptions = computed(() => {
  return buildingList.value.map(building => ({
    id: building.id,
    name: building.buildingName,
    children: (roomMap.value[building.id] || []).map(room => ({
      id: room.id,
      name: room.roomNumber
    }))
  }))
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

const loadData = async () => {
  loading.value = true
  try {
    const res = await getFacilityPage({
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
    for (const building of buildingList.value) {
      const roomsRes = await getRoomsByBuilding(building.id)
      roomMap.value[building.id] = roomsRes.data
    }
  } catch (error) {
    console.error(error)
  }
}

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.facilityName = ''
  searchForm.buildingId = null
  searchForm.status = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '新增设施'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (row) => {
  dialogTitle.value = '编辑设施'
  Object.assign(form, row)
  if (row.buildingId && row.roomId) {
    form.roomPath = [row.buildingId, row.roomId]
  }
  dialogVisible.value = true
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该设施吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteFacility(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleRoomChange = (val) => {
  if (val && val.length === 2) {
    const building = buildingList.value.find(b => b.id === val[0])
    const room = roomMap.value[val[0]]?.find(r => r.id === val[1])
    if (building && room) {
      form.buildingId = building.id
      form.buildingName = building.buildingName
      form.roomId = room.id
      form.roomNumber = room.roomNumber
    }
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (form.id) {
      await updateFacility(form)
      ElMessage.success('修改成功')
    } else {
      await saveFacility(form)
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
  form.facilityName = ''
  form.facilityType = ''
  form.roomId = null
  form.roomNumber = ''
  form.buildingId = null
  form.buildingName = ''
  form.roomPath = []
  form.purchaseDate = ''
  form.price = 0
  form.status = 'normal'
  form.remark = ''
}

onMounted(() => {
  loadData()
  loadBuildings()
})
</script>

<style scoped>
.facility-page {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}
</style>
