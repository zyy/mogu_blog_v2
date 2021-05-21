<template>
    <div class="tuijian" v-if="fourthData.length > 0">
      <h2 class="hometitle">推荐文章</h2>
      <ul class="tjpic" v-if="fourthData[0]">
        <a :href="fourthData[0].type == 0 ? VUE_MOGU_WEB + '/#/info?blogOid='+fourthData[0].oid : fourthData[0].outsideLink" target="_blank">
          <i>
            <img v-if="fourthData[0].photoList" v-lazy="fourthData[0].photoList[0]" :key="fourthData[0].photoList[0]" style="cursor:pointer" >
          </i>
          <p>{{fourthData[0].title}}</p>
        </a>
      </ul>

      <ul class="sidenews">
        <li v-for="item in sideNews" :key="item.uid">
          <a :href="item.type == 0 ? VUE_MOGU_WEB + '/#/info?blogOid='+item.oid : item.outsideLink" target="_blank">
            <i>
              <img style="cursor:pointer" v-if="item.photoList" v-lazy="item.photoList[0]" :key="item.photoList[0]">
            </i>
            <p><a href="javascript:void(0);">{{item.title}}</a></p>
          </a>

          <span>{{item.createTime}}</span>
        </li>
      </ul>
    </div>
</template>

<script>
import { getBlogByLevel } from "../../api/index";
import {getBlogByUid} from "../../api/blogContent";
export default {
  name: 'FourthRecommend',
    data() {
    	return {
        VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
	      fourthData: [], //；四级推荐数据
    	}
    },
    created() {
      var fourthParams = new URLSearchParams();
      fourthParams.append("currentPage", 0);
      fourthParams.append("pageSize", 5);
      fourthParams.append("level", 4);
      fourthParams.append("useSort", 1);
      getBlogByLevel(fourthParams).then(response => {
        if(response.code == this.$ECode.SUCCESS) {
          this.fourthData = response.data.records;
        }
      });
    },
    computed: {
      //添加一个计算属性用于简单过滤掉数组中第一个数据
      sideNews() {
        return this.fourthData.filter(blog =>
          this.fourthData.indexOf(blog) != 0
        )
      }
    },
    methods: {

    },
}
</script>

<style>
</style>
