package com.csup96.batch;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.csup96.domain.Hospital;
import com.google.gson.Gson;


public class PcrDownloadBatchTest {
	
	//@Test
	public void start() {
		 // 1. URL 주소 만들기(끝)
		/*
        StringBuffer sbUrl = new StringBuffer();

        sbUrl.append("http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService");
        sbUrl.append("?serviceKey="); // 서비스 키
        sbUrl.append("서비스키%3D%3D");
        sbUrl.append("&pageNo="); // 몇번째 페이지 인지
        sbUrl.append("1");
        sbUrl.append("&numOfRows=");
        sbUrl.append("10"); // 한 페이지당 몇개씩 가져올지
        sbUrl.append("&_type=");
        sbUrl.append("json"); // 데이터 포맷은 JSON
		 */
		String sburl = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=CzmrpkHb4vk8oXPXFGWL7pBA8v2L1uSeYlWx%2FeUf%2ByV0LdGtBWkYxvCpWEC4llWtLhNoV6QzlAKSLOuhU%2F%2BhLw%3D%3D&pageNo=1&numOfRows=10&_type=json";

		
        // 2. 다운로드 받기(끝)
        try {
            // URL라이브러리가 ==을 %3D%3D로 바꿔줌 => url이 safe한 상태
            // 쿼리 스트링 때문에!!
            // 이미 인코드된 것을 또 인코드할 수도 있는데
            // URL 라이브러리는 URL safe가 적용되어 있으면 더 이상 반영하지 않는다.
            URL url = new URL(sburl);
            // http인지 https인지 모르기때문에 다운캐스팅해서 맞춰써라
            // conn이 socket!
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));

            StringBuffer sbDownload = new StringBuffer(); // 통신결과 모아두기

            while (true) {
                String input = br.readLine();

                // 버퍼가 비었을 때 break
                if (input == null) {
                    break;
                }

                sbDownload.append(input);
            }

            // 3. 파싱
            Gson gson = new Gson();
            ResponseDto dto = gson.fromJson(sbDownload.toString(), ResponseDto.class);

