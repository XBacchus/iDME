# iDME 椤圭洰缁撴瀯锛堣鍒欏熀绾匡級

## 椤跺眰鐩綍
- `miniapp/backend`: Spring Boot 鍚庣锛圝DK17/Maven锛?- `miniapp/frontend`: Vue 鍓嶇锛圢ode.js/Vite锛?- `doc`: 璧涢璧勬枡銆佽鏄庢枃妗ｄ笌鍒嗘瀽娌夋穩
- `API_DOCS`: API 闇€姹備笌鏂囨。闂幆
- `_verify`: 杩愯鎬佹牎楠屼笌 OpenAPI 蹇収浜х墿
- `.cursor/rules`: Cursor 瑙勫垯闆?- `.memories`: 璁板繂搴擄紙涓氬姟鍜屾ā鍧楃煡璇嗭級
- `scripts`: Git Hooks 涓庤嚜鍔ㄥ寲鑴氭湰

## 瑙勫垯鏂囦欢
- `AGENTS.md`
- `CLAUDE.md`
- `.cursor/project_structure.md`
- `.cursor/database_datainfo.md`

## 鏇存柊瑕佹眰
1. 鏂板/鍒犻櫎/绉诲姩鏂囦欢鎴栫洰褰曞悗锛屽繀椤绘洿鏂版湰鏂囦欢銆?
2. 閲嶅ぇ缁撴瀯璋冩暣闇€琛ュ厖鈥滃彉鏇磋褰曗€濄€?
3. API 鍙樻洿鏃堕渶鍚屾鏍稿 `API_DOCS` 涓?`_verify/api-docs.json` 涓€鑷存€с€?

