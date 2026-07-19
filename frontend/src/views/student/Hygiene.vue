<template>
  <div class="student-hygiene">
    <div class="page-header">
      <h3 class="page-title">卫生检查记录</h3>
    </div>
    
    <el-table :data="hygieneList" border stripe v-loading="loading">
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
      <el-table-column prop="remark" label="检查备注" show-overflow-tooltip />
    </el-table>
    
    <el-empty v-if="!loading && hygieneList.length === 0" description="暂无卫生检查记录" />
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { getHygieneByRoom } from '@/api/hygiene'

const loading = ref(false)
const hygieneList = ref([])

const studentInfo = computed(() => {
  const info = localStorage.getItem('studentInfo')
  return info ? JSON.parse(info) : null
})

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
  if (!studentInfo.value?.roomId) return
  
  loading.value = true
  try {
    const res = await getHygieneByRoom(studentInfo.value.roomId)
    hygieneList.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.student-hygiene {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}
</style>
