<template>
  <div class="notice-page">
    <div class="page-header">
      <h3 class="page-title">公告管理</h3>
      <el-button type="primary" @click="handleAdd">
        <el-icon><Plus /></el-icon>发布公告
      </el-button>
    </div>
    
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="标题">
          <el-input v-model="searchForm.title" placeholder="请输入标题" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="searchForm.noticeType" placeholder="请选择" clearable style="width: 200px">
            <el-option label="通知" value="notice" />
            <el-option label="公告" value="announcement" />
            <el-option label="警告" value="warning" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSearch">查询</el-button>
          <el-button @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>
    
    <el-table :data="tableData" border stripe v-loading="loading">
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
      <el-table-column prop="viewCount" label="浏览次数" width="100" />
      <el-table-column prop="status" label="状态" width="100">
        <template #default="{ row }">
          <el-tag :type="row.status === 1 ? 'success' : 'info'">
            {{ row.status === 1 ? '已发布' : '草稿' }}
          </el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="180" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link @click="handleEdit(row)">编辑</el-button>
          <el-button type="primary" link @click="handleView(row)">查看</el-button>
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
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="700px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="类型" prop="noticeType">
          <el-select v-model="form.noticeType" placeholder="请选择" style="width: 100%">
            <el-option label="通知" value="notice" />
            <el-option label="公告" value="announcement" />
            <el-option label="警告" value="warning" />
          </el-select>
        </el-form-item>
        <el-form-item label="内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="10" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio :label="1">发布</el-radio>
            <el-radio :label="0">草稿</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
    
    <el-dialog v-model="detailVisible" title="公告详情" width="700px">
      <div class="notice-detail">
        <h2>{{ detailData.title }}</h2>
        <div class="meta">
          <span>发布人：{{ detailData.publisherName }}</span>
          <span style="margin-left: 20px;">发布时间：{{ formatDate(detailData.publishTime) }}</span>
          <span style="margin-left: 20px;">浏览：{{ detailData.viewCount }}次</span>
        </div>
        <el-divider />
        <div class="content" v-html="detailData.content?.replace(/\n/g, '<br>')"></div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getNoticePage, saveNotice, updateNotice, deleteNotice, getNoticeById } from '@/api/notice'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const detailVisible = ref(false)
const dialogTitle = ref('')
const formRef = ref(null)
const detailData = ref({})

const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}')

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  title: '',
  noticeType: ''
})

const form = reactive({
  id: null,
  title: '',
  content: '',
  noticeType: 'notice',
  publisherId: userInfo.id,
  publisherName: userInfo.realName,
  status: 1
})

const rules = {
  title: [{ required: true, message: '请输入标题', trigger: 'blur' }],
  content: [{ required: true, message: '请输入内容', trigger: 'blur' }],
  noticeType: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

const formatDate = (date) => {
  if (!date) return '-'
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
    const res = await getNoticePage({
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
  searchForm.title = ''
  searchForm.noticeType = ''
  handleSearch()
}

const handleAdd = () => {
  dialogTitle.value = '发布公告'
  resetForm()
  dialogVisible.value = true
}

const handleEdit = async (row) => {
  dialogTitle.value = '编辑公告'
  try {
    const res = await getNoticeById(row.id)
    Object.assign(form, res.data)
    dialogVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const handleView = async (row) => {
  try {
    const res = await getNoticeById(row.id)
    detailData.value = res.data
    detailVisible.value = true
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该公告吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteNotice(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return
  
  try {
    if (form.id) {
      await updateNotice(form)
      ElMessage.success('修改成功')
    } else {
      await saveNotice(form)
      ElMessage.success('发布成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const resetForm = () => {
  form.id = null
  form.title = ''
  form.content = ''
  form.noticeType = 'notice'
  form.publisherId = userInfo.id
  form.publisherName = userInfo.realName
  form.status = 1
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.notice-page {
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
