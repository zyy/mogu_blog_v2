import request from '@/utils/request'

export function getUserBlogList (params) {
  return request({
    url: process.env.WEB_API + '/createBlog/getUserBlogList',
    method: 'post',
    data: params
  })
}

export function addBlog (params) {
  return request({
    url: process.env.WEB_API + '/createBlog/add',
    method: 'post',
    data: params
  })
}

export function editBlog (params) {
  return request({
    url: process.env.WEB_API + '/createBlog/edit',
    method: 'post',
    data: params
  })
}

export function deleteBlog (params) {
  return request({
    url: process.env.WEB_API + '/createBlog/delete',
    method: 'post',
    data: params
  })
}

export function getBlogSortList (params) {
  return request({
    url: process.env.WEB_API + '/createBlog/getBlogSortList',
    method: 'post',
    data: params
  })
}

export function getBlogTagList (params) {
  return request({
    url: process.env.WEB_API + '/createBlog/getBlogTagList',
    method: 'post',
    data: params
  })
}
