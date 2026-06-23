# Bug 修复日志

| 编号 | 问题 | 原因 | 修复方案 | 涉及文件/配置 | 验证结果 |
|---|---|---|---|---|---|
| BUG-001 | 当前终端无法直接识别 `node`、`npm`、`mvn`、`mysql`、`bash` | 工具已安装但未加入用户 PATH，Maven 不存在系统安装 | 配置用户级 `JAVA_HOME` 和 PATH；将 Maven 3.9.9 放入 `project/.tools` 并忽略提交 | 用户环境变量、`.gitignore`、`project/.tools` | `node -v`、`npm -v`、`mvn -v`、`mysql --version`、`bash --version` 均可用 |
| BUG-002 | Playwright Chromium 下载慢导致截图依赖不完整 | Chromium 包体积较大，网络下载超时 | 安装 `playwright` 包，并在截图脚本中增加系统 Chrome 兜底逻辑 | `frontend/package.json`、`scripts/capture_screenshots.js` | 截图脚本成功使用 `C:\Program Files\Google\Chrome\Application\chrome.exe` 生成 19 张截图 |
| BUG-003 | `mvn spring-boot:run` 找不到主类 `edu.cqupt.visitor.VisitorApplication` | 通过 Spring Boot Maven 插件直接运行时主类解析不稳定 | 后端启动脚本改为先 `mvn -DskipTests package`，再 `java -jar target/cqupt-visitor-backend-0.1.0.jar` | `scripts/run_backend.sh` | 后端 jar 启动成功，Swagger 可访问 |
| BUG-004 | `run_all.sh` 结束后残留 8080/5173 监听进程 | Windows 下 bash 后台 PID 与最终 Java/Node 子进程不完全一致 | 启动前检查端口；退出时按 8080/5173 监听端口清理进程 | `scripts/run_all.sh`、`scripts/run_frontend.sh` | 一键脚本执行后检查 8080/5173 无残留监听 |
| BUG-005 | 当前数据库数据量与最新 `seed.sql` 不一致 | 旧库未重新执行最新初始化脚本 | 补齐 `scripts/init_database.sh`，并重新初始化 `cqupt_visitor_system` | `scripts/init_database.sh`、`database/seed.sql` | 初始化后 `sys_user=13`、`visit_apply=24`、`access_record=13`、`blacklist=4`、`operation_log=20` |
| BUG-006 | 报告导出 Word 缺少依赖 | 本地 Python 未安装 `python-docx`，Pandoc 不存在 | 安装 `python-docx`，报告生成器优先使用 python-docx 导出 docx | Python 环境、`scripts/generate_report.py` | `docs/重庆邮电大学访客管理系统设计报告.docx` 生成成功 |
| BUG-007 | 统计页前端无法稳定读取后端统计数据 | 前端统计页早期以静态数据为主，接口字段需要统一 | 增加统计 API 请求与 fallback 数据，后端补充 `/api/statistics/**` | `frontend/src/api/statistics.js`、`frontend/src/views/reports/Statistics.vue`、`StatisticsController.java` | 前端构建通过，统计页截图生成成功 |
| BUG-008 | 当前在校访客页前端本地过滤不稳定 | 前端未显式向后端传递 `accessStatus=ENTERED` | 调整当前在校访客页查询参数，由后端按状态过滤 | `frontend/src/views/access/CurrentVisitors.vue`、`AccessRecordController.java` | 当前在校访客页截图生成成功 |

## 修复结论

以上问题均已完成修复并通过构建、数据库初始化、自动截图和一键脚本验证。剩余可优化项主要是前端生产包体积较大，可在最终优化阶段考虑 ECharts 和 Element Plus 的分包加载。