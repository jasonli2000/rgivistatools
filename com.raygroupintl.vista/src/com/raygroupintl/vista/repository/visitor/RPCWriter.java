//---------------------------------------------------------------------------
// Copyright 2012 Ray Group International
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//---------------------------------------------------------------------------

package com.raygroupintl.vista.repository.visitor;

import com.raygroupintl.output.FileWrapper;
import com.raygroupintl.vista.repository.RepositoryInfo;
import com.raygroupintl.vista.repository.VistaPackages;

public class RPCWriter extends OptionRPCWriter {
	public RPCWriter(RepositoryInfo repositoryInfo, FileWrapper fileWrapper) {
		super(repositoryInfo, fileWrapper);
	}
	
	public void write(VistaPackages vps) {
		this.write(vps, this.repositoryInfo.getRPCs());
	}
}