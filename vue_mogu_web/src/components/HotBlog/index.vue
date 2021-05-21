<template>
    <div class="tuijian" v-if="hotBlogData.length > 0">
      <h2 class="hometitle">点击排行</h2>
      <ul class="tjpic" v-if="hotBlogData[0]">

        <a :href="hotBlogData[0].type == 0 ? VUE_MOGU_WEB + '/#/info?blogOid='+hotBlogData[0].oid : hotBlogData[0].outsideLink" target="_blank">
          <i>
            <img style="cursor:pointer" v-if="hotBlogData[0].photoList" v-lazy="hotBlogData[0].photoList[0]" :key="hotBlogData[0].photoList[0]">
          </i>
          <p>{{hotBlogData[0].title}}</p>
        </a>
      </ul>
      <ul class="sidenews">
        <li v-for="(item, index) in sideNews" v-if="index != 0" :key="item.uid">
          <a :href="item.type == 0 ? VUE_MOGU_WEB + '/#/info?blogOid='+item.oid : item.outsideLink" target="_blank">
            <i>
              <img style="cursor:pointer" v-if="item.photoList" v-lazy="item.photoList[0]" :key="item.photoList[0]" >
            </i>
            <p>{{item.title}}</p>
            <span>{{item.createTime}}</span>
          </a>
        </li>
      </ul>
    </div>
</template>

<script>
import { getHotBlog } from "../../api/index";
import {getBlogByUid} from "../../api/blogContent";
export default {
  name: "TagCloud",
  data() {
    return {
      VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
      hotBlogData: [] //热门博客列表
    };
  },
  created() {
    getHotBlog().then(response => {
      if (response.code == this.$ECode.SUCCESS) {
        this.hotBlogData = response.data.records;
      }
    });
  },
  computed: {
    //添加一个计算属性用于简单过滤掉数组中第一个数据
    sideNews() {
      return this.hotBlogData.filter(blog =>
        this.hotBlogData.indexOf(blog) != 0
      )
    }
  },
  methods: {

  }
};
</script>

<style>

</style>
