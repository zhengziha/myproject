package com.taotao.mapper;

import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface TbItemMapper {
    int countByExample(TbItemExample example);

    int deleteByExample(TbItemExample example);

    int deleteByPrimaryKey(Long id);

    int insert(TbItem record);

    int insertSelective(TbItem record);

    List<TbItem> selectByExample(TbItemExample example);
    /**
     * 查询所以字段by Id
     * @param id
     * @return
     */
    TbItem selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") TbItem record, @Param("example") TbItemExample example);

    
    int updateByExample(@Param("record") TbItem record, @Param("example") TbItemExample example);
    /**
     * 修改所以字段,判断为NULL不插入
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(TbItem record);
    /**
     * 修改所以字段,不判断NULL
     * @param record
     * @return
     */
    int updateByPrimaryKey(TbItem record);
    
    int updateByPrimaryKeySelecteds(@Param("it")TbItem record ,@Param("list")String[] ids);

	int deleteByPrimaryKeys(@Param("list")String[] ids);

}