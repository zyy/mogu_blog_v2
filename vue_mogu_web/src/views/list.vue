<template>
  <div>
    <div class="pagebg sh"></div>
    <div class="container">
      <h1 class="t_nav">
        <a href="/" class="n1">网站首页</a>
        <a href="/" class="n2">搜索</a>
      </h1>

      <el-tabs v-if="showSearchType && showCreateBlog" v-model="activeName" @tab-click="handleClick">
        <el-tab-pane name="0">
          <span :class="activeName==0?'tab-pane-active':'tab-pane'" slot="label" ><i class="el-icon-collection-tag"></i> <span>博客</span></span>
        </el-tab-pane>
        <el-tab-pane name="1">
          <span :class="activeName==1?'tab-pane-active':'tab-pane'" slot="label"><i class="el-icon-tickets"></i> <span>问答</span></span>
        </el-tab-pane>
      </el-tabs>

      <!--blogsbox begin-->
      <div class="blogsbox">
        <div
          v-for="item in listData"
          :key="item.uid"
          class="blogs"
          data-scroll-reveal="enter bottom over 1s"
        >
          <!--判断返回的结果是否是博客-->
          <el-row v-if="activeName=='0'">
            <h3 class="blogtitle">
              <a v-html="item.title" :href="item.type == 0 ? '/#/info?blogOid='+item.oid : item.outsideLink" target="_blank">{{item.title}}</a>
            </h3>

            <span class="blogpic">
            <a :href="item.type == 0 ? '/#/info?blogOid='+item.oid : item.outsideLink" target="_blank">
              <img v-if="item.photoUrl" v-lazy="item.photoUrl" :key="item.photoUrl" alt="">
            </a>
          </span>
            <p class="blogtext" v-html="item.summary">{{item.summary}}</p>
            <div class="bloginfo">
              <ul>
                <li class="author">
                  <span class="iconfont">&#xe60f;</span>
                  <a href="javascript:void(0);" @click="goToAuthor(item.author)">{{item.author}}</a>
                </li>
                <li class="lmname" v-if="item.blogSortName">
                  <span class="iconfont">&#xe603;</span>
                  <a href="javascript:void(0);" @click="goToList(item.blogSortUid)">{{item.blogSortName}}</a>
                </li>
                <li class="createTime"><span class="iconfont">&#xe606;</span>{{item.createTime}}</li>
              </ul>
            </div>
          </el-row>

          <!--判断返回的结果是否是问答-->
          <el-row v-else-if="activeName=='1'"  :span="24" class="questionLine">
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
                <a :href=" VUE_MOGU_WEB + '/#/qInfo?questionOid='+item.oid" target="_blank" v-html="item.title">{{item.title}}</a>
                <span v-for="(questionTag, index) in item.questionTagList" style="float: right">
                  <el-tag style="margin-right: 3px" v-if="index%3==0" type="primary">{{questionTag.name}}</el-tag>
                  <el-tag style="margin-right: 3px" v-if="index%3==1" type="danger">{{questionTag.name}}</el-tag>
                  <el-tag style="margin-right: 3px" v-if="index%3==2" type="info">{{questionTag.name}}</el-tag>
                </span>
              </div>


              <div class="bloginfo">
                <ul>
                  <li style=" padding-right: 6px">
                    <el-avatar size="small" v-if="item.user" :src="item.user.photoUrl"></el-avatar>
                    <el-avatar size="small" v-else src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
                  </li>
                  <li class="author" v-if="item.user">
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
          <div
            class="loadContent"
            @click="loadContent"
            v-if="!isEnd && !loading && totalPages>0"
          >查看更多</div>

          <div class="lds-css ng-scope" v-if="!isEnd && loading">
            <div style="width:100%;height:100%" class="lds-facebook">
              <div></div>
              <div></div>
              <div></div>
            </div>
          </div>

          <span v-if="listData.length >= 0 && isEnd &&!loading && totalPages>0">我也是有底线的~</span>

          <span v-if="totalPages == 0 && !loading">空空如也~</span>
        </div>
      </div>
      <!--blogsbox end-->

      <div class="sidebar">
        <!-- 三级推荐 -->
        <ThirdRecommend></ThirdRecommend>

        <!--标签云-->
        <TagCloud></TagCloud>

        <!--四级推荐-->
        <FourthRecommend></FourthRecommend>

        <!--点击排行-->
        <HotBlog></HotBlog>

        <Link></Link>

        <!--关注我们-->
        <FollowUs></FollowUs>
      </div>
    </div>
  </div>
</template>

<script>

