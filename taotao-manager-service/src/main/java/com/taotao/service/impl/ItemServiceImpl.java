package com.taotao.service.impl;

import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.alibaba.druid.support.json.JSONUtils;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbItemDescMapper;
import com.taotao.mapper.TbItemMapper;
import com.taotao.pojo.EasyUIDataGridResult;
import com.taotao.pojo.TaotaoResult;
import com.taotao.pojo.TbItem;
import com.taotao.pojo.TbItemDesc;
import com.taotao.pojo.TbItemDescExample;
import com.taotao.pojo.TbItemDescExample.Criterion;
import com.taotao.pojo.TbItemExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;
import com.taotao.service.ItemService;
import com.taotao.utils.IDUtils;
import com.taotao.utils.JsonUtils;
@Service
public class ItemServiceImpl implements ItemService {
	@Autowired
	private TbItemMapper itemMapper;
	
	@Autowired
	private TbItemDescMapper itemDescMapper;
	/**Spring中的ActioveMQ消息发送对象*/
	@Autowired
	private JmsTemplate jmsTemplate;
	/** 消息目的地对象*/
	@Resource(name="itemAddtopic")
	private Destination destination;
	
	@Autowired
	private JedisClient jedisClient;
	@Value("${ITEM_INFO}")
	private String ITEM_INFO;
	@Value("${ITEM_EXPIRE}")
	private Integer ITEM_EXPIRE;
	@Override
	public TbItem getItemById(long itemId) {
		//先查缓存
		try{
			String json=jedisClient.get(ITEM_INFO+":"+itemId+":BASE");
			if(StringUtils.isNotBlank(json)){
				TbItem item=JsonUtils.jsonToPojo(json, TbItem.class);
				return item;
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		//查数据库
		TbItem item=itemMapper.selectByPrimaryKey(itemId);
		//添加结果到缓存
		try{
			jedisClient.set(ITEM_INFO+":"+itemId+"BASE",JsonUtils.objectToJson(item));
			//设置过期时间，提高缓存的利用率
			jedisClient.expire(ITEM_INFO+":"+itemId+"BASE", ITEM_EXPIRE);
		}catch(Exception e){
			e.printStackTrace();
		}
		return item;
	}
	@Override
	public EasyUIDataGridResult getItemList(int page, int rows) {
		PageHelper.startPage(page, rows);
		//创建Example对象
		TbItemExample example = new TbItemExample();
		List<TbItem> list = itemMapper.selectByExample(example);
		//4.取分页信息。使用PageInfo对象取。
		PageInfo<TbItem> pageInfo = new PageInfo<>(list);
		EasyUIDataGridResult result=new EasyUIDataGridResult();
		result.setTotal(pageInfo.getTotal());
		result.setRows(list);
		return result;
	}
	@Override
	public TaotaoResult addItem(TbItem item, String desc) {
		//生成id
		final long itemId=IDUtils.genItemId();
		//补全属性
		item.setId(itemId);
		//插入数据
		item.setStatus((byte)1);
		item.setCreated(new Date());
		item.setUpdated(new Date());
		itemMapper.insert(item);
		//创建商品描述
		TbItemDesc itemDesc=new TbItemDesc();
		itemDesc.setItemId(itemId);
		itemDesc.setItemDesc(desc);
		itemDesc.setCreated(new Date());
		itemDesc.setUpdated(new Date());
		//插入商品描述
		itemDescMapper.insert(itemDesc);
		//向Activemq发送商品添加消息
		//发送消息的类型取决于第一个参数
		jmsTemplate.send(destination, new MessageCreator() {
					//回调函数
			@Override
			public Message createMessage(Session session) throws JMSException {
						//发送商品id
				TextMessage textMessage = session.createTextMessage(itemId + "");
				return textMessage;
			}
		});
		return TaotaoResult.ok();
	}
	@Override
	public TaotaoResult deleteItem(String ids) {
		String [] list=ids.split(",");
		int rows=itemMapper.deleteByPrimaryKeys(list);
		TaotaoResult result;
		if(rows<=0){
			result=TaotaoResult.build(200, "删除失败");
			System.out.println("删除失败"+rows+"条 ids:"+ids);
		}else{
			result=TaotaoResult.build(500, "删除成功");
			System.out.println("删除成功 "+rows+"条ids:"+ids);
		}
		//清除缓存中的信息
		for(int i=0;i<list.length;i++){
			jedisClient.set(ITEM_INFO+":"+list[i]+"BASE", "");
			jedisClient.set(ITEM_INFO+":"+list[i]+"DESC", "");
		}
		return result;
	}
	@Override
	public TaotaoResult updateItem(TbItem item, String desc) {
		item.setUpdated(new Date());
		int rows=itemMapper.updateByPrimaryKeySelective(item);
		//创建商品描述
		TbItemDesc itemDesc=new TbItemDesc();
		itemDesc.setItemId(item.getId());
		itemDesc.setItemDesc(desc);
		itemDesc.setUpdated(new Date());
		TbItemDescExample example=new TbItemDescExample();
		//设置查询条件
		TbItemDescExample.Criteria criteria = example.createCriteria();
		criteria.andItemIdEqualTo(item.getId());
		//插入商品描述
		int rows2=itemDescMapper.updateByExampleWithBLOBs(itemDesc, example);
		if(rows<=0||rows2<=0){
			System.out.println("修改失败 :rows="+rows+",rows2="+rows2);
			//清除缓存中的信息
			jedisClient.set(ITEM_INFO+":"+item.getId()+"BASE", "");
			jedisClient.set(ITEM_INFO+":"+item.getId()+"DESC", "");
		}else{
			System.out.println("修改成功 :rows="+rows+",rows2="+rows2);
		}
		return TaotaoResult.ok();
	}
	@Override
	public TaotaoResult instock(String ids) {
		TbItem item=new TbItem();
		item.setStatus((byte)2);
		String [] list=ids.split(",");
		int rows=itemMapper.updateByPrimaryKeySelecteds(item,list);
		if(rows<=0){
			//清除缓存中的信息
			jedisClient.set(ITEM_INFO+":"+item.getId()+"BASE", "");
			System.out.println("修改失败"+rows+"条  ids:"+ids);
		}else{
			System.out.println("修改成功"+rows+"条  ids:"+ids);
		}
		return TaotaoResult.ok();
	}
	@Override
	public TaotaoResult reshelf(String ids) {
		TbItem item=new TbItem();
		item.setStatus((byte)1);
		String [] list=ids.split(",");
		int rows=itemMapper.updateByPrimaryKeySelecteds(item,list);
		if(rows<=0){
			//清除缓存中的信息
			for(int i=0;i<list.length;i++){
				jedisClient.set(ITEM_INFO+":"+list[i]+"BASE", "");
			}
			System.out.println("修改失败 "+rows+"条ids:"+ids);
		}else{
			System.out.println("修改成功 "+rows+"条ids:"+ids);
		}
		return TaotaoResult.ok();
	}
	@Override
	public TbItemDesc getItemDescById(Long itemId) {
	    try{
	    	String json = jedisClient.get(ITEM_INFO+":"+itemId+":DESC");
	    	if(StringUtils.isNotBlank(json)){
	    		TbItemDesc desc=JsonUtils.jsonToPojo(json, TbItemDesc.class);
	    		return desc;
	    	}
	    }catch(Exception e){
	    	e.printStackTrace();
	    }
		TbItemDesc itemDesc=itemDescMapper.selectTbItemDescById(itemId);
		try{
			jedisClient.set(ITEM_INFO+":"+itemId+":DESC", JsonUtils.objectToJson(itemDesc));
			//设置过期时间，提高缓存的利用率
			jedisClient.expire(ITEM_INFO+":"+itemId+"DESC", ITEM_EXPIRE);
		}catch(Exception e){
			e.printStackTrace();
		}
		return itemDesc;
	}
	

}
