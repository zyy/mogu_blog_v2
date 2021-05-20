package com.moxi.mogublog.web.restapi;

import com.moxi.mogublog.commons.entity.WebConfig;
import com.moxi.mogublog.utils.ResultUtil;
import com.moxi.mogublog.utils.StringUtils;
import com.moxi.mogublog.web.global.SysConf;
import com.moxi.mogublog.xo.service.BlogService;
import com.moxi.mogublog.xo.service.BlogSortService;
import com.moxi.mogublog.xo.service.TagService;
import com.moxi.mogublog.xo.service.WebConfigService;
import com.moxi.mogublog.xo.vo.BlogSortVO;
import com.moxi.mogublog.xo.vo.BlogVO;
import com.moxi.mogublog.xo.vo.TagVO;
import com.moxi.mougblog.base.enums.ELevel;
import com.moxi.mougblog.base.enums.EPublish;
import com.moxi.mougblog.base.exception.ThrowableUtils;
import com.moxi.mougblog.base.global.Constants;
import com.moxi.mougblog.base.validator.group.Default;
import com.moxi.mougblog.base.validator.group.Delete;
import com.moxi.mougblog.base.validator.group.GetList;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 博客创作相关
 *
 * @author: 陌溪
 * @create: 2021-04-11-9:58
 */
@RestController
@RefreshScope
@RequestMapping("/createBlog")
@Api(value = "博客创作相关接口", tags = {"博客创作相关接口"})
@Slf4j
public class CreateBlogRestApi {

    @Autowired
    private BlogService blogService;
    @Autowired
    private BlogSortService blogSortService;
    @Autowired
    private TagService tagService;
    @Autowired
    private WebConfigService webConfigService;

    @ApiOperation(value = "获取用户的博客列表", notes = "获取用户的博客列表", response = String.class)
    @PostMapping("/getUserBlogList")
    public String getUserBlogList(HttpServletRequest request, @Validated({GetList.class}) @RequestBody BlogVO blogVO, BindingResult result) {

        // 前端没有传递用户UID时，将查询在线用户的博客列表
        if (StringUtils.isEmpty(blogVO.getUserUid()) && request.getAttribute(SysConf.USER_UID) != null) {
            blogVO.setUserUid(request.getAttribute(SysConf.USER_UID).toString());
        }

        ThrowableUtils.checkParamArgument(result);
        return ResultUtil.successWithData(blogService.getPageList(blogVO));
    }

    @ApiOperation(value = "增加博客", notes = "增加博客", response = String.class)
    @PostMapping("/add")
    public String add(HttpServletRequest request, @Validated({Default.class}) @RequestBody BlogVO blogVO, BindingResult result) {

        if (request.getAttribute(SysConf.USER_UID) == null) {
            return ResultUtil.errorWithMessage("登录后才可以创建博客！");
        }
        // 判断是否开启投稿功能
        WebConfig webConfig = webConfigService.getWebConfig();
        if (Constants.STR_ZERO.equals(webConfig.getOpenCreateBlog())) {
            return ResultUtil.errorWithMessage("后台暂未开启投稿功能");
        }
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        // 文章类型只能是博客类型
        blogVO.setType(Constants.STR_ZERO);
        // 推荐类型默认为正常
        blogVO.setLevel(ELevel.NORMAL);
        blogVO.setIsPublish(EPublish.NO_PUBLISH);
        blogVO.setWebFlag(true);
        return blogService.addBlog(blogVO);
    }

    @ApiOperation(value = "编辑博客", notes = "编辑博客", response = String.class)
    @PostMapping("/edit")
    public String edit(HttpServletRequest request, @Validated({Default.class}) @RequestBody BlogVO blogVO, BindingResult result) {
        if (request.getAttribute(SysConf.USER_UID) == null) {
            return ResultUtil.errorWithMessage("登录后才可以编辑博客！");
        }
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        blogVO.setWebFlag(true);
        return blogService.editBlog(blogVO);
    }

    @ApiOperation(value = "删除博客", notes = "删除博客", response = String.class)
    @PostMapping("/delete")
    public String delete(HttpServletRequest request, @Validated({Delete.class}) @RequestBody BlogVO blogVO, BindingResult result) {
        if (request.getAttribute(SysConf.USER_UID) == null) {
            return ResultUtil.errorWithMessage("登录后才可以编辑博客！");
        }
        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        blogVO.setWebFlag(true);
        return blogService.deleteBlog(blogVO);
    }

    @ApiOperation(value = "获取博客分类列表", notes = "获取博客分类列表", response = String.class)
    @PostMapping("/getBlogSortList")
    public String getBlogSortList(@Validated({GetList.class}) @RequestBody BlogSortVO blogSortVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取博客分类列表");
        return ResultUtil.successWithData(blogSortService.getPageList(blogSortVO));
    }


    @ApiOperation(value = "获取标签列表", notes = "获取标签列表", response = String.class)
    @PostMapping("/getBlogTagList")
    public String getList(@Validated({GetList.class}) @RequestBody TagVO tagVO, BindingResult result) {

        // 参数校验
        ThrowableUtils.checkParamArgument(result);
        log.info("获取标签列表");
        return ResultUtil.result(SysConf.SUCCESS, tagService.getPageList(tagVO));
    }
}
