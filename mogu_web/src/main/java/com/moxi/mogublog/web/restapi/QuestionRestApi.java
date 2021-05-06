package com.moxi.mogublog.web.restapi;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.moxi.mogublog.commons.entity.Blog;
import com.moxi.mogublog.commons.entity.Question;
import com.moxi.mogublog.utils.IpUtils;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.web.global.MessageConf;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.web.log.BussinessLog;
import com.moxi.mogublog.xo.global.RedisConf;
import com.moxi.mogublog.xo.service.QuestionService;
import com.moxi.mogublog.xo.vo.QuestionVO;
import com.moxi.mougblog.base.enums.EBehavior;
import com.moxi.mougblog.base.enums.EPublish;
import com.moxi.mougblog.base.enums.EStatus;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.global.ECode;
import com.moxi.mougblog.base.holder.RequestHolder;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 问答 RestApi
 *
 * @author 陌溪
 * @date 2021年5月5日18:13:52
 */
@Api(value = "问答相关接口", tags = {"问答相关接口"})
@RestController
@RequestMapping("/question")
@Slf4j
public class QuestionRestApi {

    @Autowired
    private QuestionService questionService;

    @ApiOperation(value = "获取问答列表", notes = "获取问答列表", response = String.class)
    @PostMapping("/getList")
    public String getList(@Validated({GetList.class}) @RequestBody QuestionVO questionVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        log.info("获取问答列表");
        return ResultUtil.result(SysConf.SUCCESS, questionService.getPageList(questionVO));
    }

    @ApiOperation(value = "获取问答详情", notes = "获取问答详情")
    @PostMapping("/getQuestion")
    public String getQuestion(@RequestBody QuestionVO questionVO) {
        return questionService.getQuestion(questionVO);
    }

    @ApiOperation(value = "增加问答", notes = "增加问答", response = String.class)
    @PostMapping("/add")
    public String add(@Validated({Insert.class}) @RequestBody QuestionVO questionVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        log.info("增加问答");
        return questionService.addQuestion(questionVO);
    }


    @ApiOperation(value = "编辑问答", notes = "编辑问答", response = String.class)
    @PostMapping("/edit")
    public String edit(@Validated({Update.class}) @RequestBody QuestionVO questionVO, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        log.info("编辑问答");
        return questionService.editQuestion(questionVO);
    }

    @ApiOperation(value = "批量删除问答", notes = "批量删除问答", response = String.class)
    @PostMapping("/deleteBatch")
    public String delete(@Validated({Delete.class}) @RequestBody List<QuestionVO> questionVOList, BindingResult result) {

        ThrowableUtils.checkParamArgument(result);
        log.info("批量删除问答");
        return questionService.deleteBatchQuestion(questionVOList);
    }
}

