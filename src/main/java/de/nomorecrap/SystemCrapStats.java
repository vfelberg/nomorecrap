package de.nomorecrap;

import de.nomorecrap.benchmark.GlobalStats;
import de.nomorecrap.gauge.CrapImageGenerator;
import de.nomorecrap.gauge.NewCrapImageGenerator;
import de.nomorecrap.util.*;

import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.net.URLEncoder;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


public class SystemCrapStats {

	
	private float total;
	private float crapNumber;
	private float median;
	private float average;
	private double stdDev;
	private int methodCount;
	private List<? extends Crap> crapSubjects;
	private String name;
  private CrapProject crapProject;
  private int crapMethodCount;
  private float crapThreshold = 8.0f;
  private int crapWorkLoad;
  private float crapPercentWarningThreshold;
  private float crapPercentCriticalThreshold;
private GlobalStats globalStats;
private String server;
	
	public SystemCrapStats(List<? extends Crap> crapValues,
                           String name,
                           CrapProject crapProject,
                           float crapThreshold,
                           float crapPercentWarningThreshold,
                           float crapPercentCriticalThreshold,
                           GlobalStats globalStats, String server) {
		validateParams(crapValues);
		this.name = name;
    this.crapProject = crapProject;
		this.total = 0;
		this.crapSubjects = crapValues;
    this.crapThreshold = crapThreshold;
    this.crapPercentWarningThreshold = crapPercentWarningThreshold;
    this.crapPercentCriticalThreshold = crapPercentCriticalThreshold;

		this.methodCount = crapSubjects.size();
		this.total = computeTotalCrap(crapValues);
		this.average = total / methodCount;
		this.crapNumber = average;
		this.median = computeMedian();
		this.stdDev = computeStdDev();
    this.crapMethodCount = countCrapMethods(crapValues);
    this.crapWorkLoad = computeCrapWorkLoad(crapValues);
    this.globalStats = globalStats;
    this.server = server;
	}

	private int computeCrapWorkLoad(List<? extends Crap> crapValues) {
    int crapLoad = 0;
    for (Crap method : crapValues) {
      crapLoad += method.getCrapLoad(crapThreshold);
    }
    return crapLoad;    
  }

  private int countCrapMethods(List<? extends Crap> crapValues) {
	  int count = 0;
    for (Crap method : crapValues) {
      if (method.getCrap() >= crapThreshold)
        count++;
    }
    return count;
  }

  private float computeTotalCrap(List<? extends Crap> crapValues) {
		float totalCrap = 0;
		for (Crap method : crapValues) {
			totalCrap += method.getCrap();
		}
		return totalCrap;
	}

	private void validateParams(List<? extends Crap> crapValues) {
		if (crapValues == null/* || crapValues.size() < 1*/)
			throw new IllegalArgumentException("Cannot compute for null values");
	}
	
	public float getCrapNumber() {
		return crapNumber;
	}

	public float getAverage() {
		return average;
	}

	public float getMedian() {
		return median;
	}

	public double getStdDev() {
		return stdDev;
	}

	public float getTotal() {
		return total;
	}

	public int getSubjectCount() {
		return methodCount;
	}

	private double computeStdDev() {
		// Note we are not sampling, we have the whole population, so use N, not N - 1
		return Math.sqrt(computeVariance() / getSubjectCount());
	}

	private float computeVariance() {
		float[] crapScores = crapScores(crapSubjects);
		float variance = 0;
		for (int i = 0; i < crapScores.length; i++) {
			float diff = ((float)crapScores[i]) - getAverage();
			variance += Math.pow(diff,2.0);
		}
		return variance;
	}

	private float computeMedian() {
		float[] crapNumbers = crapScores(crapSubjects);
		if (crapNumbers.length == 0)
			return 0f;
		else if (crapNumbers.length == 1)
			return crapNumbers[0];
		Arrays.sort(crapNumbers);
		return crapNumbers[crapNumbers.length / 2];

	}

	private float[] crapScores(List<? extends Crap> list) {
		float[] crapNumbers = new float[crapSubjects.size()];
		for (int i=0; i < list.size(); i++) {
			crapNumbers[i] = list.get(i).getCrap();
		}
		return crapNumbers;
	}

