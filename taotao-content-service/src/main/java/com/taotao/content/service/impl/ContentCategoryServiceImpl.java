package com.taotao.content.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.taotao.pojo.EasyUITreeNode;
import com.taotao.pojo.TaotaoResult;
import com.taotao.content.service.ContentCategoryService;
import com.taotao.jedis.JedisClient;
import com.taotao.mapper.TbContentCategoryMapper;
import com.taotao.pojo.TbContentCategory;
import com.taotao.pojo.TbContentCategoryExample;
import com.taotao.pojo.TbContentCategoryExample.Criteria;
import com.taotao.utils.JsonUtils;

/**
 * 内容分类管理service
 * <p>Title: ContentCategoryServiceImpl</p>
 * <p>Description: </p>
 * <p>Company: www.itcast.cn</p> 
 * @version 1.0
 */
@Service
public class ContentCategoryServiceImpl implements ContentCategoryService {
	@Autowired
	private TbContentCategoryMapper contentCategoryMapper;
	@Autowired
	private JedisClient jcp;
	
	@Value("${CONTENT_CATEGORY}")
	private String CONTENT_CATEGORY;
	@Override
	/**查询分类列表生成EasyUITree*/
	public List<EasyUITreeNode> getContentCategoryList(Long parentId) {
		//先查缓存
		try {
			String json=jcp.hget(CONTENT_CATEGORY,parentId.toString());
			if(StringUtils.isNotBlank(json)) {
				System.out.println("查询使用Redis"+json);
				List<EasyUITreeNode>  list=JsonUtils.jsonToList(json,EasyUITreeNode.class);
				return list;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		//根据parentId查询子节点列表
		TbContentCategoryExample example = new TbContentCategoryExample();
		//设置查询条件
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		//执行查询
		List<TbContentCategory> list = contentCategoryMapper.selectByExample(example);
		
		List<EasyUITreeNode> resultList = new ArrayList<>();
		for (TbContentCategory tbContentCategory : list) {
			EasyUITreeNode node = new EasyUITreeNode();
			node.setId(tbContentCategory.getId());
			node.setText(tbContentCategory.getName());
			node.setState(tbContentCategory.getIsParent()?"closed":"open");
			//添加到结果列表
			resultList.add(node);
		}
		//将数据库结构存入缓存
		try {
			jcp.hset(CONTENT_CATEGORY, parentId.toString(), JsonUtils.objectToJson(resultList));
		}catch (Exception e) {
			e.printStackTrace();
		}
		return resultList;
	}

	@Override
	public TaotaoResult addContentCategory(Long parentId, String name) {
		//创建一个pojo对象
		TbContentCategory contentCategory = new TbContentCategory();
		//补全对象的属性
		contentCategory.setParentId(parentId);
		contentCategory.setName(name);
		//状态。可选值:1(正常),2(删除)
		contentCategory.setStatus(1);
		//排序，默认为1
		contentCategory.setSortOrder(1);
		contentCategory.setIsParent(false);
		contentCategory.setCreated(new Date());
		contentCategory.setUpdated(new Date());
		//插入到数据库
		long rows=contentCategoryMapper.insert(contentCategory);
		if(rows<=0){
			System.out.println("添加ContentCategory失败;rows="+rows);
			return TaotaoResult.build(500, "添加ContentCategory失败");
		}else{
			System.out.println("添加ContentCategory成功;rows="+rows);
			List<Integer> list=contentCategoryMapper.getLastInsertId();
			contentCategory.setId((long)list.get(0));
		}
		//判断父节点的状态
		TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
		if (parent!=null&&!parent.getIsParent()) {
			//如果父节点为叶子节点应该改为父节点
			parent.setIsParent(true);
			//更新父节点
			contentCategoryMapper.updateByPrimaryKey(parent);
		}
		//更新redis缓存
		jcp.hdel(CONTENT_CATEGORY);
		//返回结果
		return TaotaoResult.ok(contentCategory);
	}

	@Override
	public TaotaoResult updateContentCategory(Long id, String newName) {
		
		TbContentCategory record=new TbContentCategory();
		record.setId(id);
		record.setName(newName);
		int rows=contentCategoryMapper.updateByPrimaryKeySelective(record);
		TaotaoResult result;
		if(rows<=0){
			System.out.println("修改失败"); 
			result=TaotaoResult.build(500,"修改失败");
		}else{
			result=TaotaoResult.build(200,"修改成功");
		}
		//更新redis缓存
		jcp.hdel(CONTENT_CATEGORY);
		return result;
	}

	@Override
	public TaotaoResult deleteContentCategory(Long id) {
		
		TbContentCategory tcc=contentCategoryMapper.selectByPrimaryKey(id);
		TaotaoResult result=new TaotaoResult();
		if (!tcc.getIsParent()) {//如果不是父节点
			int rows=contentCategoryMapper.deleteByPrimaryKey(id);
			if(rows<=0){
				System.out.println("删除失败"); 
				result=TaotaoResult.build(500,"删除失败");
			}else{
				result=TaotaoResult.build(200,"删除成功");
			}
		}else {//如果删除的是父节点
			deletChildrenByParrentId(id);
		}
		//更新被删除节点的父节点
		if(tcc!=null){
			//获取父节点id
			Long parentId=tcc.getParentId();
			TbContentCategory parent = contentCategoryMapper.selectByPrimaryKey(parentId);
			//判断父节点的状态
			List<EasyUITreeNode> list=getContentCategoryList(parent.getId());
			//如果父节点没有叶子节点应该改为叶子节点
			if (list==null||list.isEmpty()) {
				System.out.println("修改为子节点");
				parent.setIsParent(false);
				//更新父节点
				contentCategoryMapper.updateByPrimaryKey(parent);
			}
		}
		//更新redis缓存
		jcp.hdel(CONTENT_CATEGORY);
		return result;
	}
	//根据父节点ID递归删除子节点包含父节点
	private void deletChildrenByParrentId(long parentId) {
		TbContentCategoryExample example = new TbContentCategoryExample();
		//设置查询条件
		Criteria criteria = example.createCriteria();
		criteria.andParentIdEqualTo(parentId);
		//根据parentId查询子节点列表
		List<TbContentCategory> contentCategory=contentCategoryMapper.selectByExample(example);
		//如果父节点包含子节点
		if(contentCategory!=null&&contentCategory.size()!=0) {
			for(TbContentCategory tcc:contentCategory) {
				deletChildrenByParrentId(tcc.getId());
			}
		}
		contentCategoryMapper.deleteByPrimaryKey(parentId);
		
	}

}
