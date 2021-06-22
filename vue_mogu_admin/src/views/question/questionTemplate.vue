<template>
  <div class="app-container">
    <!-- 查询和其他操作 -->
    <div class="filter-container" style="margin: 10px 0 10px 0;" v-permission="'/blog/getList'">

      <el-row :gutter="10" style="margin-bottom: 8px;">
        <el-col :span="1.5">
          <el-button class="filter-item" type="primary" @click="handleAdd" icon="el-icon-edit" v-permission="'/blog/add'">添加模板</el-button>
        </el-col>

        <el-col :span="1.5">
          <el-button class="filter-item" type="danger" @click="handleDeleteBatch" icon="el-icon-delete" v-permission="'/blog/deleteBatch'">删除选中</el-button>
        </el-col>

      </el-row>

    </div>

    <el-table :data="tableData"
              ref="articleTable"
              style="width: 100%"
              @selection-change="handleSelectionChange"
              @sort-change="changeSort"
              :default-sort="{prop: 'createTime', order: 'descending'}">
      <el-table-column type="selection"></el-table-column>

      <el-table-column label="序号" width="60" align="center">
        <template slot-scope="scope">
          <span>{{scope.$index + 1}}</span>
        </template>
      </el-table-column>

      <el-table-column label="模板名称" width="160" align="center">
        <template slot-scope="scope">
          <span style="cursor:pointer;">{{ scope.row.name }}</span>
        </template>
      </el-table-column>


      <el-table-column label="简介" width="160" align="center">
        <template slot-scope="scope">
          <span style="cursor:pointer;">{{ scope.row.summary }}</span>
        </template>
      </el-table-column>

<!--      <el-table-column label="详情" width="500" align="center">-->
<!--        <template slot-scope="scope">-->
<!--          <span v-html="scope.row.content">{{ scope.row.content }}</span>-->
<!--        </template>-->
<!--      </el-table-column>-->

      <el-table-column label="排序" width="90" align="center" prop="clickCount" sortable="custom" :sort-by="['clickCount']">
        <template slot-scope="scope">
          <span v-html="">{{ scope.row.sort }}</span>
        </template>
      </el-table-column>

      <el-table-column label="创建时间" width="160" align="center" prop="createTime" sortable="custom" :sort-orders="['ascending', 'descending']">
        <template slot-scope="scope">
          <span>{{ scope.row.createTime }}</span>
        </template>
      </el-table-column>

      <el-table-column label="操作" fixed="right" min-width="150">
        <template slot-scope="scope">
          <el-button @click="handleEdit(scope.row)" type="primary" size="small" v-permission="'/blog/edit'">编辑</el-button>
          <el-button @click="handleDelete(scope.row)" type="danger" size="small" v-permission="'/blog/delete'">删除</el-button>
        </template>
      </el-table-column>

    </el-table>

    <!--分页-->
    <div class="block">
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page.sync="currentPage"
        :page-size="pageSize"
        layout="total, prev, pager, next, jumper"
        :total="total"
      ></el-pagination>
    </div>

    <!-- 添加或修改对话框 -->
    <el-dialog
      :title="title"
      :visible.sync="dialogFormVisible"
      :before-close="closeDialog"
      fullscreen
    >
      <el-form :model="form" :rules="rules" ref="form">

        <el-row>
          <el-col :span="16">
            <el-form-item label="模板名称" :label-width="formLabelWidth" prop="title">
              <el-input v-model="form.name" auto-complete="off"></el-input>
            </el-form-item>

            <el-form-item label="简介" :label-width="formLabelWidth">
              <el-input v-model="form.summary" auto-complete="off"></el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-row>
          <el-col :span="4.5">
            <el-form-item label="是否发布" :label-width="lineLabelWidth" prop="isPublish">
              <el-radio-group v-model="form.isPublish" size="small">
                <el-radio v-for="item in questionTemplatePublishDictList" :key="item.uid" :label="item.dictValue" border>{{item.dictLabel}}</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="内容" :label-width="formLabelWidth" prop="content">
          <CKEditor v-if="systemConfig.editorModel == '0'" ref="editor" :content="form.content" :height="360"></CKEditor>
          <MarkdownEditor v-if="systemConfig.editorModel == '1'" :content="form.content" ref="editor" :height="465"></MarkdownEditor>
        </el-form-item>

        <el-form-item style="float: right; margin-right: 20px;">
          <el-button @click="dialogFormVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitForm">确 定</el-button>
        </el-form-item>
      </el-form>
    </el-dialog>

  </div>
</template>

