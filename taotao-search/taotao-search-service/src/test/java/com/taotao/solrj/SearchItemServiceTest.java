package com.taotao.solrj;

import java.util.Timer;
import java.util.TimerTask;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.taotao.pojo.TaotaoResult;
import com.taotao.search.service.impl.SearchItemServiceImpl;

public class SearchItemServiceTest {
	private ClassPathXmlApplicationContext ctx;
	
	//@Test
	public void testSearchItemService(){
		ctx=new ClassPathXmlApplicationContext("spring/applicationContext-dao.xml",
				"spring/applicationContext-service.xml","spring/applicationContext-solr.xml");
		
		final SearchItemServiceImpl service=ctx.getBean("searchItemServiceImpl",SearchItemServiceImpl.class);
		new Timer().schedule(new TimerTask(){

			@Override
			public void run() {
				while(service!=null){
					TaotaoResult result=service.getCompleted();
					
					System.out.println("总共:"+result.getMsg()+",已完成:"+result.getData());
					if(Integer.parseInt(result.getMsg())<(Integer)result.getData())
						return;
					
				}
			}
			
		}, 3000, 500);
		TaotaoResult result=service.importItemsToIndex();
		
		System.out.println(result.getMsg());
	}
	//@Before
	public void before(){
		if(ctx!=null)
		ctx.close();
	}
}
