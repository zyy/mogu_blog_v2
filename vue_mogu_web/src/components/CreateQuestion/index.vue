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
              <el-input v-model="form.title" auto-complete="off" @input="contentChange"></el-input>
            </el-form-item>

            <el-form-item label="简介" :label-width="formLabelWidth">
              <el-input v-model="form.summary" auto-complete="off" @input="contentChange"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="6.5">
            <el-form-item label="标签" :label-width="formLabelWidth" prop="questionTagUid">
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
                  :label="item.name"
                  :value="item.uid"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>

          <el-col :span="6.5">
            <el-form-item label="问答模板" :label-width="formLabelWidth">
              <el-select
                v-model="form.questionTemplateUid"
                size="small"
                placeholder="请选择"
                style="width:210px"
                filterable
                clearable
                @change="templateChange"
              >
                <el-option
                  v-for="item in templateData"
                  :key="item.uid"
                  :label="item.name"
                  :value="item.uid"
                ></el-option>
              </el-select>
            </el-form-item>
          </el-col>

<!--          <el-col :span="6.5">-->
<!--            <el-form-item label="问答回复" :label-width="formLabelWidth" prop="openComment">-->
<!--              <el-radio v-for="item in openDictList" :key="item.uid" v-model="form.openComment" :label="item.dictValue" border size="small">{{item.dictLabel}}</el-radio>-->
<!--            </el-form-item>-->
<!--          </el-col>-->

<!--          <el-col :span="4.5">-->
<!--            <el-form-item label="是否发布" :label-width="lineLabelWidth" prop="isPublish">-->
<!--              <el-radio-group v-model="form.isPublish" size="small">-->
<!--                <el-radio v-for="item in blogPublishDictList" :key="item.uid" :label="item.dictValue" border>{{item.dictLabel}}</el-radio>-->
<!--              </el-radio-group>-->
<!--            </el-form-item>-->
<!--          </el-col>-->

        </el-row>

        <el-form-item label="内容" :label-width="formLabelWidth" prop="content">
          <CKEditor ref="editor" :content="form.content" :height="360"></CKEditor>
        </el-form-item>

        <el-form-item style="float: right; margin-right: 20px;">
          <el-button @click="cancel">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

  </div>

</template>

<script>
import CKEditor from "../CKEditor";
import {getListByDictTypeList} from "@/api/sysDictData"
import { getQuestionTagList, addQuestion, editQuestion, getQuestionTemplateList } from "@/api/question";
import {formatData} from "@/utils/webUtils";

