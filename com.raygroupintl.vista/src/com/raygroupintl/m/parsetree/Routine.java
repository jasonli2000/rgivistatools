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

package com.raygroupintl.m.parsetree;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.raygroupintl.m.parsetree.data.EntryId;

public class Routine extends BasicNode {
	private static final long serialVersionUID = 1L;
	private final static Logger LOGGER = Logger.getLogger(Routine.class.getName());

	private String name;
	private EntryList entryList;
	private ErrorNode errorNode;
	
	public Routine(String name) {
		this.name = name;
	}
	
	public Routine(String name, ErrorNode errorNode) {
		this.name = name;
		this.errorNode = errorNode;
	}
	
	public String getName() {
		return this.name;
	}
	
	public List<EntryId> getEntryIdList() {
		if (this.entryList == null) {
			return Collections.emptyList();
		} else {
			List<EntryId> result = new ArrayList<EntryId>(this.entryList.size());
			for (Entry e : this.entryList.getNodes()) {
				String tag = e.getName();
				EntryId entryId = new EntryId(this.name, tag);
				result.add(entryId);				
			}
			return result;
		}
	}
	
	public void acceptSubNodes(Visitor visitor) {
		if (this.errorNode != null) {
			this.errorNode.accept(visitor);
			if (this.errorNode.getError().isFatal()) {
				return;
			}
		}
		if (this.entryList != null) {
			this.entryList.accept(visitor);
		}
	}
	
	@Override
	public void accept(Visitor visitor) {
		visitor.visitRoutine(this);
	}
	
	@Override
	public boolean setEntryList(EntryList entryList) {
		this.entryList = entryList;
		return true;
	}
	
	public void setErrorNode(ErrorNode errorNode) {
		this.errorNode = errorNode;
	}
	
	public static Routine readSerialized(String directory, String routineName) {
		File file = new File(directory, routineName + ".ser");
		if (! file.isFile()) {
			LOGGER.log(Level.WARNING, "File for routine " + routineName + " (" + file.toString() + ") does not exist.");
			return null;
		}		
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			Routine result = (Routine) ois.readObject();
			ois.close();
			return result;
		} catch(IOException | ClassNotFoundException ioException) {
			String msg = "Unable to read object from file " + file.toString();
			LOGGER.log(Level.SEVERE, msg, ioException);
			return null;
		}		
	}
}
