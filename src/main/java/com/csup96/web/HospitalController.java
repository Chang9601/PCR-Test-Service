package com.csup96.web;


import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.csup96.domain.Hospital;
import com.csup96.domain.HospitalRepository;

import lombok.RequiredArgsConstructor;

@CrossOrigin
@RequiredArgsConstructor
@Controller
public class HospitalController {
	private static final String TAG = "HospitalController: ";
	private final HospitalRepository hospitalRepository;
	
	// GET으로 받을 경우 @Param 필요, POST는 불필요
	// x-www-form-urlencoded의 경우 POST, GET 둘 다 가능
	@GetMapping("/")
	public String index(Model model) {
		model.addAttribute("sidoCdNms", hospitalRepository.mFindSidoCdNm());
		model.addAttribute("sgguCdNms", hospitalRepository.mFindSgguCdNm("강원"));
	
		return "index"; // templates/index.mustache 찾음
	}
	
	// http://localhost:8000/api/hospital?sidoCdNm=query&sgguCdNm=query
	@GetMapping("/api/hospital")
	public @ResponseBody List<Hospital> hospitals(String sidoCdNm, String sgguCdNm) {
		System.out.println(TAG + hospitalRepository.mFindSidoCdNm().size());
		return hospitalRepository.mFindHospital(sidoCdNm, sgguCdNm);
	}
	
	@GetMapping("/api/sgguCdNm") // JSON 응답
	public @ResponseBody List<String> sgguCdNm(String sidoCdNm) {
		return hospitalRepository.mFindSgguCdNm(sidoCdNm);
	}
	
	
	
}

