package de.nomorecrap.crap4j;

import com.agitar.org.objectweb.asm.tree.analysis.AnalyzerException;
import de.nomorecrap.crap4j.benchmark.StatsDownloader;
import de.nomorecrap.crap4j.external.MetricsComplexityReader;
import de.nomorecrap.crap4j.external.SuperRunnerCoverageReader;

import java.io.IOException;
import java.util.List;


/**
 * This runner is specific to main because it runs ant on the command line.
 * 
 * A subclass of this will generate coverage data in a different manner, and will not 
 * need main.
 * 
 * @author bobevans
 *
 */
public class Crap4jRunner {

  private boolean debug;
  private boolean dontTest;
  private CoverageGeneratorStrategy coverageStrategy;
  private float crapPercentWarningThreshold;
  private float crapPercentCriticalThreshold;
  private float crapThreshold;
  private boolean downloadAverages;
  private String server;

  public Crap4jRunner(boolean debug, boolean dontTest,
                      boolean downloadAverages,
                      CoverageGeneratorStrategy strategy,
                      float crapThreshold,
                      float crapPercentWarningThreshold, float crapPercentCriticalThreshold,
                      String server) {
    this.debug = debug;
    this.dontTest = dontTest;
    this.coverageStrategy = strategy;
    this.crapThreshold = crapThreshold;
    this.crapPercentWarningThreshold = crapPercentWarningThreshold;
    this.crapPercentCriticalThreshold = crapPercentCriticalThreshold;
    this.downloadAverages = downloadAverages;
    this.server = server;
  }

  
  public void doProject(CrapProject p) throws IOException, AnalyzerException {
    if (dontTest) {
      readResults(p);
    } else {
      generateCoverageDataStatsFor(p);
    }
  }


  public void readResults(CrapProject p) throws IOException, AnalyzerException {
    SystemCrapStats s = new SystemCrapStats(buildMethodCrap(readCoverage(p),
                                                            readMethodComplexity(p)), 
                                            "Method Crap Stats", 
                                            p,
                                            crapThreshold,
                                            crapPercentWarningThreshold,
                                            crapPercentCriticalThreshold,
                                            new StatsDownloader(server).getAverage(downloadAverages), server);
    s.writeReport();
    //System.out.println(s.toString());
  }

  private List<? extends Crap> buildMethodCrap(List<MethodCoverage> covs,
      List<MethodComplexity> complexities) {
    return MethodCrap.combine(covs, complexities);
  }

  private List<MethodComplexity> readMethodComplexity(CrapProject p)
      throws IOException, AnalyzerException {
    return new MetricsComplexityReader(p).readMethodComplexities();
  }

  private List<MethodCoverage> readCoverage(CrapProject p) throws IOException {
    return new SuperRunnerCoverageReader(p).read();
  }

  private void generateCoverageDataStatsFor(CrapProject project) {
    coverageStrategy.execute(this, project, debug);
  }


}
