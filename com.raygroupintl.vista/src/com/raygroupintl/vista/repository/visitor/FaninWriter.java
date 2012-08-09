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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.raygroupintl.m.parsetree.Routine;
import com.raygroupintl.m.parsetree.data.EntryId;
import com.raygroupintl.m.parsetree.visitor.FanInRecorder;
import com.raygroupintl.output.FileWrapper;
import com.raygroupintl.output.TerminalFormatter;
import com.raygroupintl.struct.Filter;
import com.raygroupintl.vista.repository.RepositoryInfo;
import com.raygroupintl.vista.repository.RepositoryVisitor;
import com.raygroupintl.vista.repository.VistaPackage;
import com.raygroupintl.vista.repository.VistaPackages;

public class FaninWriter extends RepositoryVisitor {
	private RepositoryInfo repositoryInfo;
	private FileWrapper fileWrapper;
	private FanInRecorder faninRecorder;
	private boolean isCalled;
	
	private static class EntryIdSource {
		public Set<String> packages;
		
		public void setPackages(Set<String> packages) {
			this.packages = packages;
		}
		
		public Set<String> getPackages() {
			return this.packages;
		}
	}
	
	private static class EntryIdWithSources implements Comparable<EntryIdWithSources> {
		public EntryId entryId;
		public EntryIdSource sources;
		
		public EntryIdWithSources(EntryId entryId, EntryIdSource source) {
			this.entryId = entryId;
			this.sources = source;
		}
		
		@Override
		public int compareTo(EntryIdWithSources rhs) {
			return this.entryId.compareTo(rhs.entryId);
		}		
	}
	
	public FaninWriter(RepositoryInfo repositoryInfo, FileWrapper fileWrapper) {
		this.repositoryInfo = repositoryInfo;
		this.fileWrapper = fileWrapper;
	}
		
	@Override
	protected void visitVistaPackage(VistaPackage routinePackage) {
		Filter<EntryId> filter = routinePackage.getPackageFanoutFilter();
		this.faninRecorder.setFilter(filter);
		this.faninRecorder.setCurrentPackagePrefix(routinePackage.getPrimaryPrefix());
		super.visitVistaPackage(routinePackage);
	}

	public void visitRoutine(Routine routine) {
		if ((routine.getName().startsWith("ZZ")) || (routine.getName().charAt(0) != '%')) {
			routine.accept(this.faninRecorder);
			this.isCalled = true;
		}
	}
	
	protected void visitRoutinePackages(VistaPackages rps) {
		this.faninRecorder = new FanInRecorder();
		List<VistaPackage> packagesTBReported = new ArrayList<VistaPackage>();
		//List<VistaPackage> packages = this.repositoryInfo.getAllPackages();
		for (VistaPackage p : rps.getPackages()) {
			this.isCalled = false;
			p.accept(this);
			if (this.isCalled) {
				packagesTBReported.add(p);
			}
		}
		Map<EntryId, Set<String>> codeFanins = this.faninRecorder.getFanIns();
		
		Map<EntryId, EntryIdSource> fanins = new HashMap<EntryId, EntryIdSource>();
		Set<EntryId> codeFaninsEIs = codeFanins.keySet();
		for (EntryId ei : codeFaninsEIs) {
			Set<String> packagePrefixes = codeFanins.get(ei);
			EntryIdSource source = new EntryIdSource();
			source.setPackages(packagePrefixes);
			fanins.put(ei, source);
		}
		codeFanins = null;
				
		Map<String, List<EntryIdWithSources>> faninsByPackage = new HashMap<String, List<EntryIdWithSources>>();
		Map<String, Set<String>> sourcePackagesByPackage = new HashMap<String, Set<String>>();

		for (VistaPackage p : rps.getPackages()) {
			String name = p.getPackageName();
			faninsByPackage.put(name, new ArrayList<EntryIdWithSources>());
			sourcePackagesByPackage.put(name, new HashSet<String>());
		}
		faninsByPackage.put("UNCATEGORIZED", new ArrayList<EntryIdWithSources>());
		Set<EntryId> faninEntryIds = fanins.keySet();
		for (EntryId f : faninEntryIds) {
			String routineName = f.getRoutineName();
			if (routineName == null) continue;
			if (routineName.isEmpty()) continue;
			VistaPackage p = this.repositoryInfo.getPackageFromRoutineName(routineName);
			String packageName = p.getPackageName();
			List<EntryIdWithSources> entryIds = faninsByPackage.get(packageName);
			EntryIdSource source = fanins.get(f);
			Set<String> sourcePrefixes = source.getPackages();
			sourcePackagesByPackage.get(packageName).addAll(sourcePrefixes);
			EntryIdWithSources fws = new EntryIdWithSources(f, source);
			entryIds.add(fws);
		}
		
		TerminalFormatter tf = new TerminalFormatter();
		int ndx = 0;
		if (this.fileWrapper.start()) {
			tf.setTab(21);
			for (VistaPackage p : rps.getPackages()) {
				this.fileWrapper.writeEOL("--------------------------------------------------------------");
				this.fileWrapper.writeEOL();
				String name = p.getPackageName();
				++ndx;
				if (sourcePackagesByPackage.get(name).size() > 40) {
					this.fileWrapper.writeEOL(String.valueOf(ndx) + ". COMMON SERVICE NAME: " + name);
				} else {
					this.fileWrapper.writeEOL(String.valueOf(ndx) + ". PACKAGE NAME: " + name);					
				}
				this.fileWrapper.writeEOL();
				List<EntryIdWithSources> fs = faninsByPackage.get(name);
				if (fs.size() == 0) {
					this.fileWrapper.writeEOL("   Not used by other packages");
					this.fileWrapper.writeEOL();					
				} else {
					Collections.sort(fs);
					for (EntryIdWithSources f : fs) {
						this.fileWrapper.writeEOL("  " + f.entryId.toString());
						String title = tf.startList("CALLING PACKAGES");
						this.fileWrapper.write(title);
						List<String> sourcePackages = new ArrayList<String>(f.sources.getPackages());
						Collections.sort(sourcePackages);
						for (String source : sourcePackages) {
							VistaPackage vp = this.repositoryInfo.getPackageFromPrefix(source);							
							String pkgName = vp.getPackageName();
							String line = tf.addToList(pkgName);
							this.fileWrapper.write(line);
						}
						this.fileWrapper.writeEOL();
						this.fileWrapper.writeEOL();
					}
				}
				this.fileWrapper.writeEOL("--------------------------------------------------------------");
				this.fileWrapper.writeEOL();
			}
			this.fileWrapper.stop();
		}
	}
}