	@Override
	public String toString() {
    NumberFormat nf = FormatUtil.getNumberFormatter();
		StringBuilder buf = new StringBuilder();
		buf.append(name).append("\n_____________________\n");
		buf.append("Total Crap: ").append(nf.format(total)).append(", ");
		buf.append("Crap: ").append(nf.format(crapNumber)).append(", ");
		buf.append("Median: ").append(nf.format(median)).append(", ");
		buf.append("Average: ").append(nf.format(average)).append(", ");
		buf.append("Std Dev: ").append(nf.format(stdDev)).append(", ");
		buf.append("Method Count: ").append(methodCount).append(", ");
    buf.append("crapMethodCount: ").append(nf.format((crapMethodCount))).append(", ");
    buf.append("crapMethodPercent: ").append(nf.format((crapMethodPercent()))).append("%");

//		buf.append(assessment);
		return buf.toString();
	}

	public List<? extends Crap> getSubjects() {
		return crapSubjects;
	}

	public void printAllSubjectComplexities() {
		List<? extends Crap> crapValues = getSubjects();
		System.out.println("Subject breakdown");
		System.out.println("----------------------");	
		Collections.sort(crapValues, Crap.comparator);
		for (Crap crap : crapValues) {
			System.out.println(crap);
		}
	}
  
  public String toXml() {
    MyStringBuilder s = new MyStringBuilder();
    s.start("<crap_result>");
    crapProject.toXml(s);
    writeStats(s);
    writeMethods(s);
    s.end("</crap_result>");
    return s.toString();
  }

  private void writeStats(MyStringBuilder s) {
    s.start("<stats>");
    NumberFormat nf = FormatUtil.getNumberFormatter();
    writeEachStat(s, nf);
    
    int ones = crapLessThan(2.0f);
    int twos = (crapBetween(2.0f, 4.0f));
    int fours = (crapBetween(4.0f, 8.0f));
    int eights = (crapBetween(8.0f, 16.0f));
    int sixteens = (crapBetween(16.0f, 32.0f));
    int thirtytwos = (crapBetween(32.0f, 64.0f));
    int sixtyfours = (crapBetween(64.0f, 128.0f));
    int one28s = (crapBetween(128.0f, 256.0f));
    int two56s = (crapGE(256.0f));
    
    String projectName = URLEncoder.encode(crapProject.getProjectName());
    writeShareUrl(s, projectName, 
                  ones, twos, fours, eights, sixteens, thirtytwos, sixtyfours, one28s, two56s);
    
    writeHistogram(s, nf, 
                   ones, twos, fours, eights, sixteens, thirtytwos, sixtyfours, one28s, two56s);
    s.end("</stats>");
  }

  private NumberFormat writeEachStat(MyStringBuilder s, NumberFormat nf) {
    XmlUtil.itemToXml(s, "name", name);
    XmlUtil.itemToXml(s, "totalCrap", nf.format(total));
    XmlUtil.itemToXml(s, "crap", nf.format(crapNumber));
    XmlUtil.itemToXml(s, "median", nf.format(median));
    XmlUtil.itemToXml(s, "average", nf.format(average));
    XmlUtil.itemToXml(s, "stdDev", nf.format(stdDev));
    XmlUtil.itemToXml(s, "methodCount", Integer.toString(methodCount));
    XmlUtil.itemToXml(s, "crapMethodCount", Integer.toString(crapMethodCount));
    XmlUtil.itemToXml(s, "crapMethodPercent", nf.format(crapMethodPercent()));
    XmlUtil.itemToXml(s, "crapLoad", Integer.toString(crapWorkLoad));
    XmlUtil.itemToXml(s, "crapThreshold", Integer.toString((int) crapThreshold));
    XmlUtil.itemToXml(s, "globalAverage", nf.format(globalStats.getCrapAverage()));
    XmlUtil.itemToXml(s, "globalCraploadAverage", nf.format(globalStats.getCrapLoadAverage()));
    XmlUtil.itemToXml(s, "globalCrapMethodAverage", nf.format(globalStats.getCrapMethodAverage()));
    XmlUtil.itemToXml(s, "globalTotalMethodAverage", nf.format(globalStats.getTotalMethodAverage()));
    XmlUtil.itemToXml(s, "globalAverageDiff", nf.format(globalStats.getCrapAverageDiff(crapNumber)));
    XmlUtil.itemToXml(s, "globalCraploadAverageDiff", nf.format(globalStats.getCrapLoadAverageDiff(crapWorkLoad)));
    XmlUtil.itemToXml(s, "globalCrapMethodAverageDiff", nf.format(globalStats.getCrapMethodAverageDiff(crapMethodCount)));
    XmlUtil.itemToXml(s, "globalTotalMethodAverageDiff", nf.format(globalStats.getTotalMethodAverageDiff(methodCount)));
    return nf;
  }

  private void writeMethods(MyStringBuilder s) {
    s.start("<methods>");
    List<? extends Crap> crapValues = getSubjects();
    Collections.sort(crapValues, Crap.comparator);
    MethodCrap.setCrapLoadThreshold(crapThreshold);
    for (Crap crap : crapValues) {
      crap.toXml(s);
    }
    s.end("</methods>");
  }

