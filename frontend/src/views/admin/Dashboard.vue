<template>
  <div class="dashboard">
    <div class="stat-cards">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-card" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon :size="40"><OfficeBuilding /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ overview.totalRooms || 0 }}</div>
                <div class="stat-label">宿舍房间</div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon :size="40"><User /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ overview.totalStudents || 0 }}</div>
                <div class="stat-label">入住人数</div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon :size="40"><Money /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ overview.paidFeeAmount || 0 }}</div>
                <div class="stat-label">已收费用(元)</div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card" style="background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);">
            <div class="stat-content">
              <div class="stat-icon">
                <el-icon :size="40"><Tools /></el-icon>
              </div>
              <div class="stat-info">
                <div class="stat-value">{{ overview.pendingRepairs || 0 }}</div>
                <div class="stat-label">待处理报修</div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
    
    <el-row :gutter="20">
      <el-col :span="16">
        <el-row :gutter="20">
          <el-col :span="12">
            <div class="chart-container">
              <div class="chart-title">各楼栋入住率</div>
              <div ref="buildingChartRef" style="height: 300px;"></div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="chart-container">
              <div class="chart-title">学生学院分布</div>
              <div ref="collegeChartRef" style="height: 300px;"></div>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="12">
            <div class="chart-container">
              <div class="chart-title">卫生检查统计</div>
              <div ref="hygieneChartRef" style="height: 300px;"></div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="chart-container">
              <div class="chart-title">设施状态统计</div>
              <div ref="facilityChartRef" style="height: 300px;"></div>
            </div>
          </el-col>
        </el-row>
      </el-col>
      
      <el-col :span="8">
        <div class="chart-container">
          <div class="chart-title">房间状态分布</div>
          <div ref="roomChartRef" style="height: 200px;"></div>
        </div>
        
        <div class="chart-container" style="margin-top: 20px;">
          <div class="chart-title">费用收缴情况</div>
          <div ref="feeChartRef" style="height: 200px;"></div>
        </div>
        
        <div class="chart-container" style="margin-top: 20px;">
          <div class="chart-title">最近卫生检查</div>
          <el-table :data="recentHygiene" size="small" max-height="200">
            <el-table-column prop="roomNumber" label="房间" width="80" />
            <el-table-column prop="buildingName" label="楼栋" width="80" />
            <el-table-column prop="score" label="分数" width="60">
              <template #default="{ row }">
                <el-tag :type="getScoreType(row.score)" size="small">{{ row.score }}</el-tag>
              </template>
            </el-table-column>
            <el-table-column prop="level" label="等级">
              <template #default="{ row }">
                <el-tag :type="getLevelType(row.level)" size="small">{{ getLevelText(row.level) }}</el-tag>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import * as echarts from 'echarts'
import { 
  getOverview, 
  getBuildingStats, 
  getStudentCollegeStats,
  getHygieneStats,
  getFacilityStats,
  getRoomStats,
  getFeeStats,
  getRecentHygiene
} from '@/api/dashboard'

const buildingChartRef = ref(null)
const collegeChartRef = ref(null)
const hygieneChartRef = ref(null)
const facilityChartRef = ref(null)
const roomChartRef = ref(null)
const feeChartRef = ref(null)

let buildingChart = null
let collegeChart = null
let hygieneChart = null
let facilityChart = null
let roomChart = null
let feeChart = null

const overview = ref({})
const recentHygiene = ref([])

const getScoreType = (score) => {
  if (score >= 90) return 'success'
  if (score >= 80) return 'primary'
  if (score >= 60) return 'warning'
  return 'danger'
}

const getLevelType = (level) => {
  const types = {
    excellent: 'success',
    good: 'primary',
    qualified: 'warning',
    unqualified: 'danger'
  }
  return types[level] || 'info'
}

const getLevelText = (level) => {
  const texts = {
    excellent: '优秀',
    good: '良好',
    qualified: '合格',
    unqualified: '不合格'
  }
  return texts[level] || level
}

const initBuildingChart = (data) => {
  if (!buildingChartRef.value) return
  buildingChart = echarts.init(buildingChartRef.value)
  const option = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: data.map(item => item.buildingName) },
    yAxis: { type: 'value', name: '入住率(%)' },
    series: [{
      name: '入住率',
      type: 'bar',
      data: data.map(item => item.usageRate),
      itemStyle: {
        color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
          { offset: 0, color: '#667eea' },
          { offset: 1, color: '#764ba2' }
        ])
      },
      label: { show: true, position: 'top', formatter: '{c}%' }
    }]
  }
  buildingChart.setOption(option)
}

const initCollegeChart = (data) => {
  if (!collegeChartRef.value) return
  collegeChart = echarts.init(collegeChartRef.value)
  const option = {
    tooltip: { trigger: 'item', formatter: '{b}: {c} ({d}%)' },
    legend: { orient: 'vertical', left: 'left', top: 'center' },
    series: [{
      name: '学院分布',
      type: 'pie',
      radius: ['40%', '70%'],
      center: ['60%', '50%'],
      avoidLabelOverlap: false,
      itemStyle: { borderRadius: 10, borderColor: '#fff', borderWidth: 2 },
      label: { show: false },
      emphasis: { label: { show: true, fontSize: 14, fontWeight: 'bold' } },
      data: data
    }]
  }
  collegeChart.setOption(option)
}

