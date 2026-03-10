package cn.rtt.server.system.domain.response;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.Collections;
import java.util.List;

/**
 * @author rtt
 * @date 2024/12/11 11:11
 */
@Data
public class SysPage<T> {

    private long total; // 总记录数
    private long size; // 每页大小
    private long current; // 当前页码
    private long pages;
    private List<T> records;

    public static <T> SysPage<T> createEmpty() {
        SysPage<T> xxPage = new SysPage<>();
        xxPage.setRecords(Collections.emptyList());
        return xxPage;
    }

    public static <T> SysPage<T> transform(IPage<T> page, List<T> records) {
        SysPage<T> xxPage = new SysPage<>();
        if (page.getRecords().isEmpty() && records != null) {
            xxPage.setRecords(records);
            xxPage.setTotal(records.size());
        } else {
            xxPage.setRecords(page.getRecords());
            xxPage.setTotal(page.getTotal());
        }
        xxPage.setSize(page.getSize());
        xxPage.setPages(page.getPages());
        xxPage.setCurrent(page.getCurrent());
        return xxPage;
    }

    public static <T> SysPage<T> transform(IPage<T> page) {
        return transform(page, null);
    }

    public static <T> SysPage<T> transform(List<T> records) {
        SysPage<T> empty = createEmpty();
        empty.setRecords(records);
        return empty;
    }

    public static <T> SysPage<T> transform(T record) {
        return transform(Collections.singletonList(record));
    }
}
