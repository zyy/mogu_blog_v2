package com.moxi.mogublog.xo.vo;

import com.moxi.mougblog.base.validator.annotion.NotBlank;
import com.moxi.mougblog.base.validator.group.Insert;
import com.moxi.mougblog.base.validator.group.Update;
import com.moxi.mougblog.base.vo.BaseVO;
import lombok.Data;

/**
 * BlogSortVO
 *
 * @author: 陌溪
 * @create: 2019年12月6日12:56:08
 */
@Data
public class UserWatchVO extends BaseVO<UserWatchVO> {

    /**
     * 关注人UID
     */
    private String userUid;

    /**
     * 被关注人UID
     */
    @NotBlank(groups = {Insert.class, Update.class})
    private String toUserUid;

    /**
     * 是否是admin
     */
    private String isAdmin;
}