const initHygieneChart = (data) => {
  if (!hygieneChartRef.value) return
  hygieneChart = echarts.init(hygieneChartRef.value)
  const option = {
    tooltip: { trigger: 'item' },
    legend: { top: '5%', left: 'center' },
    series: [{
      name: '卫生检查',
      type: 'pie',
      radius: ['0%', '60%'],
      center: ['50%', '55%'],
      data: [
        { value: data.excellent, name: '优秀', itemStyle: { color: '#67C23A' } },
        { value: data.good, name: '良好', itemStyle: { color: '#409EFF' } },
        { value: data.qualified, name: '合格', itemStyle: { color: '#E6A23C' } },
        { value: data.unqualified, name: '不合格', itemStyle: { color: '#F56C6C' } }
      ]
    }]
  }
  hygieneChart.setOption(option)
}

const initFacilityChart = (data) => {
  if (!facilityChartRef.value) return
  facilityChart = echarts.init(facilityChartRef.value)
  const option = {
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    grid: { left: '3%', right: '4%', bottom: '3%', containLabel: true },
    xAxis: { type: 'category', data: ['正常', '损坏', '维修中', '已报废'] },
    yAxis: { type: 'value' },
    series: [{
      name: '设施数量',
      type: 'bar',
      data: [
        { value: data.normal, itemStyle: { color: '#67C23A' } },
        { value: data.damaged, itemStyle: { color: '#F56C6C' } },
        { value: data.repairing, itemStyle: { color: '#E6A23C' } },
        { value: data.scrapped, itemStyle: { color: '#909399' } }
      ],
      barWidth: '50%'
    }]
  }
  facilityChart.setOption(option)
}

const initRoomChart = (data) => {
  if (!roomChartRef.value) return
  roomChart = echarts.init(roomChartRef.value)
  const option = {
    tooltip: { trigger: 'item' },
    series: [{
      name: '房间状态',
      type: 'pie',
      radius: ['0%', '65%'],
      data: [
        { value: data.fullRooms, name: '已满', itemStyle: { color: '#F56C6C' } },
        { value: data.availableRooms, name: '可入住', itemStyle: { color: '#67C23A' } },
        { value: data.maintenanceRooms, name: '维修中', itemStyle: { color: '#E6A23C' } }
      ],
      label: { formatter: '{b}: {c}' }
    }]
  }
  roomChart.setOption(option)
}

const initFeeChart = (data) => {
  if (!feeChartRef.value) return
  feeChart = echarts.init(feeChartRef.value)
  const option = {
    tooltip: { trigger: 'item' },
    series: [{
      name: '费用情况',
      type: 'pie',
      radius: ['0%', '65%'],
      data: [
        { value: data.paidCount, name: '已缴费', itemStyle: { color: '#67C23A' } },
        { value: data.unpaidCount, name: '待缴费', itemStyle: { color: '#E6A23C' } },
        { value: data.overdueCount, name: '已逾期', itemStyle: { color: '#F56C6C' } }
      ],
      label: { formatter: '{b}: {c}' }
    }]
  }
  feeChart.setOption(option)
}

const loadData = async () => {
  try {
    const [overviewRes, buildingRes, collegeRes, hygieneRes, facilityRes, roomRes, feeRes, hygieneRecentRes] = await Promise.all([
      getOverview(),
      getBuildingStats(),
      getStudentCollegeStats(),
      getHygieneStats(),
      getFacilityStats(),
      getRoomStats(),
      getFeeStats(),
      getRecentHygiene()
    ])
    
    overview.value = overviewRes.data
    recentHygiene.value = hygieneRecentRes.data
    
    initBuildingChart(buildingRes.data)
    initCollegeChart(collegeRes.data)
    initHygieneChart(hygieneRes.data)
    initFacilityChart(facilityRes.data)
    initRoomChart(roomRes.data)
    initFeeChart(feeRes.data)
  } catch (error) {
    console.error(error)
  }
}

const handleResize = () => {
  buildingChart?.resize()
  collegeChart?.resize()
  hygieneChart?.resize()
  facilityChart?.resize()
  roomChart?.resize()
  feeChart?.resize()
}

onMounted(() => {
  loadData()
  window.addEventListener('resize', handleResize)
})

onUnmounted(() => {
  window.removeEventListener('resize', handleResize)
  buildingChart?.dispose()
  collegeChart?.dispose()
  hygieneChart?.dispose()
  facilityChart?.dispose()
  roomChart?.dispose()
  feeChart?.dispose()
})
</script>

<style scoped>
.dashboard {
  padding: 0;
}

.stat-cards {
  margin-bottom: 20px;
}

.stat-card {
  border-radius: 12px;
  padding: 20px;
  color: #fff;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
}

.stat-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}

.stat-content {
  display: flex;
  align-items: center;
}

.stat-icon {
  opacity: 0.8;
  margin-right: 15px;
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 28px;
  font-weight: bold;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-top: 5px;
}

.chart-container {
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.05);
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 1px solid #ebeef5;
}
</style>