## 鍙樻洿璁板綍
- 2026-03-09锛氬垵濮嬪寲鍗忎綔瑙勫垯浣撶郴锛圓GENTS銆丆LAUDE銆?cursor/rules銆?memories銆丄PI_DOCS銆乬it hooks銆侀獙鏀舵竻鍗曪級銆?- 2026-03-12锛氭柊澧炲悗绔€傞厤灞備笌 REST 鎺у埗鍣紙`dto/ApiResponse`銆乣service/XdmRuntimeService`銆乣service/MiniAppAdapterService`銆乣controller/PartController`銆乣controller/EquipmentController`銆乣controller/WorkingPlanController`銆乣controller/ProcedureController`銆乣controller/GlobalExceptionHandler`锛夛紝骞舵柊澧炲悗绔崟鍏?闆嗘垚娴嬭瘯涓?API 鏂囨。蹇収鍚屾銆?- 2026-03-13锛氳澶囨ā鍧楀瓧娈靛彛寰勪慨澶嶏紙鍓嶇 `EquipmentList.vue`銆乣EquipmentForm.vue`锛夛紝鏂板鍚庣鎸佷箙鍖栫粍浠?`service/EquipmentProductionDateStore`锛岃繍琛屾椂鏂板 `miniapp/backend/data/equipment-production-dates.json` 浣滀负 `productionDate` 鍥炴樉缂撳瓨鏂囦欢銆?- 2026-03-13锛氭柊澧炶禌棰樺缓妯℃竻鍗曟枃妗?`doc/鍥涗釜鏍稿績妯″瀷瀛楁娓呭崟.md`锛岀敤浜庡浐瀹?Equipment/Part/WorkingProcedure/WorkingPlan 瀛楁鍙ｅ緞銆?- 2026-03-13锛氱墿鏂欐ā鍧楄ˉ榻愬繀闇€瀛楁鏍￠獙锛堝墠绔?`PartForm.vue`銆佸悗绔?`MiniAppAdapterService.java`锛夛紝骞朵慨澶?Part 鏇存柊鏃跺瓧娈靛悎骞堕€昏緫锛岄伩鍏嶉儴鍒嗘洿鏂板鑷村繀濉瓧娈佃璇竻绌恒€?- 2026-03-13锛氭洿鏂版牴鐩綍 `start.bat`锛屾柊澧?miniapp 鍚庣(8080)涓庡墠绔?5173)涓€閿惎鍔ㄥ強 `-force` 鍋滄娴佺▼銆?- 2026-03-13锛氬伐鑹鸿矾绾挎ā鍧楄ˉ榻?operationTime 鍓嶅悗绔彛寰勶細鍓嶇 WorkingPlanForm.vue銆乄orkingPlanList.vue銆乄orkingPlanDetail.vue 鏂板瀛楁灞曠ず锛涘悗绔柊澧?service/WorkingPlanOperationTimeStore.java锛屽苟鍦?MiniAppAdapterService.java 澧炲姞 operationTime 鍏煎閲嶈瘯涓庣紦瀛樺洖鍐欓€昏緫銆?- 2026-03-13锛氬悓姝?Part 濂戠害鏂囨。鍒?`API_DOCS`锛堝繀濉瓧娈典笌鏇存柊璇箟锛夊苟鏇存柊楠屾敹娓呭崟 `miniapp/backend/tests/TEST_ACCEPTANCE_CHECKLIST.md`銆?- 2026-03-13锛氬伐搴忛厤缃〉鍗囩骇涓哄畬鏁存ā鍨嬭鍥句笌缂栬緫鑳藉姏锛氬悗绔?`MiniAppAdapterService.java` 琛ラ綈 `WorkingProcedure` 杩斿洖瀛楁骞惰仛鍚?`WorkingProcedure_Equipment` 鍏崇郴璁惧锛涘墠绔?`ProcedureList.vue` 鏀逛负琛ㄦ牸+缂栬緫寮圭獥锛涘悓姝ユ枃妗?`API_DOCS/API_SPECIFICATION.md`銆乣API_DOCS/frontend_api_requirements.json`銆?- 2026-03-13锛氬墠绔牴鐩綍鏂板楠屾敹婕旂ず鏁版嵁鏂囨。 `miniapp/frontend/鍓嶇妯℃嫙鏁版嵁娓呭崟.md`锛岃鐩栬澶?鐗╂枡/宸ュ簭/宸ヨ壓鍙婂叧绯诲疄浣撶殑瀹屾暣妯℃嫙鏁版嵁鍙ｅ緞銆?- 2026-03-13锛氱墿鏂欏垪琛ㄥ垎绫荤瓫閫夋敼涓烘爲褰㈤€夋嫨锛圥artList.vue锛夛紝鏀寔涓€绾у睍寮€瀛愬垎绫汇€佺埗绫诲惈瀛愮被杩囨护锛坈ategoryIds锛変笌椤甸潰杩斿洖鐘舵€佷繚鐣欙紱鍚庣 PartController.java銆丮iniAppAdapterService.java 澧炲姞 categoryIds 鍏煎銆?
- 2026-03-13锛氬悗绔垎绫绘槧灏勪慨澶嶏紙MiniAppAdapterService.java锛夛細鎸?categoryPath 鍒嗘寤烘爲骞舵寜璺緞瀹氫綅 categoryId锛屼慨澶嶇埗绫荤瓫閫夋惡甯?categoryIds 鏃剁粨鏋滀负绌虹殑闂銆?

- 2026-03-13：新增提交材料文档 `API_DOCS/xdm_api_call_list.csv`、`API_DOCS/xdm_api_call_list.md`，新增验收证据文档 `_verify/xdm_api_success_evidence.csv`、`_verify/xdm_api_success_evidence.md`。

- 2026-03-13：新增提交目录 `04_API清单`，新增文件 `04_API清单/xDM-F_API列表.xlsx`（由 `API_DOCS/xdm_api_call_list.csv` 转换生成）。

- 2026-03-13：新增提交目录 `03_xDM-F模型材料`，新增模型导出文件 `03_xDM-F模型材料/xDM-F_模型导出.xlsx` 与 `03_xDM-F模型材料/模型导出.xlsx`。

- 2026-03-13：补充同内容命名文件 `03_xDM-F模型材料/xDM-F模型导出.xlsx`，用于直接匹配提交命名。
