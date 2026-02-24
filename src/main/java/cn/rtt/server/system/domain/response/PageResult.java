package cn.rtt.server.system.domain.response;

import cn.rtt.server.system.constant.ResultCode;
import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author rtt
 * @date 2024/12/11 11:11
 */
@Data
public class PageResult<T> {

    private boolean flag;
    private int code;
    private String msg;
    private long total; // 总记录数
    private long size; // 每页大小
    private long current; // 当前页码
    private long pages;
    private List<T> records;

    public PageResult() {
        this.flag = true;
        this.code = ResultCode.SUCCESS.getCode();
        this.msg = ResultCode.SUCCESS.getMsg();
    }

    public static <T> PageResult<T> createEmpty() {
        PageResult<T> xxPage = new PageResult<>();
        xxPage.setRecords(Collections.emptyList());
        return xxPage;
    }

    public static <T> PageResult<T> transform(IPage<T> page, List<T> records) {
        PageResult<T> xxPage = new PageResult<>();
        if (page.getRecords().isEmpty() && records != null) {
            xxPage.setRecords(records);
            xxPage.setTotal(records.size());
        } else {
            xxPage.setRecords(page.getRecords());
        }
        xxPage.setSize(page.getSize());
        xxPage.setPages(page.getPages());
        xxPage.setCurrent(page.getCurrent());
        return xxPage;
    }

    public static <T> PageResult<T> transform(IPage<T> page) {
        return transform(page, null);
    }

    public static <T> PageResult<T> transform(List<T> records) {
        PageResult<T> empty = createEmpty();
        empty.setRecords(records);
        return empty;
    }

    public static <T> PageResult<T> transform(T record) {
        return transform(Collections.singletonList(record));
    }
}
