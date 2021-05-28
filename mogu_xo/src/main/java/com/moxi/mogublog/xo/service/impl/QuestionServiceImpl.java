package com.moxi.mogublog.xo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.moxi.mogublog.commons.entity.*;
import com.moxi.mogublog.utils.IpUtils;
import com.moxi.mogublog.utils.RedisUtil;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.xo.global.MessageConf;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.global.SQLConf;
import com.moxi.mogublog.xo.global.SysConf;
import com.moxi.mogublog.xo.mapper.QuestionMapper;
import com.moxi.mogublog.xo.service.QuestionService;
import com.moxi.mogublog.xo.service.QuestionTagService;
import com.moxi.mogublog.xo.service.UserService;
import com.moxi.mogublog.xo.vo.QuestionVO;
import com.moxi.mougblog.base.enums.EPublish;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.exception.exceptionType.InsertException;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.global.BaseSQLConf;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.global.ECode;
import com.moxi.mougblog.base.global.ErrorCode;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.serviceImpl.SuperServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 问答表 服务实现类
 *
 * @author 陌溪
 * @date 2021年4月26日22:56:25
 */
@Service
public class QuestionServiceImpl extends SuperServiceImpl<QuestionMapper, Question> implements QuestionService {

    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionTagService questionTagService;
    @Autowired
    private UserService userService;
    @Autowired
    private RedisUtil redisUtil;

    @Override
    public IPage<Question> getPageList(QuestionVO questionVO) {
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        // 构建搜索条件
        if (StringUtils.isNotEmpty(questionVO.getKeyword()) && !StringUtils.isEmpty(questionVO.getKeyword().trim())) {
            queryWrapper.like(SQLConf.TITLE, questionVO.getKeyword().trim());
        }
        if (!StringUtils.isEmpty(questionVO.getQuestionTagUid())) {
            queryWrapper.like(SQLConf.TAG_UID, questionVO.getQuestionTagUid());
        }
        if (!StringUtils.isEmpty(questionVO.getIsPublish())) {
            queryWrapper.eq(SQLConf.IS_PUBLISH, questionVO.getIsPublish());
        }
        if (!StringUtils.isEmpty(questionVO.getUserUid())) {
            queryWrapper.eq(SQLConf.USER_UID, questionVO.getUserUid());
        }

        //分页
        Page<Question> page = new Page<>();
        page.setCurrent(questionVO.getCurrentPage());
        page.setSize(questionVO.getPageSize());
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);

        // 判断问答方法
        if(StringUtils.isNotEmpty(questionVO.getMethodType())) {
            String methodType = questionVO.getMethodType();
            switch (methodType) {
                case "newQuestion": {
                    queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
                }; break;
                case "hotQuestion": {
                    queryWrapper.orderByDesc(SQLConf.REPLY_COUNT);
                }; break;
                case "waitQuestion": {
                    queryWrapper.eq(SQLConf.REPLY_COUNT, 0);
                    queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
                }; break;
                default: {
                    throw new QueryException("输入错误的参数");
                }
            }

        } else {
            if (StringUtils.isNotEmpty(questionVO.getOrderByAscColumn())) {
                // 将驼峰转换成下划线
                String column = StringUtils.underLine(new StringBuffer(questionVO.getOrderByAscColumn())).toString();
                queryWrapper.orderByAsc(column);
            } else if (StringUtils.isNotEmpty(questionVO.getOrderByDescColumn())) {
                // 将驼峰转换成下划线
                String column = StringUtils.underLine(new StringBuffer(questionVO.getOrderByDescColumn())).toString();
                queryWrapper.orderByDesc(column);
            } else {
                // 使用默认按sort值大小倒序
                queryWrapper.orderByDesc(SQLConf.SORT);
            }
        }

