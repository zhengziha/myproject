package com.taotao.service;

import java.util.List;

import com.taotao.pojo.EasyUITreeNode;
import com.taotao.pojo.TbItemCat;

public interface ItemCatService {
	List<EasyUITreeNode> getItemCatList(long parentId);

	TbItemCat getNodeNameById(Long itemId);
}
