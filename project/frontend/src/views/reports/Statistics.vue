<template>
  <section>
    <PageHeader title="统计报表页" description="面向课程设计报告和答辩展示，汇总访客趋势、部门排行、校门通行和审批通过率。">
      <el-button type="primary" :icon="Refresh" :loading="loading" @click="loadData">刷新报表</el-button>
    </PageHeader>

    <div class="dashboard-grid">
      <div class="metric-card" v-for="item in cards" :key="item.label">
        <div class="metric-icon"><el-icon><component :is="item.icon" /></el-icon></div>
        <div>
          <div class="metric-label">{{ item.label }}</div>
          <div class="metric-value">{{ item.value }}</div>
          <div class="metric-hint">{{ item.hint }}</div>
        </div>
      </div>
    </div>

    <div class="dashboard-charts">
      <div class="chart-card"><div class="card-title"><span>近七天访客趋势</span></div><div ref="trendRef" class="chart-box"></div></div>
      <div class="chart-card"><div class="card-title"><span>审批通过率</span></div><div ref="approvalRef" class="chart-box"></div></div>
    </div>

    <div class="two-column" style="margin-top: 14px">
      <div class="chart-card"><div class="card-title"><span>部门访客排行</span></div><div ref="deptRef" class="chart-box"></div></div>
      <div class="chart-card"><div class="card-title"><span>校门通行统计</span></div><div ref="gateRef" class="chart-box"></div></div>
    </div>
  </section>
</template>
<script setup>
import { computed, nextTick, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { Aim, Bell, Refresh, UserFilled, WarningFilled } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import { loadStatisticsDashboard } from '@/api/statistics'

const trendRef = ref()
const approvalRef = ref()
const deptRef = ref()
const gateRef = ref()
const loading = ref(false)
const report = ref(defaultReport())
const cards = computed(() => {
  const overview = report.value.overview || {}
  return [
    { label: '今日访客', value: overview.todayVisitors ?? 0, hint: '按计划访问时间统计', icon: UserFilled },
    { label: '当前在校', value: overview.currentVisitors ?? 0, hint: '门岗实时登记数据', icon: Aim },
    { label: '超时未离校', value: overview.overtimeVisitors ?? 0, hint: '需要安保人员跟进', icon: WarningFilled },
    { label: '待处理审批', value: overview.pendingApprovals ?? 0, hint: '被访人确认和部门审批', icon: Bell }
  ]
})

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    report.value = await loadStatisticsDashboard()
  } catch (error) {
    report.value = defaultReport()
  } finally {
    loading.value = false
  }
  await nextTick()
  renderCharts()
}

function renderCharts() {
  const colors = ['#1d4ed8', '#06b6d4', '#16a34a', '#f59e0b', '#dc2626']
  const trend = report.value.trend || []
  const dept = report.value.departmentRank || []
  const gates = report.value.gateSummary || []
  const approval = report.value.approvalRate || {}

  echarts.init(trendRef.value).setOption({
    color: colors, tooltip: {}, grid: { left: 36, right: 18, top: 24, bottom: 28 },
    xAxis: { type: 'category', data: trend.map((i) => i.date) }, yAxis: { type: 'value' },
    series: [{ name: '访客数', type: 'line', smooth: true, areaStyle: {}, data: trend.map((i) => i.count) }]
  })
  echarts.init(approvalRef.value).setOption({
    color: colors, tooltip: { trigger: 'item' },
    series: [{ type: 'pie', radius: ['48%', '72%'], data: [
      { name: '审批通过', value: approval.approved || 0 },
      { name: '审批拒绝', value: approval.rejected || 0 },
      { name: '待处理', value: approval.pending || 0 }
    ] }]
  })
  echarts.init(deptRef.value).setOption({
    color: colors, tooltip: {}, grid: { left: 96, right: 18, top: 18, bottom: 28 },
    xAxis: { type: 'value' }, yAxis: { type: 'category', data: dept.map((i) => i.departmentName) },
    series: [{ type: 'bar', data: dept.map((i) => i.count), barWidth: 16 }]
  })
  echarts.init(gateRef.value).setOption({
    color: colors, tooltip: { trigger: 'axis' }, legend: { top: 0 }, grid: { left: 36, right: 18, top: 36, bottom: 28 },
    xAxis: { type: 'category', data: gates.map((i) => i.gateName) }, yAxis: { type: 'value' },
    series: [
      { name: '入校', type: 'bar', data: gates.map((i) => i.entryCount) },
      { name: '离校', type: 'bar', data: gates.map((i) => i.exitCount) }
    ]
  })
}

function defaultReport() {
  return {
    overview: { todayVisitors: 24, currentVisitors: 8, overtimeVisitors: 2, pendingApprovals: 5, approvalPassRate: 82 },
    trend: [{ date: '06-16', count: 18 }, { date: '06-17', count: 22 }, { date: '06-18', count: 31 }, { date: '06-19', count: 26 }, { date: '06-20', count: 35 }, { date: '06-21', count: 20 }, { date: '06-22', count: 24 }],
    departmentRank: [{ departmentName: '计算机科学与技术学院', count: 42 }, { departmentName: '自动化学院', count: 28 }, { departmentName: '软件工程学院', count: 15 }, { departmentName: '通信与信息工程学院', count: 10 }],
    gateSummary: [{ gateName: '北校门', entryCount: 46, exitCount: 43 }, { gateName: '南校门', entryCount: 31, exitCount: 29 }, { gateName: '东区车行门', entryCount: 18, exitCount: 16 }, { gateName: '西校门', entryCount: 8, exitCount: 7 }],
    approvalRate: { approved: 82, rejected: 12, pending: 6, passRate: 82 }
  }
}
</script>