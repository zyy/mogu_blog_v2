import request from '@/utils/request'

export function getQuestionTemplateList(params) {
  return request({
    url: process.env.ADMIN_API + '/questionTemplate/getList',
    method: 'post',
    data: params
  })
}

export function addQuestionTemplate(params) {
  return request({
    url: process.env.ADMIN_API + '/questionTemplate/add',
    method: 'post',
    data: params
  })
}

export function editQuestionTemplate(params) {
  return request({
    url: process.env.ADMIN_API + '/questionTemplate/edit',
    method: 'post',
    data: params
  })
}

export function deleteBatchQuestionTemplate(params) {
  return request({
    url: process.env.ADMIN_API + '/questionTemplate/deleteBatch',
    method: 'post',
    data: params
  })
}
