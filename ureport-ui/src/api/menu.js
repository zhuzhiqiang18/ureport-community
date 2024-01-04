import request from '@/utils/request'

export function addMenu(params) {
  return request({
    url: '/menu/add',
    method: 'post',
    data: params
  })
}

export function remove(params) {
  return request({
    url: '/menu/remove',
    method: 'post',
    params
  })
}

export function modify(params) {
  return request({
    url: '/menu/modify',
    method: 'post',
    data: params
  })
}

export function get(params) {
  return request({
    url: '/menu/get',
    method: 'post',
    params
  })
}

export function getMenuList(params) {
  return request({
    url: '/menu/list',
    method: 'post',
    data: params
  })
}