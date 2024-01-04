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
package com.bstek.ureport.export.html;

import java.util.List;

/**
 * @author Jacky.gao
 * @since 2017年10月31日
 */
public class SearchFormData {

	public List<InputComponentData> items;

	public List<InputComponentData> getItems() {
		return items;
	}

	public void setItems(List<InputComponentData> items) {
		this.items = items;
	}
}
