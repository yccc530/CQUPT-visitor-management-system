<template>
  <section>
    <PageHeader title="统计报表" description="展示访客规模、审批效率、校门通行、访问事由、风险名单和系统操作情况。">
      <el-button type="primary" :icon="Refresh" :loading="loading" @click="loadData">刷新报表</el-button>
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
        <div class="card-title"><span>近七天访客趋势</span><el-tag type="primary" effect="plain">趋势</el-tag></div>
        <div ref="trendRef" class="chart-box"></div>
      </div>
      <div class="chart-card">
        <div class="card-title"><span>部门访客排行</span><el-tag type="success" effect="plain">本月</el-tag></div>
        <div ref="deptRef" class="chart-box"></div>
      </div>
    </div>

    <div class="dashboard-charts" style="margin-top: 14px">
      <div class="chart-card">
        <div class="card-title"><span>校门通行统计</span><el-tag type="info" effect="plain">入校/离校</el-tag></div>
        <div ref="gateRef" class="chart-box"></div>
      </div>
      <div class="chart-card">
        <div class="card-title"><span>访问事由分布</span><el-tag type="warning" effect="plain">本月</el-tag></div>
        <div ref="reasonRef" class="chart-box"></div>
      </div>
    </div>

    <div class="dashboard-charts" style="margin-top: 14px">
      <div class="chart-card">
        <div class="card-title"><span>审批状态分布</span><el-tag type="success" effect="plain">预约状态</el-tag></div>
        <div ref="approvalStatusRef" class="chart-box"></div>
      </div>
      <div class="chart-card">
        <div class="card-title"><span>入校离校状态分布</span><el-tag type="danger" effect="plain">通行状态</el-tag></div>
        <div ref="accessStatusRef" class="chart-box"></div>
      </div>
    </div>

    <div class="two-column" style="margin-top: 14px">
      <div class="chart-card">
        <div class="card-title"><span>黑名单风险数量</span><el-tag type="danger" effect="plain">风险等级</el-tag></div>
        <div ref="blacklistRef" class="chart-box"></div>
      </div>
      <div class="detail-card">
        <div class="card-title"><span>最近操作日志</span><el-tag type="info" effect="plain">实时审计</el-tag></div>
        <el-table :data="recentLogs" height="300" size="small" empty-text="暂无操作日志">
          <el-table-column prop="operationTime" label="时间" width="150" />
          <el-table-column prop="operatorName" label="操作人" width="96" />
          <el-table-column prop="moduleName" label="模块" width="110" />
          <el-table-column prop="operationType" label="操作" width="110" />
          <el-table-column prop="operationResult" label="结果" width="88">
            <template #default="{ row }">
              <el-tag :type="row.operationResult === 'SUCCESS' ? 'success' : 'danger'" effect="plain">{{ row.operationResult }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="requestUrl" label="接口地址" min-width="180" show-overflow-tooltip />
        </el-table>
      </div>
    </div>
  </section>
</template>

<script setup>
import { computed, nextTick, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { Aim, Bell, Refresh, TrendCharts, UserFilled, WarningFilled } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
import { loadStatisticsDashboard } from '@/api/statistics'

const loading = ref(false)
const dashboard = ref({})
const trendRef = ref()
const deptRef = ref()
const gateRef = ref()
const reasonRef = ref()
const approvalStatusRef = ref()
const accessStatusRef = ref()
const blacklistRef = ref()

const overview = computed(() => dashboard.value?.overview || {})
const recentLogs = computed(() => dashboard.value?.recentLogs || [])
const metrics = computed(() => [
  { label: '今日访客数', value: overview.value.todayVisitors ?? 0, hint: '今日计划来访总量', icon: UserFilled },
  { label: '本周访客数', value: overview.value.thisWeekVisitors ?? 0, hint: '本周预约访问规模', icon: TrendCharts },
  { label: '本月访客数', value: overview.value.thisMonthVisitors ?? 0, hint: '本月预约访问规模', icon: TrendCharts },
  { label: '当前在校', value: overview.value.currentVisitors ?? 0, hint: '已入校未离校人员', icon: Aim },
  { label: '超时未离校', value: overview.value.overtimeVisitors ?? 0, hint: '需要安保跟进', icon: WarningFilled },
  { label: '待审批预约', value: overview.value.pendingApprovals ?? 0, hint: '待确认和待部门审批', icon: Bell },
  { label: '黑名单风险', value: overview.value.blacklistRiskCount ?? 0, hint: '当前生效风险人员', icon: WarningFilled }
])

onMounted(loadData)

async function loadData() {
  loading.value = true
  try {
    dashboard.value = await loadStatisticsDashboard()
  } finally {
    loading.value = false
  }
  await nextTick()
  renderCharts()
}

function renderCharts() {
  const colors = ['#1d4ed8', '#06b6d4', '#16a34a', '#f59e0b', '#dc2626', '#7c3aed', '#0f766e', '#64748b']
  const trend = dashboard.value?.trend || []
  renderChart(trendRef.value, {
    color: colors,
    tooltip: { trigger: 'axis' },
    grid: { left: 36, right: 18, top: 30, bottom: 28 },
    xAxis: { type: 'category', data: trend.map((i) => i.date) },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ name: '访客数', type: 'line', smooth: true, areaStyle: {}, data: trend.map((i) => i.count) }]
  })

  const dept = dashboard.value?.departmentRank || []
  renderChart(deptRef.value, {
    color: colors,
    tooltip: { trigger: 'axis' },
    grid: { left: 128, right: 18, top: 18, bottom: 28 },
    xAxis: { type: 'value', minInterval: 1 },
    yAxis: { type: 'category', data: dept.map((i) => i.departmentName), inverse: true },
    series: [{ name: '访客数', type: 'bar', data: dept.map((i) => i.count), barWidth: 14 }]
  })

  const gate = dashboard.value?.gateSummary || []
  renderChart(gateRef.value, {
    color: colors,
    tooltip: { trigger: 'axis' },
    legend: { top: 0 },
    grid: { left: 48, right: 18, top: 42, bottom: 42 },
    xAxis: { type: 'category', data: gate.map((i) => i.gateName), axisLabel: { rotate: 20 } },
    yAxis: { type: 'value', minInterval: 1 },
    series: [
      { name: '入校', type: 'bar', data: gate.map((i) => i.entryCount || 0), barWidth: 14 },
      { name: '离校', type: 'bar', data: gate.map((i) => i.exitCount || 0), barWidth: 14 }
    ]
  })

  const reasons = dashboard.value?.reasonDistribution || []
  renderPie(reasonRef.value, '访问事由', reasons.map((i) => ({ name: i.reasonType, value: i.count })), colors)

  const approvalStatuses = dashboard.value?.approvalStatusDistribution || []
  renderPie(approvalStatusRef.value, '审批状态', approvalStatuses.map((i) => ({ name: i.statusName, value: i.count })), colors)

  const accessStatuses = dashboard.value?.accessStatusDistribution || []
  renderPie(accessStatusRef.value, '通行状态', accessStatuses.map((i) => ({ name: i.statusName, value: i.count })), colors)

  const blacklist = dashboard.value?.blacklistRisk?.byLevel || []
  renderChart(blacklistRef.value, {
    color: colors,
    tooltip: { trigger: 'axis' },
    grid: { left: 60, right: 18, top: 28, bottom: 32 },
    xAxis: { type: 'category', data: blacklist.map((i) => i.levelName) },
    yAxis: { type: 'value', minInterval: 1 },
    series: [{ name: '风险数量', type: 'bar', data: blacklist.map((i) => i.count), barWidth: 26 }]
  })
}

function renderPie(el, name, data, colors) {
  renderChart(el, {
    color: colors,
    tooltip: { trigger: 'item' },
    legend: { bottom: 0, type: 'scroll' },
    series: [{ name, type: 'pie', radius: ['42%', '68%'], center: ['50%', '44%'], data }]
  })
}

function renderChart(el, option) {
  if (!el) return
  echarts.dispose(el)
  echarts.init(el).setOption(option)
}
</script>