<script>
import { getQuestionTemplateList, addQuestionTemplate, editQuestionTemplate, deleteBatchQuestionTemplate } from "@/api/questionTemplate";
import { getSystemConfig} from "@/api/systemConfig";
import {formatData} from "@/utils/webUtils";
import { getCookie } from "@/utils/cookieUtils";
import {getListByDictTypeList} from "@/api/sysDictData"
import CheckPhoto from "../../components/CheckPhoto";
import CKEditor from "../../components/CKEditor";
import MarkdownEditor from "../../components/MarkdownEditor";
var querystring = require("querystring");
import { Loading } from 'element-ui';
export default {
  components: {
    CheckPhoto,
    CKEditor,
    MarkdownEditor
  },
  data() {
    return {
      queryParams:{
        keyword: "",
        tagKeyword: "", //标签搜索
        levelKeyword: "", //等级搜索
        publishKeyword: "", // 发布 搜索
      }, // 搜索条件
      pictureList: [], // 上传的图片列表
      BLOG_WEB_URL: process.env.BLOG_WEB_URL,
      multipleSelection: [], //多选，用于批量删除
      tagOptions: [], //标签候选框
      loading: false, //搜索框加载状态
      uploadLoading: null, //文件上传loading
      CKEditorData: null,
      tableData: [], //问答数据
      tagData: [], //标签数据
      tagValue: [], //保存选中标签id(编辑时)
      currentPage: 1,
      pageSize: 10,
      total: 0, //总数量
      title: "增加模板",
      dialogFormVisible: false, //控制弹出框
      subjectVisible: false, // 是否显示专题
      isFirstSubjectVisible: true, // 专题选择器是否首次显示【用于懒加载】
      formLabelWidth: "120px",
      lineLabelWidth: "120px", //一行的间隔数
      maxLineLabelWidth: "100px",
      isEditForm: false,
      photoVisible: false, //控制图片选择器的显示
      isFirstPhotoVisible: true, // 图片选择器是否首次显示【用于懒加载】
      photoList: [],
      fileIds: "",
      icon: false, //控制删除图标的显示
      interval: null, //定义触发器
      isChange: false, // 表单内容是否改变
      changeCount: 0, // 改变计数器
      questionTemplatePublishDictList: [], //是否字典
      questionStatusDictList: [], // 问答状态字典
      questionTemplatePublishDefault: null, //问答发布默认值
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
        fileUid: null,
        isPublish: null,
        author: null, //作者
        clickCount: 0
      },
      rules: {
        name: [
          {required: true, message: '名称不能为空', trigger: 'blur'}
        ],
        isPublish: [
          {required: true, message: '发布字段不能为空', trigger: 'blur'},
          {pattern: /^[0-9]\d*$/, message: '发布字段只能为自然数'},
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
  created() {
    // 判断是否需要展开条件查询
    this.getShowSearch()
    // 获取系统配置
    this.getSystemConfigList()
    // 获取字典
    this.getDictList()
    //获取问答列表
    this.questionTemplateList()
  },
  methods: {
    // 从后台获取数据,重新排序
    changeSort (val) {
      // 根据当前排序重新获取后台数据,一般后台会需要一个排序的参数
      if(val.order == "ascending") {
        this.orderByAscColumn = val.prop
        this.orderByDescColumn = ""
      } else {
        this.orderByAscColumn = ""
        this.orderByDescColumn = val.prop
      }
      this.questionTemplateList()
    },
    openLoading() {
      this.uploadLoading = Loading.service({
        lock: true,
        text: '正在努力上传中……'
      })
    },
    closeLoading() {
      this.uploadLoading.close()
    },
    // 判断是否需要展开条件查询
    getShowSearch: function () {
      let showSearch = getCookie("showSearch")
      if(showSearch == "false"){ //此时的hasAuth是true
        this.showSearch = false
      } else {
        this.showSearch = true
      }
    },
    resetBlogList: function (){
      this.queryParams = {}
      this.questionTemplateList();
    },
    questionTemplateList: function() {
      var params = {};
      params.keyword = this.queryParams.keyword;
      params.isPublish = this.queryParams.publishKeyword;
      params.currentPage = this.currentPage;
      params.pageSize = this.pageSize;
      params.orderByDescColumn = this.orderByDescColumn
      params.orderByAscColumn = this.orderByAscColumn
      getQuestionTemplateList(params).then(response => {
        if(response.code == this.$ECode.SUCCESS) {
          this.tableData = response.data.records;
          this.currentPage = response.data.current;
          this.pageSize = response.data.size;
          this.total = response.data.total;
        }
      });
    },
    /**
     * 字典查询
     */
    getDictList: function () {
      var dictTypeList =  ['sys_publish_status']
      getListByDictTypeList(dictTypeList).then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          var dictMap = response.data;
          this.questionTemplatePublishDictList = dictMap.sys_publish_status.list
          if(dictMap.sys_publish_status.defaultValue) {
            this.questionTemplatePublishDefault = dictMap.sys_publish_status.defaultValue;
          }
        }
      });
    },
    getFormObject: function() {
      var formObject = {
        uid: null,
        name: null,
        summary: null,
        content: null,
        isPublish:  this.questionTemplatePublishDefault, //是否发布
        sort: 0,
      };
      return formObject;
    },
    // 获取系统配置
    getSystemConfigList: function() {
      getSystemConfig().then(response => {
        if (response.code == this.$ECode.SUCCESS) {
          if (response.data) {
            this.systemConfig = response.data;
          }
        }
      });
    },
    submitStr: function(str, index) {
      if (str.length > index) {
        return str.slice(0, index) + "...";
      }
      return str;
    },
    // 关闭窗口
    closeDialog(done) {
      if(this.isChange) {
        this.$confirm("是否关闭问答编辑窗口", "提示", {
          confirmButtonText: "确定",
          cancelButtonText: "取消",
          type: "warning"
        })
          .then(() => {
            this.isChange = false;
            this.changeCount = 0
            done();
          })
          .catch(() => {
            this.$commonUtil.message.info("已取消")
          });
      } else {
        this.isChange = false;
        this.changeCount = 0
        done();
      }
    },
    handleFind: function() {
      this.currentPage = 1
      this.questionTemplateList();
    },
    handleAdd: function() {
      this.title = "增加模板"
      let that = this
      this.dialogFormVisible = true;
      this.form = this.getFormObject();
      this.$nextTick(() => {
        //初始化内容
        that.$refs.editor.initData();
      });
      this.isEditForm = false;
    },
    handleEdit: function(row) {
      var that = this;
      this.title = "编辑模板";
      this.form = row;
      this.$nextTick(() => {
        //DOM现在更新了
        console.log("更新内容", that.form)
        that.$refs.editor.setData(that.form.content); //设置富文本内容
      });
      that.dialogFormVisible = true;
      that.isEditForm = true;
    },
    handleDelete: function(row) {
      var that = this;
      this.$confirm("此操作将把问答删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          let params = {};
          params.uid = row.uid;
          let data = [params]
          deleteBatchQuestionTemplate(data).then(response => {
            that.$commonUtil.message.success(response.message)
            that.questionTemplateList();
          });
        })
        .catch(() => {
          that.$commonUtil.message.info("已取消删除")
        });
    },
    handleDeleteBatch: function(row) {
      var that = this;
      if(that.multipleSelection.length <= 0 ) {
        that.$commonUtil.message.error("请先选中需要删除的问答")
        return;
      }
      this.$confirm("此操作将把选中问答删除, 是否继续?", "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      })
        .then(() => {
          deleteBatchQuestionTemplate(that.multipleSelection).then(response => {
            if (response.code == this.$ECode.SUCCESS) {
              that.$commonUtil.message.success(response.message)
              that.questionTemplateList();
            } else {
              that.$commonUtil.message.error(response.message)
            }
          });
        })
        .catch(() => {
          that.$commonUtil.message.info("已取消删除")
        });
    },
    handleCurrentChange: function(val) {
      this.currentPage = val;
      this.questionTemplateList();
    },
    submitForm: function() {
      this.form.content = this.$refs.editor.getData(); //获取CKEditor中的内容
      this.$refs.form.validate((valid) => {
        if(!valid) {

        } else {
          if (this.isEditForm) {
            editQuestionTemplate(this.form).then(response => {
              if (response.code == this.$ECode.SUCCESS) {
                this.$commonUtil.message.success(response.message)
                this.dialogFormVisible = false;
                this.questionTemplateList();
              } else {
                this.$commonUtil.message.error(response.message)
              }
            });

          } else {
            addQuestionTemplate(this.form).then(response => {
              if (response.code == this.$ECode.SUCCESS) {
                this.$commonUtil.message.success(response.message)
                this.dialogFormVisible = false;
                this.questionTemplateList();
              } else {
                this.$commonUtil.message.error(response.message)
              }
            });
          }
        }
      })
    },
    // 改变多选
    handleSelectionChange(val) {
      this.multipleSelection = val;
    }
  }
};
</script>
<style scoped>


</style>
