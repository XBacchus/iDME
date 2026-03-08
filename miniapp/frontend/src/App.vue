<script setup>
import { ref } from 'vue'
import axios from 'axios'

const backendHealth = ref(null)
const xdmHealth = ref(null)
const xdmMe = ref(null)
const proxyPath = ref('/rdm/basic/api/configs')
const proxyResult = ref(null)
const loading = ref(false)
const error = ref('')

const format = (obj) => JSON.stringify(obj, null, 2)

async function request(fn) {
  loading.value = true
  error.value = ''
  try {
    await fn()
  } catch (e) {
    error.value = e?.response?.data ? format(e.response.data) : (e.message || '请求失败')
  } finally {
    loading.value = false
  }
}

function checkBackend() {
  return request(async () => {
    const { data } = await axios.get('/api/health')
    backendHealth.value = data
  })
}

function checkXdm() {
  return request(async () => {
    const { data } = await axios.get('/api/xdm/health')
    xdmHealth.value = data
  })
}

function checkMe() {
  return request(async () => {
    const { data } = await axios.get('/api/xdm/me')
    xdmMe.value = data
  })
}

function callProxy() {
  return request(async () => {
    const { data } = await axios.post('/api/xdm/proxy', {
      path: proxyPath.value,
      method: 'GET'
    })
    proxyResult.value = data
  })
}
</script>

<template>
  <main class="page">
    <header class="hero">
      <h1>iDME MiniApp 工程骨架</h1>
      <p>前端: Vue3 + Vite，后端: Spring Boot 3.0 + JDK17，数据层: xDM-F API</p>
    </header>

    <section class="actions">
      <button :disabled="loading" @click="checkBackend">检查后端健康</button>
      <button :disabled="loading" @click="checkXdm">检查 xDM 健康</button>
      <button :disabled="loading" @click="checkMe">验证 xDM 会话</button>
    </section>

    <section class="proxy">
      <label for="proxyPath">xDM API 路径</label>
      <input id="proxyPath" v-model="proxyPath" placeholder="/rdm/basic/api/configs" />
      <button :disabled="loading" @click="callProxy">调用代理接口</button>
    </section>

    <section v-if="error" class="panel error">
      <h2>错误信息</h2>
      <pre>{{ error }}</pre>
    </section>

    <section v-if="backendHealth" class="panel">
      <h2>后端健康</h2>
      <pre>{{ format(backendHealth) }}</pre>
    </section>

    <section v-if="xdmHealth" class="panel">
      <h2>xDM 健康</h2>
      <pre>{{ format(xdmHealth) }}</pre>
    </section>

    <section v-if="xdmMe" class="panel">
      <h2>xDM 当前用户</h2>
      <pre>{{ format(xdmMe) }}</pre>
    </section>

    <section v-if="proxyResult" class="panel">
      <h2>代理结果</h2>
      <pre>{{ format(proxyResult) }}</pre>
    </section>
  </main>
</template>
