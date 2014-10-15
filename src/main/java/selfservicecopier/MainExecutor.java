package selfservicecopier;

import java.io.File;

public class MainExecutor {

	private static final String srcFolder = "C:\\SelfProject\\IDE\\workspace\\nodejs\\webroot";
	private static final String destFolder = "C:\\SelfProject\\IDE\\workspace\\_migrateGitHub\\nodejs\\webroot";
	
	private static final String ply_srcFolder = "C:\\SelfProject\\IDE\\workspace_scala\\selfservice";
	private static final String ply_destFolder = "C:\\SelfProject\\IDE\\workspace\\_migrateGitHub\\selfservice";
	
	public static void main(String[] args) {
		executeCSSTransformer();
		
		//For web
		executeFileCopy(srcFolder, destFolder);
		
		executePlayCopy();
		
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
		fc.execute();
		System.out.println("========File completed.========");
	}

	private static void executeCSSTransformer() {
		System.out.println("========Transformer started========");
		CommandExecutor ce = new CommandExecutor();
		ce.scanSass("");
		System.out.println("========Transformer completed========");
	}
	
}
