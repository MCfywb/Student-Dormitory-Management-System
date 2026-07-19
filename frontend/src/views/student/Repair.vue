<template>
  <div class="student-repair">
    <div class="page-header">
      <h3 class="page-title">报修申请</h3>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增报修
      </el-button>
    </div>
    
    <el-table :data="repairList" border stripe v-loading="loading">
      <el-table-column prop="facilityName" label="设施名称" />
      <el-table-column prop="problemDesc" label="问题描述" show-overflow-tooltip />
      <el-table-column prop="reportTime" label="报修时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.reportTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="status" label="状态">
        <template #default="{ row }">
          <el-tag :type="getStatusType(row.status)">{{ getStatusText(row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="handleResult" label="处理结果" show-overflow-tooltip />
    </el-table>
    
    <el-dialog v-model="dialogVisible" title="新增报修" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="设施" prop="facilityId">
          <el-select v-model="form.facilityId" placeholder="请选择设施" style="width: 100%" @change="handleFacilityChange">
            <el-option v-for="item in facilityList" :key="item.id" :label="item.facilityName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="问题描述" prop="problemDesc">
          <el-input v-model="form.problemDesc" type="textarea" :rows="4" placeholder="请描述问题情况" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyRepairs, saveRepair } from '@/api/repair'
import { getFacilityByRoom } from '@/api/facility'

const loading = ref(false)
const repairList = ref([])
const facilityList = ref([])
const dialogVisible = ref(false)
const formRef = ref(null)

const userInfo = computed(() => {
  const info = localStorage.getItem('userInfo')
  return info ? JSON.parse(info) : null
})

const studentInfo = computed(() => {
  const info = localStorage.getItem('studentInfo')
  return info ? JSON.parse(info) : null
})

const form = reactive({
  facilityId: null,
  facilityName: '',
  roomId: studentInfo.value?.roomId,
  roomNumber: studentInfo.value?.roomNumber,
  buildingName: studentInfo.value?.buildingName,
  reporterId: userInfo.value?.id,
  reporterName: userInfo.value?.realName,
  problemDesc: ''
})

const rules = {
  facilityId: [{ required: true, message: '请选择设施', trigger: 'change' }],
  problemDesc: [{ required: true, message: '请描述问题情况', trigger: 'blur' }]
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
    const res = await getMyRepairs(userInfo.value?.id)
    repairList.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const loadFacilities = async () => {
  if (!studentInfo.value?.roomId) return
  try {
    const res = await getFacilityByRoom(studentInfo.value.roomId)
    facilityList.value = res.data
  } catch (error) {
    console.error(error)
  }
}

const handleAdd = () => {
  form.facilityId = null
  form.facilityName = ''
  form.problemDesc = ''
  dialogVisible.value = true
}

const handleFacilityChange = (val) => {
  const facility = facilityList.value.find(f => f.id === val)
  if (facility) {
    form.facilityName = facility.facilityName
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    await saveRepair(form)
    ElMessage.success('报修提交成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
  loadFacilities()
})
</script>

<style scoped>
.student-repair {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}
</style>
