package com.xxl.job.admin.dao;

import com.xxl.job.admin.core.model.XxlJobRegistry;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Date;
import java.util.List;

/**
 * Created by xuxueli on 16/9/30.
 */
@Mapper
public interface XxlJobRegistryDao {

    Logger log = LoggerFactory.getLogger(XxlJobRegistryDao.class);

    public List<Integer> findDead(@Param("nowTime") Date nowTime);

    public int removeDead(@Param("ids") List<Integer> ids);

    public List<XxlJobRegistry> findAll(@Param("nowTime") Date nowTime);

    /**
     * 达梦兼容实现：先执行插入，若触发唯一键冲突则转执行更新，替代 MySQL 专有的 INSERT ... ON DUPLICATE KEY UPDATE 语法
     * 异常转换链路：classpath 根目录 sql-error-codes.xml 将达梦错误码（-6602/-6612/-6625）映射为 DuplicateKeyException；
     * 即使错误码映射未命中（如驱动返回 SQLState 23000），也会翻译为父类 DataIntegrityViolationException，故捕获该父类兜底。
     */
    public default int registrySaveOrUpdate(@Param("registryGroup") String registryGroup,
                                            @Param("registryKey") String registryKey,
                                            @Param("registryValue") String registryValue,
                                            @Param("updateTime") Date updateTime) {

        try {
            return registrySave(registryGroup, registryKey, registryValue, updateTime);
        } catch (DataIntegrityViolationException ex) {
            log.debug("DataIntegrityViolationException registryGroup={},registryKey={},registryValue={}",
                    registryGroup, registryKey, registryValue);
            return registryUpdate(registryGroup, registryKey, registryValue, updateTime);
        }
    }

    public int registryUpdate(@Param("registryGroup") String registryGroup,
                              @Param("registryKey") String registryKey,
                              @Param("registryValue") String registryValue,
                              @Param("updateTime") Date updateTime);

    public int registrySave(@Param("registryGroup") String registryGroup,
                            @Param("registryKey") String registryKey,
                            @Param("registryValue") String registryValue,
                            @Param("updateTime") Date updateTime);

    public int registryDelete(@Param("registryGroup") String registryGroup,
                              @Param("registryKey") String registryKey,
                              @Param("registryValue") String registryValue);

}
