package com.taotao.content.service;

import java.util.List;

import com.taotao.pojo.EasyUITreeNode;
import com.taotao.pojo.TaotaoResult;

public interface ContentCategoryService {
	/**
	 * 获取内容分类列表
	 * @param parentId 父节点Id
	 * @return
	 */
	List<EasyUITreeNode> getContentCategoryList(Long parentId);
	/**
	 * 添加内容分类
	 * @param parentId 父节点Id
	 * @param name	新节点名字
	 * @return
	 */
	TaotaoResult addContentCategory(Long parentId, String name);
	/**
	 * 修改内容分类
	 * @param id 节点Id
	 * @param newName	节点新名字
	 * @return
	 */
	TaotaoResult updateContentCategory(Long id, String newName);
	/**
	 * 删除内容分类
	 * @param id 节点Id
	 * @return
	 */
	TaotaoResult deleteContentCategory(Long id);
	
}
