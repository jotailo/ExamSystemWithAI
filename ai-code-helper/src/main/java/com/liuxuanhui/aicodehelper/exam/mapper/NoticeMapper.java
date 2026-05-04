package com.liuxuanhui.aicodehelper.exam.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.liuxuanhui.aicodehelper.exam.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface NoticeMapper extends BaseMapper<Notice> {

    @Update("update notice set status = 0")
    boolean setAllNoticeIsHistoryNotice();
}
