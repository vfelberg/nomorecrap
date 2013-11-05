package de.nomorecrap.crap4j.benchmark;

public class GlobalStats {

	public static final GlobalStats NULL_STATS = new GlobalStats(-1, -1, -1, -1);
  private float crapAverage;
	private float crapLoadAverage;
	private float crapMethodAverage;
	private float totalMethodAverage;
	
	
	
	public static GlobalStats retrieveStatsFromBenchmarkSite() {
		// TODO connect to the net and get the global average stats
		// remember to do a short timeout
		
		return NULL_STATS;
	}
	
	public GlobalStats(float crapAverage, float crapLoadAverage,
			float crapMethodAverage, float totalMethodAverage) {
		super();
		this.crapAverage = crapAverage;
		this.crapLoadAverage = crapLoadAverage;
		this.crapMethodAverage = crapMethodAverage;
		this.totalMethodAverage = totalMethodAverage;
	}
	
	public float getCrapAverage() {
		return crapAverage;
	}
	public void setCrapAverage(float crapAverage) {
		this.crapAverage = crapAverage;
	}
	public float getCrapLoadAverage() {
		return crapLoadAverage;
	}
	public void setCrapLoadAverage(float crapLoadAverage) {
		this.crapLoadAverage = crapLoadAverage;
	}
	public float getCrapMethodAverage() {
		return crapMethodAverage;
	}
	public void setCrapMethodAverage(float crapMethodAverage) {
		this.crapMethodAverage = crapMethodAverage;
	}
	public float getTotalMethodAverage() {
		return totalMethodAverage;
	}
	public void setTotalMethodAverage(float totalMethodAverage) {
		this.totalMethodAverage = totalMethodAverage;
	}
	
	public float getCrapAverageDiff(float score) {
		return score - crapAverage;
	}

	public float getCrapLoadAverageDiff(int score) {
		return score - crapLoadAverage;
	}

	public float getCrapMethodAverageDiff(int score) {
		return score - crapMethodAverage;
	}

	public float getTotalMethodAverageDiff(int score) {
		return score - totalMethodAverage;
	}
	
}

