package com.taotao.content.service;

import java.util.List;

import com.taotao.pojo.EasyUIDataGridResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;


public interface ContentService {
	/**
	 * 获取内容列表根据分类ID,页数,记录条数
	 * @param categoryId	分类ID
	 * @param page	页数
	 * @param rows	记录条数
	 * @return 
	 */
	EasyUIDataGridResult getContentList(Long categoryId,int page, int rows);
	/**
	 * 获取内容列表根据分类ID
	 * @param categoryId 分类ID
	 * @return
	 */
	List<TbContent> getContentListByCid(Long categoryId);
	/**
	 * 添加内容 根据内容
	 * @param category	内容
	 */
	TaotaoResult addContent(TbContent content);
	/**
	 * 修改内容 根据内容里面的Id
	 * @param category	内容
	 */
	TaotaoResult updateContent(TbContent content);
	/**
	 * 删除内容 根据内容里面的Id
	 * @param category	内容
	 */
	TaotaoResult deleteContent(String ids);
}
