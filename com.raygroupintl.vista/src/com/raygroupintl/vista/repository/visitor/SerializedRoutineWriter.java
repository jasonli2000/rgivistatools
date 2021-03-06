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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.raygroupintl.m.parsetree.Routine;
import com.raygroupintl.vista.repository.RepositoryVisitor;
import com.raygroupintl.vista.repository.VistaPackage;
import com.raygroupintl.vista.repository.VistaPackages;

public class SerializedRoutineWriter extends RepositoryVisitor {
	private final static Logger LOGGER = Logger.getLogger(SerializedRoutineWriter.class.getName());

	private String outputDirectory;
	private int packageCount;
	
	public SerializedRoutineWriter(String outputDirectory) {
		this.outputDirectory = outputDirectory;
	}

	private void writeObject(String fileName, Serializable object) throws IOException {
		FileOutputStream fos = new FileOutputStream(fileName);
		ObjectOutputStream oos = new ObjectOutputStream(fos);
		oos.writeObject(object);
		oos.close();
	}
	
	@Override
	public void visitRoutine(Routine routine) {
		Path path = Paths.get(this.outputDirectory, routine.getName() + ".ser");
		String fileName = path.toString();
		try {
			this.writeObject(fileName, routine);
		} catch (IOException ioException) {
			String msg = "Unable to write object to file " + fileName;
			LOGGER.log(Level.SEVERE, msg, ioException);
		}		
	}
	
	@Override
	protected void visitVistaPackage(VistaPackage routinePackage) {
		++this.packageCount;
		LOGGER.info(String.valueOf(this.packageCount) + ". " + routinePackage.getPackageName() + "...writing");
		super.visitVistaPackage(routinePackage);
		LOGGER.info("..done.\n");
	}

	@Override
	protected void visitRoutinePackages(VistaPackages rps) {
		Path path = Paths.get(this.outputDirectory, "test.tst");
		String fileName = path.toString();
		try {
			this.writeObject(fileName, "test");
			super.visitRoutinePackages(rps);
		} catch (IOException ioException) {
			String msg = "Unable to write object to directory " + this.outputDirectory;
			LOGGER.log(Level.SEVERE, msg, ioException);
		}		
	}
}
