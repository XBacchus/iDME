# xDM-F API 调用列表

- 生成时间: 2026-03-13 20:32:32
- 接口总数: 32
- 运行态前缀: http://127.0.0.1:8003/rdm_497bc30622bb434c9096c34d1ba38f02_app/services

| 序号 | 接口 | 方法 | 相对路径 | 模块 | 用途 |
|---|---|---|---|---|---|
| 1 | health | GET | /v1/health | XdmGatewayService | runtime health check |
| 2 | login verifycode | GET | /v1/login/verifycode | XdmGatewayService | bootstrap session cookie/xsrf |
| 3 | login | POST | /v1/login/login | XdmGatewayService | create xDM session |
| 4 | me | GET | /rdm/basic/api/rest/me | XdmGatewayService | current user profile |
| 5 | equipment list | POST | /dynamic/api/Equipment/list | equipment | query equipment list |
| 6 | equipment get | POST | /dynamic/api/Equipment/get | equipment | query equipment detail |
| 7 | equipment create | POST | /dynamic/api/Equipment/create | equipment | create equipment |
| 8 | equipment update | POST | /dynamic/api/Equipment/update | equipment | update equipment |
| 9 | equipment delete | POST | /dynamic/api/Equipment/delete | equipment | delete equipment |
| 10 | part list | POST | /dynamic/api/Part/list | part | query/search part |
| 11 | part get | POST | /dynamic/api/Part/get | part | query part detail |
| 12 | part create | POST | /dynamic/api/Part/create | part | create part |
| 13 | part update | POST | /dynamic/api/Part/update | part | update part |
| 14 | part delete | POST | /dynamic/api/Part/delete | part | delete part |
| 15 | part-part list | POST | /dynamic/api/Part_Part/list | bom | query BOM relations |
| 16 | part-part create | POST | /dynamic/api/Part_Part/create | bom | create BOM relation |
| 17 | part-part update | POST | /dynamic/api/Part_Part/update | bom | update BOM relation (reserved) |
| 18 | part-part delete | POST | /dynamic/api/Part_Part/delete | bom | delete BOM relation |
| 19 | procedure list | POST | /dynamic/api/WorkingProcedure/list | procedure | query procedures |
| 20 | procedure get | POST | /dynamic/api/WorkingProcedure/get | procedure | query procedure detail |
| 21 | procedure create | POST | /dynamic/api/WorkingProcedure/create | procedure | create procedure |
| 22 | procedure update | POST | /dynamic/api/WorkingProcedure/update | procedure | update procedure |
| 23 | procedure-equipment list | POST | /dynamic/api/WorkingProcedure_Equipment/list | procedure | query procedure-equipment relation |
| 24 | workingplan list | POST | /dynamic/api/WorkingPlan/list | workingplan | query working plan |
| 25 | workingplan get | POST | /dynamic/api/WorkingPlan/get | workingplan | query working plan detail |
| 26 | workingplan create | POST | /dynamic/api/WorkingPlan/create | workingplan | create working plan |
| 27 | workingplan update | POST | /dynamic/api/WorkingPlan/update | workingplan | update working plan |
| 28 | workingplan delete | POST | /dynamic/api/WorkingPlan/delete | workingplan | delete working plan |
| 29 | plan-procedure list | POST | /dynamic/api/WorkingPlan_WorkingProcedure/list | workingplan | query plan-procedure relation |
| 30 | plan-procedure create | POST | /dynamic/api/WorkingPlan_WorkingProcedure/create | workingplan | create plan-procedure relation |
| 31 | plan-procedure update | POST | /dynamic/api/WorkingPlan_WorkingProcedure/update | workingplan | update plan-procedure relation (reserved) |
| 32 | plan-procedure delete | POST | /dynamic/api/WorkingPlan_WorkingProcedure/delete | workingplan | delete plan-procedure relation |
