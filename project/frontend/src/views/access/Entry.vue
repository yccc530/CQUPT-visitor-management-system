<template>
  <section>
    <PageHeader title="入校登记页" description="核验通过后登记入校，记录校门、安保人员和入校时间。" />
    <div class="two-column">
      <div class="form-card">
        <div class="card-title"><span>入校登记</span><el-tag type="success" effect="plain">ENTERED</el-tag></div>
        <el-form :model="form" label-width="100px">
          <el-form-item label="预约ID"><el-input-number v-model="form.applyId" :min="1" style="width: 100%" /></el-form-item>
          <el-form-item label="预约编号"><el-input v-model="form.applyNo" /></el-form-item>
          <el-form-item label="通行码"><el-input v-model="form.passCode" /></el-form-item>
          <el-form-item label="入校校门"><el-input-number v-model="form.gateId" :min="1" style="width: 100%" /></el-form-item>
          <el-button type="primary" style="width: 100%" :loading="loading" @click="submit">登记入校</el-button>
        </el-form>
      </div>
      <div class="table-card">
        <div class="card-title"><span>当前在校参考</span><el-tag type="info" effect="plain">实时</el-tag></div>
        <el-table :data="mockAccessRecords.filter(i => i.accessStatus !== 'EXITED')" stripe>
          <el-table-column prop="applyNo" label="预约编号" />
          <el-table-column prop="visitorName" label="访客" />
          <el-table-column prop="entryGateName" label="入校校门" />
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
const form = reactive({ applyId: null, applyNo: '', passCode: '', gateId: 1 })
async function submit() { loading.value = true; try { await workflowApi.entry(form); ElMessage.success('入校登记成功') } finally { loading.value = false } }
</script>