package com.liuxuanhui.aicodehelper.exam.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liuxuanhui.aicodehelper.exam.annotation.Cache;
import com.liuxuanhui.aicodehelper.exam.entity.Notice;
import com.liuxuanhui.aicodehelper.exam.exception.BusinessException;
import com.liuxuanhui.aicodehelper.exam.exception.CommonErrorCode;
import com.liuxuanhui.aicodehelper.exam.mapper.NoticeMapper;
import com.liuxuanhui.aicodehelper.exam.service.NoticeService;
import com.liuxuanhui.aicodehelper.exam.utils.CommonUtils;
import com.liuxuanhui.aicodehelper.exam.utils.RedisUtil;
import com.liuxuanhui.aicodehelper.exam.vo.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements NoticeService {

    private final NoticeMapper noticeMapper;
    private final RedisUtil redisUtil;

    @Override
    public boolean setAllNoticeIsHistoryNotice() {
        return noticeMapper.setAllNoticeIsHistoryNotice();
    }

    @Cache(prefix = "currentNewNotice", ttl = 10, randomTime = 2, timeUnit = TimeUnit.HOURS)
    @Override
    public String getCurrentNotice() {
        Notice notice = noticeMapper.selectOne(new QueryWrapper<Notice>().eq("status", "1"));
        return notice != null ? notice.getContent() : "";
    }

    @Override
    public PageResponse<Notice> getAllNotices(String content, Integer pageNo, Integer pageSize) {
        IPage<Notice> noticeIPage = new Page<>(pageNo, pageSize);
        QueryWrapper<Notice> wrapper = new QueryWrapper<>();
        CommonUtils.setLikeWrapper(wrapper, Collections.singletonMap("content", content));
        wrapper.orderByDesc("status", "update_time", "create_time");
        noticeIPage = noticeMapper.selectPage(noticeIPage, wrapper);
        return PageResponse.<Notice>builder().data(noticeIPage.getRecords()).total(noticeIPage.getTotal()).build();
    }

    @Override
    public void publishNotice(Notice notice) {
        if (notice.getStatus() == 1) {
            setAllNoticeIsHistoryNotice();
            notice.setCreateTime(new Date());
            boolean save = noticeMapper.insert(notice) > 0;
            if (redisUtil.get("currentNewNotice") != null && save) {
                redisUtil.set("currentNewNotice", notice.getContent());
            }
        } else if (notice.getStatus() == 0) {
            notice.setCreateTime(new Date());
            noticeMapper.insert(notice);
        } else {
            throw new BusinessException(CommonErrorCode.E_300001);
        }
    }

    @Override
    public void deleteNoticeByIds(String noticeIds) {
        String[] ids = noticeIds.split(",");
        Notice currentNotice = noticeMapper.selectOne(new QueryWrapper<Notice>().eq("status", "1"));
        if (currentNotice == null) return;
        for (String id : ids) {
            if (currentNotice.getNId().equals(Integer.parseInt(id))) {
                continue;
            }
            noticeMapper.deleteById(Integer.parseInt(id));
        }
    }

    @Override
    public void updateNotice(Notice notice) {
        QueryWrapper<Notice> wrapper = new QueryWrapper<Notice>().eq("n_id", notice.getNId());
        Notice targetNotice = noticeMapper.selectOne(wrapper);

        if (notice.getStatus() == 1) {
            setAllNoticeIsHistoryNotice();
            targetNotice.setUpdateTime(new Date());
            targetNotice.setContent(notice.getContent());
            targetNotice.setStatus(notice.getStatus());
            boolean update = noticeMapper.update(targetNotice, wrapper) > 0;
            if (redisUtil.get("currentNewNotice") != null && update) {
                redisUtil.set("currentNewNotice", notice.getContent());
            }
        } else if (notice.getStatus() == 0) {
            targetNotice.setUpdateTime(new Date());
            targetNotice.setContent(notice.getContent());
            targetNotice.setStatus(notice.getStatus());
            noticeMapper.update(targetNotice, wrapper);
        } else {
            throw new BusinessException(CommonErrorCode.E_300002);
        }
    }
}
