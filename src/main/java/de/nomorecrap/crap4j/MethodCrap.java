package de.nomorecrap.crap4j;

import de.nomorecrap.crap4j.util.FormatUtil;
import de.nomorecrap.crap4j.util.MyStringBuilder;
import de.nomorecrap.crap4j.util.XmlUtil;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class MethodCrap extends MethodInfo implements Crap {

	private float coverage;
	private MethodComplexity complexity;
	private float crap;
  private static float crapThreshold;

	public MethodCrap(String methodSignature, float coverage, MethodComplexity complexity2) {
		validateParams(coverage, complexity2);
		this.matchingMethodSignature = methodSignature;
		this.coverage = coverage;
		this.complexity = complexity2;
		this.crap = computeCrap();
	}

  protected float computeCrap() {
    return (float) (getComplexity() * (getComplexity() * ( Math.pow(1.0 - getCoverage(), 3.0) )) + getComplexity());
  }


	public float getCrap() {
		return crap;
	}
  
  public int getCrapLoad(float crapThreshold) {
    int crapLoad = 0;
    if (getCrap() >= crapThreshold) {
      int complexity = getComplexity();
      float coverage = getCoverage();
      crapLoad += complexity * (1.0 - coverage);
      crapLoad += complexity / crapThreshold;
    }
    return crapLoad;
  }

	public int getComplexity() {
		return complexity.getComplexity();
	}

	public float getCoverage() {
		return coverage;
	}

	private void validateParams(float coverage, MethodComplexity complexity2) {
		if (coverage < 0.0 || coverage > 1.0 || complexity2 == null || complexity2.getComplexity() < 0)
			throw new IllegalArgumentException(
					"coverage is 0 or greater and complexity is 0 (really 1) or greater");
	}

	public static List<? extends Crap> combine(List<MethodCoverage> covs, List<MethodComplexity> complexities) {
		List<MethodCrap> methodCraps = new ArrayList<MethodCrap>();
		Collections.sort(covs);
		Collections.sort(complexities);
		for (MethodComplexity complexity : complexities) {
			MethodCoverage cov = (MethodCoverage) findSameMethod(covs, complexity);
			float receivedCoverage = 0.0f;
			if (cov != null)
				receivedCoverage = cov.getCoverage();
			else {
//				System.out.println("No Coverage: "+complexity);
			
			}
			
			methodCraps.add(new MethodCrap(complexity.getMatchingMethodSignature(), 
                      receivedCoverage,	
                      complexity));
		}
		
		// build methodcraps for the leftover coverages -- NOTE - should never be here.
//		for (MethodCoverage coverage : covs) {
//			System.out.println("LeftOver Coverage: "+coverage);
//			methodCraps.add(new MethodCrapByFormula(coverage.getMatchingMethodSignature(), coverage.getCoverage(), 0));			
//		}
    // TODO RBE What do we really want to do with coverage leftovers? If we can't compute the complexity, we are hosed.
		return methodCraps;
	}

	private static MethodInfo findSameMethod(List<? extends MethodInfo> methods, MethodInfo method) {
		String methodSignature = method.getMatchingMethodSignature();
		MethodInfo match = null;
		for (MethodInfo currentMethod : methods) {
			if (currentMethod.matchingMethodSignature.equals(methodSignature)) {
				match = currentMethod;
			}
		}
		if (match != null) {
			methods.remove(match);
		}
		return match;
	}

	@Override
	public String toString() {
    NumberFormat nf = FormatUtil.getNumberFormatter();
		return prettyMethodSignature()+" : "+nf.format(crap)+" : "+nf.format(complexity.getComplexity()) + " : "+ nf.format(coverage);
	}
  
  

	public void toXml(MyStringBuilder s) {
    NumberFormat nf = FormatUtil.getNumberFormatter();
    s.start("<method>");
    XmlUtil.itemToXml(s, "package", complexity.getPackageName());
    XmlUtil.itemToXml(s, "className", complexity.getClassName());
    XmlUtil.itemToXml(s, "methodName", escapeXml(complexity.getMethodName()));
    XmlUtil.itemToXml(s, "methodSignature", escapeXml(complexity.getSigOrDescriptor()));

    XmlUtil.itemToXml(s, "fullMethod", escapeXml(complexity.prettyMethodSignature()));
    XmlUtil.itemToXml(s, "crap", nf.format(crap));
    XmlUtil.itemToXml(s, "complexity", Integer.toString(complexity.getComplexity()));
    XmlUtil.itemToXml(s, "coverage", nf.format(coverage * 100.0f));
    XmlUtil.itemToXml(s, "crapLoad", Integer.toString(getCrapLoad(crapThreshold)));
    s.end("</method>");
  }
  
  public static void setCrapLoadThreshold(float crapThreshold2) {
    crapThreshold = crapThreshold2;
  }

  private String escapeXml(String prettyMethodSignature) {
    return prettyMethodSignature.replace("<","&lt;").replace(">", "&gt;");
  }

  @Override
  public String prettyMethodSignature() {
    return complexity.prettyMethodSignature();
  }


}
