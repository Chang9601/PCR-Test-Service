package com.csup96.batch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.csup96.domain.Hospital;
import com.csup96.domain.HospitalRepository;
import com.google.gson.Gson;

import lombok.RequiredArgsConstructor;

// 하루에 1번씩 다운로드, DB 업데이트
// PCR 검사기관 추가 될 수 있다.
// 4개 병원이 있다면 4개가 DB에 insert
// 다음날 5개 병원이 있다면 원본 데이터 삭제 후 새로 추가
// 공공 데이터를 바로 서비스해주는 방식은 하루에 트래픽이 1000이라서 서비스하기 힘들다. 
@RequiredArgsConstructor
@Component
public class PcrDownloadBatch {

	// DI
	private final HospitalRepository hospitalRepository;

	// 초 분 시 일 월 주
	@Scheduled(cron = "0 39 * * * *", zone = "Asia/Seoul") // 매 시간 마다 실행
	public void startBatch() {
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
	}

	/*
	 * // 초 분 시 일 월 주
	 * 
	 * @Scheduled(cron = "0 48 * * * *", zone = "Asia/Seoul") // 48분 마다 실행 public
	 * void startBatch() { //System.out.println("1분 마다 실행");
	 * 
	 * // 1. 리스트 준비 List<Hospital> hospitals = new ArrayList<>();
	 * 
	 * // 2. API 한 번 호출 후 totalCount 확인 RestTemplate rt = new RestTemplate(); int
	 * totalCount = 2;
	 * 
	 * String totalCountUrl =
	 * "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=CzmrpkHb4vk8oXPXFGWL7pBA8v2L1uSeYlWx%2FeUf%2ByV0LdGtBWkYxvCpWEC4llWtLhNoV6QzlAKSLOuhU%2F%2BhLw%3D%3D&pageNo=1&numOfRows="
	 * + totalCount + "&_type=json";
	 * 
	 * ResponseDto totalCountDto = rt.getForObject(totalCountUrl,
	 * ResponseDto.class); totalCount =
	 * totalCountDto.getResponse().getBody().getTotalCount();
	 * 
	 * //3. totalCount만큼 한 번에 가져오기 String url =
	 * "http://apis.data.go.kr/B551182/rprtHospService/getRprtHospService?serviceKey=CzmrpkHb4vk8oXPXFGWL7pBA8v2L1uSeYlWx%2FeUf%2ByV0LdGtBWkYxvCpWEC4llWtLhNoV6QzlAKSLOuhU%2F%2BhLw&pageNo=1&numOfRows="
	 * + totalCount + "&_type=json"; ResponseDto responseDto = rt.getForObject(url,
	 * ResponseDto.class); List<Item> items =
	 * responseDto.getResponse().getBody().getItems().getItem();
	 * System.out.println("가져온 데이터 크기:" + items.size());
	 * 
	 * // 컬렉션 복사 hospitals = items.stream().map((e) -> { return Hospital.builder().
	 * addr(e.getAddr()) .mgtStaDd(e.getMgtStaDd()) .pcrPsblYn(e.getPcrPsblYn())
	 * .ratPsblYn(e.getRatPsblYn()) .recuClCd(e.getRecuClCd()) .rnum(e.getRnum())
	 * .rprtWorpClicFndtTgtYn(e.getRprtWorpClicFndtTgtYn())
	 * .sgguCdNm(e.getSgguCdNm()) .sidoCdNm(e.getSidoCdNm()) .telno(e.getTelno())
	 * .xPosWgs84(e.getXPosWgs84()) .yPosWgs84(e.getYPosWgs84()) .xPos(e.getXPos())
	 * .yPos(e.getYPos()) .yadmNm(e.getYadmNm()) .ykihoEnc(e.getYkihoEnc())
	 * .build(); }).collect(Collectors.toList());
	 * 
	 * // 배치시간에 DB에 INSERT, 하루에 1번 예정 hospitalRepository.saveAll(hospitals); }
	 */
}
