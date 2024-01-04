<template>
  <div style="width: 100%;height: 100%;position: absolute;">
    <iframe v-if="url" :src="url" id="iframe" style="width: 100%;height: 100%;border: 0px;" />
  </div>
</template>

<script>
import axios from 'axios'
import { getSign } from '@/api/report'
import { getToken } from '@/utils/auth' // get token from cookie

export default {
  data() {
    return {
      url: ''
    }
  },
  mounted: function() {
    this.url = "http://localhost:9528/show/test1.ureport.xml?X-Token=" + getToken()
  },
  methods: {
    // src="http://192.16.9.237:9000/login"
    async init() {
      const form = {
        params: {
          fileName: 'test1.ureport.xml',
        },
        query: {
          doctor: '001',
          dept: '心内科',
          start: '2019-01-31'
        }
      }
      const res = await getSign(form)
      const data = res.data || {}
      axios({
        method: 'post',
        url: 'http://192.16.8.238:9000/report/show/test1.ureport.xml',
        headers: {
          Sign: data.sign
        },
        data: data
      }).then((res) => {
        var iframe = document.querySelector('#iframe')
        iframe.contentWindow.document.open()
        iframe.contentWindow.document.write(res.data)
        iframe.contentWindow.document.close()
      })
    }
  }
}
</script>

<style>
</style>
