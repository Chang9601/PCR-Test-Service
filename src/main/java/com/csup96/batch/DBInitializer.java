package com.csup96.batch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.csup96.domain.Hospital;
import com.csup96.domain.HospitalRepository;
import com.google.gson.Gson;

@Configuration
public class DBInitializer {
	
	// Spring Boot 시작 시 한 번 실행
	@Bean
	public CommandLineRunner initDB(HospitalRepository hospitalRepository) {
		return (args) -> {
			// 1. URL 주소 만들기 - totalCount 확인용
			String totalCountCheckURL = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=CzmrpkHb4vk8oXPXFGWL7pBA8v2L1uSeYlWx%2FeUf%2ByV0LdGtBWkYxvCpWEC4llWtLhNoV6QzlAKSLOuhU%2F%2BhLw%3D%3D&pageNo=1&numOfRows=2&_type=json";

			// 2. 다운로드 받기 - totalCount 확인용
			try {
				URL urlTemp = new URL(totalCountCheckURL);
				HttpURLConnection connTemp = (HttpURLConnection) urlTemp.openConnection();

				BufferedReader brTemp = new BufferedReader(new InputStreamReader(connTemp.getInputStream(), "utf-8"));

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
				String sbURL = "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=CzmrpkHb4vk8oXPXFGWL7pBA8v2L1uSeYlWx%2FeUf%2ByV0LdGtBWkYxvCpWEC4llWtLhNoV6QzlAKSLOuhU%2F%2BhLw%3D%3D&pageNo=1&numOfRows="
						+ totalCount + "&_type=json";
				// (2) 다운로드 받기 - totalCount 확인용
				URL url = new URL(sbURL);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();

				BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "utf-8"));

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
				
				List<Item> items = responseDto.getResponse().getBody().getItems().getItem();
				List<Hospital> hospitals = new ArrayList<>();
				
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
				
				
				// 기존 데이터 전부 삭제(yml 파일: ddl auto update로 변경)
				hospitalRepository.deleteAll();
				
				// 배치시간에 DB에 삽입
				hospitalRepository.saveAll(hospitals);

			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	}
}
