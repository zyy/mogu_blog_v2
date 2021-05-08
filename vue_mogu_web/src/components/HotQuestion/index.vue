<template>
    <div class="tuijian" v-if="hotQuestionData.length > 0">
      <h2 class="hometitle">热门回答</h2>
      <ul class="questionContent">
        <li v-for="(item, index) in hotQuestionData" :key="item.uid">
          <span class="index" v-if="index==0"> <el-tag type="danger">{{index + 1}}</el-tag></span>
          <span class="index" v-else-if="index==1"> <el-tag type="success">{{index + 1}}</el-tag></span>
          <span class="index" v-else-if="index==2"> <el-tag type="primary">{{index + 1}}</el-tag></span>
          <span class="index" v-else> <el-tag type="info">{{index + 1}}</el-tag></span>
          &nbsp;<a href="javascript:void(0);" @click="goToInfo(item)">{{item.title}}</a>
        </li>
      </ul>
    </div>
</template>

<script>
import { getQuestionList } from "../../api/question";
export default {
  name: "HotQuestion",
  data() {
    return {
      hotQuestionData: [] //热门博客列表
    };
  },
  created() {
    var params = {};
    params.currentPage = 0;
    params.pageSize = 10;
    params.orderByDescColumn = "clickCount";
    getQuestionList(params).then(response => {
      if (response.code == this.$ECode.SUCCESS) {
        this.hotQuestionData = response.data.records;
      }
    });
  },
  methods: {
    //跳转到文章详情【或推广链接】
    goToInfo(question) {
      let routeData = this.$router.resolve({
        path: "/qInfo",
        query: {oid: question.oid}
      });
      window.open(routeData.href, '_blank');
    },
  }
};
</script>

<style scoped>
.questionContent .index {
  /*font-size: 16px;*/
}
.questionContent li{
  margin-bottom: 4px;
  border-bottom: 1px solid rgba(0,0,0,.0625);
}
.questionContent li :hover {

}
</style>
