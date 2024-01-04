import request from '@/utils/request'

export function addRole(params) {
  return request({
    url: '/role/add',
    method: 'post',
    data: params
  })
}

export function removeRole(params) {
  return request({
    url: '/role/remove',
    method: 'post',
    params
  })
}

export function modifyRole(params) {
  return request({
    url: '/role/modify',
    method: 'post',
    data: params
  })
}

export function getRole(params) {
  return request({
    url: '/role/get',
    method: 'post',
    params
  })
}

export function getRoleList(params) {
  return request({
    url: '/role/list',
    method: 'post',
    data: params
  })
}