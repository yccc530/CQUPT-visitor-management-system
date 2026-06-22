<template>
  <section>
    <PageHeader title="离校登记页" description="对已入校或超时未离校访客补充离校时间，已离校记录不能重复登记。" />
    <div class="two-column">
      <div class="form-card">
        <div class="card-title"><span>离校登记</span><el-tag type="info" effect="plain">EXITED</el-tag></div>
        <el-form :model="form" label-width="100px">
          <el-form-item label="出入记录ID"><el-input-number v-model="form.accessRecordId" :min="1" style="width: 100%" /></el-form-item>
          <el-form-item label="预约ID"><el-input-number v-model="form.applyId" :min="1" style="width: 100%" /></el-form-item>
          <el-form-item label="预约编号"><el-input v-model="form.applyNo" /></el-form-item>
          <el-form-item label="通行码"><el-input v-model="form.passCode" /></el-form-item>
          <el-form-item label="离校校门"><el-input-number v-model="form.gateId" :min="1" style="width: 100%" /></el-form-item>
          <el-button type="primary" style="width: 100%" :loading="loading" @click="submit">登记离校</el-button>
        </el-form>
      </div>
      <div class="detail-card">
        <div class="card-title"><span>操作提示</span><el-tag type="warning" effect="plain">防重复</el-tag></div>
        <el-alert title="系统会校验该访客是否已入校，并阻止重复离校登记。" type="info" show-icon :closable="false" />
        <el-table style="margin-top: 14px" :data="mockAccessRecords" stripe>
          <el-table-column prop="visitorName" label="访客" />
          <el-table-column prop="entryTime" label="入校时间" />
          <el-table-column prop="accessStatus" label="状态"><template #default="scope"><StatusTag kind="access" :value="scope.row.accessStatus" /></template></el-table-column>
        </el-table>
      </div>
    </div>
  </section>
</template>
<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import StatusTag from '@/components/StatusTag.vue'
import { workflowApi } from '@/api/workflow'
import { mockAccessRecords } from '@/api/mock'
const loading = ref(false)
const form = reactive({ accessRecordId: null, applyId: null, applyNo: '', passCode: '', gateId: 2 })
async function submit() { loading.value = true; try { await workflowApi.exit(form); ElMessage.success('离校登记成功') } finally { loading.value = false } }
</script>