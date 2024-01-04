import request from '@/utils/request'

export function digest(data) {
  return request({
    url: '/sys/user/digest',
    method: 'post',
  })
}

export function login(data) {
  return request({
    url: '/sys/user/login',
    method: 'post',
    headers: {'Authorization': data},
  })
}

export function logout(data) {
  return request({
    url: '/sys/user/logout',
    method: 'post',
  })
}

export function getInfo(token) {
  return request({
    url: '/sys/user/info',
    method: 'get',
    params: { token }
  })
}

export function addUser(params) {
  return request({
    url: '/sys/user/add',
    method: 'post',
    data: params
  })
}

export function removeUser(params) {
  return request({
    url: '/sys/user/remove',
    method: 'post',
    params
  })
}

export function modifyUser(params) {
  return request({
    url: '/sys/user/modify',
    method: 'post',
    data: params
  })
}

export function getUser(params) {
  return request({
    url: '/sys/user/get',
    method: 'post',
    params
  })
}

export function getUserList(params) {
  return request({
    url: '/sys/user/list',
    method: 'post',
    data: params
  })
}
