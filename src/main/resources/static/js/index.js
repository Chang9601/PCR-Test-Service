	document.querySelector("#btn-submit").addEventListener("click", (e) => {
		
		let sidoCdNm = document.querySelector("#sidoCdNm").value;
		let sgguCdNm = document.querySelector("#sgguCdNm").value;
		
		//console.log(sidoCdNm);
		//console.log(sgguCdNm);	
		
		getHospital(sidoCdNm, sgguCdNm);
	});

	let getHospital = async (sidoCdNm, sgguCdNm) => {		
		let resp = await fetch(`http://localhost:5000/api/hospital?sidoCdNm=${sidoCdNm}&sgguCdNm=${sgguCdNm}`);
		let respPasing = await resp.json();
		
		//console.log(respPasing);
		setHospital(respPasing);
	}
	
	let setHospital = (respPasing) => {
		let tbodyHospitalDom = document.querySelector("#tbody-hospital")
		tbodyHospitalDom.innerHTML = "";

		respPasing.forEach((e) => {
			let trEL = document.createElement("tr");	
			
			let tdEL1 = document.createElement("td");	
			let tdEL2 = document.createElement("td");	
			let tdEL3 = document.createElement("td");	
			
			tdEL1.innerHTML = e.yadmNm;
			tdEL2.innerHTML = e.pcrPsblYn;
			tdEL3.innerHTML = e.addr;
			
			//console.log(tdEL1);
			//console.log(tdEL2);
			//console.log(tdEL3);
			
			trEL.append(tdEL1);
			trEL.append(tdEL2);
			trEL.append(tdEL3);
			
			//console.log("==========================");
			//console.log(trEL)
			//console.log("==========================");
			
			//console.log("==========================");
			
			trEL.text = e;
			tbodyHospitalDom.append(trEL);
		});
	}

	let setSgguCdNm = (respPasing) => {
		let sgguCdNmDom = document.querySelector("#sgguCdNm");
		
		// 초기화
		sgguCdNmDom.innerHTML = "";

		//console.log(respPasing);
		// 반환이 필요없으니 forEach
		respPasing.forEach((e) => {
			// element 생성 후 삽입		
			let optionEL = document.createElement("option");			
			optionEL.text = e;
			sgguCdNmDom.append(optionEL);
		});
	}

	let getSgguCdNm = async (sidoCdNm) => {
		let resp = await fetch(`http://localhost:5000/api/sgguCdNm?sidoCdNm=${sidoCdNm}`);
		let respPasing = await resp.json();
		//console.log(respPasing);
		setSgguCdNm(respPasing);
	}

	let sidoCdNmDom = document.querySelector("#sidoCdNm");
	sidoCdNmDom.addEventListener("change", (e) => {
		//console.log(e.target.value);
		
		let sidoCdNm = e.target.value;
		// 백틱 사용해서 자바스크립트 변수 바인딩
		getSgguCdNm(sidoCdNm);
	});