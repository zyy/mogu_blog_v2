<template>
  <article>

    <el-tabs v-model="activeName" @tab-click="handleClick">
      <el-tab-pane name="1">
        <span :class="activeName==1?'tab-pane-active':'tab-pane'" slot="label" ><i class="el-icon-collection-tag"></i> <span>最新</span></span>
      </el-tab-pane>
      <el-tab-pane name="2">
        <span :class="activeName==2?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-tickets"></i> <span>最热</span></span>
      </el-tab-pane>
      <el-tab-pane name="3">
        <span :class="activeName==3?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-chat-dot-square"></i> <span>待回答</span></span>
      </el-tab-pane>
    </el-tabs>

    <div class="blogsbox" style="min-height: 773px">
      <div
        v-for="item in newQuestionData"
        :key="item.uid"
        class="blogs"
        v-if="item.user"
        data-scroll-reveal="enter bottom over 1s"
      >
        <el-row  :span="24" class="questionLine">
          <el-col  :xs="6" :sm="4">
            <div class="itemNum">
              <el-tag type="success">回答 {{item.replyCount}}</el-tag>
            </div>
            <div class="itemNum">
              <el-tag type="warning">阅读 {{item.clickCount}}</el-tag>
            </div>
          </el-col>

          <el-col :xs="18" :sm="20">
            <div class="blogtitle">
              <a href="javascript:void(0);" @click="goToInfo(item)">{{item.title}}</a>
              <span v-for="(questionTag, index) in item.questionTagList" style="float: right">
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

    <div class="sidebar">
      <!--标签云-->
      <HotQuestion></HotQuestion>
<!--      <TagCloud></TagCloud>-->

    </div>

  </article>
</template>

<script>
import TagCloud from "../components/TagCloud";
import HotQuestion from "../components/HotQuestion";
import {getQuestionList} from "../api/question";
import { Loading } from 'element-ui';
import {getBlogByUid} from "../api/blogContent";
export default {
  name: "index",
  components: {
    //注册组件
    TagCloud,
    HotQuestion,
  },
  data() {
    return {
      loadingInstance: null, // loading对象
      VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
      newQuestionData: [], //最新文章
      hotBlogData: [], //最热文章
      hotTagData: [], //最新标签
      keyword: "",
      currentPage: 1,
      pageSize: 15,
      total: 0, //总数量
      isEnd: false, //是否到底底部了
      loading: false, //是否正在加载
      activeName: "1", // 激活页
      methodType: "newQuestion", // 激活方法
    };
  },
  mounted() {
    // 注册scroll事件并监听
    this.loading = false;
  },
  created() {
    // 获取最新问答
    this.questionList();
  },
  methods: {
    //跳转到问答详情
    goToInfo(question) {
      let routeData = this.$router.resolve({
        path: "/qInfo",
        query: {oid: question.oid}
      });
      window.open(routeData.href, '_blank');
    },

    //最新博客列表
    questionList() {
      var that = this;
      that.loadingInstance = Loading.service({
        lock: true,
        text: '正在努力加载中……',
        background: 'rgba(0, 0, 0, 0.7)'
      })

      var params = {};
      params.currentPage = this.currentPage;
      params.pageSize = this.pageSize;
      params.orderByDescColumn = "createTime";
      params.methodType = this.methodType
      getQuestionList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          let newQuestionData = response.data.records;
          that.newQuestionData = newQuestionData
          that.total = response.data.total;
          that.pageSize = response.data.size;
          that.currentPage = response.data.current;
          //全部加载完毕
          if (newQuestionData.length < this.pageSize) {
            this.isEnd = true;
          }
        }
        that.loadingInstance.close();
      },function(err){
        that.loadingInstance.close();
      });
    },

    loadContent: function () {
      this.loading = false;
      this.currentPage = this.currentPage + 1;
      var params = {};
      params.methodType = this.methodType
      params.currentPage = this.currentPage;
      params.pageSize = this.pageSize;
      params.orderByDescColumn = "createTime";
      getQuestionList(params).then(response => {
        if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
          this.isEnd = false;
          let records = response.data.records
          let newData = this.newQuestionData.concat(records);
          this.newQuestionData = newData;
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
    handleClick(tab, event) {
      switch (tab.index) {
        case "0": {
          this.methodType = "newQuestion"
        } break;
        case "1": {
          this.methodType = "hotQuestion"
        } break;
        case "2": {
          this.methodType = "waitQuestion"
        } break;
      }
      this.questionList()
    }
  }
};
</script>

<style>
.el-loading-mask {
  z-index: 2002;
}
.isEnd {
  float: left;
  width: 100%;
  height: 80px;
  text-align: center;
}

.ng-scope {
  margin: 0 auto;
  width: 18%;
  height: 10%;
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

@keyframes lds-facebook_1 {
  0% {
    top: 0px;
    height: 200px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

@-webkit-keyframes lds-facebook_1 {
  0% {
    top: 0px;
    height: 200px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

@keyframes lds-facebook_2 {
  0% {
    top: 20px;
    height: 160px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

@-webkit-keyframes lds-facebook_2 {
  0% {
    top: 20px;
    height: 160px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

@keyframes lds-facebook_3 {
  0% {
    top: 40px;
    height: 120px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

@-webkit-keyframes lds-facebook_3 {
  0% {
    top: 40px;
    height: 120px;
  }
  50% {
    top: 80px;
    height: 40px;
  }
  100% {
    top: 80px;
    height: 40px;
  }
}

.lds-facebook {
  position: relative;
}

.lds-facebook div {
  position: absolute;
  width: 20px;
}

.lds-facebook div:nth-child(1) {
  left: 40px;
  background: #1d0e0b;
  -webkit-animation: lds-facebook_1 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  animation: lds-facebook_1 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  -webkit-animation-delay: -0.2s;
  animation-delay: -0.2s;
}

.lds-facebook div:nth-child(2) {
  left: 90px;
  background: #774023;
  -webkit-animation: lds-facebook_2 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  animation: lds-facebook_2 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  -webkit-animation-delay: -0.1s;
  animation-delay: -0.1s;
}

.lds-facebook div:nth-child(3) {
  left: 140px;
  background: #d88c51;
  -webkit-animation: lds-facebook_3 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
  animation: lds-facebook_3 1s cubic-bezier(0, 0.5, 0.5, 1) infinite;
}

.lds-facebook {
  width: 90px !important;
  height: 90px !important;
  -webkit-transform: translate(-45px, -45px) scale(0.45) translate(45px, 45px);
  transform: translate(-45px, -45px) scale(0.45) translate(45px, 45px);
}

.iconfont {
  font-size: 15px;
  margin-right: 2px;
}

.questionLine .itemNum {
  height: 50px;
  width: 100%;
  line-height: 50px;
  justify-content: center;
  margin: 0 auto;
  font-weight: bold;
}

.t_nav .n1 {
  margin-right: 10px;
}

.tab-pane {
  font-size: 16px;
  font-weight: bold;
  color: #282828;
}

.tab-pane-active {
  font-size: 16px;
  font-weight: bold;
  color: #00a7eb;
}

</style>
