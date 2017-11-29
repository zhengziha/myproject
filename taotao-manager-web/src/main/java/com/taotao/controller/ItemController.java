package com.taotao.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taotao.pojo.EasyUIDataGridResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.service.ItemService;

@Controller
public class ItemController {

	@Autowired
	private ItemService service;
	
	@RequestMapping("/item/{itemId}")
	@ResponseBody
	public TbItem getItem(@PathVariable Long itemId){
		return service.getItemById(itemId);
	}
	@RequestMapping("/rest/item/query/item/desc/{itemId}")
	@ResponseBody
	public TaotaoResult getItemDescById(@PathVariable Long itemId){
		System.out.println("getItemDescById()");
		TbItemDesc desc=service.getItemDescById(itemId);
		System.out.println(desc);
		return new TaotaoResult(desc);
	}
	@RequestMapping("/item/list")
	@ResponseBody
	public EasyUIDataGridResult getItemList(Integer page,Integer rows){
		return service.getItemList(page, rows);
	}
	@RequestMapping(value="/item/save",method=RequestMethod.POST)
	@ResponseBody
	public TaotaoResult addItem(TbItem item,String desc){
		TaotaoResult result=service.addItem(item, desc);
		return result;
	}
	@RequestMapping("/rest/item/delete")
	@ResponseBody
	TaotaoResult deleteItem(String ids){
		System.out.println("deleteItem ids="+ids);
		TaotaoResult result=service.deleteItem(ids);
		return result;
	}
	@RequestMapping("/rest/item/update")
	@ResponseBody
	TaotaoResult updateItem(TbItem item,String desc){
		System.out.println("updateItem TbItem="+item);
		TaotaoResult result=service.updateItem(item, desc);
		return result;
	}
	@RequestMapping("/rest/item/instock")
	@ResponseBody
	TaotaoResult instock(String ids){
		System.out.println("instock ids="+ids);
		TaotaoResult result=service.instock(ids);
		return result;
	}
	@RequestMapping("/rest/item/reshelf")
	@ResponseBody
	TaotaoResult reshelf(String ids){
		System.out.println("reshelf ids="+ids);
		TaotaoResult result=service.reshelf(ids);
		return result;
	}
}
