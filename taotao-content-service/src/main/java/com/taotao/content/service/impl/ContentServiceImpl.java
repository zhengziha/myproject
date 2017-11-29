package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.pojo.EasyUIDataGridResult;
import com.taotao.pojo.TaotaoResult;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.taotao.content.service.ContentService;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbContentMapper;
import com.taotao.pojo.TbContent;
import com.taotao.pojo.TbContentExample;
import com.taotao.pojo.TbContentExample.Criteria;
import com.taotao.utils.JsonUtils;

@Service
public class ContentServiceImpl implements ContentService {

	@Autowired
	private TbContentMapper contentMapper;
	
	@Autowired
	private JedisClient jcp;
	
	@Value("${CONTENT_KEY}")
	private String CONTENT_KEY;
	
	@Override
	public TaotaoResult addContent(TbContent content) {
		//补全pojo的属性
		content.setCreated( new Date());
		content.setUpdated(new Date());
		//插入到内容表
		contentMapper.insert(content);
		jcp.hdel(CONTENT_KEY, content.getCategoryId().toString());
		return TaotaoResult.ok();
	}

	@Override
	public EasyUIDataGridResult getContentList(Long cid, int page, int rows) {
		
		PageHelper.startPage(page, rows);
		TbContentExample contentExample=new TbContentExample();
		TbContentExample.Criteria criteria=contentExample.createCriteria();
		criteria.andCategoryIdEqualTo(cid);
		List<TbContent> list=contentMapper.selectByExampleWithBLOBs(contentExample);
		//转换成分页对象
		PageInfo<TbContent> pageInfo = new PageInfo<>(list);
		//创建EasyUIDate对象
		EasyUIDataGridResult result=new EasyUIDataGridResult();
		result.setTotal(pageInfo.getTotal());
		result.setRows(list);
		return result;
	}

	@Override
	public TaotaoResult updateContent(TbContent content) {
		content.setUpdated(new Date());
		contentMapper.updateByPrimaryKeyWithBLOBs(content);
		jcp.hdel(CONTENT_KEY, ""+content.getCategoryId());
		return TaotaoResult.ok();
	}
	
	@Override
	public TaotaoResult deleteContent(String ids) {
		String[]arr=ids.split(",");
		List<Long> list=new ArrayList<Long>();
		for(String s:arr) {
			list.add(Long.parseLong(s));
		}
		System.out.println(list);
		
		TbContentExample contentExample=new TbContentExample();
		TbContentExample.Criteria criteria=contentExample.createCriteria();
		criteria.andIdIn(list);
		contentMapper.deleteByExample(contentExample);
		jcp.hdel(CONTENT_KEY, arr);
		return TaotaoResult.ok();
	}
	/**查询内容列表根据内容ID*/
	@Override
	public List<TbContent> getContentListByCid(Long cid) {
		//先查询Redis
		try{
			String json=jcp.hget(CONTENT_KEY,""+cid);
			if(StringUtils.isNotBlank(json)) {
				List<TbContent> list=JsonUtils.jsonToList(json, TbContent.class);
				System.out.println("查询使用Redis"+json);
				return list;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		//查不到,查数据库
		TbContentExample example= new TbContentExample();
		Criteria criteria=example.createCriteria();
		//设置查询条件
		criteria.andCategoryIdEqualTo(cid);
		List<TbContent> list=contentMapper.selectByExample(example);
		//添加到缓存
		try {
			jcp.hset(CONTENT_KEY, ""+cid, JsonUtils.objectToJson(list));
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		return list;
	}

}
