package com.taotao.portal.pojo;
/*
 * vardata=[
    {
        "srcB": "http://image.taotao.com/images/2015/03/03/2015030304360302109345.jpg",
        "height": 240,
        "alt": "",
        "width": 670,
        "src": "http://image.taotao.com/images/2015/03/03/2015030304360302109345.jpg",
        "widthB": 550,
        "href": "http://sale.jd.com/act/e0FMkuDhJz35CNt.html?cpdad=1DLSUE",
        "heightB": 240
    },
    */
public class ADDNode {
	 private Integer height;
	 private Integer width;
	 private Integer widthB;
	 private Integer heightB;
	 private String src;
	 private String srcB;
	 private String href;
	 private String alt;
	 
public String getAlt() {
		return alt;
	}
	public void setAlt(String alt) {
		this.alt = alt;
	}
public Integer getHeight() {
	return height;
}
public void setHeight(Integer height) {
	this.height = height;
}
public Integer getWidth() {
	return width;
}
public void setWidth(Integer width) {
	this.width = width;
}
public Integer getWidthB() {
	return widthB;
}
public void setWidthB(Integer widthB) {
	this.widthB = widthB;
}
public Integer getHeightB() {
	return heightB;
}
public void setHeightB(Integer heightB) {
	this.heightB = heightB;
}
public String getSrc() {
	return src;
}
public void setSrc(String src) {
	this.src = src;
}
public String getSrcB() {
	return srcB;
}
public void setSrcB(String srcB) {
	this.srcB = srcB;
}
public String getHref() {
	return href;
}
public void setHref(String href) {
	this.href = href;
}
@Override
public String toString() {
	return "AD1Node [height=" + height + ", width=" + width + ", widthB=" + widthB + ", heightB=" + heightB + ", src="
			+ src + ", srcB=" + srcB + ", href=" + href + ", alt=" + alt + "]";
}

 
}
