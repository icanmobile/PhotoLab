/*
        LDR ToneMapper

        Copyright (C) 2009 Nasca Octavian Paul
        Author: Nasca Octavian Paul

        This program is free software: you can redistribute it and/or modify
        it under the terms of the GNU General Public License as published by
        the Free Software Foundation, either version 2 of the License, or
        (at your option) any later version.

        This program is distributed in the hope that it will be useful,
        but WITHOUT ANY WARRANTY; without even the implied warranty of
        MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
        GNU General Public License for more details.

        You should have received a copy of the GNU General Public License
        along with this program.  If not, see <http://www.gnu.org/licenses/>.

 */

#include <stdlib.h>
#include <stdio.h>
#include <math.h>
#include "ToneMappingInt.h"

#define FUNC_LOOKUP_TABLE_SIZE 65536

ToneMappingInt::ToneMappingInt():ToneMappingBase(){
	par.info_fast_mode=true;
	for (int nstage=0;nstage<TONEMAPPING_MAX_STAGES;nstage++){
		precomputed[nstage].func_lookup_table=new unsigned char [FUNC_LOOKUP_TABLE_SIZE];
		for (int i=0;i<FUNC_LOOKUP_TABLE_SIZE;i++) precomputed[nstage].func_lookup_table[i]=0;
		precomputed[nstage].changed=true;
	};
	current_func_lookup_table=precomputed[0].func_lookup_table;

};
ToneMappingInt::~ToneMappingInt(){
	for (int nstage=0;nstage<TONEMAPPING_MAX_STAGES;nstage++){
		delete [] precomputed[nstage].func_lookup_table;
	};
};
void ToneMappingInt::set_power(int nstage,REALTYPE value){
	ToneMappingBase::set_power(nstage,value);
	precomputed[nstage].changed=true;
};
void ToneMappingInt::set_function_id(int value){
	ToneMappingBase::set_function_id(value);
	for (int nstage=0;nstage<TONEMAPPING_MAX_STAGES;nstage++) precomputed[nstage].changed=true;
};



void ToneMappingInt::recompute_func_table(int nstage){
	int pos=0;

	// 
	// WHAT IS THE LOOKUP TABLE FOR?
	// For mapping function.
	//
	unsigned char *func_lookup_table = precomputed[nstage].func_lookup_table;
	current_process_power_value = par.get_power(nstage);

	// 
	// x1???
	// x2???
	// The range of x1: [0,1]
	// The range of x2: [0,1]
	// 
	// func(x1,x2) ===> x3
	//
	// The range of x3: [0,1]
	// And final value is converted into the range [0,256)
	//
	for (int x1=0;x1<256;x1++){
		for (int x2=0;x2<256;x2++){

			// So "func" is a role of mapping function.
			REALTYPE f=func(x1/255.0,x2/255.0);
			func_lookup_table[pos]=(int) (f*255.0);
			pos++;
		};
	};
	precomputed[nstage].changed=false;
};


void ToneMappingInt::update_preprocessed_values()
{
	for (int nstage=0;nstage<TONEMAPPING_MAX_STAGES;nstage++)
	{
		if (precomputed[nstage].changed &&
			par.stage[nstage].enabled) 
			recompute_func_table(nstage);
	};
};



/**
 *	Get Analyze histogram
 *	and find of min/maximum index.
 *	Index ==> VALUE 
 */
void ToneMappingInt::get_min_max_data(unsigned char *img,
									  int size,
									  int &min,
									  int &max){
	const int ucsize=256; //size of a unsigned char

	//first, we compute the histogram
	unsigned int histogram[ucsize];

	// make it zero.
	for (int i=0;i<ucsize;i++) 
		histogram[i]=0;

	// Count histogram.	
	for (int i=0;i<size;i++){
		histogram[img[i]]++;
	};
	
	// I want to strip the lowest and upper 0.1 procents 
	// (in the histogram) of the pixels 
	//
	// ====> WHY 0.1 %
	//		 Is it for removal any outlier data????
	unsigned int desired_sum=size/1000;
	unsigned int sum_min=0;
	unsigned int sum_max=0;
	for (int i=0;i<ucsize;i++)
	{
		sum_min+=histogram[i];
		if (sum_min>desired_sum){
			min=i;
			break;
		};
	};
	
	for (int i=ucsize-1;i>=0;i--){
		sum_max+=histogram[i];
		if (sum_max>desired_sum){
			max=i;
			break;
		};
	};
	
	if (min>=max){
		min=0;
		max=255;
	};
};


/**
 *	Simply spread color value into [0,255] range.
 */
