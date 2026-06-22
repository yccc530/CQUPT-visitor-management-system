<template>
  <section>
    <PageHeader :title="title" :description="description">
      <el-button v-if="creatable" type="primary" :icon="Plus" @click="openCreate">新增</el-button>
    </PageHeader>

    <div class="filter-card">
      <el-form :model="filters" inline>
        <el-form-item v-for="item in filterItems" :key="item.prop" :label="item.label">
          <el-input v-if="item.type !== 'select'" v-model="filters[item.prop]" clearable :placeholder="item.placeholder || '请输入'" />
          <el-select v-else v-model="filters[item.prop]" clearable :placeholder="item.placeholder || '请选择'" style="width: 180px">
            <el-option v-for="option in item.options || []" :key="option.value" :label="option.label" :value="option.value" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="loadData">查询</el-button>
          <el-button @click="resetFilters">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <div class="table-card">
      <div class="card-title">
        <span>{{ tableTitle }}</span>
        <el-tag type="info" effect="plain">{{ rows.length }} 条记录</el-tag>
      </div>
      <el-table v-loading="loading" :data="rows" empty-text="暂无数据，稍后可通过新增或初始化数据补充" stripe>
        <el-table-column v-for="column in columns" :key="column.prop" :prop="column.prop" :label="column.label" :min-width="column.width || 120" show-overflow-tooltip>
          <template #default="scope">
            <StatusTag v-if="column.statusKind" :kind="column.statusKind" :value="scope.row[column.prop]" />
            <span v-else>{{ formatCell(scope.row[column.prop]) }}</span>
          </template>
        </el-table-column>
        <el-table-column v-if="editable || removable || $slots.actions" label="操作" fixed="right" min-width="170">
          <template #default="scope">
            <div class="action-row">
              <slot name="actions" :row="scope.row" />
              <el-button v-if="editable" size="small" type="primary" plain @click="openEdit(scope.row)">编辑</el-button>
              <el-button v-if="removable" size="small" type="danger" plain @click="removeRow(scope.row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="table-footer">
        <el-pagination background layout="prev, pager, next, total" :current-page="page.current" :page-size="page.size" :total="page.total" @current-change="changePage" />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="620px">
      <el-form ref="formRef" :model="form" label-width="118px">
        <el-form-item v-for="item in formItems" :key="item.prop" :label="item.label" :required="item.required">
          <el-input v-if="!item.type || item.type === 'input'" v-model="form[item.prop]" :placeholder="item.placeholder || '请输入'" />
          <el-input v-else-if="item.type === 'textarea'" v-model="form[item.prop]" type="textarea" :rows="3" />
          <el-input-number v-else-if="item.type === 'number'" v-model="form[item.prop]" :min="0" />
          <el-select v-else-if="item.type === 'select'" v-model="form[item.prop]" style="width: 100%" :placeholder="item.placeholder || '请选择'">
            <el-option v-for="option in item.options || []" :key="option.value" :label="option.label" :value="option.value" />
          </el-select>
          <el-date-picker v-else-if="item.type === 'datetime'" v-model="form[item.prop]" type="datetime" value-format="YYYY-MM-DDTHH:mm:ss" style="width: 100%" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="saving" @click="saveRow">保存</el-button>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Plus, Search } from '@element-plus/icons-vue'
import PageHeader from './PageHeader.vue'
import StatusTag from './StatusTag.vue'
import { formatDateTime } from '@/utils/status'

const props = defineProps({
  title: { type: String, required: true },
  description: { type: String, default: '' },
  tableTitle: { type: String, default: '数据列表' },
  columns: { type: Array, required: true },
  filterItems: { type: Array, default: () => [] },
  formItems: { type: Array, default: () => [] },
  api: { type: Object, default: null },
  mockData: { type: Array, default: () => [] },
  creatable: { type: Boolean, default: true },
  editable: { type: Boolean, default: true },
  removable: { type: Boolean, default: true }
})

const loading = ref(false)
const saving = ref(false)
const rows = ref([])
const filters = reactive({})
const form = reactive({})
const dialogVisible = ref(false)
const editingId = ref(null)
const page = reactive({ current: 1, size: 10, total: 0 })
const dialogTitle = computed(() => editingId.value ? '编辑信息' : '新增信息')

onMounted(loadData)

function formatCell(value) {
  if (String(value || '').includes('T')) return formatDateTime(value)
  return value ?? '-'
}

async function loadData() {
  loading.value = true
  try {
    const result = props.api?.list ? await props.api.list({ current: page.current, size: page.size, ...filters }) : null
    rows.value = result?.records || result || []
    page.total = result?.total ?? rows.value.length
  } catch (error) {
    rows.value = props.mockData
    page.total = props.mockData.length
  } finally {
    loading.value = false
  }
}

function resetFilters() {
  Object.keys(filters).forEach((key) => { filters[key] = '' })
  page.current = 1
  loadData()
}

function changePage(current) {
  page.current = current
  loadData()
}

function fillForm(row = {}) {
  Object.keys(form).forEach((key) => delete form[key])
  for (const item of props.formItems) form[item.prop] = row[item.prop] ?? item.default ?? ''
}

function openCreate() {
  editingId.value = null
  fillForm()
  dialogVisible.value = true
}

function openEdit(row) {
  editingId.value = row.id
  fillForm(row)
  dialogVisible.value = true
}

async function saveRow() {
  saving.value = true
  try {
    if (editingId.value && props.api?.update) await props.api.update(editingId.value, form)
    else if (props.api?.create) await props.api.create(form)
    ElMessage.success('保存成功')
    dialogVisible.value = false
    loadData()
  } catch (error) {
    ElMessage.warning('后端不可用时页面保留演示数据，联调时将写入数据库')
  } finally {
    saving.value = false
  }
}

async function removeRow(row) {
  await ElMessageBox.confirm('确认删除该记录吗？', '操作确认', { type: 'warning' })
  try {
    if (props.api?.remove) await props.api.remove(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    ElMessage.warning('当前为演示数据或无删除权限')
  }
}
</script>