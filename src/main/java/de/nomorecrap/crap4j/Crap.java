package de.nomorecrap.crap4j;

import de.nomorecrap.crap4j.util.MyStringBuilder;

import java.util.Comparator;


public interface Crap {
	
	public float getCrap();
	
	public Comparator comparator = new Comparator() {			
		public int compare(Object o1, Object o2) {
			float o1crap = ((Crap)o1).getCrap();
			float o2crap = ((Crap)o2).getCrap();
			if (o1crap > o2crap)
				return 1;
			else if (o2crap > o1crap)
				return -1;
			else
				return 0;
		}

	};

	public float getCoverage();
  
  public int getCrapLoad(float crapThreshold);

  public void toXml(MyStringBuilder myStringBuilder);
}