        IPage<Question> pageList = questionService.page(page, queryWrapper);
        List<Question> questionList = pageList.getRecords();
        this.setQuestionTagAndUser(questionList);
        pageList.setRecords(questionList);
        return pageList;
    }

    @Override
    public Map<String, Object> getQuestionByKeyword(String keywords, Long currentPage, Long pageSize) {
        final String keyword = keywords.trim();
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper -> wrapper.like(SQLConf.TITLE, keyword).or().like(SQLConf.SUMMARY, keyword));
        queryWrapper.eq(SQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.eq(SQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.select(Question.class, i -> !i.getProperty().equals(SQLConf.CONTENT));
        queryWrapper.orderByDesc(SQLConf.CLICK_COUNT);
        Page<Question> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);

        IPage<Question> iPage = questionService.page(page, queryWrapper);
        List<Question> questionList = iPage.getRecords();
        questionList.forEach(item -> {
            // 给标题和简介设置高亮
            item.setTitle(StringUtils.getHitCode(item.getTitle(), keyword));
            item.setSummary(StringUtils.getHitCode(item.getSummary(), keyword));
        });
        this.setQuestionTagAndUser(questionList);

        Map<String, Object> map = new HashMap<>();
        // 返回总记录数
        map.put(SysConf.TOTAL, iPage.getTotal());
        // 返回总页数
        map.put(SysConf.TOTAL_PAGE, iPage.getPages());
        // 返回当前页大小
        map.put(SysConf.PAGE_SIZE, pageSize);
        // 返回当前页
        map.put(SysConf.CURRENT_PAGE, iPage.getCurrent());
        // 返回数据
        map.put(SysConf.QUESTION_LIST, questionList);
        return map;
    }

    @Override
    public IPage<Question> getQuestionListByAuthor(String author, Long currentPage, Long pageSize) {

        QueryWrapper<User> userQueryWrapper = new QueryWrapper<>();
        userQueryWrapper.eq(SQLConf.NICK_NAME, author);
        List<User> userList = userService.list(userQueryWrapper);
        List<String> userUidList = new ArrayList<>();
        userList.forEach(item -> {
            userUidList.add(item.getUid());
        });
        IPage<Question> pageList = new Page<>();
        if(userUidList.size() == 0) {
            return pageList;
        }

        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        Page<Question> page = new Page<>();
        page.setCurrent(currentPage);
        page.setSize(pageSize);
        queryWrapper.in(SQLConf.USER_UID, userUidList);
        queryWrapper.eq(BaseSQLConf.IS_PUBLISH, EPublish.PUBLISH);
        queryWrapper.eq(BaseSQLConf.STATUS, EStatus.ENABLE);
        queryWrapper.orderByDesc(SQLConf.CREATE_TIME);
        queryWrapper.select(Question.class, i -> !i.getProperty().equals(SysConf.CONTENT));
        pageList = questionService.page(page, queryWrapper);
        List<Question> list = pageList.getRecords();
        this.setQuestionTagAndUser(list);
        pageList.setRecords(list);
        return pageList;
    }

    @Override
    public String getQuestion(QuestionVO questionVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        String ip = IpUtils.getIpAddr(request);
        if (questionVO.getOid() == 0) {
            throw new QueryException(ErrorCode.PARAM_INCORRECT, MessageConf.PARAM_INCORRECT);
        }
        QueryWrapper<Question> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(SysConf.OID, questionVO.getOid());
        queryWrapper.last(SysConf.LIMIT_ONE);
        Question question = questionService.getOne(queryWrapper);
        if (question == null || question.getStatus() == EStatus.DISABLED || EPublish.NO_PUBLISH.equals(question.getIsPublish())) {
            return ResultUtil.result(ECode.ERROR, MessageConf.BLOG_IS_DELETE);
        }
        List<Question> questionList = new ArrayList<>();
        questionList.add(question);
        this.setQuestionTagAndUser(questionList);
        //从Redis取出数据，判断该用户是否点击过
        String jsonResult = redisUtil.get(RedisConf.QUESTION_CLICK + Constants.SYMBOL_COLON + ip + Constants.SYMBOL_WELL + question.getOid());
        if (StringUtils.isEmpty(jsonResult)) {
            //给问答点击数增加
            Integer clickCount = question.getClickCount() + 1;
            question.setClickCount(clickCount);
            question.updateById();
            //将该用户点击记录存储到redis中, 24小时后过期
            redisUtil.setEx(RedisConf.QUESTION_CLICK + Constants.SYMBOL_COLON + ip + Constants.SYMBOL_WELL + question.getOid(), question.getClickCount().toString(),
                    24, TimeUnit.HOURS);
        }

        // 判断是否开启图片懒加载
        if(Constants.STR_ONE.equals(questionVO.getIsLazy())) {
            String blogContent = question.getContent();
            if(StringUtils.isNotEmpty(blogContent)) {
                question.setContent(blogContent.replaceAll(" src=", " data-src="));
            }
        }

        return ResultUtil.successWithData(questionList.get(0));
    }

    @Override
    public String addQuestion(QuestionVO questionVO) {
        HttpServletRequest request = RequestHolder.getRequest();
        if(request.getAttribute(SysConf.USER_UID) == null) {
            throw new InsertException("用户未登录");
        }
        Question question = new Question();
        String userUid = request.getAttribute(SysConf.USER_UID).toString();
        BeanUtils.copyProperties(questionVO, question, SysConf.STATUS);
        question.setUserUid(userUid);
        question.insert();
        return ResultUtil.successWithMessage(MessageConf.INSERT_SUCCESS);
    }

    @Override
    public String editQuestion(QuestionVO questionVO) {
        Question question = questionService.getById(questionVO.getUid());
        BeanUtils.copyProperties(questionVO, question, SysConf.STATUS, SysConf.UID);
        question.updateById();
        return ResultUtil.successWithMessage(MessageConf.UPDATE_SUCCESS);
    }

    @Override
    public String deleteBatchQuestion(List<QuestionVO> questionVOList) {
        if (questionVOList.size() <= 0) {
            return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
        }
        List<String> uidList = new ArrayList<>();
        questionVOList.forEach(item -> {
            uidList.add(item.getUid());
        });
        Collection<Question> questionList = questionService.listByIds(uidList);
        questionList.forEach(item -> {
            item.setStatus(EStatus.DISABLED);
        });
        questionService.updateBatchById(questionList);
        return ResultUtil.successWithMessage(MessageConf.DELETE_SUCCESS);
    }

    /**
     * 设置问答标签和用户
     * @param questionList
     */
    private void setQuestionTagAndUser(List<Question> questionList) {
        Set<String> questionTagUidSet = new HashSet<>();
        Set<String> userUidList = new HashSet<>();
        questionList.forEach(item -> {
            if (StringUtils.isNotEmpty(item.getQuestionTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getQuestionTagUid(), SysConf.FILE_SEGMENTATION);
                for (String itemTagUid : tagUidsTemp) {
                    questionTagUidSet.add(itemTagUid);
                }
            }
            if(StringUtils.isNotEmpty(item.getUserUid())) {
                userUidList.add(item.getUserUid());
            }
        });

        // 获取问答标签
        Collection<QuestionTag> questionTagList = new ArrayList<>();
        if (questionTagUidSet.size() > 0) {
            questionTagList = questionTagService.listByIds(questionTagUidSet);
        }
        Map<String, QuestionTag> tagMap = new HashMap<>();
        questionTagList.forEach(item -> {
            tagMap.put(item.getUid(), item);
        });

        // 获取提问者
        List<User> userList = userService.getUserListAndAvatarByIds(userUidList);
        Map<String, User> userMap = new HashMap<>();
        userList.forEach(item -> {
            userMap.put(item.getUid(), item);
        });

        for (Question item : questionList) {
            //获取标签
            if (StringUtils.isNotEmpty(item.getQuestionTagUid())) {
                List<String> tagUidsTemp = StringUtils.changeStringToString(item.getQuestionTagUid(), SysConf.FILE_SEGMENTATION);
                List<QuestionTag> tagListTemp = new ArrayList<>();

                tagUidsTemp.forEach(tag -> {
                    tagListTemp.add(tagMap.get(tag));
                });
                item.setQuestionTagList(tagListTemp);
            }

            //获取用户
            if (StringUtils.isNotEmpty(item.getUserUid())) {
                item.setUser(userMap.get(item.getUserUid()));
            }
        }
    }

}
