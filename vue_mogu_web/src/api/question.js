import request from '@/utils/request'

export function getQuestionList(params) {
  return request({
    url: process.env.WEB_API + '/question/getList',
    method: 'post',
    data: params
  })
}

export function getQuestionTagList(params) {
  return request({
    url: process.env.WEB_API + '/question/getTagList',
    method: 'post',
    data: params
  })
}

export function getQuestionTemplateList(params) {
  return request({
    url: process.env.WEB_API + '/question/getTemplateList',
    method: 'post',
    data: params
  })
}

export function getQuestion(params) {
  return request({
    url: process.env.WEB_API + '/question/getQuestion',
    method: 'post',
    data: params
  })
}

export function addQuestion(params) {
  return request({
    url: process.env.WEB_API + '/question/add',
    method: 'post',
    data: params
  })
}

export function editQuestion(params) {
  return request({
    url: process.env.WEB_API + '/question/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchQuestion(params) {
  return request({
    url: process.env.WEB_API + '/question/deleteBatch',
    method: 'post',
    data: params
  })
}
