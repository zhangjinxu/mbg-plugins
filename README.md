# mbg-plugins
一些好用的MyBatis Generator插件

###1.生成干净的注释
#####1）配置如下：
```
<commentGenerator type="com.github.zhangjinxu.mbgplugins.comment.CleanCommentGenerator">
    <!--使用@mbg.generated表示代码是否可以被覆盖-->
    <!--是否阻止生成所有注释-->
    <property name="suppressAllComments" value="false"/>
    <!--是否阻止在注释中生成日期-->
    <property name="suppressDate" value="true"/>
    <!--注释中生成日期时日期的格式化格式-->
    <property name="dateFormat" value="yyyy/MM/dd HH:mm:ss"/>
    <!--是否给字段添加来自数据库表中的注释-->
    <property name="addRemarkComments" value="true"/>
</commentGenerator>
```
#####2）实体类注释效果如下：
````
/**
 *
 * 对应表: test
 */
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
````

###2.在targetRuntime=MyBatis3Simple模式下，给xml文件生成表中所有字段引用的sql
#####1）配置如下：
````
<plugin type="com.github.zhangjinxu.mbgplugins.plugin.BaseColumnListPlugin"/>
````
#####2）效果如下：
````
<sql id="Base_Column_List">
    <!--
    @mbg.generated
    -->
    id,name,time
</sql>
````

###3.给实体类添加lombok的@Data注解
#####1）配置如下：
````
<plugin type="com.github.zhangjinxu.mbgplugins.plugin.LombokDataPlugin"/>
````
#####2）具体效果请查看test包下的实体类示例

###4.给实体类生成toExample方法，将model中不为空的字段转换为对应Example类中的条件
#####1)配置如下：
````
<plugin type="com.github.zhangjinxu.mbgplugins.plugin.ModelToExamplePlugin"/>
````

###5.保护实体类，Mapper接口，Mapper xml文件中自定义的内容不受再次生成的代码的覆盖
#####1）具体规则为：注释中标有@mbg.generator标记的为自动生成的内容，下次生成可以覆盖为新的内容。
#####2）配置如下：
````
<plugin type="com.github.zhangjinxu.mbgplugins.plugin.OverridePlugin"/>
````





