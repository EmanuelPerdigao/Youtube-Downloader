package myProject.YoutubeConverter;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/request")
public class MainController {

    byte[] byteArray;

    ExecutorService fixedPool = Executors.newFixedThreadPool(10);

    @RequestMapping(method = RequestMethod.POST, path = {"/url"})
    public ResponseEntity<Map<String, Object>> receiveUrl(@RequestBody ResponseURL url) throws ExecutionException, InterruptedException {

        System.out.println(url.getUrl());

        if (isYouTubeUrl(url.url)) {
            CLientWorker cLientWorker = new CLientWorker(url);
            Future<byte[]> FileInBytes = fixedPool.submit(cLientWorker);

            try {
                byteArray = FileInBytes.get();

                StringBuilder musicName = new StringBuilder();
                int index;
                byte[] contentBytes;


                // Find the '#' character to separate music name and content bytes
                for (index = 0; index < byteArray.length; index++)
                {

                    if ((char) byteArray[index] != '#')
                    {
                        musicName.append((char) byteArray[index]);
                    }
                    else
                    {
                        index += 1;
                        break;
                    }
                }

                // Calculate the length of the contentBytes array
                int newLength = byteArray.length - index;
                contentBytes = new byte[newLength];

                // Copy the bytes from byteArray starting from the index to contentBytes
                System.arraycopy(byteArray, index, contentBytes, 0, newLength);

                Base64 Base64 = null;

                String musicBytesBase64 = Base64.getEncoder().encodeToString(contentBytes);

                // Create a JSON object with "musicName" and "musicBytes" fields
                Map<String, Object> responseJson = new HashMap<>();
                responseJson.put("musicName", musicName.toString());
                responseJson.put("musicBytes", musicBytesBase64);

                return new ResponseEntity<>(responseJson, HttpStatus.OK);

            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }


    public static boolean isYouTubeUrl(String url) {

        // Regular expression to match YouTube video URLs
        String youtubeRegex = "^(https?\\:\\/\\/)?(www\\.youtube\\.com|youtu\\.be)\\/.+$";
        Pattern pattern = Pattern.compile(youtubeRegex);
        Matcher matcher = pattern.matcher(url);

        return matcher.matches();
    }
}
