'use strict'
const merge = require('webpack-merge')
const prodEnv = require('./prod.env')

module.exports = merge(prodEnv, {
  NODE_ENV: '"development"',

  //开发环境
  ADMIN_API: '"http://101.132.194.128:8607/mogu-admin"',
  PICTURE_API: '"http://101.132.194.128:8607/mogu-picture"',
  WEB_API: '"http://101.132.194.128:8607/mogu-web"',
  Search_API: '"http://101.132.194.128:8607/mogu-search"',
  Spider_API: '"http://101.132.194.128:8607/mogu-spider"',
  FILE_API: '"http://101.132.194.128:8600/"',
  BLOG_WEB_URL: '"http://101.132.194.128:9527"',
  SOLR_API: '"http://101.132.194.128:8080/solr"',
  ELASTIC_SEARCH: '"http://101.132.194.128:5601"',
})
