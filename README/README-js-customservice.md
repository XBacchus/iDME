# 服务编排开发指导

## Javascript服务编排-配置
xdm提供了两种执行js代码的方式；
一种传统的将入参写入编排代码中，交由NashornSandbox执行，是原来的逻辑。\
用户脚本中的{#param}参数，参与计算过程，会在js执行完成后，将结果中没有{#param}形式的内容。
> 配置：xdm.runtime.custom.param-process-strategy=legacy

一种是将编排代码进行预编译，用户入参作为沙箱入参进行执行，性能大约能提升一倍。\
用户脚本中的{#param}参数，不参与计算过程，会在js执行完成后，将结果中的{#param}替换成参数值。
> 配置：xdm.runtime.custom.param-process-strategy=template


## Javascript服务编排-最佳实践
```javascript
// 定义参数，在沙箱执行时会进行替换，根据参数处理策略不同，替换方式不同
var request = "";

// 定义sql变量，用于最终的数据查询
var sql = "";

// 如果选择查询类型1
if (request.prog == "Type1") {
    sql += " select * from xdm_tenant ";
}

// 如果选择查询类型2
if (request.prog == "Type2") {
    sql += " select * from xdm_typedefinition ";
}

sql += " where 1=1 ";

// 如果传了name，拼接查询条件
if (request.name) {
    sql += " and name = '{#name}' ";
}

// 为了适配template参数处理策略，建议不要让{#param}参与js计算过程，例如
// var name = '{#name}'
// if (name == 'xxx') {}

// 为了使全量数据服务识别到入参，可以定义变量而不使用
var prog = '{#prog}';


```