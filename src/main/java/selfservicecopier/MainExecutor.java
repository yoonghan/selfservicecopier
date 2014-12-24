package selfservicecopier;

import java.io.File;

public class MainExecutor {

	private static final String srcFolder = "C:\\SelfProject\\IDE\\workspace_scala\\nodejs";
	private static final String destFolder = "C:\\SelfProject\\IDE\\workspace\\_migrateGitHub\\nodejs";
	
	private static final String ply_srcFolder = "C:\\SelfProject\\IDE\\workspace_scala\\selfservice";
	private static final String ply_destFolder = "C:\\SelfProject\\IDE\\workspace\\_migrateGitHub\\selfservice";
	
	/**Node Modules for copy**/
	//Will hit exception if new modules are to be copied into main folder. Expected for double verification.
	private static final String[] folderCopies = new String[]{
		"\\webroot", 
		"\\node_modules\\mime", 
		"\\node_modules\\replacer"};
	/**Node Modules for copy**/
	
	public static void main(String[] args) {
		executeCSSTransformer();
		
		//For web
		for(String folder: folderCopies){
			executeFileCopy(srcFolder + folder, destFolder + folder);
		}
		executePlayCopy();
	
		executePlayCompiler();
	}

	private static void executePlayCopy() {
		
		final String APP_FOLDER = "/app";
		
		System.out.println("========Play copy started.========");
		FolderCopier fc = new FolderCopier(ply_srcFolder+APP_FOLDER, ply_destFolder+APP_FOLDER);
		fc.execute();
		System.out.println("========Copy conf files.========");
		
		String[] copySet = new String[]{
				"/conf/application.conf",
				"/conf/play.plugins",
				"/conf/routes",
				"/build.sbt"};
		
		for(String toCopy:copySet){
			System.out.println(">>>"+toCopy);
			fc.directCopy(new File(ply_srcFolder+toCopy), new File(ply_destFolder+toCopy));
		}
		
		System.out.println("========Play copy ended.========");
	}

	private static void executeFileCopy(String _srcFolder, String _destFolder) {
		System.out.println("========File started.========");
		FolderCopier fc = new FolderCopier(_srcFolder, _destFolder);
		fc.addFoldersToSkip("karma");
		fc.addFoldersToSkip("karma-chrome-launcher");
		fc.addFoldersToSkip("karma-jasmine");
		fc.addFoldersToSkip("test");
		fc.execute();
		System.out.println("========File completed.========");
	}

	private static void executeCSSTransformer() {
		System.out.println("========Transformer started========");
		CommandExecutor ce = new CommandExecutor();
		ce.scanSass("");
		System.out.println("========Transformer completed========");
	}
	
	private static void executePlayCompiler() {
		System.out.println("========Play Compiler started========");
		CommandExecutor ce = new CommandExecutor();
		ce.compilePlay();
		System.out.println("========Play Compiler completed========");
	}
	
}
