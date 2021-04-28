import request from '@/utils/request'

export function getQuestionTagList(params) {
  return request({
    url: process.env.ADMIN_API + '/questionTag/getList',
    method: 'post',
    data: params
  })
}

export function addQuestionTag(params) {
  return request({
    url: process.env.ADMIN_API + '/questionTag/add',
    method: 'post',
    data: params
  })
}

export function editQuestionTag(params) {
  return request({
    url: process.env.ADMIN_API + '/questionTag/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchQuestionTag(params) {
  return request({
    url: process.env.ADMIN_API + '/questionTag/deleteBatch',
    method: 'post',
    data: params
  })
}
