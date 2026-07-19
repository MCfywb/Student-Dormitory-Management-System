<template>
  <div class="student-fee">
    <div class="page-header">
      <h3 class="page-title">费用查询</h3>
    </div>
    
    <el-tabs v-model="activeTab">
      <el-tab-pane label="待缴费" name="unpaid">
        <el-table :data="unpaidFees" border stripe v-loading="loading">
          <el-table-column prop="feeTypeName" label="费用类型" />
          <el-table-column prop="amount" label="金额(元)" width="120" />
          <el-table-column prop="academicYear" label="学年" width="120" />
          <el-table-column prop="semester" label="学期" width="100" />
          <el-table-column prop="dueDate" label="应缴日期" width="120" />
          <el-table-column prop="payStatus" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getPayStatusType(row.payStatus)">{{ getPayStatusText(row.payStatus) }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="120">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="handlePay(row)">缴费</el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-tab-pane>
      
      <el-tab-pane label="已缴费" name="paid">
        <el-table :data="paidFees" border stripe v-loading="loading">
          <el-table-column prop="feeTypeName" label="费用类型" />
          <el-table-column prop="amount" label="金额(元)" width="120" />
          <el-table-column prop="academicYear" label="学年" width="120" />
          <el-table-column prop="semester" label="学期" width="100" />
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
        </el-table>
      </el-tab-pane>
    </el-tabs>
    
    <el-dialog v-model="payVisible" title="缴费" width="400px">
      <el-form label-width="100px">
        <el-form-item label="缴费金额">
          <span style="font-size: 24px; color: #f56c6c; font-weight: bold;">¥{{ payForm.amount }}</span>
        </el-form-item>
        <el-form-item label="支付方式">
          <el-radio-group v-model="payForm.payMethod">
            <el-radio label="wechat">微信支付</el-radio>
            <el-radio label="alipay">支付宝</el-radio>
            <el-radio label="card">银行卡</el-radio>
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
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { getMyFees, payFee } from '@/api/fee'

const loading = ref(false)
const activeTab = ref('unpaid')
const feeList = ref([])
const payVisible = ref(false)
const payForm = reactive({ id: null, amount: 0, payMethod: 'wechat' })

const studentInfo = computed(() => {
  const info = localStorage.getItem('studentInfo')
  return info ? JSON.parse(info) : null
})

const unpaidFees = computed(() => feeList.value.filter(f => f.payStatus !== 'paid'))
const paidFees = computed(() => feeList.value.filter(f => f.payStatus === 'paid'))

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
  if (!studentInfo.value?.id) return
  loading.value = true
  try {
    const res = await getMyFees(studentInfo.value.id)
    feeList.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.student-fee {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}
</style>
