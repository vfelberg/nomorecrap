package de.nomorecrap;

public class MethodCoverage extends MethodInfo {
	private float coverage;

	public MethodCoverage(String methodSignature, float coverage) {
		super();
		this.matchingMethodSignature = methodSignature;
		this.coverage = coverage;
	}
	
	public float getCoverage() {
		return coverage;
	}
	
	@Override
	public String toString() {
		return matchingMethodSignature +" : "+coverage;
	}

	public int compareTo(Object o) {
		MethodCoverage otherMethodCoverage = (MethodCoverage) o;
		String method = otherMethodCoverage.matchingMethodSignature;
		return matchingMethodSignature.compareTo(method);
	}
  
  

  @Override
  public boolean equals(Object obj) {
    if (isInvalidComparable(obj))
      return false;
    MethodCoverage m = (MethodCoverage)obj;
    return matchingMethodSignature.equals(m.matchingMethodSignature) && coverage == m.coverage;
  }

private boolean isInvalidComparable(Object obj) {
	return obj == null || !(obj instanceof MethodCoverage);
}

  @Override
  public int hashCode() {
    return matchingMethodSignature.hashCode() ^ new Float(coverage).hashCode();
  }

  @Override
  public String prettyMethodSignature() {
      return "Implement This, or don't call IT!! -- When done refactoring, make it abstract";
  }
	
	
	
}
