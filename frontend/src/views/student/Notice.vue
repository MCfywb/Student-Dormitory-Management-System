<template>
  <div class="student-notice">
    <div class="page-header">
      <h3 class="page-title">公告通知</h3>
    </div>
    
    <el-table :data="noticeList" border stripe v-loading="loading" @row-click="handleView">
      <el-table-column prop="title" label="标题" />
      <el-table-column prop="noticeType" label="类型" width="100">
        <template #default="{ row }">
          <el-tag :type="getTypeColor(row.noticeType)">{{ getTypeText(row.noticeType) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="publisherName" label="发布人" width="100" />
      <el-table-column prop="publishTime" label="发布时间" width="180">
        <template #default="{ row }">
          {{ formatDate(row.publishTime) }}
        </template>
      </el-table-column>
      <el-table-column prop="viewCount" label="浏览" width="80" />
    </el-table>
    
    <el-dialog v-model="detailVisible" title="公告详情" width="700px">
      <div class="notice-detail">
        <h2>{{ currentNotice.title }}</h2>
        <div class="meta">
          <span>发布人：{{ currentNotice.publisherName }}</span>
          <span style="margin-left: 20px;">发布时间：{{ formatDate(currentNotice.publishTime) }}</span>
          <span style="margin-left: 20px;">浏览：{{ currentNotice.viewCount }}次</span>
        </div>
        <el-divider />
        <div class="content" v-html="currentNotice.content?.replace(/\n/g, '<br>')"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { getNoticeList, getNoticeById } from '@/api/notice'

const loading = ref(false)
const noticeList = ref([])
const detailVisible = ref(false)
const currentNotice = ref({})

const formatDate = (date) => {
  if (!date) return ''
  return new Date(date).toLocaleString('zh-CN')
}

const getTypeColor = (type) => {
  const colors = { notice: 'primary', announcement: 'success', warning: 'danger' }
  return colors[type] || ''
}

const getTypeText = (type) => {
  const texts = { notice: '通知', announcement: '公告', warning: '警告' }
  return texts[type] || type
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getNoticeList(50)
    noticeList.value = res.data
  } catch (error) {
    console.error(error)
  } finally {
    loading.value = false
  }
}

const handleView = async (row) => {
  try {
    const res = await getNoticeById(row.id)
    currentNotice.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.student-notice {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}

.notice-detail h2 {
  margin: 0 0 15px 0;
  color: #303133;
}

.notice-detail .meta {
  color: #909399;
  font-size: 14px;
}

.notice-detail .content {
  line-height: 1.8;
  color: #606266;
}
</style>
