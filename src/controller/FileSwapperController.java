package controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class FileSwapperController {

    private Path dirToWatch;
    private StringProperty dirToWatchString = new SimpleStringProperty("No dir selected...");
    private Path fileToMove;
    private StringProperty fileToMoveString = new SimpleStringProperty("No file selected...");
    private StringProperty processInfo = new SimpleStringProperty("");


    public void setDirToWatch(File dirToWatch){
        this.dirToWatch = dirToWatch == null ? null : dirToWatch.toPath();
        dirToWatchString.setValue(dirToWatch == null ? "No dir selected..." : dirToWatch.toString());
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

    public StringProperty processInfoStringPropery(){
        return processInfo;
    }

    public String startListening() throws IOException, InterruptedException{
        if ( !Files.exists(dirToWatch) || !Files.isDirectory(dirToWatch)){
            return "Invalid directory path!";
        }

        if ( !Files.exists(fileToMove) || Files.isDirectory(fileToMove)){
            return "Invalid file path!";
        }

        new Thread(new FileSwapListener(dirToWatch, fileToMove)).start();
        processInfo.setValue(String.format("Listening for %s in %s", fileToMove.getFileName().toString(), dirToWatch.toString()));
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
                WatchService service = FileSystems.getDefault().newWatchService();
                dirToWatch.register(service, StandardWatchEventKinds.ENTRY_CREATE);

                boolean fileMoved = false;

                while ( !fileMoved ) {
                    WatchKey key = service.take(); //This is a blocking call
                    for ( WatchEvent<?> event : key.pollEvents() ) {
                        Thread.sleep(100); // Files do not get switched if there is no delay.
                        try {
                            WatchEvent<Path> pathEvent = (WatchEvent<Path>) event;
                            if ( pathEvent.context().equals(fileToWatch.getFileName())) {
                                Files.delete(fileToWatch);
                                Files.move(fileToMove, fileToWatch);
                                fileMoved = true;
                            }
                        } catch ( Exception e ) {
                           processInfo.setValue(e.getLocalizedMessage());
                        }
                    }
                    key.reset();
                }
            } catch ( Exception e ){
                processInfo.setValue(e.getLocalizedMessage());
            }
        }
    }
}
