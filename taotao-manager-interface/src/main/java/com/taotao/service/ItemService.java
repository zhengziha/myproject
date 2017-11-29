package com.taotao.service;

import com.taotao.pojo.EasyUIDataGridResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
/**
 * 商品服务
 * @author 47606
 *
 */
public interface ItemService {

	TbItem getItemById(long itemId);
	
	EasyUIDataGridResult getItemList(int page,int rows);
	
	TaotaoResult addItem(TbItem item,String desc);
	
	TaotaoResult deleteItem(String ids) ;
	
	TaotaoResult updateItem(TbItem item,String desc);
	
	TaotaoResult instock(String ids);
	
	TaotaoResult reshelf(String ids);

	TbItemDesc getItemDescById(Long itemId);
}
