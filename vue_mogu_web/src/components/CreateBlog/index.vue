<template>

  <div>
    <!-- 添加或修改对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="dialogFormVisible"
      :before-close="beforeClose"
      fullscreen
    >
      <el-form :model="form" :rules="rules" ref="form">
        <el-row>
          <el-col :span="16">
            <el-form-item label="标题" :label-width="formLabelWidth" prop="title">
              <el-input v-model="form.title" auto-complete="off"></el-input>
            </el-form-item>

            <el-form-item label="简介" :label-width="formLabelWidth">
              <el-input v-model="form.summary" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>

          <el-col :span="8">
            <el-form-item label="标题图" :label-width="formLabelWidth">
              <div class="imgBody" v-if="form.photoList">
                <i
                  class="el-icon-error inputClass"
                  v-show="icon"
                  @click="deletePhoto()"
                  @mouseover="icon = true"
                ></i>
                <img
                  @mouseover="icon = true"
                  @mouseout="icon = false"
                  v-bind:src="form.photoList[0]"
                  style="display:inline; width: 195px;height: 105px;"
                >
              </div>
              <div v-else class="uploadImgBody" @click="checkPhoto">
                <i class="el-icon-plus avatar-uploader-icon"></i>
              </div>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="6.5">
            <el-form-item label="分类" :label-width="formLabelWidth" prop="blogSortUid">
              <el-select
                v-model="form.blogSortUid"
                size="small"
                placeholder="请选择"
                style="width:150px"
              >
                <el-option
                  v-for="item in blogSortData"
                  :key="item.uid"
                  :label="item.sortName"
                  :value="item.uid"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="6.5">
            <el-form-item label="标签" label-width="80px">
              <el-select
                v-model="tagValue"
                multiple
                size="small"
                placeholder="请选择"
                style="width:210px"
                filterable
              >
                <el-option
                  v-for="item in tagData"
                  :key="item.uid"
                  :label="item.content"
                  :value="item.uid"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="6.5">
            <el-form-item label="是否原创" :label-width="formLabelWidth" prop="isOriginal">
              <el-radio-group v-model="form.isOriginal" size="small">
                <el-radio v-for="item in blogOriginalDictList" :key="item.uid" :label="item.dictValue" border>{{item.dictLabel}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>

        </el-row>


        <el-form-item label="作者" :label-width="formLabelWidth" v-if="form.isOriginal==0" prop="author">
          <el-input v-model="form.author" auto-complete="off"></el-input>
        </el-form-item>

        <el-form-item label="文章出处" :label-width="formLabelWidth" v-if="form.isOriginal==0">
          <el-input v-model="form.articlesPart" auto-complete="off"></el-input>
        </el-form-item>

        <el-form-item label="外链" :label-width="formLabelWidth" v-if="form.type == 1" prop="outsideLink">
          <el-input v-model="form.outsideLink" auto-complete="off"></el-input>
        </el-form-item>


        <el-form-item label="内容" :label-width="formLabelWidth" prop="content">
          <CKEditor   ref="editor" :content="form.content" :height="360"></CKEditor>
        </el-form-item>

      </el-form>

      <span slot="footer" class="dialog-footer">
        <el-button @click="cancel">取 消</el-button>
        <el-button type="primary" @click="submitForm">确 定</el-button>
    </span>

    </el-dialog>

    <!--头像裁剪-->
    <avatar-cropper
      v-show="imagecropperShow"
      :key="imagecropperKey"
      :width="400"
      :height="200"
      :url="url"
      lang-type="zh"
      :noCircle="true"
      :noSquare="true"
      @close="close"
      @crop-upload-success="cropSuccess"
    />

  </div>



</template>

<script>
import CKEditor from "../CKEditor";
import AvatarCropper from '@/components/AvatarCropper'
import {getListByDictTypeList} from "@/api/sysDictData"
import {addBlog, editBlog, getBlogSortList, getBlogTagList} from "@/api/createBlog"
import {formatData} from "@/utils/webUtils";

export default {
  props: ["visible", "isEdit", "formData"],
  created() {
    this.blogSortList()
    this.tagList()
  },
  components: {
    CKEditor,
    AvatarCropper
  },
  watch: {
    visible: function() {
      this.dialogFormVisible = this.visible;
    },
  },
  mounted() {
    this.getDictList()
  },
  data() {
    return {
      form: {},
      icon: false,
      formLabelWidth: "120px",
      lineLabelWidth: "120px", //一行的间隔数
      maxLineLabelWidth: "100px",
      blogOriginalDictList: [], //存储区域字典
      openDictList: [], // 是否启动字典
      blogOriginalDefault: null, //博客原创默认值
      openDefault: null, // 是否开启评论默认值
      blogSortData: [],
      tagData: [], //标签数据
      tagValue: [], //保存选中标签id(编辑时)
      dialogFormVisible: this.visible,
      title: "写文章",
      imagecropperShow: false,
      imagecropperKey: 0,
      url: process.env.PICTURE_API + "/file/cropperPicture",

      rules: {
        title: [
          {required: true, message: '标题不能为空', trigger: 'blur'}
        ],
        blogSortUid: [
          {required: true, message: '分类不能为空', trigger: 'blur'}
        ],
        isOriginal: [
          {required: true, message: '原创字段不能为空', trigger: 'blur'},
          {pattern: /^[0-9]\d*$/, message: '原创字段只能为自然数'},
        ],
        content: [
          {required: true, message: '内容不能为空', trigger: 'blur'}
        ],
        outsideLink: [
          {required: true, message: '外链地址不能为空', trigger: 'blur'},
          {pattern:  /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/, message: '请输入有效的URL'},
        ],
      }
    }
  },
  methods: {
    /**
     * 字典查询
     */
    getDictList: function () {
      // 判断是否处于编辑模式
      let that = this
      var dictTypeList =  [ 'sys_original_status', 'sys_normal_disable']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          var dictMap = response.data;
          this.blogOriginalDictList = dictMap.sys_original_status.list
          this.openDictList = dictMap.sys_normal_disable.list

          if(dictMap.sys_original_status.defaultValue) {
            this.blogOriginalDefault = dictMap.sys_original_status.defaultValue;
          }
          if(dictMap.sys_normal_disable.defaultValue) {
            this.openDefault = dictMap.sys_normal_disable.defaultValue;
          }

          if(this.isEdit) {
            this.form = this.formData
            setTimeout(()=>{
              that.$refs.editor.setData(that.form.content); //设置富文本内容
            },100)
            that.tagValue = [];
            if (this.form.tagList) {
              var json = this.form.tagList;
              for (var i = 0, l = json.length; i < l; i++) {
                if (json[i] != null) {
                  that.tagValue.push(json[i]["uid"]);
                }
              }
            }
          } else {
            this.form = this.getFormObject()
          }
        }
      });
    },
    getFormObject: function() {
      console.log("是否原创", this.blogOriginalDefault)
      var formObject = {
        uid: null,
        title: null,
        summary: null,
        content: null,
        tagUid: null,
        fileUid: null,
        isOriginal: this.blogOriginalDefault, //是否原创
        author: null, //作者
        articlesPart: null //文章出处，默认蘑菇博客
      };
      return formObject;
    },
    submitForm: function() {
      if(this.tagValue.length <= 0) {
        this.$commonUtil.message.error("标签不能为空!")
        return;
      }
      this.form.content = this.$refs.editor.getData();
      this.form.tagUid = this.tagValue.join(",");
      this.$refs.form.validate((valid) => {
        if(!valid) {
          console.log("校验出错")
        } else {
          var params = this.form
          if (this.isEdit) {
            editBlog(this.form).then(response => {
              if (response.code == this.$ECode.SUCCESS) {
                this.$commonUtil.message.success(response.message)
                // this.$emit("beforeClose", "");
                setTimeout(()=> {
                  location.reload();
                }, 500)
              } else {
                this.$commonUtil.message.error(response.message)
              }
            });

          } else {
            addBlog(this.form).then(response => {
              if (response.code == this.$ECode.SUCCESS) {
                this.$commonUtil.message.success(response.message)
                // this.$emit("beforeClose", "");
                setTimeout(()=> {
                  location.reload();
                }, 500)

              } else {
                this.$commonUtil.message.error(response.message)
              }
            });
          }
        }
      })
    },
    blogSortList: function() {
      var blogSortParams = {};
      blogSortParams.pageSize = 100;
      blogSortParams.currentPage = 1;
      getBlogSortList(blogSortParams).then(response => {
        if(response.code == this.$ECode.SUCCESS) {
          this.blogSortData = response.data.records;
          this.sortOptions = response.data.records;
        }
      });
    },
    tagList: function() {
      var tagParams = {};
      tagParams.pageSize = 100;
      tagParams.currentPage = 1;
      getBlogTagList(tagParams).then(response => {
        this.tagData = response.data.records;
        this.tagOptions = response.data.records;
      });
    },
    checkPhoto: function () {
      this.imagecropperShow = true
    },
    // 关闭时的回调
    beforeClose(done) {
      //取消时，开始状态
      this.$emit("beforeClose", "");
      done();
    },
    cancel: function () {
      this.$emit("beforeClose", "");
    },
    // 头像裁剪关闭回调
    close() {
      this.imagecropperShow = false
    },
    cropSuccess(resData) {
      this.imagecropperShow = false
      this.imagecropperKey = this.imagecropperKey + 1
      console.log("裁剪后的数据", resData)
      let array = new Array()
      array.push(resData[0].url)
      this.form.photoList = array
      this.form.fileUid = resData[0].uid
    },
    deletePhoto: function() {
      console.log("删除")
      let bakFrom = this.form
      this.form = {}
      bakFrom.photoList = null;
      bakFrom.fileUid = "";
      this.form = bakFrom
    },

  }
}
</script>

<style scoped>
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  margin: 0, 0, 0, 10px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409eff;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width:  195px;
  height: 105px;
  line-height: 105px;
  text-align: center;
}
.imgBody {
  width:  195px;
  height: 105px;
  border: solid 2px #ffffff;
  float: left;
  position: relative;
}
.uploadImgBody {
  margin-left: 5px;
  width:  195px;
  height: 105px;
  border: dashed 1px #c0c0c0;
  float: left;
  position: relative;
}
.uploadImgBody :hover {
  border: dashed 1px #00ccff;
}
.inputClass {
  position: absolute;
}
</style>
