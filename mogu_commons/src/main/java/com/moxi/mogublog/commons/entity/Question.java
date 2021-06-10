package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

import java.util.List;

/**
 * 问答表
 *
 * @author 陌溪
 * @date 2021年4月26日21:46:29
 */
@Data
@TableName("t_question")
public class Question extends SuperEntity<Question> {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一oid【自动递增】
     */
    private Integer oid;

    /**
     * 问答标题
     */
    private String title;

    /**
     * 问答简介
     * updateStrategy = FieldStrategy.IGNORED ：表示更新时候忽略非空判断
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String summary;

    /**
     * 问答内容
     */
    private String content;

    /**
     * 问答标签uid
     */
    private String questionTagUid;

    /**
     * 问答模板UID
     */
    private String questionTemplateUid;

    /**
     * 问答点击数
     */
    private Integer clickCount;

    /**
     * 回答次数
     */
    private Integer replyCount;

    /**
     * 问答收藏数
     */
    private Integer collectCount;

    /**
     * 标题图片UID
     */
    @TableField(updateStrategy = FieldStrategy.IGNORED)
    private String fileUid;

    /**
     * 管理员UID
     */
    private String adminUid;

    /**
     * 是否发布
     */
    private String isPublish;

    /**
     * 排序字段，数值越大，越靠前
     */
    private Integer sort;

    /**
     * 是否开启评论(0:否， 1:是)
     */
    private String openComment;

    /**
     * 用户UID
     */
    private String userUid;

    /**
     * 问答状态，0:创建，1:回答，2:已采纳
     */
    private int questionStatus;


    /**
     * 问答来源【0 后台添加，1 用户投稿】
     */
    private String questionSource;


    // 以下字段不存入数据库，封装为了方便使用

    /**
     * 标签,一篇问答对应多个标签
     */
    @TableField(exist = false)
    private List<Tag> tagList;

    /**
     * 问答标题图
     */
    @TableField(exist = false)
    private String photoUrl;

    /**
     * 问答标签列表
     */
    @TableField(exist = false)
    private List<QuestionTag> questionTagList;

    /**
     * 提问用户
     */
    @TableField(exist = false)
    private User user;

}