export default {
  props: ["visible", "isEdit", "formData"],
  created() {
    this.questionTagList()
    this.questionTemplateList()
  },
  components: {
    CKEditor
  },
  watch: {
    visible: function() {
      this.dialogFormVisible = this.visible;
    },
  },
  data() {
    return {
      multipleSelection: [], //多选，用于批量删除
      tagOptions: [], //标签候选框
      loading: false, //搜索框加载状态
      uploadLoading: null, //文件上传loading
      CKEditorData: null,
      tableData: [], //问答数据
      tagData: [], //标签数据
      tagValue: [], //保存选中标签id(编辑时)
      templateData: [],
      templateValue: "",
      currentPage: 1,
      pageSize: 10,
      total: 0, //总数量
      title: "增加问答",
      dialogFormVisible: this.visible, //控制弹出框
      subjectVisible: false, // 是否显示专题
      formLabelWidth: "120px",
      lineLabelWidth: "120px", //一行的间隔数
      maxLineLabelWidth: "100px",
      photoVisible: false, //控制图片选择器的显示
      isFirstPhotoVisible: true, // 图片选择器是否首次显示【用于懒加载】
      photoList: [],
      fileIds: "",
      icon: false, //控制删除图标的显示
      interval: null, //定义触发器
      isChange: false, // 表单内容是否改变
      changeCount: 0, // 改变计数器
      blogPublishDictList: [], //是否字典
      questionStatusDictList: [], // 问答状态字典
      openDictList: [], // 是否启动字典
      blogPublishDefault: null, //问答发布默认值
      openDefault: null, // 是否开启评论默认值
      questionStatusDefault: null, // 问答状态默认值
      fileList: [],
      localUploadVisible: false,
      systemConfig: {}, // 系统配置
      orderByDescColumn: "", // 降序字段
      orderByAscColumn: "", // 升序字段
      form: {
        uid: null,
        title: null,
        summary: null,
        content: null,
        questionTagUid: null,
        fileUid: null,
        isPublish: null,
        author: null, //作者
        clickCount: 0
      },
      rules: {
        title: [
          {required: true, message: '标题不能为空', trigger: 'blur'}
        ],
        isPublish: [
          {required: true, message: '发布字段不能为空', trigger: 'blur'},
          {pattern: /^[0-9]\d*$/, message: '发布字段只能为自然数'},
        ],
        questionTagUid: [
          {required: true, message: '问答标签不能为空', trigger: 'blur'}
        ],
        content: [
          {required: true, message: '内容不能为空', trigger: 'blur'}
        ],
        outsideLink: [
          {required: true, message: '外链地址不能为空', trigger: 'blur'},
          {pattern:  /^((https|http|ftp|rtsp|mms)?:\/\/)[^\s]+/, message: '请输入有效的URL'},
        ],
      }
    };
  },
  mounted() {
    console.log("开启问答编辑")
    this.getDictList()
  },
  methods: {
    /**
     * 字典查询
     */
    getDictList: function () {
      let that = this
      var dictTypeList =  ['sys_publish_status', 'sys_normal_disable', 'sys_question_status']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          var dictMap = response.data;
          this.blogPublishDictList = dictMap.sys_publish_status.list
          this.openDictList = dictMap.sys_normal_disable.list
          this.questionStatusDictList = dictMap.sys_question_status.list

          if(dictMap.sys_publish_status.defaultValue) {
            this.blogPublishDefault = dictMap.sys_publish_status.defaultValue;
          }
          if(dictMap.sys_normal_disable.defaultValue) {
            this.openDefault = dictMap.sys_normal_disable.defaultValue;
          }
          if(dictMap.sys_question_status.defaultValue) {
            this.questionStatusDefault = dictMap.sys_question_status.defaultValue;
          }
        }

        console.log("传递过来的问答", this.isEdit)
        console.log("传递过来的问答", this.formData)
        if(this.isEdit) {
          this.form = this.formData
          setTimeout(()=>{
            that.$refs.editor.setData(that.form.content); //设置富文本内容
          },100)
          that.tagValue = [];
          if (this.form.questionTagList) {
            let json = this.form.questionTagList;
            for (var i = 0, l = json.length; i < l; i++) {
              if (json[i] != null) {
                that.tagValue.push(json[i]["uid"]);
              }
            }
          }
          console.log("this.form", this.form)
        } else {
          this.form = this.getFormObject()
        }
      });

    },
    getFormObject: function() {
      var formObject = {
        uid: null,
        title: null,
        summary: null,
        content: null,
        tagUid: null,
        fileUid: null,
        isPublish: this.blogPublishDefault, //是否发布
        questionTemplateUid: "", //问答模板UID
        type: this.blogTypeDefault, // 文章类型
        author: null, //作者
        openComment: this.openDefault, // 是否启动
      };
      return formObject;
    },
    submitForm: function() {
      this.form.content = this.$refs.editor.getData(); //获取CKEditor中的内容
      this.form.questionTagUid = this.tagValue.join(",");
      this.$refs.form.validate((valid) => {
        if(!valid) {

        } else {
          let params = formatData(this.form);
          if (this.isEdit) {
            editQuestion(this.form).then(response => {
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
            addQuestion(this.form).then(response => {
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
    questionTemplateList: function () {
      let params = {};
      params.pageSize = 500;
      params.currentPage = 1;
      getQuestionTemplateList(params).then(response => {
        this.templateData = response.data.records;
      });
    },
    templateChange: function (templateUid) {
      let that = this
      let templateData = this.templateData
      for(let a=0; a<templateData.length; a++) {
        if(templateData[a].uid == templateUid) {
          this.$nextTick(() => {
            //DOM现在更新了
            that.form.content = templateData[a].content
            that.$refs.editor.setData(that.form.content); //设置富文本内容
          });
          break;
        }
      }
    },
    questionTagList: function() {
      let tagParams = {};
      tagParams.pageSize = 500;
      tagParams.currentPage = 1;
      getQuestionTagList(tagParams).then(response => {
        this.tagData = response.data.records;
        this.tagOptions = response.data.records;
      });
    },
    cancel: function () {
      this.$emit("beforeClose", "");
    },
    // 关闭时的回调
    beforeClose(done) {
      //取消时，开始状态
      this.$emit("beforeClose", "");
      done();
    },
    // 内容改变，触发监听
    contentChange: function() {

    },

  }
}
</script>

<style scoped>

</style>
