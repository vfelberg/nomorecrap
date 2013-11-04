package de.nomorecrap.external;

import com.agitar.org.objectweb.asm.tree.analysis.AnalyzerException;
import de.nomorecrap.ComplexityReader;
import de.nomorecrap.MethodComplexity;
import de.nomorecrap.CrapProject;
import de.nomorecrap.complexity.CyclomaticComplexity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * This class will read the complexity numbers that were generated by the 
 * ant script which was launched by the runner. 
 * 
 * @author bobevans
 *
 */
public class MetricsComplexityReader implements ComplexityReader {

	CrapProject project;

	public MetricsComplexityReader(CrapProject p) {
		this.project = p;		
	}

	public List<MethodComplexity> readMethodComplexities() throws IOException, AnalyzerException{
		List<MethodComplexity> list = new ArrayList<MethodComplexity>();
		List<File> allClassesToMeasure = project.allProjectClasses();
		CyclomaticComplexity cc = new CyclomaticComplexity();
		for (File className : allClassesToMeasure) {
			list.addAll(cc.getMethodComplexitiesFor(className));
		}
		return list;
	}

}