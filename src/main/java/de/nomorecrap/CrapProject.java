package de.nomorecrap;

import de.nomorecrap.util.FileUtil;
import de.nomorecrap.util.MyStringBuilder;
import de.nomorecrap.util.XmlUtil;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CrapProject {

	private static final String PROJECT_ID_FILE = ".crap4j_project_id";
	private String projectDir;
	private List<String> libClasspaths;
	private List<String> testClassDirs;
	private List<String> classDirs;
	private List<String> sourceDirs;
	private String outputDir;
	private Long projectId;

	public CrapProject(String projectDir, List<String> libClasspath,
                       List<String> testClassDirs, List<String> projectClassDirs,
                       List<String> sourceDirs, String outputDir) {
		validateProjectDir("projectDir", projectDir);
		validateList("libClasspath", libClasspath);
		validateList("testClassDirs", testClassDirs);
		validateList("classDirs", projectClassDirs);
		validateList("sourceDirs", sourceDirs);
		this.projectDir = projectDir;
		this.libClasspaths = makeAbsolute(libClasspath);
		
		this.testClassDirs = makeAbsolute(testClassDirs);
		this.classDirs = makeAbsolute(projectClassDirs);
		this.sourceDirs = makeAbsolute(sourceDirs);
		if (isBlank(outputDir)) {
			outputDir = projectDir + File.separator + "agitar" + File.separator
					+ "reports" + File.separator + "crap4j";
			this.outputDir = outputDir;
		} else {
			if (FileUtil.isAbsolute(outputDir)) {
				this.outputDir = outputDir;
			} else {
				this.outputDir = makeFilePathAbsoluteWithProject(outputDir);
			}
		}
		FileUtil.ensureDirectory(this.outputDir);
		projectId = ensureProjectId();
	}

	private void validateList(String string, List<String> collection) {
		if (null == collection)
			throw new IllegalArgumentException(string+" cannot be null");
		
	}

	private void validateProjectDir(String propName, String projectDir2) {
		validateNonBlank(propName, projectDir2);
		validateFileExists(projectDir2);
	}

	private void validateNonBlank(String propName, String projectDir2) {
		if (isBlank(projectDir2))
			throw new IllegalArgumentException(propName+" cannot be null");
	}

	private boolean isBlank(String projectDir2) {
		return null == projectDir2 || projectDir2.length() == 0;
	}

	private void validateFileExists(String projectDir2) {
		File f = new File(projectDir2);
		if (!f.exists()) 
			throw new IllegalArgumentException("Project Dir, "+projectDir+" does not exist!");
	}

	private Long ensureProjectId() {
		Long projectId = loadProjectId();
		if (projectId == null) {
			projectId = makeProjectId();
			writeProjectId(projectId);
		}
		return projectId;
	}

	private void writeProjectId(Long projectId2) {
		File f = getProjectIdFile();
		DataOutputStream out = null;
		try {
			out = new DataOutputStream(new FileOutputStream(f));
			try {
				out.writeLong(projectId2);
			} catch (IOException e1) {
				e1.printStackTrace();
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
	}

	private Long loadProjectId() {
		Long id = null;
		File f = getProjectIdFile();
		if (f.exists()) {
			DataInputStream io = null;
			try {
				io = new DataInputStream(new FileInputStream(f));
				try {
					id = io.readLong();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (io != null) {
						try {
							io.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
		return id;
	}

	protected File getProjectIdFile() {
		File f = new File(outputDir(), PROJECT_ID_FILE);
		return f;
	}

	private Long makeProjectId() {
		return System.currentTimeMillis();
	}

	private List<String> makeAbsolute(List<String> sourceDirs2) {
		List<String> absolutePaths = new ArrayList<String>();
		for (String path : sourceDirs2) {
			if (path == null)
				continue;
			if (FileUtil.isAbsolute(path))
				absolutePaths.add(path);
			else {
				absolutePaths.add(makeFilePathAbsoluteWithProject(path));
			}
		}
		return absolutePaths;
	}

	private String makeFilePathAbsoluteWithProject(String path) {
		return FileUtil.joinPath(projectDir, path);
	}

	public String projectDir() {
		return projectDir;
	}

	public List<String> sourceDirs() {
		return sourceDirs;
	}

	public List<String> classDirs() {
		return classDirs;
	}

	public List<String> libClasspaths() {
		return libClasspaths;
	}

	public List<String> testClassDirs() {
		return testClassDirs;
	}

	public String outputDir() {
		return outputDir;
	}

	public List<String> allClasspaths() {
		List<String> all = new ArrayList<String>();
		all.addAll(FileUtil.directoriesAndJarsOnly(classDirs()));
		all.addAll(FileUtil.directoriesAndJarsOnly(testClassDirs()));
		all.addAll(libClasspaths());
		return all;
	}

	public List<File> allProjectClasses() {
		return FileUtil.removeTestClassFiles(getClassesInDirList(classDirs()));
	}
	
	public List<File> allTestClasses() {
	  return FileUtil.removeNonTestClassFiles(getClassesInDirList(testClassDirs()));
	}

  private List<File> getClassesInDirList(List<String> classDirs2) {
    List<File> classNames = new ArrayList<File>();
    if (classDirs2 == null)
      return classNames;
    for (String dirName : classDirs2) {
			classNames.addAll(getClassesIn(dirName));
		}
		return classNames;
  }

	private List<File> getClassesIn(String dirName) {
		List<File> classNames = new ArrayList<File>();
		File f = new File(dirName);
		if (f.isDirectory()) {
		  classNames.addAll(FileUtil.getAllFilesInDirMatchingPattern(dirName,
		                                                             ".*.class", false));
		} else {
		  if (dirName.endsWith(".class"))
		    classNames.add(f);
		}
		return classNames;
	}

	public String getCoverageDir() throws IOException {
		return new File(projectDir + File.separator + "agitar" + File.separator
				+ ".results").getCanonicalPath();
	}

	public String getProjectName() {
		return new File(projectDir).getName();
	}

	public void toXml(MyStringBuilder s) {
		XmlUtil.itemToXml(s, "project", projectDir);
		XmlUtil.itemToXml(s, "project_id", projectId.toString());
		XmlUtil.itemToXml(s, "timestamp", SimpleDateFormat.getInstance().format(
				Calendar.getInstance().getTime()));
		XmlUtil.collectionToXml(s, "classDirectories", "classDirectory", classDirs);
		XmlUtil.collectionToXml(s, "testClassDirectories", "testClassDirectory",
				testClassDirs);
		XmlUtil.collectionToXml(s, "sourceDirectories", "sourceDirectory", sourceDirs);
		XmlUtil.collectionToXml(s, "libClasspaths", "libClasspath", libClasspaths);
	}

	public File getReportFile() {
		return new File(outputDir(), "report.xml");
	}

	public File getReportHtmlFile() {
		return new File(outputDir(), "index.html");
	}

	public Long getProjectId() {
		return projectId;
	}

	// @Override
	// public boolean equals(Object obj) {
	// if (!(obj instanceof CrapProject))
	// return false;
	// CrapProject other = (CrapProject)obj;
	// return (projectId == other.projectId);
	// }
	//
	// @Override
	// public int hashCode() {
	// return projectDir.hashCode() + pathHashCode(classDirs()) +
	// pathHashCode(libClasspaths()) + pathHashCode(testClassDirs());
	// }
	//
	// private int pathHashCode(List<String> classDirs2) {
	// int hashcode = 0;
	// for (String string : classDirs2) {
	// hashcode += string.hashCode();
	// }
	// return hashcode;
	// }

}
