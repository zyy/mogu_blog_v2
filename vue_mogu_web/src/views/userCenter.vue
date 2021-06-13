<template>
  <div>
    <article>
      <div style="min-height: 1000px">

        <el-row :gutter="24" type="flex" justify="center">

          <el-col :span="6" style="text-align: center">
            <div>14</div>
            <div>文章</div>
          </el-col>

          <el-col :span="6" style="text-align: center">
            <el-row>
              <div>1</div>
              <div>问答</div>
            </el-row>
          </el-col>

          <el-col :span="6" style="text-align: center">
            <el-row>
              <div>1</div>
              <div>关注</div>
            </el-row>
          </el-col>

          <el-col :span="6" style="text-align: center">
            <el-row>
              <div>1</div>
              <div>粉丝</div>
            </el-row>
          </el-col>

        </el-row>

        <el-row :gutter="24" class="userInfo">
          <el-col style="text-align: center; margin-top: 5px">
            <el-avatar :size="80" :src="userInfo.photoUrl" @error="errorHandler">
              <img src="https://cube.elemecdn.com/e/fd/0fc7d20532fdaf769a25683617711png.png"/>
            </el-avatar>
            <div class="nickname">{{ userInfo.nickName }}</div>
            <div class="gender">
              <span v-if="userInfo.gender == '1'">男</span>
              <span v-if="userInfo.gender == '2'">女</span>
              <span v-if="userInfo.occupation"> | {{userInfo.occupation}}</span>
            </div>
            <div class="summary">{{userInfo.summary}}</div>
            <div class="love">
              <el-button type="danger" icon="el-icon-view">关注</el-button>
            </div>

          </el-col>
        </el-row>


        <el-tabs class="userEvent" v-model="activeName" @tab-click="handleClick" style="margin-top: 5px">
          <el-tab-pane name="1" label="博客">
            <span :class="activeName==1?'tab-pane-active':'tab-pane'" slot="label" ><i class="el-icon-collection-tag"></i> <span>博客</span></span>

            <el-tabs class="userEvent2" v-model="activeName2" @tab-click="handleClick2">
              <el-tab-pane label="最新博客" name="1"></el-tab-pane>
              <el-tab-pane label="最热博客" name="2"></el-tab-pane>
            </el-tabs>

            <div style="min-height: 773px">
              <div
                v-for="item in listData"
                :key="item.uid"
                class="blogs"
                data-scroll-reveal="enter bottom over 1s"
              >
                <el-row  :span="24" class="questionLine">
                  <el-col>
                    <div class="blogtitle">

                      <el-row :gutter="24">
                        <el-col :xs="8" :lg="3">
                          <span class="blogpic">
                            <a :href=" VUE_MOGU_WEB + '/#/info?blogOid='+item.oid" target="_blank">
                              <img v-if="item.photoList && item.photoList.length > 0 " v-lazy="item.photoList[0]" :key="item.photoList[0]"  alt>
                            </a>
                          </span>
                        </el-col>

                        <el-col :xs="16" :lg="12">
                          <a :href=" VUE_MOGU_WEB + '/#/info?blogOid='+item.oid" target="_blank">{{item.title}}</a>
                        </el-col>

                        <span v-for="(blogTag, index) in item.tagList" style="float: right; margin-right: 10px">
                          <el-tag style="margin-right: 3px" v-if="index%3==0" type="primary">{{blogTag.content}}</el-tag>
                          <el-tag style="margin-right: 3px" v-if="index%3==1" type="danger">{{blogTag.content}}</el-tag>
                          <el-tag style="margin-right: 3px" v-if="index%3==2" type="info">{{blogTag.content}}</el-tag>
                        </span>
                      </el-row>

                    </div>

                    <div class="bloginfo">
                      <ul>
                        <li class="author">
                          <span class="iconfont">&#xe60f;</span>
                          <a href="javascript:void(0);">{{item.author}}</a>
                        </li>

                        <li class="lmname" v-if="item.blogSort">
                          <span class="iconfont">&#xe603;</span>
                          <a
                            href="javascript:void(0);"
                            @click="goToList(item.blogSort.uid)"
                          >{{item.blogSort.sortName}}</a>
                        </li>

                        <li class="view">
                          <span class="iconfont">&#xe8c7;</span>
                          <span>{{item.clickCount}}</span>
                        </li>

                        <li class="createTime">
                          <span class="iconfont">&#xe606;</span>
                          {{item.createTime}}
                        </li>

                      </ul>
                    </div>
                  </el-col>
                </el-row>

              </div>

              <div class="isEnd">
                <div class="loadContent" @click="loadContent" v-if="!isEnd&&!loading">点击加载更多</div>
                <div class="lds-css ng-scope" v-if="!isEnd&&loading">
                  <div style="width:100%;height:100%" class="lds-facebook">
                    <div></div>
                    <div></div>
                    <div></div>
                  </div>
                </div>

                <span v-if="isEnd">我也是有底线的~</span>
              </div>
            </div>

          </el-tab-pane>
          <el-tab-pane name="2" label="问答">
            <span :class="activeName==2?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-tickets"></i> <span>问答</span></span>

            <el-tabs class="userEvent2" v-model="activeName2" @tab-click="handleClick2">
              <el-tab-pane label="最新问答" name="1"></el-tab-pane>
              <el-tab-pane label="最热问答" name="2"></el-tab-pane>
            </el-tabs>

            <div style="min-height: 773px">
              <div
                v-for="item in listData"
                :key="item.uid"
                class="blogs"
                v-if="item.user && item.isPublish == 1"
                data-scroll-reveal="enter bottom over 1s"
              >
                <el-row  :span="24" class="questionLine">
                  <el-col>
                    <div class="blogtitle">
                      <a :href=" VUE_MOGU_WEB + '/#/qInfo?questionOid='+item.oid" target="_blank">{{item.title}}</a>
                      <span v-for="(questionTag, index) in item.questionTagList" style="float: right; margin-right: 10px">
                        <el-tag style="margin-right: 3px" v-if="index%3==0" type="primary">{{questionTag.name}}</el-tag>
                        <el-tag style="margin-right: 3px" v-if="index%3==1" type="danger">{{questionTag.name}}</el-tag>
                        <el-tag style="margin-right: 3px" v-if="index%3==2" type="info">{{questionTag.name}}</el-tag>
                      </span>
                    </div>

                    <div class="bloginfo">
                      <ul>
                        <li style=" padding-right: 6px">
                          <el-avatar size="small" v-if="item.user.photoUrl" :src="item.user.photoUrl"></el-avatar>
                          <el-avatar size="small" v-else src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
                        </li>
                        <li class="author" >
                          <a  href="javascript:void(0);">{{item.user.nickName}}</a>
                        </li>

                        <li class="lmname" v-if="item.blogSort">
                          <span class="iconfont">&#xe603;</span>
                          <a
                            href="javascript:void(0);"
                            @click="goToList(item.blogSort.uid)"
                          >{{item.blogSort.sortName}}</a>
                        </li>

                        <li class="view">
                          <span class="iconfont">&#xe8c7;</span>
                          <span>{{item.clickCount}}</span>
                        </li>

                        <li class="createTime">
                          <span class="iconfont">&#xe606;</span>
                          {{item.createTime}}
                        </li>

                      </ul>
                    </div>
                  </el-col>
                </el-row>

              </div>

              <div class="isEnd">
                <div class="loadContent" @click="loadContent" v-if="!isEnd&&!loading">点击加载更多</div>
                <div class="lds-css ng-scope" v-if="!isEnd&&loading">
                  <div style="width:100%;height:100%" class="lds-facebook">
                    <div></div>
                    <div></div>
                    <div></div>
                  </div>
                </div>

                <span v-if="isEnd">我也是有底线的~</span>
              </div>
            </div>
          </el-tab-pane>

