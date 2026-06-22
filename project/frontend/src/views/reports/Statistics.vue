<template>
  <section>
    <PageHeader title="统计报表页" description="面向课程设计报告和答辩展示，汇总访客趋势、部门排行、校门通行和审批通过率。">
      <el-button type="primary" :icon="Refresh" @click="renderCharts">刷新报表</el-button>
    </PageHeader>
    <div class="dashboard-grid">
      <div class="metric-card" v-for="item in cards" :key="item.label">
        <div class="metric-icon"><el-icon><component :is="item.icon" /></el-icon></div>
        <div><div class="metric-label">{{ item.label }}</div><div class="metric-value">{{ item.value }}</div><div class="metric-hint">{{ item.hint }}</div></div>
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
import { nextTick, onMounted, ref } from 'vue'
import * as echarts from 'echarts'
import { Aim, Bell, Refresh, UserFilled, WarningFilled } from '@element-plus/icons-vue'
import PageHeader from '@/components/PageHeader.vue'
const trendRef = ref(); const approvalRef = ref(); const deptRef = ref(); const gateRef = ref()
const cards = [
  { label: '今日访客', value: 24, hint: '较昨日 +12%', icon: UserFilled },
  { label: '当前在校', value: 8, hint: '门岗实时数据', icon: Aim },
  { label: '超时未离校', value: 2, hint: '需跟进处理', icon: WarningFilled },
  { label: '待处理审批', value: 5, hint: '被访人/部门审批', icon: Bell }
]
onMounted(renderCharts)
async function renderCharts() {
  await nextTick()
  const colors = ['#1d4ed8', '#06b6d4', '#16a34a', '#f59e0b', '#dc2626']
  echarts.init(trendRef.value).setOption({ color: colors, tooltip: {}, grid: { left: 36, right: 18, top: 24, bottom: 28 }, xAxis: { type: 'category', data: ['6/16','6/17','6/18','6/19','6/20','6/21','6/22'] }, yAxis: { type: 'value' }, series: [{ name: '访客数', type: 'line', smooth: true, areaStyle: {}, data: [18, 22, 31, 26, 35, 20, 24] }] })
  echarts.init(approvalRef.value).setOption({ color: colors, tooltip: { trigger: 'item' }, series: [{ type: 'pie', radius: ['48%', '72%'], data: [{ name: '审批通过', value: 82 }, { name: '审批拒绝', value: 12 }, { name: '待处理', value: 6 }] }] })
  echarts.init(deptRef.value).setOption({ color: colors, tooltip: {}, grid: { left: 78, right: 18, top: 18, bottom: 28 }, xAxis: { type: 'value' }, yAxis: { type: 'category', data: ['计算机学院','自动化学院','保卫处','党政办','通信学院'] }, series: [{ type: 'bar', data: [42, 28, 15, 10, 8], barWidth: 16 }] })
  echarts.init(gateRef.value).setOption({ color: colors, tooltip: { trigger: 'axis' }, legend: { top: 0 }, grid: { left: 36, right: 18, top: 36, bottom: 28 }, xAxis: { type: 'category', data: ['北校门','南校门','东区车行门','西校门'] }, yAxis: { type: 'value' }, series: [{ name: '入校', type: 'bar', data: [46, 31, 18, 8] }, { name: '离校', type: 'bar', data: [43, 29, 16, 7] }] })
}
</script>