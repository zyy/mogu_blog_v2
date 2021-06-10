import request from '@/utils/request'

export function getMe (params) {
  return request({
    url: process.env.WEB_API + '/about/getMe',
    method: 'get',
    params
  })
}

export function getContact (params) {
    return request({
      url: process.env.WEB_API + '/about/getContact',
      method: 'get',
      params
    })
}

export function getBlogListByUser (params) {
  return request({
    url: process.env.WEB_API + '/about/getBlogListByUser',
    method: 'post',
    data: params
  })
}

export function getQuestionListByUser (params) {
  return request({
    url: process.env.WEB_API + '/about/getQuestionListByUser',
    method: 'post',
    data: params
  })
}

export function getUserByUid (params) {
  return request({
    url: process.env.WEB_API + '/about/getUserByUid',
    method: 'get',
    params
  })
}

