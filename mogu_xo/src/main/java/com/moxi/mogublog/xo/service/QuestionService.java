package com.moxi.mogublog.xo.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.moxi.mogublog.commons.entity.Question;
import com.moxi.mogublog.xo.vo.QuestionVO;
import com.moxi.mougblog.base.service.SuperService;

import java.util.List;

/**
 * 问答表 服务类
 *
 * @author 陌溪
 * @date 2021年4月26日22:10:12
 */
public interface QuestionService extends SuperService<Question> {
    /**
     * 获取问答列表
     *
     * @param questionVO
     * @return
     */
    IPage<Question> getPageList(QuestionVO questionVO);

    /**
     * 新增问答
     *
     * @param questionVO
     */
    String addQuestion(QuestionVO questionVO);

    /**
     * 编辑问答
     *
     * @param questionVO
     */
    String editQuestion(QuestionVO questionVO);

    /**
     * 批量删除问答
     *
     * @param questionVOList
     */
    String deleteBatchQuestion(List<QuestionVO> questionVOList);

}
