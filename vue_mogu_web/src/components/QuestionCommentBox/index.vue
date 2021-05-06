<template>
  <div>
    <div class="questionCommentBox">
<!--      <CKEditor ref="editor" :content="value" :height="260"></CKEditor>-->
      <MarkdownEditor :content="value" ref="editor" :height="350"></MarkdownEditor>
    </div>
    <div class="bottom">
      <el-button class="submit p2" type="primary"  @click="handleSubmit">提交回答</el-button>
      <el-button class="cancel p2" type="info" @click="handleCancle">取消</el-button>
    </div>
  </div>

</template>

<script>
  import {dateFormat} from '../../utils/webUtils'
  import {mapGetters, mapMutations} from 'vuex';
  import CKEditor from "@/components/CKEditor";
  import MarkdownEditor from "@/components/MarkdownEditor";


  export default {
    name: 'QuestionCommentBox',
    props: {
      userInfo: {
        type: Object
      },
      // 回复的对象
      toInfo: {
        type: Object
      },
      // 博客信息
      commentInfo: {
        type: Object
      },
      showCancel: {
        type: Boolean,
        default: true
      }
    },
    components: {
      CKEditor,
      MarkdownEditor
    },
    data() {
      return {
        comments: [],
        submitting: false,
        value: '',
        user: {},
        count: 1024
      }
    },
    computed: {
      ...mapGetters(['getUserPhoto'])
    },
    mounted() {

      // 获取宽高
      window.onresize = () => {
        return (() => {
          this.resizeWin();
        })();
      };
      this.resizeWin();
    },
    methods: {
      //拿到vuex中的写的方法
      ...mapMutations(['setLoginMessage']),
      vaildCount: function() {
        var count = 1024 - this.value.length;
        if(count <= 0) {
          this.count = 0
        } else {
          this.count = count;
        }
      },
      handleSubmit() {
        let info = this.$store.state.user.userInfo
        let isLogin = this.$store.state.user.isLogin
        let content = this.$refs.editor.getData(); //获取编辑器中的内容

        if(!isLogin) {
          this.$notify.error({
            title: '警告',
            message: '登录后才可以评论哦~',
            offset: 100
          });
          // 未登录，自动弹出登录框
          this.setLoginMessage(Math.random())
          return;
        }

        if(content == "") {
          this.$notify.error({
            title: '警告',
            message: '评论内容不能为空哦~',
            offset: 100
          });
          return;
        }

        let userUid = info.uid;
        let toUserUid = "";
        let toCommentUid = "";
        let questionUid = "";

        // 评论来源： MESSAGE_BOARD，ABOUT，BLOG_INFO 等 代表来自某些页面的评论
        let source = "";
        if(this.toInfo) {
          toUserUid = this.toInfo.uid;
          toCommentUid = this.toInfo.commentUid;
        }
        if(this.commentInfo) {
          questionUid = this.commentInfo.questionUid;
          source = this.commentInfo.source;
        }
        this.comments = {
          userUid: userUid,
          toCommentUid: toCommentUid,
          toUserUid: toUserUid,
          content: content,
          blogUid: questionUid,
          source: source,
          reply: [],
        }
        this.value = '';
        this.count = 1024;
        this.comments.createTime = dateFormat("YYYY-mm-dd HH:MM:SS", new Date());
        this.$emit("submit-box", this.comments)
      },
      handleCancle() {
        this.value = '';
        this.count = 1024;
        if(this.toInfo) {
          this.$emit("cancel-box", this.toInfo.commentUid)
        }
      },
      resizeWin() {
        //当前window 宽
        let centerWidth = document.documentElement.scrollWidth;
        if (centerWidth > 800) {

        } else {

        }
      },
    },
  };
</script>


<style>
  @import "../../assets/css/emoji.css";

  .emoji-panel-wrap {
    box-sizing: border-box;
    border: 1px solid #cccccc;
    border-radius: 5px;
    background-color: #ffffff;
    width: 650px;
    height: 350px;
    position: absolute;
    z-index: 99;
    top: 10px;
  }
  .emoji-size-small {
    zoom: 0.3;
    margin: 5px;
    vertical-align: middle;
  }
  .emoji-size-large {
    zoom: 0.5; // emojipanel表情大小
    margin: 5px;
  }
  .questionCommentBox {
    /*min-width: 700px;*/
    width: 100%;
    height: 350px;
    margin: 0 auto;
  }

  .bottom {
    position: relative;
    width: 98%;
    height: 60px;
    line-height: 40px;
    margin-top: 20px;
  }
  .bottom .p2 {
    float: right;
    margin-top: 5px;
    margin-right: 10px;
  }
  .emoji-panel-btn img{
    height: 35px;
    width: 35px;
  }
  .emoji-panel-btn:hover {
    cursor: pointer;
    opacity: 0.8;
  }
  .emoji-item-common {
    background: url("../../assets/img/emoji_sprite.png");
    display: inline-block;
  }
  .emoji-item-common:hover {
     cursor: pointer;
   }
  .emoji-size-small {
    // 表情大小
    zoom: 0.3;
  }

  @media only screen and (min-width: 300px) and (max-width: 767px) {

    .emoji-panel-wrap {
      box-sizing: border-box;
      border: 1px solid #cccccc;
      border-radius: 5px;
      background-color: #ffffff;
      width: 300px;
      height: 270px;
      position: absolute;
      z-index: 99;
      top: 10px;
    }
  }

</style>
