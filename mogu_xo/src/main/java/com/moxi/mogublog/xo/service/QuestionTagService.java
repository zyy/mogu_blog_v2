package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.QuestionTag;
import com.moxi.mogublog.xo.vo.QuestionTagVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 问答标签表 服务类
 *
 * @author 陌溪
 * @date 2021年4月26日22:10:12
 */
public interface QuestionTagService extends SuperService<QuestionTag> {

    /**
     * 获取问答标签列表
     *
     * @param questionTagVO
     * @return
     */
    IPage<QuestionTag> getPageList(QuestionTagVO questionTagVO);

    /**
     * 获取全部问答标签列表
     *
     * @return
     */
    List<QuestionTag> getList();

    /**
     * 新增问答标签
     *
     * @param questionTagVO
     * @return
     */
    String addQuestionTag(QuestionTagVO questionTagVO);

    /**
     * 编辑问答标签
     *
     * @param questionTagVO
     * @return
     */
    String editQuestionTag(QuestionTagVO questionTagVO);

    /**
     * 批量删除问答标签
     *
     * @param questionTagVOList
     * @return
     */
    String deleteBatchQuestionTag(List<QuestionTagVO> questionTagVOList);

}
