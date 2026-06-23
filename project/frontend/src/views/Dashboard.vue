<template>
  <section>
    <PageHeader title="首页统计驾驶舱" description="集中展示今日访客、在校状态、审批待办、通行趋势和部门访客分布。">
      <el-button type="primary" :icon="Refresh" :loading="loading" @click="loadData">刷新数据</el-button>
    </PageHeader>

    <div class="dashboard-grid">
      <div v-for="item in metrics" :key="item.label" class="metric-card">
        <div class="metric-icon"><el-icon><component :is="item.icon" /></el-icon></div>
        <div>
          <div class="metric-label">{{ item.label }}</div>
          <div class="metric-value">{{ item.value }}</div>
          <div class="metric-hint">{{ item.hint }}</div>
        </div>
      </div>
    </div>

    <div class="dashboard-charts">
      <div class="chart-card">
        <div class="card-title"><span>近七天访客趋势</span><el-tag type="primary" effect="plain">动态统计</el-tag></div>
        <div ref="trendRef" class="chart-box"></div>
      </div>
      <div class="chart-card">
        <div class="card-title"><span>部门访客排行</span><el-tag type="success" effect="plain">本月</el-tag></div>
        <div ref="deptRef" class="chart-box"></div>
      </div>
    </div>

    <div class="two-column" style="margin-top: 14px">
      <div class="chart-card">
        <div class="card-title"><span>校门通行统计</span><el-tag type="info" effect="plain">入校/离校</el-tag></div>
        <div ref="gateRef" class="chart-box"></div>
      </div>
      <div class="detail-card">
        <div class="card-title"><span>快捷入口</span><el-tag type="warning" effect="plain">高频业务</el-tag></div>
        <div class="quick-grid">
          <div v-for="item in quickEntries" :key="item.path" class="quick-entry" @click="router.push(item.path)">
            <el-icon><component :is="item.icon" /></el-icon>
            <span>{{ item.title }}</span>
          </div>
        </div>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, nextTick, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import * as echarts from 'echarts'
import { Aim, Bell, Calendar, Refresh, TrendCharts, UserFilled, WarningFilled } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import { loadDashboardData } from '@/api/statistics'
import { mockAccessRecords, mockApplies } from '@/api/mock'

const router = useRouter()
const loading = ref(false)
const dashboard = ref(null)
const applies = ref(mockApplies)
const accessRecords = ref(mockAccessRecords)
const trendRef = ref()
const deptRef = ref()
const gateRef = ref()

const overview = computed(() => dashboard.value?.overview || {})
const metrics = computed(() => [
  { label: '今日访客数', value: overview.value.todayVisitors ?? applies.value.length, hint: '今日预约、入校与离校访客', icon: UserFilled },
  { label: '本周访客数', value: overview.value.thisWeekVisitors ?? 0, hint: '按本周计划访问时间统计', icon: TrendCharts },
  { label: '当前在校访客', value: overview.value.currentVisitors ?? applies.value.filter((i) => i.accessStatus === 'ENTERED').length, hint: '门岗实时登记在校人数', icon: Aim },
  { label: '超时未离校', value: overview.value.overtimeVisitors ?? applies.value.filter((i) => i.accessStatus === 'OVERTIME').length, hint: '需要安保跟进处理', icon: WarningFilled },
  { label: '审批待处理', value: overview.value.pendingApprovals ?? applies.value.filter((i) => ['PENDING_HOST', 'HOST_CONFIRMED', 'PENDING_DEPT'].includes(i.applyStatus)).length, hint: '被访人确认与部门审批', icon: Bell }
])
const quickEntries = [
  { title: '提交预约', path: '/visit/apply', icon: Calendar },
  { title: '部门审批', path: '/approval/department', icon: Bell },
  { title: '门岗核验', path: '/gate/verify', icon: Aim },
  { title: '统计报表', path: '/reports', icon: TrendCharts }
]

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    const data = await loadDashboardData()
    dashboard.value = data.dashboard
    if (data.applies.length) applies.value = data.applies
    if (data.accessRecords.length) accessRecords.value = data.accessRecords
  } catch (error) {
    dashboard.value = null
    applies.value = mockApplies
    accessRecords.value = mockAccessRecords
  } finally {
    loading.value = false
  }
  await nextTick()
  renderCharts()
}

function renderCharts() {
  const colors = ['#1d4ed8', '#06b6d4', '#16a34a', '#f59e0b', '#dc2626', '#7c3aed', '#0f766e']
  const trendData = dashboard.value?.trend || []
  renderChart(trendRef.value, {
    color: colors,
    tooltip: { trigger: 'axis' },
    grid: { left: 34, right: 16, top: 28, bottom: 26 },
    xAxis: { type: 'category', data: trendData.map((i) => i.date) },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ name: '访客数', type: 'line', smooth: true, areaStyle: {}, data: trendData.map((i) => i.count) }]
  })

  const deptData = dashboard.value?.departmentRank || []
  renderChart(deptRef.value, {
    color: colors,
    tooltip: { trigger: 'axis' },
    grid: { left: 120, right: 18, top: 18, bottom: 28 },
    xAxis: { type: 'value', minInterval: 1 },
    yAxis: { type: 'category', data: deptData.map((i) => i.departmentName), inverse: true },
    series: [{ name: '访客数', type: 'bar', data: deptData.map((i) => i.count), barWidth: 16 }]
  })

  const gateData = dashboard.value?.gateSummary || []
  renderChart(gateRef.value, {
    color: colors,
    tooltip: { trigger: 'item' },
    legend: { bottom: 0 },
    series: [{
      name: '通行量',
      type: 'pie',
      radius: ['45%', '70%'],
      data: gateData.map((i) => ({ name: i.gateName, value: i.totalCount ?? ((i.entryCount || 0) + (i.exitCount || 0)) }))
    }]
  })
}

function renderChart(el, option) {
  if (!el) return
  echarts.dispose(el)
  echarts.init(el).setOption(option)
}
</script>
