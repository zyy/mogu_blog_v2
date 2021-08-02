package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.commons.entity.Admin;
import com.moxi.mogublog.commons.entity.User;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.web.annotion.log.BussinessLog;
import com.moxi.mogublog.web.global.MessageConf;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.*;
import com.moxi.mogublog.xo.vo.BlogVO;
import com.moxi.mogublog.xo.vo.QuestionVO;
import com.moxi.mogublog.xo.vo.UserWatchVO;
import com.moxi.mougblog.base.enums.EBehavior;
import com.moxi.mougblog.base.enums.EPublish;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.exception.exceptionType.QueryException;
import com.moxi.mougblog.base.validator.group.GetList;
import com.moxi.mougblog.base.validator.group.Insert;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 关于我 RestApi
 *
 * @author 陌溪
 * @date 2018年11月12日14:51:54
 */
@RestController
@RequestMapping("/about")
@Api(value = "关于我相关接口", tags = {"关于我相关接口"})
@Slf4j
public class UserCenterRestApi {

    @Autowired
    private AdminService adminService;
    @Autowired
    private WebConfigService webConfigService;
    @Autowired
    private BlogService blogService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserWatchService userWatchService;

    /**
     * 获取关于我的信息
     */
    @BussinessLog(value = "关于我", behavior = EBehavior.VISIT_PAGE)
    @ApiOperation(value = "关于我", notes = "关于我")
    @GetMapping("/getMe")
    public String getMe() {

        log.info("获取关于我的信息");
        return ResultUtil.result(SysConf.SUCCESS, adminService.getAdminByUser(SysConf.ADMIN));
    }

    @ApiOperation(value = "获取联系方式", notes = "获取联系方式")
    @GetMapping("/getContact")
    public String getContact() {
        log.info("获取联系方式");
        return ResultUtil.result(SysConf.SUCCESS, webConfigService.getWebConfigByShowList());
    }

    @ApiOperation(value = "通过ID获取用户【包含用户和管理员】", notes = "通过ID获取用户")
    @GetMapping("/getUserByUid")
    public String getUserByUid(HttpServletRequest request, @ApiParam(name = "adminUid", value = "管理员uid", required = false) @RequestParam(name = "adminUid", required = false, defaultValue = "") String adminUid,
                               @ApiParam(name = "userUid", value = "用户uid", required = false) @RequestParam(name = "userUid", required = false, defaultValue = "") String userUid) {
        Object currentUserUid = request.getAttribute(SysConf.USER_UID);
        if (StringUtils.isNotEmpty(adminUid)) {
            List<String> adminUidList = new ArrayList<>();
            adminUidList.add(adminUid);
            List<Admin> adminList = adminService.getAdminListByUid(adminUidList);
            if (adminList.size() == 0) {
                throw new QueryException(MessageConf.PARAM_INCORRECT);
            }
            // 默认没有关注用户
            Boolean isWatchUser = false;
            if (currentUserUid != null) {
                UserWatchVO userWatchVO = new UserWatchVO();
                userWatchVO.setUserUid(currentUserUid.toString());
                userWatchVO.setToUserUid(adminUid);
                isWatchUser = userWatchService.checkUserWatch(userWatchVO);
            } else {
                log.info("前端用户未登录");
            }
            // 获取管理员
            Admin admin = adminList.get(0);
            admin.setIsWatchUser(isWatchUser);
            return ResultUtil.successWithData(admin);
        } else if (StringUtils.isNotEmpty(userUid)) {
            List<String> userUidList = new ArrayList<>();
            userUidList.add(userUid);
            List<User> userList = userService.getUserListAndAvatarByIds(userUidList);
            if (userList.size() > 0) {
                // 默认没有关注用户
                Boolean isWatchUser = false;
                if (currentUserUid != null) {
                    UserWatchVO userWatchVO = new UserWatchVO();
                    userWatchVO.setUserUid(currentUserUid.toString());
                    userWatchVO.setToUserUid(userUid);
                    isWatchUser = userWatchService.checkUserWatch(userWatchVO);
                } else {
                    log.info("前端用户未登录");
                }
                User user = userList.get(0);
                user.setIsWatchUser(isWatchUser);
                return ResultUtil.successWithData(user);
            } else {
                return ResultUtil.errorWithMessage("该用户不存在");
            }
        }
        log.info("通过用户获取博客列表");
        return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
    }