void ToneMappingInt::stretch_contrast_8bit_rgb_image(
			unsigned char *img,
			int sizex,
			int sizey,
			int min,
			int max,
			unsigned char *stretch_contrast_table)
{
	const int ucsize=256; //size of a unsigned char
	bool delete_table=false;
	if (stretch_contrast_table==NULL) {
		stretch_contrast_table=new unsigned char[ucsize];
		delete_table=true;
	};

	
	//compute the lookup table for stretching the contrast
	int diff=max-min;
	for (int i=0;i<ucsize;i++){

		// In order to optimize code,
		// use 24.8 fixed pooint value.
		int f=((i-min)<<8)/diff;
		if (f<0) f=0;
		if (f>255) f=255;
		stretch_contrast_table[i]=f;
	};
	
	int size=sizex*sizey;
	//apply the lookup table
	for (int i=0;i<(size*3);i++){
		img[i]=stretch_contrast_table[img[i]];
	};
	
	if (delete_table) 
		delete [] stretch_contrast_table;
	
};


/**
 *	TONE MAPPING MAIN FUNCTIONS
 */
void ToneMappingInt::process_8bit_rgb_image(unsigned char *img,int sizex,int sizey, pCallbackFunc pProgress){

	pProgress(5);

	// ????
	update_preprocessed_values();
	
	int size=sizex*sizey;
	const int ucsize=256; //size of a unsigned char
	
	unsigned char stretch_contrast_table[ucsize];
	unsigned char *srcimg=new unsigned char[size*3];
	unsigned char *blurimage=new unsigned char[size];

	// Copy img ==> srcimg.
	for (int i=0;i<size*3;i++) 
		srcimg[i]=img[i];


	pProgress(10);

	// 
	// CONTRAST stretch.
	// 
	if (par.stretch_contrast)
	{
		//stretch the contrast
		int min,max;
		get_min_max_data(img,size*3,min,max);
		stretch_contrast_8bit_rgb_image(
					img,
					sizex,
					sizey,
					min,
					max,
					stretch_contrast_table);
	}else{
		for (int i=0;i<ucsize;i++) 
			stretch_contrast_table[i]=i;//no contrast stretch
	};


	pProgress(20);
	
	for (int nstage=0;nstage<TONEMAPPING_MAX_STAGES;nstage++){

		pProgress( (70-20)/(TONEMAPPING_MAX_STAGES-nstage) + 20);

		if (!par.stage[nstage].enabled)
			continue;


		// 
		// IS THIS FOR GETTING HIGH-CONTRAST IMAGE?
		// 
		int pos;
		//compute the desatured image
		pos=0;
		for (int i=0;i<size;i++)
		{
			blurimage[i]=((int)img[pos]+(int)img[pos+1]+(int)img[pos+2])/3;
			pos+=3;
		};
		
		current_func_lookup_table=precomputed[nstage].func_lookup_table;

		//blur
		// jhlee.
		// VERY FAST BLUR
		// Similar with GAUSIAN BLUR but seems to be a little bit different.
		inplace_blur_8bit_process(
					blurimage,
					sizex,
					sizey,
					par.get_blur(nstage));


				
		//modify saturation values
		pos=0;
		for (int i=0;i<size;i++){
			unsigned char src_r=img[pos];
			unsigned char src_g=img[pos+1];
			unsigned char src_b=img[pos+2];

			unsigned char blur=blurimage[i];

			unsigned char dest_r=fast_func(src_r, blur);
			unsigned char dest_g=fast_func(src_g, blur);			
			unsigned char dest_b=fast_func(src_b, blur);

			img[pos]=dest_r;
			img[pos+1]=dest_g;
			img[pos+2]=dest_b;
			pos+=3;				
		};		
	};

	pProgress(70);
	
	int high_saturation_value = 100-par.high_saturation;
	int low_saturation_value = 100-par.low_saturation;
	
	if ((par.high_saturation!=100)||(par.low_saturation!=100)){
		int pos=0;
		for (int i=0;i<size;i++){
			unsigned int src_h,src_s,src_v;
			unsigned int dest_h,dest_s,dest_v;
			rgb2hsv(srcimg[pos],srcimg[pos+1],srcimg[pos+2],src_h,src_s,src_v);
			rgb2hsv(img[pos],img[pos+1],img[pos+2],dest_h,dest_s,dest_v);

			// 
			// ?????
			// INTERPOLATION saturation between SOURCE & TONE MAPPED.
			// 
			unsigned int dest_saturation=(src_s * high_saturation_value + dest_s * (100-high_saturation_value))/100;

			// 
			// If newly generated pixel is brighter than original one,
			// compute new saturation value...
			//
			// ???? WHAT MEANS ????
			// 
			if (dest_v>src_v)
			{
				// ONLY apply portion of lighting.
				int s1=dest_saturation*src_v/(dest_v+1);
				dest_saturation=(low_saturation_value*s1+par.low_saturation*dest_saturation)/100;
			};

			hsv2rgb(dest_h,dest_saturation,dest_v,img[pos],img[pos+1],img[pos+2]);

			pos+=3;
		};
	};

	pProgress(80);

	// 
	//Unsharp Mask filter
	// - COLOR ENHANCE.
	// 
	if (par.unsharp_mask.enabled){
		unsigned char *val=new unsigned char[size];
		//compute the desatured image
		int pos=0;
		for (int i=0;i<size;i++){
			val[i]=blurimage[i]=((int)img[pos]+(int)img[pos+1]+(int)img[pos+2])/3;
			//val[i]=blurimage[i]=max3(img[pos],img[pos+1],img[pos+2]);
			pos+=3;
		};

		REALTYPE blur_value=par.get_unsharp_mask_blur();
		inplace_blur_8bit_process(blurimage,sizex,sizey,blur_value);

		pos=0;
		int pow=(int)(250*par.get_unsharp_mask_power());
		int threshold=(par.unsharp_mask.threshold*pow)/100;
		int threshold2=threshold/2;

		for (int i=0;i<size;i++){
			int dval=((val[i]-blurimage[i])*pow)/100;

			int abs_dval=abs(dval);
			if (abs_dval<threshold){
				if (abs_dval>threshold2){
					bool sign=(dval<0);					
					dval=(abs_dval-threshold2)*2;
					if (sign) dval=-dval;
				}else {
					dval=0;
				};
			};
			int r=img[pos]+dval;
			int g=img[pos+1]+dval;
			int b=img[pos+2]+dval;

			if (r<0) r=0;
			if (r>255) r=255;
			if (g<0) g=0;
			if (g>255) g=255;
			if (b<0) b=0;
			if (b>255) b=255;

			img[pos]=r;
			img[pos+1]=g;
			img[pos+2]=b;
			pos+=3;
		};


		delete[]val;
	};

	delete[]blurimage;
	delete[]srcimg;

	pProgress(85);
};

