package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.*;

public class FileSwapperController {

    private Path dirToWatch;
    private StringProperty dirToWatchString = new SimpleStringProperty("No dir selected...");
    private Path fileToMove;
    private StringProperty fileToMoveString = new SimpleStringProperty("No file selected...");

    public Path getDirToWatch(){
        return dirToWatch;
    }

    public void setDirToWatch(File dirToWatch){
        this.dirToWatch = dirToWatch == null ? null : dirToWatch.toPath();
        dirToWatchString.setValue(dirToWatch == null ? "No dir selected..." : dirToWatch.toString());
    }

    public Path getFileToMove(){
        return fileToMove;
    }

    public void setFileToMove(File fileToMove){
        this.fileToMove = fileToMove == null ? null : fileToMove.toPath();
        fileToMoveString.setValue(fileToMove == null ? "No file selected..." : fileToMove.toString());
    }

    public StringProperty dirToWatchStringProperty(){
        return dirToWatchString;
    }

    public StringProperty fileToMoveStringProperty(){
        return fileToMoveString;
    }

    public String startListening() throws IOException, InterruptedException{
        if ( !Files.exists(dirToWatch) || !Files.isDirectory(dirToWatch)){
            return "Invalid directory path!";
        }

        if ( !Files.exists(fileToMove) || Files.isDirectory(fileToMove)){
            return "Invalid file path!";
        }

        new Thread(new FileSwapListener(dirToWatch, fileToMove)).start();
        return null;
    }

    private class FileSwapListener implements Runnable{

        private Path dirToWatch;
        private Path fileToMove;
        private Path fileToWatch;

        public FileSwapListener(Path dirToWatch, Path fileToMove){
            this.dirToWatch = dirToWatch;
            this.fileToMove = fileToMove;
            fileToWatch = dirToWatch.resolve(fileToMove.getFileName());
        }

        @Override
        public void run(){
            try {
                PrintWriter logger = new PrintWriter(new File("./log.txt"));

                WatchService service = FileSystems.getDefault().newWatchService();
                dirToWatch.register(service, StandardWatchEventKinds.ENTRY_CREATE);

                boolean fileMoved = false;

                while ( !fileMoved ) {
                    WatchKey key = service.take();
                    for ( WatchEvent<?> event : key.pollEvents() ) {
                        Thread.sleep(100); // Files do not get switched if this is not here.
                        try {
                            WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                            logger.println(pathEvent.context());
                            if ( pathEvent.context().equals(fileToWatch.getFileName())) {
                                Files.delete(fileToWatch);
                                Files.move(fileToMove, fileToWatch);
                                fileMoved = true;
                                logger.println("File moved");
                            }
                        } catch ( Exception e ) {
                            logger.println(e.getMessage());
                            /* ¯\_(ツ)_/¯ */
                        }
                    }
                    key.reset();
                }

                logger.flush();
                logger.close();
            } catch ( Exception e ){
                /* ¯\_(ツ)_/¯ */
            }
        }
    }
}