package com.taotao.search.service.impl;

import java.util.List;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taotao.pojo.SearchItem;
import com.taotao.pojo.TaotaoResult;
import com.taotao.search.mapper.SearchItemMapper;
import com.taotao.search.service.SearchItemService;

@Service
public class SearchItemServiceImpl implements SearchItemService{
	@Autowired
	private SearchItemMapper searchItemMapper;
	@Autowired
	private SolrServer solrServer;
	private Integer total;
	private Integer completed=0;
	@Override
	public TaotaoResult importItemsToIndex() {
		//1查询出商品
		List<SearchItem> itemList=searchItemMapper.getItemList();
		total=itemList.size();
		//2,遍历商品
		for(SearchItem searchItem:itemList){
			SolrInputDocument document=new SolrInputDocument();
			document.addField("id", searchItem.getId());
			document.addField("item_title", searchItem.getTitle());
			document.addField("item_sell_point", searchItem.getSell_point());
			document.addField("item_price", searchItem.getPrice());
			document.addField("item_image", searchItem.getImage());
			document.addField("item_category_name", searchItem.getCategory_name());
			document.addField("item_desc", searchItem.getItem_desc());
			try {
				solrServer.add(document);
//				System.out.println("import index>>>>>>ID:"+searchItem.getId());
				solrServer.commit();
				completed++;
			} catch (Exception e) {
				e.printStackTrace();
				return TaotaoResult.build(500, "服务器繁忙,添加索引失败");
			}
		}
		
		return TaotaoResult.ok();
	}
	
	public TaotaoResult getCompleted(){
		if(total!=null&&completed!=null)
			return new TaotaoResult(200,total.toString(),completed);
		return new TaotaoResult(200,"0",completed);
	}
}
