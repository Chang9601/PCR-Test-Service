package com.csup96.domain;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface HospitalRepository extends JpaRepository<Hospital, Integer> {

		// name query 사용 시 어노테이션 필요
		@Query(value = "SELECT * FROM hospital WHERE sidoCdNm = :sidoCdNm AND sgguCdNm = :sgguCdNm AND pcrPsblYn = 'Y'", nativeQuery = true)
		public List<Hospital> mFindHospital(@Param("sidoCdNm") String sidoCdNm, @Param("sgguCdNm") String sgguCdNm);
		
		// String 가능하지만 저장 X, order by 추가
		@Query(value = "SELECT distinct sidoCdNm FROM hospital order by sidoCdNm", nativeQuery = true)
		public List<String> mFindSidoCdNm();
		
		@Query(value = "SELECT distinct sgguCdNm FROM hospital WHERE sidoCdNm = :sidoCdNm order by sgguCdNm", nativeQuery = true)
		public List<String> mFindSgguCdNm(@Param("sidoCdNm") String sidoCdNm);
}