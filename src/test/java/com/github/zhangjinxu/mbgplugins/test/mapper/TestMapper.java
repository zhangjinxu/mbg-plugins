package com.github.zhangjinxu.mbgplugins.test.mapper;

import com.github.zhangjinxu.mbgplugins.test.model.Test;
import java.util.List;

public interface TestMapper {
    /**
     * @mbg.generated
     */
    int deleteByPrimaryKey(Integer id);

    /**
     * @mbg.generated
     */
    int insert(Test record);

    /**
     * @mbg.generated
     */
    Test selectByPrimaryKey(Integer id);

    /**
     * @mbg.generated
     */
    List<Test> selectAll();

    /**
     * @mbg.generated
     */
    int updateByPrimaryKey(Test record);
}