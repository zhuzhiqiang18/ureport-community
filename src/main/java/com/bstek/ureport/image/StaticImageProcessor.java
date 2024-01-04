/*******************************************************************************
 * Copyright 2017 Bstek
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package com.bstek.ureport.image;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import com.bstek.ureport.exception.ReportComputeException;
import com.bstek.ureport.provider.image.DefaultImageProvider;
import com.bstek.ureport.provider.image.HttpImageProvider;
import com.bstek.ureport.provider.image.HttpsImageProvider;
import com.bstek.ureport.provider.image.ImageProvider;

/**
 * @author Jacky.gao
 * @since 2017年3月20日
 */
public class StaticImageProcessor implements ImageProcessor<String> {
	
	private static Collection<ImageProvider> imageProviders = new ArrayList<ImageProvider>();
	
	static {
		imageProviders.add(new DefaultImageProvider());
		imageProviders.add(new HttpImageProvider());
		imageProviders.add(new HttpsImageProvider());
	}
	
	@Override
	public InputStream getImage(String path) {
		ImageProvider targetImageProvider=null;
		for(ImageProvider provider:imageProviders){
			if(provider.support(path)){
				targetImageProvider = provider;
				break;
			}
		}
		if(targetImageProvider==null){
			throw new ReportComputeException("Unsupport image path :"+path);
		}
		try{
			InputStream inputStream = targetImageProvider.getImage(path);
			return inputStream;			
		}catch(Exception ex){
			throw new ReportComputeException(ex);
		}
	}
}
