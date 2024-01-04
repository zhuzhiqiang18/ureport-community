import request from '@/utils/request'

export function loadData(params,token) {
  return request({
    url: '/ureport/loadData',
    method: 'post',
    headers: {'X-Token': token},
    data:params
  })
}

export function downloadWord(params,token) {
  return request({
    url: '/ureport/download/word',
    method: 'post',
    headers: {'X-Token': token},
    responseType: 'arraybuffer',
    data:params
  })
}

export function downloadExcel(params,token) {
  return request({
    url: '/ureport/download/excel',
    method: 'post',
    headers: {'X-Token': token},
    responseType: 'arraybuffer',
    data:params
  })
}

export function downloadPDF(params,token) {
  return request({
    url: '/ureport/download/pdf',
    method: 'post',
    headers: {'X-Token': token},
    responseType: 'arraybuffer',
    data:params
  })
}

export function print(params,token) {
  return request({
    url: '/ureport/print',
    method: 'post',
    headers: {'X-Token': token},
    responseType: 'arraybuffer',
    data:params
  })
}

export function refreshSearchForm(params,token) {
  return request({
    url: '/ureport/refreshForm',
    method: 'post',
    headers: {'X-Token': token},
    data:params
  })
}