<!--          <el-tab-pane name="3" label="收藏">-->
<!--            <span :class="activeName==3?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-chat-dot-square"></i> <span>收藏</span></span>-->
<!--          </el-tab-pane>-->
<!--          <el-tab-pane name="4" label="关注">-->
<!--            <span :class="activeName==4?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-chat-dot-square"></i> <span>关注</span></span>-->
<!--          </el-tab-pane>-->

        </el-tabs>

      </div>
    </article>

  </div>
</template>

<script>
import {Loading} from "element-ui";
import {getQuestionListByUser, getBlogListByUser, getUserByUid} from "../api/about";

export default {
  data() {
    return {
      VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
      listData: [],
      activeName: "1", // 激活页
      activeName2: "1",
      keyword: "",
      currentPage: 1,
      pageSize: 15,
      total: 0, //总数量
      isEnd: false, //是否到底底部了
      loading: false, //是否正在加载
      orderByDescColumn: "create_time", // 降序字段
      adminUid: "", // 管理员uid
      userUid: "", // 管理员uid
      userInfo: {}, // 用户信息【管理员或者前端用户】
    };
  },
  components: {
    //注册组件
  },
  mounted() {

  },
  created() {
    this.adminUid = this.$route.query.adminUid;
    this.userUid = this.$route.query.userUid;
    // 获取列表
    this.getDataList(1);
    // 获取用户信息
    this.getUserInfo()
  },
  methods: {
    getUserInfo: function() {
      let params = new URLSearchParams()
      if(this.adminUid) {
        params.append("adminUid", this.adminUid)
      }
      if(this.userUid) {
        params.append("userUid", this.userUid)
      }
      getUserByUid(params).then(response=> {
        if(response.code == this.$ECode.SUCCESS) {
          this.userInfo = response.data
        } else {
          this.$message.error(response.message)
        }
      })
    },
    //跳转到文章详情
    goToInfo(uid) {
      let routeData = this.$router.resolve({
        path: "/info",
        query: { blogUid: uid }
      });
      window.open(routeData.href, "_blank");
    },
    errorHandler: function () {
       return true
    },
    // 一级tab点击事件
    handleClick(tab, event) {
      switch (tab.label) {
        case "博客": {
          this.orderByDescColumn = "create_time";
          this.getDataList(1)
        } break;
        case "问答": {
          this.orderByDescColumn = "create_time";
          this.getDataList(2)
        } break;
        case "收藏": {

        } break;
        case "关注": {

        } break;
      }

    },
    // 二级tab点击事件
    handleClick2(tab, event) {
      console.log(tab.label)
      let falg = 0
      switch (tab.label) {
        case "最新博客": {
          this.orderByDescColumn = "create_time";
          this.getDataList(1)
        } break;
        case "最热博客": {
          this.orderByDescColumn = "click_count";
          this.getDataList(1)
        } break;
        case "最新问答": {
          this.orderByDescColumn = "create_time";
          this.getDataList(2)
        } break;
        case "最热问答": {
          this.orderByDescColumn = "click_count";
          this.getDataList(2)
        } break;
      }
    },
    //最新博客列表
    getDataList(type) {
      let that = this;
      that.loadingInstance = Loading.service({
        lock: true,
        text: '正在努力加载中……',
        background: 'rgba(0, 0, 0, 0.7)'
      })
      let params = {};
      params.currentPage = this.currentPage;
      params.pageSize = this.pageSize;
      params.orderByDescColumn = this.orderByDescColumn
      params.adminUid = this.adminUid
      params.userUid = this.userUid

      switch (type) {
        //###################### 获取博客列表 ######################
        case 1 : {
          getBlogListByUser(params).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              console.log("获取的博客列表", response.data)
              let newBlogData = response.data.records;
              that.listData = newBlogData
              that.total = response.data.total;
              that.pageSize = response.data.size;
              that.currentPage = response.data.current;
              //全部加载完毕
              if (newBlogData.length < this.pageSize) {
                this.isEnd = true;
              }
            }
            that.loadingInstance.close();
          },function(err){
            that.loadingInstance.close();
          });
        }; break
        //###################### 获取博客列表结束 ######################

        //###################### 获取问答列表 ######################
        case 2 : {
          getQuestionListByUser(params).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              let newData = response.data.records;
              that.listData = newData
              that.total = response.data.total;
              that.pageSize = response.data.size;
              that.currentPage = response.data.current;
              //全部加载完毕
              if (newData.length < this.pageSize) {
                this.isEnd = true;
              }
            }
            that.loadingInstance.close();
          },function(err){
            that.loadingInstance.close();
          });
        }; break
        //###################### 获取问答列表结束 ######################
      }



    },

    loadContent: function () {
      this.loading = false;
      this.currentPage = this.currentPage + 1;
      var params = {};
      params.methodType = this.methodType
      params.currentPage = this.currentPage;
      params.pageSize = this.pageSize;
      params.orderByDescColumn = "createTime";
      getQuestionListByUser(params).then(response => {
        if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
          this.isEnd = false;
          let records = response.data.records
          let newData = this.newQuestionData.concat(records);
          this.listData = newData;
          this.total = response.data.total;
          this.pageSize = response.data.size;
          this.currentPage = response.data.current;
          //全部加载完毕
          if (records.length < this.pageSize) {
            this.isEnd = true;
          }
        } else {
          this.isEnd = true;
        }
        this.loading = false;
      });
    },
  }
};
</script>


