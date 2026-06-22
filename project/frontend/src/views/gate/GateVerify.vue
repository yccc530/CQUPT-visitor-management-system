<template>
  <section>
    <PageHeader title="门岗核验页" description="按预约编号、手机号、证件号或通行码核验访客是否允许入校。" />
    <div class="two-column">
      <div class="form-card">
        <div class="card-title"><span>核验条件</span><el-tag type="primary" effect="plain">门岗安保</el-tag></div>
        <el-form :model="form" label-width="100px">
          <el-form-item label="通行码"><el-input v-model="form.passCode" placeholder="如 PC202606220001" /></el-form-item>
          <el-form-item label="预约编号"><el-input v-model="form.applyNo" placeholder="如 VA202606220001" /></el-form-item>
          <el-form-item label="手机号"><el-input v-model="form.phone" /></el-form-item>
          <el-form-item label="证件号"><el-input v-model="form.idNumber" /></el-form-item>
          <el-form-item label="校门ID"><el-input-number v-model="form.gateId" :min="1" style="width: 100%" /></el-form-item>
          <el-button type="primary" :loading="loading" style="width: 100%" @click="verify">核验通行资格</el-button>
        </el-form>
      </div>
      <div class="detail-card">
        <div class="card-title"><span>核验结果</span><el-tag :type="result?.allowed ? 'success' : 'info'">{{ result?.allowed ? '允许入校' : '等待核验' }}</el-tag></div>
        <el-descriptions v-if="result" :column="1" border>
          <el-descriptions-item label="结果说明">{{ result.message }}</el-descriptions-item>
          <el-descriptions-item label="预约编号">{{ result.applyNo || '-' }}</el-descriptions-item>
          <el-descriptions-item label="访客姓名">{{ result.visitorName || '-' }}</el-descriptions-item>
          <el-descriptions-item label="手机号">{{ result.phone || '-' }}</el-descriptions-item>
          <el-descriptions-item label="预约状态"><StatusTag kind="apply" :value="result.applyStatus" /></el-descriptions-item>
          <el-descriptions-item label="访问状态"><StatusTag kind="access" :value="result.accessStatus" /></el-descriptions-item>
          <el-descriptions-item label="有效期">{{ formatDateTime(result.validFrom) }} 至 {{ formatDateTime(result.validTo) }}</el-descriptions-item>
        </el-descriptions>
        <div v-else class="empty-panel">输入核验条件后，系统将显示访客身份、预约状态和通行资格。</div>
      </div>
    </div>
  </section>
</template>
<script setup>
import { reactive, ref } from 'vue'
import PageHeader from '@/components/PageHeader.vue'
import StatusTag from '@/components/StatusTag.vue'
import { workflowApi } from '@/api/workflow'
import { formatDateTime } from '@/utils/status'
const loading = ref(false)
const result = ref(null)
const form = reactive({ passCode: '', applyNo: '', phone: '', idNumber: '', gateId: 1 })
async function verify() {
  loading.value = true
  try { result.value = await workflowApi.verifyGate(form) }
  finally { loading.value = false }
}
</script>