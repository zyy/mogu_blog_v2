<template>
  <div class="sidebarDiv zhuanti" v-if="thirdData.length > 0">
    <h2 class="hometitle">特别推荐</h2>
    <ul>
      <li  v-for="item in thirdData" :key="item.uid" style="cursor: pointer">
        <a :href="item.type == 0 ? VUE_MOGU_WEB + '/#/info?blogOid='+item.oid : item.outsideLink" target="_blank">
          <i>
            <img  v-if="item.photoList" v-lazy="item.photoList[0]" :key="item.photoList[0]">
          </i>
          <p  style="cursor: pointer">{{splitStr(item.title, 30)}}<span>阅读</span> </p>
        </a>

      </li>
    </ul>
  </div>
</template>

<script>
import { getBlogByLevel } from "../../api/index";
import {getBlogByUid} from "../../api/blogContent";
export default {
  name: 'ThirdRecommend',
    data() {
    	return {
        VUE_MOGU_WEB: process.env.VUE_MOGU_WEB,
        slideList: [],
	      thirdData: [], //；一级推荐数据
    	}
    },
    created() {
      var thirdParams = new URLSearchParams();
      thirdParams.append("currentPage", 0);
      thirdParams.append("pageSize", 3);
      thirdParams.append("level", 3);
      thirdParams.append("useSort", 1);
      getBlogByLevel(thirdParams).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          this.thirdData = response.data.records;
        }
      });
    },
    methods: {
      splitStr(str, flagCount) {
        return this.$commonUtil.splitStr(str, flagCount)
      }
    },

}
</script>

<style>
</style>
