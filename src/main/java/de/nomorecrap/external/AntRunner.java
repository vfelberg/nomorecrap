package de.nomorecrap.external;

import de.nomorecrap.util.StreamCopier;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * This class will launch an ant job for the super runner 
 * 
 * with the super runner classpath,
 * the project classpath, and
 * the test classes
 * 
 * after it generates an ant file.
 * 
 * 
 * @author bobevans
 *
 */
public class AntRunner {

	private String antFile;
  private String junitLib;
  private String antHome;
  private boolean debug;
  private String agitatorEclipseApiPluginDir;
	
	public AntRunner(String antFile, 
                   String antHome,
                   String junitLib,
                   String agitatorEclipseApiPluginDir,
                   boolean debug) {
		this.antFile = antFile;
    this.antHome = antHome;
    this.junitLib = junitLib;
    this.agitatorEclipseApiPluginDir = agitatorEclipseApiPluginDir;
    this.debug = debug;
	}

	public int run() {		
//    return runAntProgramatically();
		return runAsExternalProcess();
	}

  private int runAsExternalProcess() {
    String[] cmdarray = buildAntCmdJavaLauncher();
    if (isDebug())
      printCmd(cmdarray);
		int exitStatus = -1;
		try {
			Process out = Runtime.getRuntime().exec(cmdarray);
      new StreamCopier(out.getInputStream(), System.out, false);
      new StreamCopier(out.getErrorStream(), System.out, false);
      exitStatus = out.waitFor();
			checkExitStatus(exitStatus);
		} catch (Exception e) {
			System.out.println("Could not execute ant file.");
			e.printStackTrace();
		}
		System.out.println("Ant exited with status "+exitStatus);
		return exitStatus;
  }

  private void checkExitStatus(int exitStatus) {
    if (exitStatus != 0)
    	throw new RuntimeException("Ant run failed with status: "+exitStatus);
  }

	private void printCmd(String[] cmdarray) {
    for (String string : cmdarray) {
      System.out.println(string);
    }
  }

//  private void observeProcess(final Process out) {
//		Thread listener = new Thread() {
//
//      @Override
//      public void run() {
//
//      BufferedReader procOutput = null;
//      try {
//        procOutput = new BufferedReader(new InputStreamReader (out.getInputStream()));
//        int c;
//        try {
//          while ((c = procOutput.read()) != -1) {
//            System.out.print((char) c);
//          }
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//      } finally {
//        try {
//          procOutput.close();
//        } catch (IOException e) {
//          e.printStackTrace();
//        }
//      }
//      }
//    
//    };
//    listener.start();
//	}

//  ava -Dant.home=c:\ant org.apache.tools.ant.launch.Launcher [options] [target]
  /**
   /Library/Java/Home/bin/java -classpath /Developer/Java/Ant/lib/ant-launcher.jar -Dant.home=/Developer/Java/Ant -Dant.library.dir=/Developer/Java/Ant/lib -Djikes.class.path=:/Library/Java/Home/../Classes/charsets.jar:/Library/Java/Home/../Classes/classes.jar:/Library/Java/Home/../Classes/dt.jar:/Library/Java/Home/../Classes/jce.jar:/Library/Java/Home/../Classes/jconsole.jar:/Library/Java/Home/../Classes/jsse.jar:/Library/Java/Home/../Classes/laf.jar:/Library/Java/Home/../Classes/ui.jar org.apache.tools.ant.launch.Launcher -cp  -v -f crap_build.xml
   */
  private String[] buildAntCmdJavaLauncher() {
    List<String> cmdOps = new ArrayList<String>();
    cmdOps.add("java");
    cmdOps.add("-classpath");
    cmdOps.add(antLauncherJar());
    cmdOps.add("-Dant.home="+antHome()); 
    cmdOps.add("-Dant.library.dir="+antHome()+ File.separator + "lib");
    cmdOps.add("org.apache.tools.ant.launch.Launcher");
    if (isDebug())
      cmdOps.add("-v");
    cmdOps.add("-lib");
    cmdOps.add("\"" + junitLib() + "\"");
    cmdOps.add("-f");
    cmdOps.add(antFile);
    
    return cmdOps.toArray(new String[cmdOps.size()]);
  }

  private String antLauncherJar() {
    return (agitatorEclipseApiPluginDir()+ File.separator + 
    "lib"+ File.separator + "ant-launcher.jar");
  }

  private String agitatorEclipseApiPluginDir() {
    return agitatorEclipseApiPluginDir;
  }

  private String junitLib() {
    return junitLib;
  }

  private String antHome() {
    return antHome;
  }

  private boolean isDebug() {
    return debug;
  }

}
