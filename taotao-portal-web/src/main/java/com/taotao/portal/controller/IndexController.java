package com.taotao.portal.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.taotao.content.service.ContentService;
import com.taotao.pojo.TbContent;
import com.taotao.portal.pojo.ADDNode;
import com.taotao.portal.pojo.AdNodeUtils;
import com.taotao.utils.JsonUtils;

@Controller
public class IndexController {
	//大广告
	@Value("${AD1_CATEGORY_ID}")
	private Long AD1_CATEGORY_ID;
	
	@Value("${AD1_WIDTH}")
	private Integer AD1_WIDTH;
	@Value("${AD1_HEIGHT}")
	private Integer AD1_HEIGHT;
	
	@Value("${AD1_WIDTHB}")
	private Integer AD1_WIDTHB;
	@Value("${AD1_HEIGHTB}")
	private Integer AD1_HEIGHTB;
	//右上角广告
	@Value("${ADRIGHT_CATEGORY_ID}")
	private Long ADRIGHT_CATEGORY_ID;
	
	@Value("${ADRIGHT_WIDTH}")
	private Integer ADRIGHT_WIDTH;
	@Value("${ADRIGHT_HEIGHT}")
	private Integer ADRIGHT_HEIGHT;
	
	@Value("${ADRIGHT_WIDTHB}")
	private Integer ADRIGHT_WIDTHB;
	@Value("${ADRIGHT_HEIGHTB}")
	private Integer ADRIGHT_HEIGHTB;
	@Autowired
	private ContentService contentService;
	
	@RequestMapping("/index")
	public String showIndex(Model model){
		//根据cid查询轮播图内容列表
		List<TbContent> list=contentService.getContentListByCid(AD1_CATEGORY_ID);
		//把列表转换为Ad1Node列表
		List<ADDNode> ad1Nodes=new ArrayList<>();
		for(TbContent tbContent:list){
			ADDNode node=AdNodeUtils.paserToAdNode(tbContent);
			node.setHeight(AD1_HEIGHT);
			node.setHeightB(AD1_HEIGHTB);
			node.setWidth(AD1_WIDTH);
			node.setWidthB(AD1_WIDTHB);
			ad1Nodes.add(node);
		}
		//把列表转换成json数据
		String ad1Json=JsonUtils.objectToJson(ad1Nodes);
		//把json数据传递给页面
		model.addAttribute("ad1",ad1Json);
		List<TbContent> adRightlist=contentService.getContentListByCid(ADRIGHT_CATEGORY_ID);
		List<ADDNode> adRightNodes=new ArrayList<>();
		for(TbContent tbContent:adRightlist){
			
			ADDNode adRightNode=AdNodeUtils.paserToAdNode(tbContent);
			adRightNode.setHeight(ADRIGHT_HEIGHT);
			adRightNode.setHeightB(ADRIGHT_HEIGHTB);
			
			adRightNode.setWidth(ADRIGHT_WIDTH);
			adRightNode.setWidthB(ADRIGHT_WIDTHB);
			adRightNodes.add(adRightNode);
		}
		String adRightJson=JsonUtils.objectToJson(adRightNodes);
		model.addAttribute("adRight",adRightJson);
		return "index";
	}
	
}
