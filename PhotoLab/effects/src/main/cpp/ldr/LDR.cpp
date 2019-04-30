/**
 * Created by JONG HO BAEK on 20,February,2019
 * email: icanmobile@gmail.com
 */

#include "LDR.h"

ToneMappingInt* g_pMapper = NULL;

CLdr::CLdr
(
	void
)
{

}

CLdr::~CLdr
(
	void
)
{

}

void
CLdr::init
(
	float power,
	float blur
)
{
	ToneMappingParameters params;
    params.info_fast_mode = 1;
    params.low_saturation = 100;
    params.high_saturation = 100;
    params.stretch_contrast = 1;
    params.function_id = 0;

    params.stage[0].enabled = 1;
    params.stage[0].power = power;
    params.stage[0].blur = blur;

    params.stage[1].enabled = 1;
    params.stage[1].power = power;
    params.stage[1].blur = blur;

    params.stage[2].enabled = 0;
    params.stage[3].enabled = 0;

    params.unsharp_mask.enabled = 1;
    params.unsharp_mask.power = 19.1446;
    params.unsharp_mask.blur = 4;
    params.unsharp_mask.threshold = 9;

	g_pMapper = new ToneMappingInt();
	g_pMapper->apply_parameters(params);
}

void
CLdr::deinit
(
	void
)
{
	if( g_pMapper != NULL )
	{
		delete g_pMapper;
		g_pMapper = NULL;
	}
}

void
CLdr::toneMapping
(
    uint8_t* pixels,
	int width,
	int height,
    pCallbackFunc pProgress
)
{   
    g_pMapper->process_8bit_rgb_image(pixels, width, height, pProgress);
}

void
CLdr::toneMapping
(
	uint8_t* pixels,
	int width,
	int height,
    float power,
    float blur,
    pCallbackFunc pProgress
)
{
	// 
    // Two stages are sufficient
    // to make HDR tone mapped photo.
    //
    // More stages creates more distortion.
    // 
    ToneMappingParameters params;
    params.info_fast_mode = 1;
    params.low_saturation = 100;
    params.high_saturation = 100;
    params.stretch_contrast = 1;
    params.function_id = 0;

    params.stage[0].enabled = 1;
    params.stage[0].power = power;
    params.stage[0].blur = blur;

    params.stage[1].enabled = 1;
    params.stage[1].power = power;
    params.stage[1].blur = blur;

    params.stage[2].enabled = 0;
    params.stage[3].enabled = 0;

    params.unsharp_mask.enabled = 1;
    params.unsharp_mask.power = 19.1446;
    params.unsharp_mask.blur = 4;
    params.unsharp_mask.threshold = 9;

    // Integer mapper ==> faster mapper but not accurate.

	ToneMappingInt mapper;
    mapper.apply_parameters(params);
    mapper.process_8bit_rgb_image(pixels, width, height, pProgress);
}
