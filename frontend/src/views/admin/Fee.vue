<template>
  <div class="fee-page">
    <div class="page-header">
      <h3 class="page-title">费用管理</h3>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>新增费用
      </el-button>
    </div>
    
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="学号">
          <el-input v-model="searchForm.studentNo" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item label="缴费状态">
          <el-select v-model="searchForm.payStatus" placeholder="请选择" clearable style="width: 200px">
            <el-option label="未支付" value="unpaid" />
            <el-option label="已支付" value="paid" />
            <el-option label="已逾期" value="overdue" />
          </el-select>
        </el-form-item>
        <el-form-item label="学年">
          <el-select v-model="searchForm.academicYear" placeholder="请选择" clearable style="width: 200px">
            <el-option label="2023-2024" value="2023-2024" />
            <el-option label="2024-2025" value="2024-2025" />
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
      <el-table-column prop="studentName" label="学生姓名" width="100" />
      <el-table-column prop="feeTypeName" label="费用类型" />
      <el-table-column prop="amount" label="金额(元)" width="100" />
      <el-table-column prop="academicYear" label="学年" width="120" />
      <el-table-column prop="semester" label="学期" width="100" />
      <el-table-column prop="dueDate" label="应缴日期" width="120" />
      <el-table-column prop="payStatus" label="缴费状态">
        <template #default="{ row }">
          <el-tag :type="getPayStatusType(row.payStatus)">{{ getPayStatusText(row.payStatus) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="payTime" label="支付时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.payTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="payMethod" label="支付方式">
        <template #default="{ row }">
          {{ getPayMethodText(row.payMethod) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" width="150" fixed="right">
        <template #default="{ row }">
          <el-button type="success" link @click="handlePay(row)" v-if="row.payStatus !== 'paid'">缴费</el-button>
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
    
    <el-dialog v-model="dialogVisible" title="新增费用" width="500px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="学生" prop="studentId">
          <el-select v-model="form.studentId" placeholder="请选择学生" style="width: 100%" filterable @change="handleStudentChange">
            <el-option v-for="item in studentList" :key="item.id" :label="`${item.studentNo} - ${item.realName}`" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="费用类型" prop="feeTypeId">
          <el-select v-model="form.feeTypeId" placeholder="请选择" style="width: 100%" @change="handleFeeTypeChange">
            <el-option v-for="item in feeTypeList" :key="item.id" :label="item.typeName" :value="item.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="金额" prop="amount">
          <el-input-number v-model="form.amount" :min="0" :precision="2" />
        </el-form-item>
        <el-form-item label="学年" prop="academicYear">
          <el-select v-model="form.academicYear" placeholder="请选择" style="width: 100%">
            <el-option label="2023-2024" value="2023-2024" />
            <el-option label="2024-2025" value="2024-2025" />
          </el-select>
        </el-form-item>
        <el-form-item label="学期" prop="semester">
          <el-select v-model="form.semester" placeholder="请选择" style="width: 100%">
            <el-option label="第一学期" value="第一学期" />
            <el-option label="第二学期" value="第二学期" />
          </el-select>
        </el-form-item>
        <el-form-item label="应缴日期" prop="dueDate">
          <el-date-picker v-model="form.dueDate" type="date" placeholder="选择日期" style="width: 100%" value-format="YYYY-MM-DD" />
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
    
    <el-dialog v-model="payVisible" title="缴费" width="400px">
      <el-form label-width="100px">
        <el-form-item label="缴费金额">
          <span style="font-size: 20px; color: #f56c6c; font-weight: bold;">{{ payForm.amount }} 元</span>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-radio-group v-model="payForm.payMethod">
            <el-radio label="wechat">微信支付</el-radio>
            <el-radio label="alipay">支付宝</el-radio>
            <el-radio label="card">银行卡</el-radio>
            <el-radio label="cash">现金</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="payVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmPay">确认支付</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getFeePage, saveFee, deleteFee, payFee } from '@/api/fee'
import { getFeeTypeList } from '@/api/fee'
import { getStudentPage } from '@/api/student'

const loading = ref(false)
const tableData = ref([])
const studentList = ref([])
const feeTypeList = ref([])
const dialogVisible = ref(false)
const payVisible = ref(false)
const formRef = ref(null)
const payForm = reactive({ id: null, amount: 0, payMethod: 'wechat' })

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  studentNo: '',
  payStatus: '',
  academicYear: ''
})

const form = reactive({
  studentId: null,
  studentNo: '',
  studentName: '',
  feeTypeId: null,
  feeTypeName: '',
  amount: 0,
  academicYear: '',
  semester: '',
  dueDate: '',
  remark: ''
})

const rules = {
  studentId: [{ required: true, message: '请选择学生', trigger: 'change' }],
  feeTypeId: [{ required: true, message: '请选择费用类型', trigger: 'change' }],
  amount: [{ required: true, message: '请输入金额', trigger: 'blur' }],
  academicYear: [{ required: true, message: '请选择学年', trigger: 'change' }],
  semester: [{ required: true, message: '请选择学期', trigger: 'change' }]
}

const formatDate = (date) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN')
}

const getPayStatusType = (status) => {
  const types = { unpaid: 'warning', paid: 'success', overdue: 'danger' }
  return types[status] || ''
}

const getPayStatusText = (status) => {
  const texts = { unpaid: '未支付', paid: '已支付', overdue: '已逾期' }
  return texts[status] || status
}

const getPayMethodText = (method) => {
  const texts = { wechat: '微信', alipay: '支付宝', card: '银行卡', cash: '现金' }
  return texts[method] || '-'
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getFeePage({
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

const loadStudents = async () => {
  try {
    const res = await getStudentPage({ current: 1, size: 1000 })
    studentList.value = res.data.records
  } catch (error) {
    console.error(error)
  }
}

const loadFeeTypes = async () => {
  try {
    const res = await getFeeTypeList()
    feeTypeList.value = res.data
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
  searchForm.payStatus = ''
  searchForm.academicYear = ''
  handleSearch()
}

const handleAdd = () => {
  resetForm()
  dialogVisible.value = true
}

const handlePay = (row) => {
  payForm.id = row.id
  payForm.amount = row.amount
  payForm.payMethod = 'wechat'
  payVisible.value = true
}

const handleConfirmPay = async () => {
  try {
    await payFee(payForm.id, payForm.payMethod)
    ElMessage.success('支付成功')
    payVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该费用记录吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteFee(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleStudentChange = (val) => {
  const student = studentList.value.find(s => s.id === val)
  if (student) {
    form.studentNo = student.studentNo
    form.studentName = student.realName
  }
}

const handleFeeTypeChange = (val) => {
  const feeType = feeTypeList.value.find(f => f.id === val)
  if (feeType) {
    form.feeTypeName = feeType.typeName
    form.amount = feeType.price
  }
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    await saveFee(form)
    ElMessage.success('新增成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const resetForm = () => {
  form.studentId = null
  form.studentNo = ''
  form.studentName = ''
  form.feeTypeId = null
  form.feeTypeName = ''
  form.amount = 0
  form.academicYear = ''
  form.semester = ''
  form.dueDate = ''
  form.remark = ''
}

onMounted(() => {
  loadData()
  loadStudents()
  loadFeeTypes()
})
</script>

<style scoped>
.fee-page {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}
</style>
