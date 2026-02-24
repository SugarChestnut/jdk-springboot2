package cn.rtt.server.system.dao;

import cn.rtt.server.system.domain.entity.BaseEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.repository.CrudRepository;

import java.util.*;

/**
 * @author rtt
 * @date 2025/6/13 13:38
 */
public class BaseRepository<M extends BaseMapper<T>, T> extends CrudRepository<M, T> {

    public List<T> queryByConditions(Map<String, Object> conditions) {
        List<T> list = new ArrayList<>();
        if (null == conditions) {
            return list;
        }

        Map<String, Object> where = new HashMap<>();

        Set<String> keys = conditions.keySet();
        for (String key : keys) {
            if (null == conditions.get(key)) {
                continue;
            }
            where.put(key, conditions.get(key));
        }

        return listByMap(where);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean updateById(T entity) {
        if(entity instanceof BaseEntity) {
            BaseEntity e = (BaseEntity) entity;
            e.setGmtModified(null);
            return super.updateById((T) e);
        }
        return super.updateById(entity);
    }

}
