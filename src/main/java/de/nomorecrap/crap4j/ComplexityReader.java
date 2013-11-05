package de.nomorecrap.crap4j;

import com.agitar.org.objectweb.asm.tree.analysis.AnalyzerException;

import java.io.IOException;
import java.util.List;

public interface ComplexityReader {

	public List<MethodComplexity> readMethodComplexities() throws IOException, AnalyzerException;


}
