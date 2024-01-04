import request from '@/utils/request'

export function getFileData(params) {
  return request({
    url: '/designer/get',
    method: 'get',
    params
  })
}

export function scriptValidation(params) {
  params.content = encodeURIComponent(params.content)
  return request({
    url: '/designer/scriptValidation',
    method: 'post',
    params
  })
}

export function parseDataSet(params) {
  return request({
    url: '/designer/parseDataSet',
    method: 'post',
    params
  })
}

export function saveReportFile(params) {
  return request({
    url: '/designer/save',
    method: 'post',
    data:params
  })
}

export function getReports(params) {
  return request({
    url: '/designer/getList',
    method: 'post',
    params
  })
}

export function deleteReportFile(params) {
  return request({
    url: '/designer/remove',
    method: 'post',
    params
  })
}

export function uploadExcelTemplate(params) {
  return request({
    url: '/designer/import/excel',
    headers: {'Content-Type': 'multipart/form-data',},
    method: 'post',
    data:params
  })
}

export function uploadImageTemplate(params) {
  return request({
    url: '/designer/import/image',
    headers: {'Content-Type': 'multipart/form-data',},
    method: 'post',
    data:params
  })
}

export function getImages(params) {
  return request({
    url: '/designer/image/files',
    method: 'post',
    data:params
  })
}
