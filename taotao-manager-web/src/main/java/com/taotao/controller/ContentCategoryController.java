package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.content.service.ContentCategoryService;
import com.taotao.pojo.EasyUITreeNode;
import com.taotao.pojo.TaotaoResult;

@Controller
public class ContentCategoryController {
	@Autowired
	private ContentCategoryService contentCategoryService;
	
	@RequestMapping("/content/category/list")
	@ResponseBody
	public List<EasyUITreeNode> getContentCategooryList(@RequestParam(value="id",defaultValue="0") Long parentId){
		List<EasyUITreeNode> list=contentCategoryService.getContentCategoryList(parentId);
		return list;
	}
	@RequestMapping("/content/category/create")
	@ResponseBody
	public TaotaoResult createContentCategory(@RequestParam(value="parentId",defaultValue="0")Long parentId,String name){
		return contentCategoryService.addContentCategory(parentId, name);
	}
	@RequestMapping("/content/category/update")
	@ResponseBody
	TaotaoResult updateContentCategory(Long id, String name){
		return contentCategoryService.updateContentCategory(id, name);
	}
	@RequestMapping("/content/category/delete")
	@ResponseBody
	TaotaoResult deleteContentCategory(Long id){
		return contentCategoryService.deleteContentCategory(id);
	}
}
