package myProject.YoutubeConverter;

import org.python.util.PythonInterpreter;

import java.io.*;
import java.util.concurrent.Callable;

public class CLientWorker implements Callable<byte[]> {

    private ResponseURL url;

    public CLientWorker(ResponseURL url){
        this.url = url;
    }

    private byte[] processDownload(String url, String fileFormat) throws IOException {

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        try {

            String command = "python3 ./python/main.py \"" + url + "\" " + fileFormat;

            Process process = Runtime.getRuntime().exec(command);

            // Read the Python script's output
            InputStream inputStream = process.getInputStream();

            byte[] buffer = new byte[1024];
            int bytesRead;


           while ((bytesRead = inputStream.read(buffer)) != -1) {

                output.write(buffer, 0, bytesRead);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return output.toByteArray();
    }

    @Override
    public byte[] call() throws Exception {
        return processDownload(url.getUrl(), url.getFileFormat());
    }
}
