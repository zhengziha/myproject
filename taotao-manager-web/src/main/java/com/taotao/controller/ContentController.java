package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.content.service.ContentService;
import com.taotao.pojo.EasyUIDataGridResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbContent;

@Controller
public class ContentController {
	@Autowired
	ContentService service;
	
	@RequestMapping("/content/query/list")
	@ResponseBody
	public EasyUIDataGridResult getContentList(Long categoryId, int page, int rows) {
//		System.out.println("do getContentList() controller");
		return service.getContentList(categoryId, page, rows);
	}
	@RequestMapping("/content/save")
	@ResponseBody
	public TaotaoResult addContent(TbContent content) {
//		System.out.println("addContent() content="+content);
		service.addContent(content);
		
		return TaotaoResult.ok();
	}
	@RequestMapping("/rest/content/edit")
	@ResponseBody
	public TaotaoResult updateContent(TbContent content) {
//		System.out.println("updateContent() content="+content);
		service.updateContent(content);
		
		return TaotaoResult.ok();
	}
	@RequestMapping("/content/delete")
	@ResponseBody
	public TaotaoResult deleteContent(String ids) {
		
//		System.out.println("deleteContent() ids="+ids);
		service.deleteContent(ids);
		
		return TaotaoResult.ok();
	}
	
}
