package com.csup96.batch;

import java.util.List;
import lombok.Data;

// 1. Import(alt + shift + o) - IntelliJ 키 맵핑
// 2. private -> private(ctrl + r)
// 3. Lombok 사용
// 4.
@Data
class Body {

	private Items items;
	private Integer numOfRows;
	private Integer pageNo;
	private Integer totalCount;
}

@Data
class Header {
	private String resultCode;
	private String resultMsg;
}

@Data
class Item {

	private String addr;
	private Integer mgtStaDd;
	private String pcrPsblYn;
	private String ratPsblYn;
	private Integer recuClCd;
	private Integer rnum;
	private String rprtWorpClicFndtTgtYn;
	private String sgguCdNm;
	private String sidoCdNm;
	private String telno;
	private Double xPosWgs84;
	private Integer xPos;
	private Double yPosWgs84;
	private Integer yPos;
	private String yadmNm;
	private String ykihoEnc;
}

@Data
class Items {
	
	private List<Item> item;
}

@Data
class Response {

	private Header header;
	private Body body;
}

@Data
public class ResponseDto {

	private Response response;
}