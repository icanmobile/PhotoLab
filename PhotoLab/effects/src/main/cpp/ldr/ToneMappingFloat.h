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

#ifndef TONE_MAPPING_FLOAT_H
#define TONE_MAPPING_FLOAT_H
#include "ToneMappingBase.h"

class ToneMappingFloat:public ToneMappingBase{
	public:
		ToneMappingFloat();
		~ToneMappingFloat();

		void process_8bit_rgb_image(unsigned char *img,int sizex,int sizey);
		void process_rgb_image(REALTYPE *img,int sizex,int sizey);
		void update_preprocessed_values();

	private:
		void inplace_blur(REALTYPE *data,int sizex, int sizey,REALTYPE blur);
		void stretch_contrast(REALTYPE *data, int datasize);
		inline void rgb2hsv(const REALTYPE &r, const REALTYPE &g, const REALTYPE &b,
				REALTYPE &h,REALTYPE &s, REALTYPE &v){

			REALTYPE maxrg=(r>g)?r:g;
			REALTYPE max=(maxrg>b)?maxrg:b;

			REALTYPE minrg=(r<g)?r:g;
			REALTYPE min=(minrg<b)?minrg:b;

			REALTYPE delta=max-min;

			//hue
			if (min==max){
				h=0.0;
			}else{
				if (max==r){
					h=fmod(60.0*(g-b)/delta+360.0,360.0);
				}else{
					if (max==g){
						h=60.0*(b-r)/delta+120.0;
					}else{//max==b
						h=60.0*(r-g)/delta+240.0;
					};
				};
			};

			//saturation
			if (max<1e-6){
				s=0;
			}else{
				s=1.0-min/max;
			};

			//value
			v=max;

		};

		inline void hsv2rgb(const REALTYPE &h,const REALTYPE &s,const REALTYPE &v,
				REALTYPE &r,REALTYPE &g, REALTYPE &b){
			REALTYPE hfi=floor(h/60.0);
			REALTYPE f=(h/60.0)-hfi;
			int hi=((int)hfi)%6;

			REALTYPE p=v*(1.0-s);
			REALTYPE q=v*(1.0-f*s);
			REALTYPE t=v*(1.0-(1.0-f)*s);
			switch (hi){
				case 0:
					r=v;g=t;b=p;
					break;
				case 1:
					r=q;g=v;b=p;
					break;
				case 2:
					r=p;g=v;b=t;
					break;
				case 3:
					r=p;g=q;b=v;
					break;
				case 4:
					r=t;g=p;b=v;
					break;
				case 5:
					r=v;g=p;b=q;
					break;
			};
		};

};


#endif


