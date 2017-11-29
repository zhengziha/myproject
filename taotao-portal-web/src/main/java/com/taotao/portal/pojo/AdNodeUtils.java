package com.taotao.portal.pojo;

import com.taotao.pojo.TbContent;

/**
 * 把TbContent简单转换成ADDNode
 * @author 47606
 *
 */
public class AdNodeUtils {
	public static ADDNode paserToAdNode(TbContent tbContent){
		ADDNode node=new ADDNode();
		
		node.setAlt(tbContent.getTitle());
		node.setSrc(tbContent.getPic());
		node.setSrcB(tbContent.getPic2());
		node.setHref(tbContent.getUrl());
		return node;
	}
}
