<template>
  <div class="student-info-page">
    <div class="page-header">
      <h3 class="page-title">学生管理</h3>
      <el-button type="primary" @click="handleAdd">新增学生</el-button>
    </div>
    
    <div class="search-form">
      <el-form :inline="true" :model="searchForm">
        <el-form-item label="学号">
          <el-input v-model="searchForm.studentNo" placeholder="请输入学号" clearable />
        </el-form-item>
        <el-form-item label="姓名">
          <el-input v-model="searchForm.realName" placeholder="请输入姓名" clearable />
        </el-form-item>
        <el-form-item label="学院">
          <el-select v-model="searchForm.college" placeholder="请选择" clearable style="width: 200px">
            <el-option label="计算机学院" value="计算机学院" />
            <el-option label="外国语学院" value="外国语学院" />
            <el-option label="经管学院" value="经管学院" />
            <el-option label="机械学院" value="机械学院" />
            <el-option label="艺术学院" value="艺术学院" />
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
      <el-table-column prop="realName" label="姓名" width="100" />
      <el-table-column prop="gender" label="性别" width="60">
        <template #default="{ row }">
          <el-tag :type="row.gender === '男' ? 'primary' : 'danger'" size="small">{{ row.gender }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column prop="phone" label="手机号" width="120" />
      <el-table-column prop="college" label="学院" />
      <el-table-column prop="major" label="专业" />
      <el-table-column prop="className" label="班级" />
      <el-table-column prop="grade" label="年级" width="80" />
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
    
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学号" prop="studentNo">
              <el-input v-model="form.studentNo" placeholder="请输入学号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="姓名" prop="realName">
              <el-input v-model="form.realName" placeholder="请输入姓名" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="性别" prop="gender">
              <el-select v-model="form.gender" placeholder="请选择" style="width: 100%">
                <el-option label="男" value="男" />
                <el-option label="女" value="女" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="手机号" prop="phone">
              <el-input v-model="form.phone" placeholder="请输入手机号" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="form.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学院" prop="college">
              <el-select v-model="form.college" placeholder="请选择学院" style="width: 100%">
                <el-option label="计算机学院" value="计算机学院" />
                <el-option label="外国语学院" value="外国语学院" />
                <el-option label="经管学院" value="经管学院" />
                <el-option label="机械学院" value="机械学院" />
                <el-option label="艺术学院" value="艺术学院" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="专业" prop="major">
              <el-input v-model="form.major" placeholder="请输入专业" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="班级" prop="className">
              <el-input v-model="form.className" placeholder="如：软工2101班" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="年级" prop="grade">
              <el-select v-model="form.grade" placeholder="请选择年级" style="width: 100%">
                <el-option label="2021级" value="2021级" />
                <el-option label="2022级" value="2022级" />
                <el-option label="2023级" value="2023级" />
                <el-option label="2024级" value="2024级" />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
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
import { getStudentPage, addStudent, updateStudent, deleteStudent } from '@/api/student'

const loading = ref(false)
const tableData = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('')
const isEdit = ref(false)
const formRef = ref(null)

const pagination = reactive({
  current: 1,
  size: 10,
  total: 0
})

const searchForm = reactive({
  studentNo: '',
  realName: '',
  college: ''
})

const form = reactive({
  id: null,
  studentNo: '',
  realName: '',
  gender: '男',
  phone: '',
  email: '',
  college: '',
  major: '',
  className: '',
  grade: ''
})

const rules = {
  studentNo: [
    { required: true, message: '请输入学号', trigger: 'blur' }
  ],
  realName: [
    { required: true, message: '请输入姓名', trigger: 'blur' }
  ],
  gender: [
    { required: true, message: '请选择性别', trigger: 'change' }
  ],
  phone: [
    { required: true, message: '请输入手机号', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }
  ],
  college: [
    { required: true, message: '请选择学院', trigger: 'change' }
  ],
  major: [
    { required: true, message: '请输入专业', trigger: 'blur' }
  ],
  className: [
    { required: true, message: '请输入班级', trigger: 'blur' }
  ],
  grade: [
    { required: true, message: '请选择年级', trigger: 'change' }
  ]
}

const loadData = async () => {
  loading.value = true
  try {
    const res = await getStudentPage({
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
  searchForm.studentNo = ''
  searchForm.realName = ''
  searchForm.college = ''
  handleSearch()
}

const resetForm = () => {
  form.id = null
  form.studentNo = ''
  form.realName = ''
  form.gender = '男'
  form.phone = ''
  form.email = ''
  form.college = ''
  form.major = ''
  form.className = ''
  form.grade = ''
}

const handleAdd = () => {
  resetForm()
  isEdit.value = false
  dialogTitle.value = '新增学生'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  resetForm()
  isEdit.value = true
  dialogTitle.value = '编辑学生'
  form.id = row.id
  form.studentNo = row.studentNo
  form.realName = row.realName
  form.gender = row.gender
  form.phone = row.phone
  form.email = row.email
  form.college = row.college
  form.major = row.major
  form.className = row.className
  form.grade = row.grade
  dialogVisible.value = true
}

const handleSubmit = async () => {
  const valid = await formRef.value.validate().catch(() => false)
  if (!valid) return

  try {
    if (isEdit.value) {
      await updateStudent(form)
      ElMessage.success('修改成功')
    } else {
      await addStudent(form)
      ElMessage.success('新增成功')
    }
    dialogVisible.value = false
    loadData()
  } catch (error) {
    console.error(error)
  }
}

const handleDelete = (row) => {
  ElMessageBox.confirm(`确定删除学生 ${row.realName} 吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteStudent(row.id)
      ElMessage.success('删除成功')
      loadData()
    } catch (error) {
      console.error(error)
    }
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.student-info-page {
  background: #fff;
  padding: 20px;
  border-radius: 8px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-title {
  margin: 0;
  font-size: 18px;
  color: #333;
}

.search-form {
  margin-bottom: 20px;
}
</style>
