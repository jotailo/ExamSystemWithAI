package com.liuxuanhui.aicodehelper.exam.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.liuxuanhui.aicodehelper.exam.entity.Notice;
import com.liuxuanhui.aicodehelper.exam.vo.PageResponse;

public interface NoticeService extends IService<Notice> {

    boolean setAllNoticeIsHistoryNotice();

    String getCurrentNotice();

    PageResponse<Notice> getAllNotices(String content, Integer pageNo, Integer pageSize);

    void publishNotice(Notice notice);

    void deleteNoticeByIds(String noticeIds);

    void updateNotice(Notice notice);
}
