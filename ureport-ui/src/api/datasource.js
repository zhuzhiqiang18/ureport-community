import request from '@/utils/request'

export function checkConnection(params) {
  return request({
    url: '/datasource/test',
    method: 'post',
    data:params
  })
}

export function addDruid(params) {
  return request({
    url: '/datasource/add',
    method: 'post',
    data:params
  })
}

export function editDruid(params) {
  return request({
    url: '/datasource/update',
    method: 'post',
    data:params
  })
}

export function removeDruid(params) {
  return request({
    url: '/datasource/delete',
    method: 'post',
    params:params
  })
}

export function selectDruidByPrimaryKey(params) {
  return request({
    url: '/datasource/select',
    method: 'post',
    params:params
  })
}

export function selectBuildinDruidList() {
  return request({
    url: '/datasource/selectBuildinDruidList',
    method: 'post',
  })
}

export function selectDruidTableList(params) {
  return request({
    url: '/datasource/tableList',
    method: 'post',
    data:params
  })
}

export function selectDruidTableFieldList(params) {
  return request({
    url: '/datasource/tableFieldList',
    method: 'post',
    data:params
  })
}


export function tableDataList(params) {
  return request({
    url: '/datasource/tableDataList',
    method: 'post',
    params:params
  })
}

export function previewData(params) {
  return request({
    url: '/datasource/preview',
    method: 'post',
    data:params
  })
}

export function testConnection(params) {
  return request({
    url: '/datasource/testConnection',
    method: 'post',
    data:params
  })
}

export function previewApiData(params) {
  return request({
    url: '/datasource/api',
    method: 'post',
    data:params
  })
}
