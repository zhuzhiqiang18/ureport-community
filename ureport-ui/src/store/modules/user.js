import { digest, login, logout, getInfo } from '@/api/user'
import { getToken, setToken, removeToken } from '@/utils/auth'
import { resetRouter } from '@/router'
import md5 from 'js-md5'
import uuid from 'node-uuid'

const getDefaultState = () => {
  return {
    token: getToken(),
    name: '',
    avatar: '',
    menus: [],
  }
}

const state = getDefaultState()

const mutations = {
  RESET_STATE: (state) => {
    Object.assign(state, getDefaultState())
  },
  SET_TOKEN: (state, token) => {
    state.token = token
  },
  SET_NAME: (state, name) => {
    state.name = name
  },
  SET_AVATAR: (state, avatar) => {
    state.avatar = avatar
  },
  SET_MENUS: (state, menus) => {
    state.menus = menus
  }
}

const actions = {
  // user login
  async login({ commit }, userInfo) {
    const { username, password } = userInfo
    const res = await digest()
    const nonce = res.data || ''
    const nc = '000001'
    const cnonce = uuid.v1()
    const name = username.trim()
    const response = md5(md5(name + password) + nonce + nc + cnonce)
    const authorization = `Digest username=${name},nonce=${nonce},nc=${nc},cnonce=${cnonce},response=${response}`
    return new Promise((resolve, reject) => {
      login(authorization).then(response => {
        const { data } = response
        commit('SET_TOKEN', data.token)
        setToken(data.token)
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // get user info
  getInfo({ commit, state }) {
    return new Promise((resolve, reject) => {
      getInfo(state.token).then(response => {
        const { data } = response

        if (!data) {
          return reject('Verification failed, please Login again.')
        }

        const { name, avatar } = data
        const menus = []
        const menuList = data.menus || []
        // const mapping = {}
        // for(const index in menuList){
        //   const item = menuList[index]
        //   let component = item.component
        //   if (item.menuType === 0) {
        //     component = 'Layout'
        //   }
        //   if(item.url && item.url.indexOf('?') > -1){
        //     const arr = item.url.split('?')
        //     item.url = arr[0]
        //     const params = arr[1].split("&")
        //     item.params = {}
        //     for(const m in params) {
        //       const p = params[m].split("=")
        //       item.params[p[0]] = p[1]
        //     }
        //   }
        //   mapping[item.menuId] = {
        //     path: item.url,
        //     name: 'Name' + item.menuId,
        //     component: component,
        //     hidden: item.visible === 0,
        //     meta: {
        //       title: item.menuName,
        //       noCache: true,
        //       params: item.params
        //     }
        //   }
        //   if(item.icon) {
        //     mapping[item.menuId].meta.icon = item.icon
        //   }
        // }
        // for(const index in menuList){
        //   const item = menuList[index]
        //   const parent = mapping[item.parentId]
        //   const child = mapping[item.menuId]
        //   if (item.parentId === 0) {
        //     menus.push(child)
        //   } else {
        //     let children = parent.children
        //     if (children == null) {
        //       children = []
        //       parent.children = children
        //     }
        //     children.push(child)
        //   }
        // }
        // menus.push({
        //   path: "/404",
        //   component: "404",
        //   hidden: true
        // }, {
        //   path: "*",
        //   redirect: "/404",
        //   hidden: true
        // })
        commit('SET_NAME', name)
        commit('SET_AVATAR', avatar)
        commit("SET_MENUS", menus)
        resolve(data)
      }).catch(error => {
        reject(error)
      })
    })
  },

  // user logout
  logout({ commit, state }) {
    return new Promise((resolve, reject) => {
      logout(state.token).then(() => {
        global.antRouter = []
        removeToken() // must remove  token  first
        resetRouter()
        commit('RESET_STATE')
        resolve()
      }).catch(error => {
        reject(error)
      })
    })
  },

  // remove token
  resetToken({ commit }) {
    return new Promise(resolve => {
      removeToken() // must remove  token  first
      commit('RESET_STATE')
      resolve()
    })
  }
}

export default {
  namespaced: true,
  state,
  mutations,
  actions
}
