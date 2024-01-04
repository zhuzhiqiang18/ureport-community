import axios from 'axios'
import { MessageBox, Message } from 'element-ui'
import store from '@/store'
import { getToken } from '@/utils/auth'
import route from '@/router/index'

// create an axios instance
const service = axios.create({
  baseURL: process.env.VUE_APP_BASE_API, // url = base url + request url
  // withCredentials: true, // send cookies when cross-domain requests
  timeout: 200000 // request timeout
})

// request interceptor
service.interceptors.request.use(
  config => {
    // do something before request is sent

    if (store.getters.token) {
      // let each request carry token
      // ['X-Token'] is a custom headers key
      // please modify it according to the actual situation
      config.headers['X-Token'] = getToken()
    }
    return config
  },
  error => {
    // do something with request error
    console.log(error) // for debug
    return Promise.reject(error)
  }
)

// response interceptor
service.interceptors.response.use(
  /**
   * If you want to get http information such as headers or status
   * Please return  response => response
  */

  /**
   * Determine the request status by custom code
   * Here is just an example
   * You can also judge the status by HTTP Status Code
   */
  response => {
    if(response.status === 200 && download(response)) {
      return {}
    }
    if(response.status === 200 && print(response)) {
      return {}
    }
    const res = response.data
    // if the custom code is not 20000, it is judged as an error.
    if (res.code !== 20000) {
      Message({
        message: res.message || 'Error',
        type: 'error',
        duration: 5 * 1000
      })
      // 50008: Illegal token; 50012: Other clients logged in; 50014: Token expired;
      if (res.code === 50008 || res.code === 50012 || res.code === 50014) {
        // to re-login
        MessageBox.confirm('You have been logged out, you can cancel to stay on this page, or log in again', 'Confirm logout', {
          confirmButtonText: 'Re-Login',
          cancelButtonText: 'Cancel',
          type: 'warning'
        }).then(() => {
          store.dispatch('user/resetToken').then(() => {
            location.reload()
          })
        })
      }
      if (res.code === 401 ) {
        route.replace({ path: "/401" })
        return {}
      }
      return Promise.reject(new Error(res.message || 'Error'))
    } else {
      return res
    }
  },
  error => {
    console.log('err' + error) // for debug
    Message({
      message: error.message,
      type: 'error',
      duration: 5 * 1000
    })
    return Promise.reject(error)
  }
)

function print(response) {
  // 判断后端是否返回文件流，若是则下载文件
  const headers = response.headers
  if(headers && headers["content-type"] && headers["content-type"].indexOf('application/pdf') > -1) {// 判断返回文件流
    const url = window.URL.createObjectURL(new Blob([response.data],{'type':'application/pdf'}))
    let iframe = document.getElementById("print_preview")
    if(iframe === null) {
      let iframe = document.createElement("iframe")
      document.body.appendChild(iframe)
      iframe.setAttribute('id', "print_preview")
      iframe.setAttribute('style', 'position:absolute;width:0px;height:0px;left:0px;top:0px;');
      iframe.setAttribute('src', url)
      iframe.onload = function () {
        iframe.contentWindow.print()
      }
    } else {
      iframe.setAttribute('src', url)
      iframe.onload = function () {
        iframe.contentWindow.print()
      }
    }
    return true
  }
  return false
}

function download(response) {
  // 判断后端是否返回文件流，若是则下载文件
  const headers = response.headers
  if(headers && headers["content-type"] && headers["content-type"].indexOf('application/octet-stream') > -1) {// 判断返回文件流
    const blob = new Blob([response.data])
    const link = document.createElement("a")
    link.download = decodeURI(headers["content-disposition"].replace(/.*filename=/, ""))
    link.style.display = "none"
    link.href = URL.createObjectURL(blob)
    document.body.appendChild(link)
    link.click()
    URL.revokeObjectURL(link.href)
    document.body.removeChild(link)
    return true
  }
  return false
}

export default service