void ToneMappingInt::process_8bit_rgb_image(unsigned char *img,int sizex,int sizey){

	// ????
	update_preprocessed_values();

	int size=sizex*sizey;
	const int ucsize=256; //size of a unsigned char

	unsigned char stretch_contrast_table[ucsize];
	unsigned char *srcimg=new unsigned char[size*3];
	unsigned char *blurimage=new unsigned char[size];

	// Copy img ==> srcimg.
	for (int i=0;i<size*3;i++)
		srcimg[i]=img[i];


	//
	// CONTRAST stretch.
	//
	if (par.stretch_contrast)
	{
		//stretch the contrast
		int min,max;
		get_min_max_data(img,size*3,min,max);
		stretch_contrast_8bit_rgb_image(
				img,
				sizex,
				sizey,
				min,
				max,
				stretch_contrast_table);
	}else{
		for (int i=0;i<ucsize;i++)
			stretch_contrast_table[i]=i;//no contrast stretch
	};

	for (int nstage=0;nstage<TONEMAPPING_MAX_STAGES;nstage++){

		if (!par.stage[nstage].enabled)
			continue;


		//
		// IS THIS FOR GETTING HIGH-CONTRAST IMAGE?
		//
		int pos;
		//compute the desatured image
		pos=0;
		for (int i=0;i<size;i++)
		{
			blurimage[i]=((int)img[pos]+(int)img[pos+1]+(int)img[pos+2])/3;
			pos+=3;
		};

		current_func_lookup_table=precomputed[nstage].func_lookup_table;

		//blur
		// jhlee.
		// VERY FAST BLUR
		// Similar with GAUSIAN BLUR but seems to be a little bit different.
		inplace_blur_8bit_process(
				blurimage,
				sizex,
				sizey,
				par.get_blur(nstage));



		//modify saturation values
		pos=0;
		for (int i=0;i<size;i++){
			unsigned char src_r=img[pos];
			unsigned char src_g=img[pos+1];
			unsigned char src_b=img[pos+2];

			unsigned char blur=blurimage[i];

			unsigned char dest_r=fast_func(src_r, blur);
			unsigned char dest_g=fast_func(src_g, blur);
			unsigned char dest_b=fast_func(src_b, blur);

			img[pos]=dest_r;
			img[pos+1]=dest_g;
			img[pos+2]=dest_b;
			pos+=3;
		};
	};

	int high_saturation_value = 100-par.high_saturation;
	int low_saturation_value = 100-par.low_saturation;

	if ((par.high_saturation!=100)||(par.low_saturation!=100)){
		int pos=0;
		for (int i=0;i<size;i++){
			unsigned int src_h,src_s,src_v;
			unsigned int dest_h,dest_s,dest_v;
			rgb2hsv(srcimg[pos],srcimg[pos+1],srcimg[pos+2],src_h,src_s,src_v);
			rgb2hsv(img[pos],img[pos+1],img[pos+2],dest_h,dest_s,dest_v);

			//
			// ?????
			// INTERPOLATION saturation between SOURCE & TONE MAPPED.
			//
			unsigned int dest_saturation=(src_s * high_saturation_value + dest_s * (100-high_saturation_value))/100;

			//
			// If newly generated pixel is brighter than original one,
			// compute new saturation value...
			//
			// ???? WHAT MEANS ????
			//
			if (dest_v>src_v)
			{
				// ONLY apply portion of lighting.
				int s1=dest_saturation*src_v/(dest_v+1);
				dest_saturation=(low_saturation_value*s1+par.low_saturation*dest_saturation)/100;
			};

			hsv2rgb(dest_h,dest_saturation,dest_v,img[pos],img[pos+1],img[pos+2]);

			pos+=3;
		};
	};

	//
	//Unsharp Mask filter
	// - COLOR ENHANCE.
	//
	if (par.unsharp_mask.enabled){
		unsigned char *val=new unsigned char[size];
		//compute the desatured image
		int pos=0;
		for (int i=0;i<size;i++){
			val[i]=blurimage[i]=((int)img[pos]+(int)img[pos+1]+(int)img[pos+2])/3;
			//val[i]=blurimage[i]=max3(img[pos],img[pos+1],img[pos+2]);
			pos+=3;
		};

		REALTYPE blur_value=par.get_unsharp_mask_blur();
		inplace_blur_8bit_process(blurimage,sizex,sizey,blur_value);

		pos=0;
		int pow=(int)(250*par.get_unsharp_mask_power());
		int threshold=(par.unsharp_mask.threshold*pow)/100;
		int threshold2=threshold/2;

		for (int i=0;i<size;i++){
			int dval=((val[i]-blurimage[i])*pow)/100;

			int abs_dval=abs(dval);
			if (abs_dval<threshold){
				if (abs_dval>threshold2){
					bool sign=(dval<0);
					dval=(abs_dval-threshold2)*2;
					if (sign) dval=-dval;
				}else {
					dval=0;
				};
			};
			int r=img[pos]+dval;
			int g=img[pos+1]+dval;
			int b=img[pos+2]+dval;

			if (r<0) r=0;
			if (r>255) r=255;
			if (g<0) g=0;
			if (g>255) g=255;
			if (b<0) b=0;
			if (b>255) b=255;

			img[pos]=r;
			img[pos+1]=g;
			img[pos+2]=b;
			pos+=3;
		};


		delete[]val;
	};

	delete[]blurimage;
	delete[]srcimg;

};