  private void writeHistogram(MyStringBuilder s, NumberFormat nf, int ones, int twos, int fours, int eights,
                              int sixteens, int thirtytwos, int sixtyfours, int one28s, int two56s) {
    s.start("<histogram>");
    float scale = computeScaleBasedOnLargest(ones, twos, fours, eights, sixteens, thirtytwos, sixtyfours, one28s,
                                             two56s);
    makeHist(s, "one", Integer.toString(ones), nf.format(adjustedHeight(ones, scale)));
    makeHist(s, "two", Integer.toString(twos), nf.format(adjustedHeight(twos, scale)));
    makeHist(s, "four", Integer.toString(fours), nf.format(adjustedHeight(fours, scale)));
    makeHist(s, "eight", Integer.toString(eights), nf.format(adjustedHeight(eights, scale)));
    makeHist(s, "sixteen", Integer.toString(sixteens), nf.format(adjustedHeight(sixteens, scale)));
    makeHist(s, "thirtytwo", Integer.toString(thirtytwos), nf.format(adjustedHeight(thirtytwos, scale)));
    makeHist(s, "sixtyfour", Integer.toString(sixtyfours), nf.format(adjustedHeight(sixtyfours, scale)));
    makeHist(s, "one28", Integer.toString(one28s), nf.format(adjustedHeight(one28s, scale)));
    makeHist(s, "two56", Integer.toString(two56s), nf.format(adjustedHeight(two56s, scale)));
    s.end("</histogram>");
  }

  private float computeScaleBasedOnLargest(int ones, int twos, int fours, int eights, int sixteens, int thirtytwos,
                                           int sixtyfours, int one28s, int two56s) {
    int[] places = {ones, twos, fours, eights, sixteens, thirtytwos, sixtyfours, one28s, two56s};
    Arrays.sort(places);
    int largest = places[places.length-1];
    float maxHeight = 170.0f;
    float scale = maxHeight / (float)largest;
    return scale;
  }

  private void writeShareUrl(MyStringBuilder s, String projectName, int ones, int twos, int fours, int eights,
                             int sixteens, int thirtytwos, int sixtyfours, int one28s, int two56s) {
    String url = server+"stats/new?stat[project_hash]="+crapProject.getProjectId().toString()+
                  "&amp;stat[project_url]="+projectName +
                  "&amp;stat[crap]="+URLEncoder.encode(FormatUtil.getUSNumberFormatter().format(crapMethodPercent()))+
                  "&amp;stat[crap_load]="+Integer.toString(crapWorkLoad)+
                  "&amp;stat[crap_methods]="+Integer.toString(crapMethodCount)+
                  "&amp;stat[total_methods]="+Integer.toString(methodCount)+
                  "&amp;stat[ones]="+Integer.toString(ones)+
                  "&amp;stat[twos]="+Integer.toString(twos)+
                  "&amp;stat[fours]="+Integer.toString(fours)+
                  "&amp;stat[eights]="+Integer.toString(eights)+
                  "&amp;stat[sixteens]="+Integer.toString(sixteens)+
                  "&amp;stat[thirtytwos]="+Integer.toString(thirtytwos)+
                  "&amp;stat[sixtyfours]="+Integer.toString(sixtyfours)+
                  "&amp;stat[one28s]="+Integer.toString(one28s)+
                  "&amp;stat[two56s]="+Integer.toString(two56s)
                  ;
    XmlUtil.itemToXml(s, "shareStatsUrl", url);
  }

  private float adjustedHeight(int ones, float scale) {
    float minHeight = 28;
    if (ones > 0) 
      return Math.max(ones  * scale, minHeight);
    else
      return 0f;
  }

  private void makeHist(MyStringBuilder s, String place, String value, String height) {
    s.start("<hist>");
    XmlUtil.itemToXml(s, "place", place);
    XmlUtil.itemToXml(s, "value", value);
    XmlUtil.itemToXml(s, "height", height+"px");
    s.end("</hist>");
  }

  private int crapBetween(float f, float f2) {
    int count = 0;
    for (Crap method : crapSubjects) {
      if (method.getCrap() >= f && method.getCrap() < f2)
        count++;
    }
    return count;
  }

  private int crapLessThan(float f) {
    int count = 0;
    for (Crap method : crapSubjects) {
      if (method.getCrap() < f)
        count++;
    }
    return count;
  }
  
