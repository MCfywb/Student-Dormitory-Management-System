<template>
  <div class="repair-page">
    <div class="page-header">
      <h3 class="page-title">维修管理</h3>
    </div>
    
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="房间号">
          <el-input v-model="searchForm.roomNumber" placeholder="请输入房间号" clearable />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="searchForm.status" placeholder="请选择" clearable style="width: 200px">
            <el-option label="待处理" value="pending" />
            <el-option label="处理中" value="processing" />
            <el-option label="已完成" value="completed" />
            <el-option label="已拒绝" value="rejected" />
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
      <el-table-column prop="roomNumber" label="房间号" width="100" />
      <el-table-column prop="buildingName" label="楼栋" />
      <el-table-column prop="reporterName" label="报修人" />
      <el-table-column prop="reportTime" label="报修时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.reportTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="problemDesc" label="问题描述" show-overflow-tooltip />
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="handlerName" label="处理人" />
      <el-table-column prop="repairCost" label="维修费用" width="100" />
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleProcess(row)" v-if="row.status === 'pending'">处理</el-button>
          <el-button type="primary" link @click="handleView(row)">详情</el-button>
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
    
    <el-dialog v-model="processVisible" title="处理维修" width="500px">
      <el-form ref="processFormRef" :model="processForm" :rules="processRules" label-width="100px">
        <el-form-item label="处理结果" prop="status">
          <el-radio-group v-model="processForm.status">
            <el-radio label="processing">处理中</el-radio>
            <el-radio label="completed">已完成</el-radio>
            <el-radio label="rejected">已拒绝</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理说明" prop="handleResult">
          <el-input v-model="processForm.handleResult" type="textarea" :rows="3" placeholder="请输入处理说明" />
        </el-form-item>
        <el-form-item label="维修费用" prop="repairCost" v-if="processForm.status === 'completed'">
          <el-input-number v-model="processForm.repairCost" :min="0" :precision="2" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="processVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmitProcess">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="detailVisible" title="维修详情" width="600px">
      <el-descriptions :column="2" border>
        <el-descriptions-item label="设施名称">{{ detailData.facilityName }}</el-descriptions-item>
        <el-descriptions-item label="房间号">{{ detailData.roomNumber }}</el-descriptions-item>
        <el-descriptions-item label="楼栋">{{ detailData.buildingName }}</el-descriptions-item>
        <el-descriptions-item label="报修人">{{ detailData.reporterName }}</el-descriptions-item>
        <el-descriptions-item label="报修时间">{{ formatDate(detailData.reportTime) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(detailData.status)">{{ getStatusText(detailData.status) }}</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="问题描述" :span="2">{{ detailData.problemDesc }}</el-descriptions-item>
        <el-descriptions-item label="处理人">{{ detailData.handlerName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="处理时间">{{ formatDate(detailData.handleTime) }}</el-descriptions-item>
        <el-descriptions-item label="处理结果" :span="2">{{ detailData.handleResult || '-' }}</el-descriptions-item>
        <el-descriptions-item label="维修费用">{{ detailData.repairCost ? detailData.repairCost + '元' : '-' }}</el-descriptions-item>
      </el-descriptions>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getRepairPage, handleRepair, getRepairById } from '@/api/repair'

const loading = ref(false)
const tableData = ref([])
const processVisible = ref(false)
const detailVisible = ref(false)
const processFormRef = ref(null)
const detailData = ref({})
const currentId = ref(null)

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  roomNumber: '',
  status: ''
})

const processForm = reactive({
  status: 'completed',
  handleResult: '',
  repairCost: 0
})

const processRules = {
  status: [{ required: true, message: '请选择处理结果', trigger: 'change' }],
  handleResult: [{ required: true, message: '请输入处理说明', trigger: 'blur' }]
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const getStatusType = (status) => {
  const types = { pending: 'warning', processing: 'primary', completed: 'success', rejected: 'danger' }
  return types[status] || ''
}

const getStatusText = (status) => {
  const texts = { pending: '待处理', processing: '处理中', completed: '已完成', rejected: '已拒绝' }
  return texts[status] || status
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getRepairPage({
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

const handleSearch = () => {
  pagination.current = 1
  loadData()
}

const handleReset = () => {
  searchForm.roomNumber = ''
  searchForm.status = ''
  handleSearch()
}

const handleProcess = (row) => {
  currentId.value = row.id
  processForm.status = 'completed'
  processForm.handleResult = ''
  processForm.repairCost = 0
  processVisible.value = true
}

const handleView = async (row) => {
  try {
    const res = await getRepairById(row.id)
    detailData.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const handleSubmitProcess = async () => {
  const valid = await processFormRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    await handleRepair(
      currentId.value,
      processForm.status,
      processForm.handleResult,
      userInfo.id,
      userInfo.realName,
      processForm.repairCost
    )
    ElMessage.success('处理成功')
    processVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.repair-page {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}
</style>
