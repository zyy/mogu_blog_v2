import request from '@/utils/request'

export function getQuestionList(params) {
  return request({
    url: process.env.ADMIN_API + '/question/getList',
    method: 'post',
    data: params
  })
}

export function addQuestion(params) {
  return request({
    url: process.env.ADMIN_API + '/question/add',
    method: 'post',
    data: params
  })
}

export function editQuestion(params) {
  return request({
    url: process.env.ADMIN_API + '/question/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchQuestion(params) {
  return request({
    url: process.env.ADMIN_API + '/question/deleteBatch',
    method: 'post',
    data: params
  })
}