<style>
.el-tabs__active-bar {
 display: none;
}
.userEvent .el-tabs__nav {
  width: 100%;
  text-align: center;
}
.userEvent2 .el-tabs__nav {
  width: 0%;
}
.userEvent2 .el-tabs__nav-wrap::after {
  display: none;
}

.tab-pane {
  font-size: 18px;
  color: #282828;
}

.tab-pane-active {
  font-size: 18px;
  color: #00a7eb;
}

</style>
<style scoped>
  .blogs .blogpic {
    width: 100%;
  }
  .blogs .blogpic img {
    width: 100px;
  }

  .bloginfo {
    margin-top: 10px;
  }
  .userInfo .nickname {
    font-size: 24px;
    font-weight: 700;
    color: #333;
    line-height: 56px;
  }
  .userInfo .gender {
    font-size: 14px;
    color: #999
  }
  .userInfo .summary {
    font-size: 16px;
    color: #666;
    padding: 16px
  }
  .userInfo .love {
    margin-top: 3px;
  }
  .isEnd {
    float: left;
    width: 100%;
    height: 80px;
    text-align: center;
  }
  .loadContent {
    width: 120px;
    height: 30px;
    line-height: 30px;
    font-size: 16px;
    margin: 0 auto;
    color: aliceblue;
    cursor: pointer;
    background: rgba(0, 0, 0, 0.8);
  }
</style>