/**
 *	BLUR PROCESSING
 */
void ToneMappingInt::inplace_blur_8bit_process(
			unsigned char *data,
			int sizex, 
			int sizey, 
			REALTYPE blur)
{
	blur/=preview_zoom;

	// WHAT IS AF?
	REALTYPE af=exp(log(0.5)/blur*sqrt(2));
	if ((af<=0.0)||(af>=1.0)) return;
	
	unsigned int a=(int)(65536*af*af);
	if (a==0) 
		return;
	
	for (int y=0;y<sizey;y++)
	{
		int pos=y*sizex;
		unsigned int old=data[pos]<<8;
		pos++;
		for (int x=1;x<sizex;x++){
			old=((data[pos]<<8)*(65535^a) + old*a)>>16;
			data[pos]=old>>8;
			pos++;
		};
		pos=y*sizex+sizex-1;;
		for (int x=1;x<sizex;x++){
			old=((data[pos]<<8)*(65535^a)+old*a)>>16;
			data[pos]=old>>8;
			pos--;
		};

	};
	
	for (int x=0;x<sizex;x++){
		int pos=x;
		unsigned int old=data[pos]<<8;
		for (int y=1;y<sizey;y++){
			old=((data[pos]<<8)*(65535^a)+old*a)>>16;
			data[pos]=old>>8;
			pos+=sizex;
		};
		pos=x+sizex*(sizey-1);
		for (int y=1;y<sizey;y++){
			old=((data[pos]<<8)*(65535^a)+old*a)>>16;
			data[pos]=old>>8;
			pos-=sizex;
		};

	};
	
};

