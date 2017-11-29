package com.taotao.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.pojo.EasyUITreeNode;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItemCat;
import com.taotao.service.ItemCatService;
/**
 * 物品分类
 * @author soft01
 *
 */
@Controller
public class ItemCatController {
	@Autowired
	private ItemCatService itemService;
	
	@RequestMapping("/item/cat/list")
	@ResponseBody
	public List<EasyUITreeNode> getItemCatList(@RequestParam(name="id", defaultValue="0") Long parentId){
		
		return itemService.getItemCatList(parentId);
	}
	@RequestMapping("/rest/item/param/item/query/{itemId}")
	@ResponseBody
	public TaotaoResult getNodeNameById(Long itemId){
		TbItemCat itemCat=itemService.getNodeNameById(itemId);
		return new TaotaoResult(itemCat);
		
	}
}
