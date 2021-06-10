package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 问答模板表
 *
 * @author 陌溪
 * @since 2021年6月9日08:01:34
 */
@Data
@TableName("t_question_template")
public class QuestionTemplate extends SuperEntity<QuestionTemplate> {

    private static final long serialVersionUID = 1L;

    /**
     * 标签名
     */
    private String name;

    /**
     * 标签简介
     */
    private String summary;

    /**
     * 模板内容
     */
    private String content;

    /**
     * 是否发布
     */
    private String isPublish;

    /**
     * 排序字段，数值越大，越靠前
     */
    private int sort;
}
