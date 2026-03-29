# xDM-F API 调用成功证据

- 生成时间: 2026-03-13 20:34:58
- 证据日志: xdm.log
- 已匹配 200: 28
- 未匹配样本: 4

## 已匹配 200

| 序号 | 接口 | 方法 | 相对路径 | 日志时间 | 行号 |
|---|---|---|---|---|---|
| 1 | health | GET | /v1/health | 2026-03-13T20:33:13.989+08:00 | 10359 |
| 2 | login verifycode | GET | /v1/login/verifycode | 2026-03-13T16:56:48.784+08:00 | 3065 |
| 3 | login | POST | /v1/login/login | 2026-03-13T16:56:49.082+08:00 | 3075 |
| 4 | me | GET | /rdm/basic/api/rest/me | 2026-03-13T20:33:14.041+08:00 | 10360 |
| 5 | equipment list | POST | /dynamic/api/Equipment/list | 2026-03-13T17:31:40.498+08:00 | 4394 |
| 6 | equipment get | POST | /dynamic/api/Equipment/get | 2026-03-13T20:33:33.761+08:00 | 10366 |
| 7 | equipment create | POST | /dynamic/api/Equipment/create | 2026-03-13T18:28:40.016+08:00 | 6057 |
| 9 | equipment delete | POST | /dynamic/api/Equipment/delete | 2026-03-13T18:13:29.441+08:00 | 5620 |
| 10 | part list | POST | /dynamic/api/Part/list | 2026-03-13T17:19:48.818+08:00 | 4133 |
| 11 | part get | POST | /dynamic/api/Part/get | 2026-03-13T17:22:23.189+08:00 | 4204 |
| 12 | part create | POST | /dynamic/api/Part/create | 2026-03-13T17:22:07.344+08:00 | 4192 |
| 13 | part update | POST | /dynamic/api/Part/update | 2026-03-13T17:22:35.975+08:00 | 4210 |
| 14 | part delete | POST | /dynamic/api/Part/delete | 2026-03-13T17:23:32.518+08:00 | 4242 |
| 15 | part-part list | POST | /dynamic/api/Part_Part/list | 2026-03-13T18:06:45.288+08:00 | 5326 |
| 16 | part-part create | POST | /dynamic/api/Part_Part/create | 2026-03-13T18:07:26.923+08:00 | 5338 |
| 19 | procedure list | POST | /dynamic/api/WorkingProcedure/list | 2026-03-13T17:31:25.435+08:00 | 4385 |
| 20 | procedure get | POST | /dynamic/api/WorkingProcedure/get | 2026-03-13T18:56:30.679+08:00 | 8021 |
| 21 | procedure create | POST | /dynamic/api/WorkingProcedure/create | 2026-03-13T18:56:30.711+08:00 | 8024 |
| 22 | procedure update | POST | /dynamic/api/WorkingProcedure/update | 2026-03-13T17:49:23.196+08:00 | 4612 |
| 23 | procedure-equipment list | POST | /dynamic/api/WorkingProcedure_Equipment/list | 2026-03-13T17:50:13.005+08:00 | 4635 |
| 24 | workingplan list | POST | /dynamic/api/WorkingPlan/list | 2026-03-13T16:56:49.253+08:00 | 3078 |
| 25 | workingplan get | POST | /dynamic/api/WorkingPlan/get | 2026-03-13T17:07:40.821+08:00 | 3530 |
| 26 | workingplan create | POST | /dynamic/api/WorkingPlan/create | 2026-03-13T17:06:45.725+08:00 | 3334 |
| 27 | workingplan update | POST | /dynamic/api/WorkingPlan/update | 2026-03-13T17:07:40.861+08:00 | 3702 |
| 28 | workingplan delete | POST | /dynamic/api/WorkingPlan/delete | 2026-03-13T17:07:41.118+08:00 | 3713 |
| 29 | plan-procedure list | POST | /dynamic/api/WorkingPlan_WorkingProcedure/list | 2026-03-13T17:09:53.226+08:00 | 4092 |
| 30 | plan-procedure create | POST | /dynamic/api/WorkingPlan_WorkingProcedure/create | 2026-03-13T18:57:13.444+08:00 | 8048 |
| 32 | plan-procedure delete | POST | /dynamic/api/WorkingPlan_WorkingProcedure/delete | 2026-03-13T18:57:24.653+08:00 | 8062 |

## 未匹配样本

| 序号 | 接口 | 方法 | 相对路径 |
|---|---|---|---|
| 8 | equipment update | POST | /dynamic/api/Equipment/update |
| 17 | part-part update | POST | /dynamic/api/Part_Part/update |
| 18 | part-part delete | POST | /dynamic/api/Part_Part/delete |
| 31 | plan-procedure update | POST | /dynamic/api/WorkingPlan_WorkingProcedure/update |