import ThirdRecommend from "../components/ThirdRecommend";
import FourthRecommend from "../components/FourthRecommend";
import TagCloud from "../components/TagCloud";
import HotBlog from "../components/HotBlog";
import FollowUs from "../components/FollowUs";
import {
  searchBlog,
  searchBlogByTag,
  searchBlogBySort,
  searchBlogByAuthor
} from "../api/search";
import {getBlogByUid} from "../api/blogContent";
import {getWebConfig} from "../api";
import {mapMutations} from "vuex";

export default {
  name: "list",
  data() {
    return {
      VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
      listData: [], // 查询出的文章
      keywords: "",
      currentPage: 1,
      totalPages: 0,
      pageSize: 10,
      total: 0, //总数量
      tagUid: "",
      searchlistData: [], //搜索出来的文章
      sortUid: "",
      showCreateBlog: false, // 是否开启创作
      showSearchType: false, // 是否显示按钮
      isEnd: false, //是否到底底部了
      activeName: "0", // 激活页
      loading: false //内容是否正在加载
    };
  },
  components: {
    FourthRecommend,
    ThirdRecommend,
    TagCloud,
    HotBlog,
    FollowUs,
  },
  created() {
    this.keywords = this.$route.query.keyword;
    this.tagUid = this.$route.query.tagUid;
    this.sortUid = this.$route.query.sortUid;
    this.author = this.$route.query.author;

    if (
      this.keywords == undefined &&
      this.tagUid == undefined &&
      this.sortUid == undefined &&
      this.author == undefined
    ) {
      return;
    }
    this.getWebConfigInfo();
    this.search();
  },
  mounted() {
    // 注册scroll事件并监听
    // var that = this;
    // window.addEventListener("scroll", function() {
    //   let scrollTop = document.documentElement.scrollTop; //当前的的位置
    //   let scrollHeight = document.documentElement.scrollHeight; //最高的位置
    //   if (scrollTop >= 0.6 * scrollHeight && !that.isEnd && !that.loading) {
    //     that.loading = true;
    //     that.currentPage = that.currentPage + 1;
    //     that.search();
    //   }
    // });
  },
  watch: {
    $route(to, from) {
      this.keywords = this.$route.query.keyword;
      this.tagUid = this.$route.query.tagUid;
      this.sortUid = this.$route.query.sortUid;
      this.searchlistData = [] // 清空查询出来的博客
      this.search();
    }
  },
  methods: {
    ...mapMutations(['setWebConfigData']),
    //跳转到文章详情
    goToInfo(blog) {
      if(blog.type == "0") {
        let routeData = this.$router.resolve({
          path: "/info",
          query: {blogOid: blog.oid}
        });
        window.open(routeData.href, '_blank');
      } else if(blog.type == "1") {
        var params = new URLSearchParams();
        params.append("uid", blog.uid);
        getBlogByUid(params).then(response => {
          // 记录一下用户点击日志
        });
        window.open(blog.outsideLink, '_blank');
      }
    },
    /**
     * 获取网站配置
     */
    getWebConfigInfo: function() {
      let webConfigData = this.$store.state.app.webConfigData
      if(webConfigData.createTime) {
        this.contact = webConfigData;
        this.mailto = "mailto:" + this.contact.email;
        this.openComment = webConfigData.openComment
        console.log("是否开启投稿", webConfigData)
        this.showCreateBlog = webConfigData.openCreateBlog == "1" ? true:false
      } else {
        getWebConfig().then(response => {
          if (response.code == this.$ECode.SUCCESS) {
            this.info = response.data;
            // 存储在Vuex中
            this.setWebConfigData(response.data)
            this.openComment = this.info.openComment
            this.showCreateBlog = this.info.openCreateBlog == "1" ? true:false
          }
        });
      }
    },
    //点击了分类
    goToList(uid) {
      let routeData = this.$router.resolve({
        path: "/list",
        query: { sortUid: uid }
      });
      window.open(routeData.href, '_blank');
    },
    goToAuthor(author) {
      let routeData = this.$router.resolve({
        path: "/list",
        query: {author: author}
      });
      window.open(routeData.href, '_blank');
    },
    // 加载内容
    loadContent: function() {
      var that = this;
      that.currentPage = that.currentPage + 1;
      that.search();
    },
    handleClick(tab, event) {
      this.searchlistData = []
      this.listData = []
      this.search()
    },
    search: function() {
      var that = this;
      that.loading = true;
      if (this.keywords != undefined) {
        this.showSearchType = true
        var params = new URLSearchParams();
        params.append("currentPage", that.currentPage);
        params.append("pageSize", that.pageSize);
        params.append("keywords", that.keywords);
        params.append("searchType", that.activeName);
        searchBlog(params).then(response => {
          if (response.code == this.$ECode.SUCCESS) {
            that.isEnd = false;
            //获取总页数
            that.total = response.data.total;
            that.pageSize = response.data.pageSize;
            that.currentPage = response.data.currentPage;
            let listData = [];
            if(that.activeName == "1") {
              listData = response.data.questionList
              that.totalPages = response.data.questionList.length;
            } else {
              listData = response.data.blogList
              that.totalPages = response.data.blogList.length;
            }

            // 判断搜索的博客是否有内容
            if(response.data.total <= 0) {
              that.isEnd = true;
              that.loading = false;
              this.listData = []
              return;
            }

            //全部加载完毕
            if (listData.length < that.pageSize) {
              that.isEnd = true;
            }

            listData = that.searchlistData.concat(listData);
            that.searchlistData = listData;
            this.listData = listData;
          } else {
            that.isEnd = true;
          }
          that.loading = false;
        });
      } else if (this.tagUid != undefined) {
        this.showSearchType = false
        var params = new URLSearchParams();
        params.append("tagUid", that.tagUid);
        params.append("currentPage", that.currentPage);
        params.append("pageSize", that.pageSize);
        searchBlogByTag(params).then(response => {
          if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
            that.isEnd = false;
            //获取总页数
            that.totalPages = response.data.total;
            var listData = response.data.records;
            that.total = response.data.total;
            that.pageSize = response.data.size;
            that.currentPage = response.data.current;

            //全部加载完毕
            if (listData.length < that.pageSize) {
              that.isEnd = true;
            }
            // 设置分类名
            for (var i = 0; i < listData.length; i++) {
              listData[i].blogSort = listData[i].blogSort.sortName;
            }
            listData = that.searchlistData.concat(listData);
            that.searchlistData = listData;
            this.listData = listData;
            that.loading = false;
          } else {
            that.isEnd = true;
            that.loading = false;
          }
        });
      } else if (this.sortUid != undefined) {
        this.showSearchType = false
        var params = new URLSearchParams();
        params.append("blogSortUid", that.sortUid);
        params.append("currentPage", that.currentPage);
        params.append("pageSize", that.pageSize);
        searchBlogBySort(params).then(response => {
          if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
            that.isEnd = false;
            //获取总页数
            that.totalPages = response.data.total;
            var listData = response.data.records;
            that.total = response.data.total;
            that.pageSize = response.data.size;
            that.currentPage = response.data.current;
            //全部加载完毕
            if (listData.length < that.pageSize) {
              that.isEnd = true;
            }
            for (var i = 0; i < listData.length; i++) {
              listData[i].blogSort = listData[i].blogSort.sortName;
            }
            listData = that.searchlistData.concat(listData);
            that.searchlistData = listData;
            this.listData = listData;
            that.loading = false;
          } else {
            that.isEnd = true;
            that.loading = false;
          }
        });
      } else if (this.author != undefined) {
        console.log("以作者查询")

        this.showSearchType = true
        var params = new URLSearchParams();
        params.append("author", that.author);
        params.append("searchType", that.activeName);
        params.append("currentPage", that.currentPage);
        params.append("pageSize", that.pageSize);
        searchBlogByAuthor(params).then(response => {
          if (response.code == this.$ECode.SUCCESS && response.data.records.length > 0) {
            that.loading = false;
            that.isEnd = false;
            //获取总页数
            that.totalPages = response.data.total;
            var listData = response.data.records;
            that.total = response.data.total;
            that.pageSize = response.data.size;
            that.currentPage = response.data.current;

            //全部加载完毕
            if (listData.length < that.pageSize) {
              that.isEnd = true;
            }

            // 遍历博客分类信息
            if(that.activeName == "0") {
              for (var i = 0; i < listData.length; i++) {
                if (listData[i].blogSort == undefined) {
                  listData[i].blogSort = "未分类";
                } else {
                  listData[i].blogSort = listData[i].blogSort.sortName;
                }
              }
            }

            listData = that.searchlistData.concat(listData);
            that.searchlistData = listData;
            this.listData = listData;
            that.loading = false;
          } else {
            that.isEnd = true;
            that.loading = false;
          }
        });
      }
    }
  }
};
</script>

<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
</style>

<style>
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
  border-radius: 25px;
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

.questionLine .itemNum {
  height: 50px;
  width: 100%;
  line-height: 50px;
  justify-content: center;
  margin: 0 auto;
  font-weight: bold;
}
</style>

