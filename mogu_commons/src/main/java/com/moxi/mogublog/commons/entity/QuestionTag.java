package com.moxi.mogublog.commons.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.moxi.mougblog.base.entity.SuperEntity;
import lombok.Data;

/**
 * 问答标签表
 *
 * @author 陌溪
 * @since 2021年4月26日21:40:40
 */
@Data
@TableName("t_question_tag")
public class QuestionTag extends SuperEntity<QuestionTag> {

    private static final long serialVersionUID = 1L;

    /**
     * 父标签Uid
     */
    private String parentUid;

    /**
     * 标签名
     */
    private String name;

    /**
     * 标签简介
     */
    private String summary;

    /**
     * 点击量
     */
    private int clickCount;

    /**
     * 排序字段，数值越大，越靠前
     */
    private int sort;
}
