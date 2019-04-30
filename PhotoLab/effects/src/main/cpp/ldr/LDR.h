/**
 * Created by JONG HO BAEK on 20,February,2019
 * email: icanmobile@gmail.com
 */

#ifndef __LDR_H__
#define __LDR_H__

#include <vector>
#include "stdlib.h"
#include <string>

#include "ToneMappingParameters.h"
#include "ToneMappingBase.h"
#include "ToneMappingInt.h"


using namespace std;

class CLdr {
public:
	CLdr();
	~CLdr();

public:
	void
	init
	(
		float power,
		float blur
	);

	void
	deinit
	(
		void
	);

	void
	toneMapping
	(
	    uint8_t* pixels,
		int width,
		int height,
		pCallbackFunc pProgress
	);

	void
	toneMapping
	(
		uint8_t* pixels,
		int width,
		int height,
		float power,
		float blur,
		pCallbackFunc pProgress
	);
};
typedef CLdr* PCLDR;

#endif //__HDRTONEMAPPING_H__
