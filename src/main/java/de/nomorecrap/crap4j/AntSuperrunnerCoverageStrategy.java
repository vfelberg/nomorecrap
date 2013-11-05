package de.nomorecrap.crap4j;

import de.nomorecrap.crap4j.external.AntBuilder;
import de.nomorecrap.crap4j.external.AntRunner;
import de.nomorecrap.crap4j.util.FileUtil;

import java.io.IOException;

public class AntSuperrunnerCoverageStrategy implements CoverageGeneratorStrategy {

  private Main main;

  public AntSuperrunnerCoverageStrategy(Main main) {
    this.main = main;
  }

  public void execute(Crap4jRunner runner, CrapProject crapProject, boolean debug) {
    try {
      FileUtil.ensureCleanDirectory(crapProject.getCoverageDir());
    } catch (IOException e1) {
      // TODO Auto-generated catch block
      e1.printStackTrace();
      return;
    }
    AntBuilder antBuilder = new AntBuilder(main.agitatorEclipseCoveragePluginDir,
                                           main.agitatorEclipseApiPluginDir);
    String antFile = antBuilder.buildFileForProject(crapProject);
    AntRunner antRunner = new AntRunner(antFile,
                                        main.antHome, 
                                        main.junitLib,
                                        main.agitatorEclipseApiPluginDir, 
                                        debug);
    
    try {
    	antRunner.run();
      runner.readResults(crapProject);
    } catch (Throwable t) {
      // TODO Auto-generated catch block
      t.printStackTrace();
    } 

  }
}
