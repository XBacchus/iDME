# TEST_ACCEPTANCE_CHECKLIST

## 浣跨敤璇存槑
1. 鎺ュ彛濂戠害銆佺綉鍏充唬鐞嗐€佹祦绋嬭鍒欑浉鍏虫敼鍔ㄥ繀椤绘洿鏂版湰娓呭崟銆?
2. 姣忔潯鐢ㄤ緥鏍囪鐘舵€侊細`鉁卄锛堥€氳繃锛?`鈿狅笍`锛堥儴鍒嗭級/`鉂宍锛堝け璐ワ級銆?
3. 姣忔潯璁板綍蹇呴』鍏宠仈娴嬭瘯鏂囦欢鎴栨祴璇曞懡浠ゃ€?
4. 鑻ユ帴鍙ｅ绾﹀彉鏇达紝鍚屾椂鏍稿 `API_DOCS/*` 涓?`_verify/api-docs.json`銆?

## 涓夋寮忛獙鏀舵ā鏉?
1. 璁捐杈撳叆
2. 璁捐棰勬湡
3. 杈撳嚭瀵规瘮

## 楠屾敹璁板綍
- 2026-03-09 | 瑙勫垯浣撶郴鍒濆鍖栧熀绾?| 鐘舵€侊細鉁?| 鏂囦欢锛氳鍒欐枃妗ｅ垵濮嬪寲锛堟棤涓氬姟閫昏緫鍙樻洿锛?- 2026-03-12 | 鍚庣閫傞厤灞傦紙parts/equipments/working-plans/procedures锛夋墦閫氬苟缁熶竴杩斿洖缁撴瀯 | 鐘舵€侊細鉁?| 鏂囦欢锛欰piAdapterIntegrationTest.java銆乆dmRuntimeServiceTest.java | 鍛戒护锛歚mvn clean test`
- 2026-03-13 | Equipment 瀛楁鍙ｅ緞瀵归綈锛?2瀛楁锛夈€佸垱寤烘棩鏈熷閿欎笌鍥炴樉銆佹墿灞曞瓧娈垫竻绌恒€佸墠绔煡璇笌鍏ㄥ瓧娈靛睍绀?| 鐘舵€侊細鉁?| 鏂囦欢锛歁iniAppAdapterService.java銆丒quipmentList.vue銆丒quipmentForm.vue銆丄piAdapterIntegrationTest.java銆丄piIntegrationTest.java | 鍛戒护锛歚mvn "-Dtest=ApiAdapterIntegrationTest,ApiIntegrationTest,XdmRuntimeServiceTest" test`銆乣npm run build`銆丮CP楠屾敹锛?equipments CRUD锛?- 2026-03-13 | Equipment `productionDate` 鍥炴樉鏀逛负鍙寔涔呭寲锛堥噸鍚悗涓嶄涪澶憋級 | 鐘舵€侊細鉁?| 鏂囦欢锛欵quipmentProductionDateStore.java銆丮iniAppAdapterService.java銆丄PI_DOCS/frontend_api_requirements.json銆丄PI_DOCS/miniapp-backend.json | 鍛戒护锛歚mvn "-Dtest=ApiAdapterIntegrationTest,ApiIntegrationTest,XdmRuntimeServiceTest" test`銆佹墜宸ラ噸鍚┛閫忥紙create->restart->get锛?- 2026-03-13 | Part 蹇呭～瀛楁濂戠害鏀舵暃锛坧artNo/partName/specification/stockQty/supplier锛? 鏇存柊鍚堝苟鏍￠獙 | 鐘舵€侊細鉁?| 鏂囦欢锛歁iniAppAdapterService.java銆丳artForm.vue銆丄piAdapterIntegrationTest.java銆丄PI_DOCS/API_SPECIFICATION.md銆丄PI_DOCS/frontend_api_requirements.json銆丄PI_DOCS/api_doc.json | 鍛戒护锛歚mvn test`銆乣npm run build`銆丮CP sub-agent 楠屾敹锛?parts CRUD銆佸垎绫荤鐞嗐€佺増鏈鐞嗭級
- 2026-03-13 | Procedures 椤甸潰涓?WorkingProcedure 瀛楁鍙ｅ緞淇锛堣ˉ榻?蹇呴渶瀛楁灞曠ず銆佺紪杈戞牎楠屻€佽澶囧叧绯昏仛鍚堛€佹椂闂存牸寮忓吋瀹癸級 | 鐘舵€侊細鉁?| 鏂囦欢锛歁iniAppAdapterService.java銆丳rocedureList.vue銆丄PI_SPECIFICATION.md銆乫rontend_api_requirements.json銆乸roject_structure.md | 鍛戒护锛歚mvn -q -DskipTests compile`銆乣npm run build`銆丮CP sub-agent 楠屾敹锛?procedures 椤甸潰鍔犺浇/缂栬緫/蹇呭～鏍￠獙/淇濆瓨鍥炴樉锛?- 2026-03-13 | 鐗╂枡鍒楄〃鍒嗙被绛涢€夋敮鎸佹爲褰㈠睍寮€涓庣埗鍒嗙被鍚瓙鍒嗙被杩囨护锛坈ategoryIds锛夛紝骞朵繚鐣欏垪琛ㄩ〉鍒嗙被灞曞紑/閫変腑鐘舵€?| 鐘舵€侊細鈿狅笍锛堝緟MCP鍔熻兘娴嬭瘯瀹屾垚鍚庤浆鉁咃級 | 鏂囦欢锛歅artList.vue銆丳artController.java銆丮iniAppAdapterService.java銆丄PI_SPECIFICATION.md銆乫rontend_api_requirements.json銆乸roject_structure.md | 鍛戒护锛歮vn -q "-Dtest=ApiIntegrationTest" test銆乶pm run build銆丮CP sub-agent 鍔熻兘娴嬭瘯锛堝垎绫诲睍寮€/绛涢€?鐘舵€佷繚鐣欙級
- 2026-03-13 | 物料列表分类树筛选终验（MCP Playwright）：一级展开子分类、父类携带categoryIds筛选、返回后状态保留 | 状态：✅ | 文件：PartList.vue、PartController.java、MiniAppAdapterService.java | 证据：GET /api/parts?keyword=&categoryId=1001&categoryIds=1001,1002,1003&page=1&size=20
