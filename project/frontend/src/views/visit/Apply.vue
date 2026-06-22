<template>
  <section>
    <PageHeader title="访客预约申请页" description="录入访客身份、被访人、来访时间、车辆和随行人员，提交后进入被访人确认环节。" />
    <div class="form-card">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="112px">
        <div class="three-column">
          <el-form-item label="访客姓名" prop="visitorName"><el-input v-model="form.visitorName" /></el-form-item>
          <el-form-item label="证件类型" prop="idType"><el-select v-model="form.idType"><el-option label="身份证" value="ID_CARD" /><el-option label="护照" value="PASSPORT" /></el-select></el-form-item>
          <el-form-item label="证件号码" prop="idNumber"><el-input v-model="form.idNumber" /></el-form-item>
        </div>
        <div class="three-column">
          <el-form-item label="手机号" prop="phone"><el-input v-model="form.phone" /></el-form-item>
          <el-form-item label="访客单位"><el-input v-model="form.company" /></el-form-item>
          <el-form-item label="性别"><el-select v-model="form.gender"><el-option label="男" value="男" /><el-option label="女" value="女" /></el-select></el-form-item>
        </div>
        <div class="two-column">
          <el-form-item label="被访人ID" prop="hostUserId"><el-input-number v-model="form.hostUserId" :min="1" style="width: 100%" /></el-form-item>
          <el-form-item label="访问部门ID" prop="departmentId"><el-input-number v-model="form.departmentId" :min="1" style="width: 100%" /></el-form-item>
        </div>
        <div class="two-column">
          <el-form-item label="计划开始" prop="planStartTime"><el-date-picker v-model="form.planStartTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" /></el-form-item>
          <el-form-item label="计划结束" prop="planEndTime"><el-date-picker v-model="form.planEndTime" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width:100%" /></el-form-item>
        </div>
        <el-form-item label="来访事由" prop="visitReason"><el-input v-model="form.visitReason" type="textarea" :rows="3" /></el-form-item>
        <div class="three-column">
          <el-form-item label="车牌号"><el-input v-model="form.vehiclePlateNo" /></el-form-item>
          <el-form-item label="车辆类型"><el-input v-model="form.vehicleType" /></el-form-item>
          <el-form-item label="车辆品牌"><el-input v-model="form.vehicleBrand" /></el-form-item>
        </div>
        <div class="card-title"><span>随行人员</span><el-button size="small" type="primary" plain @click="addCompanion">添加</el-button></div>
        <el-table :data="form.companions" border empty-text="暂无随行人员">
          <el-table-column label="姓名"><template #default="scope"><el-input v-model="scope.row.companionName" /></template></el-table-column>
          <el-table-column label="证件号"><template #default="scope"><el-input v-model="scope.row.idNumber" /></template></el-table-column>
          <el-table-column label="手机号"><template #default="scope"><el-input v-model="scope.row.phone" /></template></el-table-column>
          <el-table-column label="关系"><template #default="scope"><el-input v-model="scope.row.relationRemark" /></template></el-table-column>
          <el-table-column label="操作" width="90"><template #default="scope"><el-button type="danger" link @click="form.companions.splice(scope.$index, 1)">移除</el-button></template></el-table-column>
        </el-table>
        <div style="margin-top: 18px; display: flex; justify-content: flex-end; gap: 10px">
          <el-button @click="resetForm">重置</el-button>
          <el-button type="primary" :loading="saving" @click="submit">提交预约</el-button>
        </div>
      </el-form>
    </div>
  </section>
</template>

<script setup>
import { reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import PageHeader from '@/components/PageHeader.vue'
import { workflowApi } from '@/api/workflow'

const formRef = ref()
const saving = ref(false)
const now = new Date()
const later = new Date(Date.now() + 2 * 60 * 60 * 1000)
const form = reactive(defaultForm())
const rules = {
  visitorName: [{ required: true, message: '请输入访客姓名', trigger: 'blur' }],
  idType: [{ required: true, message: '请选择证件类型', trigger: 'change' }],
  idNumber: [{ required: true, message: '请输入证件号码', trigger: 'blur' }],
  phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }],
  hostUserId: [{ required: true, message: '请输入被访人ID', trigger: 'blur' }],
  departmentId: [{ required: true, message: '请输入部门ID', trigger: 'blur' }],
  visitReason: [{ required: true, message: '请输入来访事由', trigger: 'blur' }],
  planStartTime: [{ required: true, message: '请选择计划开始时间', trigger: 'change' }],
  planEndTime: [{ required: true, message: '请选择计划结束时间', trigger: 'change' }]
}

function toInputTime(date) { return date.toISOString().slice(0, 19) }
function defaultForm() { return { visitorName: '', idType: 'ID_CARD', idNumber: '', phone: '', company: '', gender: '男', hostUserId: 2, departmentId: 2, visitReason: '', planStartTime: toInputTime(now), planEndTime: toInputTime(later), vehiclePlateNo: '', vehicleType: '', vehicleColor: '', vehicleBrand: '', companions: [] } }
function addCompanion() { form.companions.push({ companionName: '', idType: 'ID_CARD', idNumber: '', phone: '', relationRemark: '' }) }
function resetForm() { Object.assign(form, defaultForm()) }
async function submit() {
  await formRef.value?.validate()
  saving.value = true
  try {
    const result = await workflowApi.submitApply(form)
    ElMessage.success(`预约提交成功：${result.applyNo}`)
    resetForm()
  } finally { saving.value = false }
}
</script>