  private int crapGE(float f) {
    int count = 0;
    for (Crap method : crapSubjects) {
      if (method.getCrap() >= f)
        count++;
    }
    return count;
  }

  private float crapMethodPercent() {
    return ((float)crapMethodCount / (float)methodCount)*100.0f;
  }

  public void writeXmlToFile(String statsXml) {
    File out = getXmlReportFile();
    FileUtil.writeFile(out, statsXml);
  }

  private File getXmlReportFile() {
    return crapProject.getReportFile();
  }

  public void generatePicture() {
    CrapImageGenerator cig = new CrapImageGenerator(crapMethodPercent(), 
                                                  crapProject.outputDir(), 
                                                  "crapGauge.png",
                                                  crapPercentWarningThreshold,
                                                  crapPercentCriticalThreshold);
    cig.makeGaugeAndWriteToFile();
  }

  public void generateBarPicture() {
    NewCrapImageGenerator cig = new NewCrapImageGenerator(crapMethodPercent(), 
                                                  crapProject.outputDir(), 
                                                  "crapBar.png", false, /*
                                                  crapPercentWarningThreshold,*/
                                                  crapPercentCriticalThreshold * 100.0f,
                                                  globalStats.getCrapAverage());
    cig.makeGaugeAndWriteToFile();
  }

  
  public void generateHtml() {
    copyImages();
    try {
      StreamSource xmlFile = new StreamSource(getXmlReportFile());
      generateMainPage(xmlFile);
      generateDetailCrapPage(xmlFile);
      generateDetailCrapLoadPage(xmlFile);
      generateDetailComplexityPage(xmlFile);
      generateDetailCoveragePage(xmlFile);
    } catch (Exception ex) {
      ex.printStackTrace();
    } 
  }

  private void copyImages() {
    copyFile(getRelativeResource("g_backbar.gif"), 
             new File(crapProject.outputDir(), "g_backbar.gif"));
    copyFile(getRelativeResource("g_colorbar3.jpg"), 
        new File(crapProject.outputDir(), "g_colorbar3.jpg"));
    
  }

  public void copyFile(InputStream stream, File outFile) {
    try {
      BufferedInputStream f = new BufferedInputStream(new DataInputStream(stream));
      BufferedOutputStream out = new BufferedOutputStream(new DataOutputStream(new FileOutputStream(outFile)));
      new StreamCopier(f, out, false);
    } catch (FileNotFoundException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  private void makeHtml(StreamSource xmlFile, InputStream xslt, File outDetail) throws TransformerConfigurationException, TransformerFactoryConfigurationError, TransformerException {
    StreamSource xsltFile = new StreamSource(xslt);
    Transformer transformer = TransformerFactory.newInstance().newTransformer(xsltFile);
    transformer.transform(xmlFile, new StreamResult(outDetail));
  }

  private void generateMainPage(StreamSource xmlFile) throws FileNotFoundException, TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
    InputStream xslt = getRelativeResource("report.xslt");
    File outDetail = crapProject.getReportHtmlFile();    
    makeHtml(xmlFile, xslt, outDetail);
  }

  private void generateDetailCrapPage(StreamSource xmlFile) throws FileNotFoundException, TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
    InputStream xslt = getRelativeResource("detail_crap.xslt");
    File outDetail = new File(crapProject.outputDir(), "detail_crap.html");

    makeHtml(xmlFile, xslt, outDetail);
  }

  private void generateDetailCrapLoadPage(StreamSource xmlFile) throws FileNotFoundException, TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
    InputStream xslt = getRelativeResource("detail_crap_load.xslt");
    File outDetail = new File(crapProject.outputDir(), "detail_crap_load.html");

    makeHtml(xmlFile, xslt, outDetail);
  }
  
  private void generateDetailComplexityPage(StreamSource xmlFile) throws FileNotFoundException, TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
    InputStream xslt = getRelativeResource("detail_complexity.xslt");
    File outDetail = new File(crapProject.outputDir(), "detail_complexity.html");

    makeHtml(xmlFile, xslt, outDetail);
  }

  private void generateDetailCoveragePage(StreamSource xmlFile) throws FileNotFoundException, TransformerFactoryConfigurationError, TransformerConfigurationException, TransformerException {
    InputStream xslt = getRelativeResource("detail_coverage.xslt");
    File outDetail = new File(crapProject.outputDir(), "detail_coverage.html");

    makeHtml(xmlFile, xslt, outDetail);
  }

  public InputStream getRelativeResource(String file) {
    return SystemCrapStats.class.getResourceAsStream(file);
  }

  public void writeReport() {
    writeXmlToFile(toXml());
//    generatePicture();
    generateBarPicture();
    generateHtml();
  }
}
