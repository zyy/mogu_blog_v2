package com.moxi.mogublog.xo.vo;

import com.moxi.mougblog.base.validator.annotion.NotBlank;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

/**
 * QuestionTemplateVO
 *
 * @author: 陌溪
 * @create: 2021年6月9日08:02:37
 */
@Data
public class QuestionTemplateVO extends BaseVO<QuestionTemplateVO> {


    /**
     * 模板名称
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String name;

    /**
     * 标签简介
     */
    private String summary;

    /**
     * 模板内容
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String content;

    /**
     * 是否发布
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String isPublish;

    /**
     * 排序字段
     */
    private Integer sort;

    /**
     * OrderBy排序字段（desc: 降序）
     */
    private String orderByDescColumn;

    /**
     * OrderBy排序字段（asc: 升序）
     */
    private String orderByAscColumn;

    /**
     * 无参构造方法，初始化默认值
     */
    QuestionTemplateVO() {

    }

}
