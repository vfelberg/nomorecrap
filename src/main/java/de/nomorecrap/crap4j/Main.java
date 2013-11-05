package de.nomorecrap.crap4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import com.agitar.org.objectweb.asm.tree.analysis.AnalyzerException;

/**
 * This is the command line launcher and command line ant launcher for the Crap4jRunner.
 *
 * The other option is the eclipse launcher, Crap4jAction.
 *
 * @author bobevans
 *
 */
public class Main {
	public String crap4jHome;
	public String antHome;
	public String agitatorEclipseApiPluginDir;
	public String agitatorEclipseCoveragePluginDir;
	public String junitLib;

  public static Main createMain(String crap4jHome) {
    //String crap4jHome = getCrap4jHome();
    String agitatorEclipseApiPlugin = getAgitatorEclipseApiPlugin(crap4jHome);
    String agitatorEclipseCoveragePluginDir = getAgitatorEclipseCoveragePluginDir(crap4jHome);
    String junitLib = getJunitLib(agitatorEclipseApiPlugin);
    String antHome = getAntHome(agitatorEclipseApiPlugin);

    Main main = Main.createMain(crap4jHome,
                            agitatorEclipseApiPlugin,
                            agitatorEclipseCoveragePluginDir,
                            junitLib,
                            antHome);
    return main;
  }


	public static Main createMain(String crap4jHome,
                                              String agitatorEclipseApiPlugin,
                                              String agitatorEclipseCoveragePluginDir,
                                              String junitLib, String antHome) {
			return new Main(crap4jHome,
                          agitatorEclipseApiPlugin,
                          agitatorEclipseCoveragePluginDir,
                          junitLib,
                          antHome);
	}

	private Main(String crap4jHome,
                 String agitatorEclipseApiPlugin,
                 String agitatorEclipseCoveragePluginDir,
                 String junitLib,
                 String antHome) {
		try {
			this.crap4jHome = crap4jHome;
      this.agitatorEclipseApiPluginDir = agitatorEclipseApiPlugin;
      this.agitatorEclipseCoveragePluginDir = agitatorEclipseCoveragePluginDir;
      this.junitLib = junitLib;
      this.antHome = antHome;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

  public static String getAntHome(String apiPluginDir) {
    return System.getProperty("ANT_HOME", apiPluginDir);
  }

  public static String getJunitLib(String apiPluginDir) {
    return apiPluginDir + File.separator
    		+ "lib" + File.separator + "junit.jar";
  }

  public static String getAgitatorEclipseCoveragePluginDir(String crap4jHome) {
    return crap4jHome + File.separator
    		+ "lib" + File.separator
    		+ "com.agitar.eclipse.coverage_4.2.0.401405";
  }

  public static String getAgitatorEclipseApiPlugin(String crap4jHome) {
    return crap4jHome + File.separator
    		+ "lib" + File.separator
    		+ "com.agitar.eclipse.api_4.2.0.401405";
  }

  public static String getCrap4jHome() {
    String crap4jHome = System.getProperty("CRAP4J_HOME");
    if (isBlank(crap4jHome)) {
    	crap4jHome = getCrap4jHomeFromClass().replace('/', File.separatorChar).replace("%20", " ");
    	if (isAbsoluteWindowsPath(crap4jHome))
    		crap4jHome = removeDriveLetter(crap4jHome);
    }
    return crap4jHome;
  }


  private static String removeDriveLetter(String crap4jHome) {
    return crap4jHome.substring(1);
  }


  private static boolean isAbsoluteWindowsPath(String crap4jHome) {
    return crap4jHome.indexOf(':') != -1
    		&& crap4jHome.startsWith("\\");
  }


  private static boolean isBlank(String crap4jHome) {
    return crap4jHome == null || crap4jHome.equals("");
  }

	private static String getCrap4jHomeFromClass() {
		URL foo = Main.class.getResource("Main.class");
		String filePath = foo.getPath();

		int lastIndexOf = filePath.lastIndexOf(File.separator+"lib"+File.separator);
		if (lastIndexOf == -1) {
			int lastIndexOfBin = filePath.lastIndexOf(File.separator+"bin"+File.separator);
			if (lastIndexOfBin != -1 && lastIndexOfBin < filePath.length())
			  return filePath.substring(0, lastIndexOfBin);
			else
			  throw new IllegalArgumentException("Cannot figure out Crap4j Home!!");
		} else
		  return filePath.substring(5, lastIndexOf);
	}

	public static void main(String[] args) {
		Options options = parseArgs(args);
		if (!options.valid()) {
			System.exit(1);
		}
		CrapProject p = new CrapProject(options.getProjectDir(),
                                    options.getLibClasspaths(),
                                    options.getTestClassDirs(),
                                    options.getClassDirs(),
                                    options.getSourceDirs(),
                                    options.getOutputDir());
		try {
			Main main = createMain("");
			main.run(p, options.getDebug(), options.getDontTest(), options.getDownloadAverages(), options.getServer());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


  public void run(CrapProject project,
                  boolean debug,
                  boolean dontTest,
                  boolean downloadAverages,
                  String server) throws IOException, AnalyzerException {
    Crap4jRunner runner = new Crap4jRunner(debug,
                                           dontTest,
                                           downloadAverages,
                                           new AntSuperrunnerCoverageStrategy(this), 30.0f, 10.0f,
                                           5.0f, server);
    runner.doProject(project);
  }

	public static Options parseArgs(String[] args) {
		Options options = new Options();
		CmdLineParser parser = new CmdLineParser(options);
		try {
			parser.parseArgument(args);
		} catch (CmdLineException e) {
			System.err.println(e.getMessage());
			printUsage(parser);
		}
		if (!options.valid())
			printUsage(parser);
		return options;
	}

	private static void printUsage(CmdLineParser parser) {
		System.err.println("crap4j.[ sh | bat ] [parameters]");
		System.err.println("\twhere parameters are:");
		parser.printUsage(System.err);
	}


}
