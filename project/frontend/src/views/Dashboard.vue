<template>
  <section>
    <PageHeader title="首页统计驾驶舱" description="集中展示今日访客、在校状态、审批待办、通行趋势和部门访客分布。">
      <el-button type="primary" :icon="Refresh" @click="loadData">刷新数据</el-button>
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
        <div class="card-title"><span>近七天访客趋势</span><el-tag type="primary" effect="plain">访问趋势</el-tag></div>
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
const applies = ref(mockApplies)
const accessRecords = ref(mockAccessRecords)
const trendRef = ref()
const deptRef = ref()
const gateRef = ref()

const metrics = computed(() => [
  { label: '今日访客数', value: applies.value.length, hint: '含预约、在校与已离校', icon: UserFilled },
  { label: '当前在校访客', value: applies.value.filter((i) => i.accessStatus === 'ENTERED').length, hint: '门岗实时登记', icon: Aim },
  { label: '超时未离校', value: applies.value.filter((i) => i.accessStatus === 'OVERTIME').length, hint: '需安保跟进', icon: WarningFilled },
  { label: '审批待处理', value: applies.value.filter((i) => ['PENDING_HOST', 'HOST_CONFIRMED', 'PENDING_DEPT'].includes(i.applyStatus)).length, hint: '确认与部门审批', icon: Bell }
])
const quickEntries = [
  { title: '提交预约', path: '/visit/apply', icon: Calendar },
  { title: '部门审批', path: '/approval/department', icon: Bell },
  { title: '门岗核验', path: '/gate/verify', icon: Aim },
  { title: '统计报表', path: '/reports', icon: TrendCharts }
]

onMounted(loadData)

async function loadData() {
  try {
    const data = await loadDashboardData()
    if (data.applies.length) applies.value = data.applies
    if (data.accessRecords.length) accessRecords.value = data.accessRecords
  } catch (error) {
    applies.value = mockApplies
    accessRecords.value = mockAccessRecords
  }
  await nextTick()
  renderCharts()
}

function renderCharts() {
  const colors = ['#1d4ed8', '#06b6d4', '#16a34a', '#f59e0b', '#dc2626']
  echarts.init(trendRef.value).setOption({ color: colors, tooltip: {}, grid: { left: 34, right: 16, top: 28, bottom: 26 }, xAxis: { type: 'category', data: ['周一','周二','周三','周四','周五','周六','周日'] }, yAxis: { type: 'value' }, series: [{ type: 'line', smooth: true, areaStyle: {}, data: [18, 26, 21, 32, 28, 19, 24] }] })
  echarts.init(deptRef.value).setOption({ color: colors, tooltip: {}, grid: { left: 70, right: 18, top: 18, bottom: 28 }, xAxis: { type: 'value' }, yAxis: { type: 'category', data: ['计算机学院','自动化学院','保卫处','党政办'] }, series: [{ type: 'bar', data: [42, 28, 15, 10], barWidth: 16 }] })
  echarts.init(gateRef.value).setOption({ color: colors, tooltip: { trigger: 'item' }, legend: { bottom: 0 }, series: [{ type: 'pie', radius: ['45%', '70%'], data: [{ name: '北校门', value: 46 }, { name: '南校门', value: 31 }, { name: '东区车行门', value: 18 }, { name: '西校门', value: 8 }] }] })
}
</script>