            // 4. 검증    
            List<Item> hospitals = dto.getResponse().getBody().getItems().getItem();
            for(Item item : hospitals) {
    			System.out.println(item.getYadmNm());
    			System.out.println("PCR 여부: " + item.getPcrPsblYn());           	
            }

        } catch (Exception e) {
            e.printStackTrace();
        }		
	}
	
	@Test
	public void download() {
	     // 1. URL 주소 만들기 - totalCount 확인용
		/*
        StringBuffer totalCountCheckURL = new StringBuffer();

        totalCountCheckURL.append("http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService");
        totalCountCheckURL.append("?serviceKey="); // 서비스 키
        totalCountCheckURL.append("서비스키%3D%3D");
        totalCountCheckURL.append("&pageNo="); // 몇번째 페이지 인지
        totalCountCheckURL.append("1");
        totalCountCheckURL.append("&numOfRows=");
        totalCountCheckURL.append("2"); // totalCount 체크만 할 것이기 때문에 2개만 받아도 된다.
                                        // 1개만 받으면 List를 Object로 받기 때문에 2개를 받는다.
        totalCountCheckURL.append("&_type=");
        totalCountCheckURL.append("json"); // 데이터 포맷은 JSON
		*/

		String totalCountCheckURL = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=CzmrpkHb4vk8oXPXFGWL7pBA8v2L1uSeYlWx%2FeUf%2ByV0LdGtBWkYxvCpWEC4llWtLhNoV6QzlAKSLOuhU%2F%2BhLw%3D%3D&pageNo=1&numOfRows=2&_type=json";
		
        // 2. 다운로드 받기 - totalCount 확인용
        try {
            URL urlTemp = new URL(totalCountCheckURL);
            HttpURLConnection connTemp = (HttpURLConnection) urlTemp.openConnection();

            BufferedReader brTemp = new BufferedReader(
                    new InputStreamReader(connTemp.getInputStream(), "utf-8"));

            StringBuffer sbTotalCountCheck = new StringBuffer(); // 통신결과 모아두기

            while (true) {
                String input = brTemp.readLine();

                // 버퍼가 비었을 때 break
                if (input == null) {
                    break;
                }

                sbTotalCountCheck.append(input);
            }

            // 3. 검증 - totalCountCheck
            System.out.println(sbTotalCountCheck.toString());

            // 4. 파싱
            // 결과를 1개만 받아오면 리스트 타입도 중괄호로 받아오기 때문에 파싱 오류 발생
            Gson gsonTemp = new Gson();
            ResponseDto totalCountCheckDto = gsonTemp.fromJson(sbTotalCountCheck.toString(), ResponseDto.class);

            // 5. totalCount 담기
            int totalCount = totalCountCheckDto.getResponse().getBody().getTotalCount();
            System.out.println("totalCount : " + totalCount);

            // 6. 전체 데이터 받기(totalCount)
            // (1) URL 주소 만들기
            /*
            StringBuffer sbURL = new StringBuffer();

            sbURL.append("http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService");
            sbURL.append("?serviceKey="); // 서비스 키
            sbURL.append("서비스키%3D%3D");
            sbURL.append("&pageNo="); // 몇번째 페이지 인지
            sbURL.append("1");
            sbURL.append("&numOfRows=");
            sbURL.append(totalCount); // totalCount 체크만 할 것이기 때문에 2개만 받아도 된다.
                                      // 1개만 받으면 List를 Object로 받기 때문에 2개를 받는다.
            sbURL.append("&_type=");
            sbURL.append("json"); // 데이터 포맷은 JSON
			*/
            
            String sbURL = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=CzmrpkHb4vk8oXPXFGWL7pBA8v2L1uSeYlWx%2FeUf%2ByV0LdGtBWkYxvCpWEC4llWtLhNoV6QzlAKSLOuhU%2F%2BhLw%3D%3D&pageNo=1&numOfRows=" 
            		+ totalCount + "&_type=json";
            // (2) 다운로드 받기 - totalCount 확인용
            URL url = new URL(sbURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            BufferedReader br = new BufferedReader(
                    new InputStreamReader(conn.getInputStream(), "utf-8"));

            StringBuffer sb = new StringBuffer(); // 통신결과 모아두기

            while (true) {
                String input = br.readLine();

                // 버퍼가 비었을 때 break
                if (input == null) {
                    break;
                }
                sb.append(input);
            }

            // (3) 파싱
            // 결과를 1개만 받아오면 리스트 타입도 중괄호로 받아오기 때문에 파싱 오류 발생
            Gson gson = new Gson();
            ResponseDto responseDto = gson.fromJson(sb.toString(), ResponseDto.class);

            // 7. 사이즈 검증
            if (responseDto.getResponse().getBody().getItems().getItem().size() == totalCount) {
                System.out.println("SUCCESS");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
	}		
	
	
	/*
	//@Test
	public void start() {
		// 1. 공공 데이터 다운로드	
		RestTemplate rt = new RestTemplate();
		//rt.setMessageConverters(messageConverters);
	
		// 테스트 시 %3D 제외(URL 인코딩 불필요)
		String url = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=CzmrpkHb4vk8oXPXFGWL7pBA8v2L1uSeYlWx%2FeUf%2ByV0LdGtBWkYxvCpWEC4llWtLhNoV6QzlAKSLOuhU%2F%2BhLw%3D%3D&pageNo=1&numOfRows=10&_type=json";
		
		ResponseDto dto = rt.getForObject(url, ResponseDto.class);
		System.out.println("값: " + dto);
		
		List<Item> hospitals = dto.getResponse().getBody().getItems().getItem();
		for(Item item : hospitals) {
			System.out.println(item.getYadmNm());
			System.out.println("PCR 여부: " + item.getPcrPsblYn());
		}
	}
	*/
	
	/*
	// 공공데이터 다운로드 테스트 컬렉션 담기(전체 데이터)
	//@Test
	public void download() throws UnsupportedEncodingException {
		RestTemplate rt = new RestTemplate();
		//rt.setMessageConverters(messageConverters);
				
		// 1. 리스트 준비
		List<Hospital> hospitals = new ArrayList<>();
		
		// 2. API 한 번 호출 후 totalCount 확인	
		int totalCount = 2;
		
		String totalCountUrl = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=CzmrpkHb4vk8oXPXFGWL7pBA8v2L1uSeYlWx%2FeUf%2ByV0LdGtBWkYxvCpWEC4llWtLhNoV6QzlAKSLOuhU%2F%2BhLw&pageNo=1&numOfRows=" 
				+ totalCount + "&_type=json";
		
		ResponseDto totalCountDto = rt.getForObject(totalCountUrl, ResponseDto.class);
		totalCount = totalCountDto.getResponse().getBody().getTotalCount();
		
		//3. totalCount만큼 한 번에 가져오기
		String url = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=CzmrpkHb4vk8oXPXFGWL7pBA8v2L1uSeYlWx%2FeUf%2ByV0LdGtBWkYxvCpWEC4llWtLhNoV6QzlAKSLOuhU%2F%2BhLw&pageNo=1&numOfRows=" 
				+ totalCount + "&_type=json";	
		ResponseDto responseDto = rt.getForObject(url, ResponseDto.class);
		List<Item> items = responseDto.getResponse().getBody().getItems().getItem();
		System.out.println("가져온 데이터 크기:" + items.size());
		
		// 컬렉션 복사
		hospitals = items.stream().map((e) -> {
			return Hospital.builder().
					addr(e.getAddr())
					.mgtStaDd(e.getMgtStaDd())
					.pcrPsblYn(e.getPcrPsblYn())
					.ratPsblYn(e.getRatPsblYn())
					.recuClCd(e.getRecuClCd())
					.rprtWorpClicFndtTgtYn(e.getRprtWorpClicFndtTgtYn())
					.sgguCdNm(e.getSgguCdNm())
					.sidoCdNm(e.getSidoCdNm())
					.telno(e.getTelno())
					.xPosWgs84(e.getXPosWgs84())
					.yPosWgs84(e.getYPosWgs84())
					.xPos(e.getXPos())
					.yPos(e.getYPos())
					.yadmNm(e.getYadmNm())
					.ykihoEnc(e.getYkihoEnc())
					.build();
			}
		).collect(Collectors.toList());
	}
	*/	
} 