    @ApiOperation(value = "通过ID获取用户个人中心统计信息", notes = "通过ID获取用户个人中心统计信息")
    @GetMapping("/getUserCenterByUid")
    public String getInfoByUid(HttpServletRequest request,
                               @ApiParam(name = "adminUid", value = "管理员uid", required = false) @RequestParam(name = "adminUid", required = false, defaultValue = "") String adminUid,
                               @ApiParam(name = "userUid", value = "用户uid", required = false) @RequestParam(name = "userUid", required = false, defaultValue = "") String userUid) {
        if (StringUtils.isNotEmpty(adminUid)) {
            // 获取发表的文章数
            BlogVO blogVO = new BlogVO();
            blogVO.setAdminUid(adminUid);
            Map<String, Integer> map = new HashMap<>();
            map.put("blogCount", blogService.getBlogCount(blogVO));
            // 获取发表的问答数
            QuestionVO questionVO = new QuestionVO();
            questionVO.setAdminUid(adminUid);
            map.put("questionCount", questionService.getQuestionCount(questionVO));
            // 获取关注人数
            UserWatchVO watches = new UserWatchVO();
            watches.setUserUid(adminUid);
            map.put("watchCount", userWatchService.getUserWatchCount(watches));
            // 获取粉丝人数
            UserWatchVO followes = new UserWatchVO();
            followes.setToUserUid(adminUid);
            map.put("followCount", userWatchService.getUserWatchCount(followes));
            return ResultUtil.successWithData(map);
        } else if (StringUtils.isNotEmpty(userUid)) {
            BlogVO blogVO = new BlogVO();
            blogVO.setUserUid(userUid);
            Map<String, Integer> map = new HashMap<>();
            map.put("blogCount", blogService.getBlogCount(blogVO));
            QuestionVO questionVO = new QuestionVO();
            questionVO.setUserUid(userUid);
            map.put("questionCount", questionService.getQuestionCount(questionVO));
            // 获取关注人数
            UserWatchVO watches = new UserWatchVO();
            watches.setUserUid(userUid);
            map.put("watchCount", userWatchService.getUserWatchCount(watches));
            // 获取粉丝人数
            UserWatchVO followes = new UserWatchVO();
            followes.setToUserUid(userUid);
            map.put("followCount", userWatchService.getUserWatchCount(followes));
            return ResultUtil.successWithData(map);
        }

        log.info("通过ID获取用户个人中心信息");
        return ResultUtil.errorWithMessage(MessageConf.PARAM_INCORRECT);
    }

    @ApiOperation(value = "通过用户获取博客列表", notes = "通过用户获取博客列表")
    @PostMapping("/getBlogListByUser")
    public String getBlogListByUser(@Validated({GetList.class}) @RequestBody BlogVO blogVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("通过用户获取博客列表");
        blogVO.setIsPublish(EPublish.PUBLISH);
        return ResultUtil.result(SysConf.SUCCESS, blogService.getPageList(blogVO));
    }

    @ApiOperation(value = "通过用户获取问答列表", notes = "通过用户获取问答列表")
    @PostMapping("/getQuestionListByUser")
    public String getQuestionListByUser(@Validated({GetList.class}) @RequestBody QuestionVO questionVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("通过用户获取问答列表");
        questionVO.setIsPublish(EPublish.PUBLISH);
        return ResultUtil.result(SysConf.SUCCESS, questionService.getPageList(questionVO));
    }

    @ApiOperation(value = "获取用户关注列表", notes = "获取用户关注列表")
    @PostMapping("/getUserWatchList")
    public String getUserWatchList(@Validated({GetList.class}) @RequestBody UserWatchVO userWatchVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取用户关注列表");
        return ResultUtil.successWithData(userWatchService.getPageList(userWatchVO));
    }


    @ApiOperation(value = "关注某人", notes = "关注某人")
    @PostMapping("/addUserWatch")
    public String addUserWatch(@Validated({Insert.class}) @RequestBody UserWatchVO userWatchVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("关注某人");
        return userWatchService.addUserWatch(userWatchVO);
    }

    @ApiOperation(value = "取消关注某人", notes = "取消关注某人")
    @PostMapping("/deleteUserWatch")
    public String deleteUserWatch(@Validated({Insert.class}) @RequestBody UserWatchVO userWatchVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("取消关注某人");
        return userWatchService.deleteUserWatch(userWatchVO);
    }

    @ApiOperation(value = "获取问答列表", notes = "获取问答列表", response = String.class)
    @PostMapping("/getQuestionList")
    public String getQuestionList(HttpServletRequest request, @Validated({GetList.class}) @RequestBody QuestionVO questionVO, BindingResult result) {
        ThrowableUtils.checkParamArgument(result);
        log.info("获取问答列表");
        // 前端没有传递用户UID时，将查询在线用户的博客列表
        if (StringUtils.isEmpty(questionVO.getUserUid()) && request.getAttribute(SysConf.USER_UID) != null) {
            questionVO.setUserUid(request.getAttribute(SysConf.USER_UID).toString());
        }
        return ResultUtil.result(SysConf.SUCCESS, questionService.getPageList(questionVO));
    }
}

