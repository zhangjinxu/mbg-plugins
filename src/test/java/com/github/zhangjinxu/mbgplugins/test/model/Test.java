package com.github.zhangjinxu.mbgplugins.test.model;

import java.util.Date;
import lombok.Data;

/**
 *
 * 对应表: test
 */
@Data
public class Test {
    /**
     *
     * 对应字段: test.id
     * @mbg.generated
     */
    private Integer id;

    /**
     *
     * 对应字段: test.name
     * @mbg.generated
     */
    private String name;

    /**
     *
     * 对应字段: test.time
     * @mbg.generated
     */
    private Date time;
}