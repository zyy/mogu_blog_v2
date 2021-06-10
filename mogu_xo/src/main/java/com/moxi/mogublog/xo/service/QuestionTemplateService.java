package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.QuestionTemplate;
import com.moxi.mogublog.xo.vo.QuestionTemplateVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 问答模板表 服务类
 *
 * @author 陌溪
 * @date 2021年6月9日08:04:46
 */
public interface QuestionTemplateService extends SuperService<QuestionTemplate> {

    /**
     * 获取问答标签列表
     *
     * @param questionTemplateVO
     * @return
     */
    IPage<QuestionTemplate> getPageList(QuestionTemplateVO questionTemplateVO);

    /**
     * 新增问答标签
     *
     * @param questionTemplateVO
     * @return
     */
    String addQuestionTemplate(QuestionTemplateVO questionTemplateVO);

    /**
     * 编辑问答标签
     *
     * @param questionTemplateVO
     * @return
     */
    String editQuestionTemplate(QuestionTemplateVO questionTemplateVO);

    /**
     * 批量删除问答标签
     *
     * @param questionTemplateVOList
     * @return
     */
    String deleteBatchQuestionTemplate(List<QuestionTemplateVO> questionTemplateVOList);

}
