package plugin;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import org.gradle.api.DefaultTask;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ApiDownloadTask extends DefaultTask {
    private final Logger LOGGER = Logging.getLogger(ApiDownloadTask.class);
    private final OkHttpClient httpClient = new OkHttpClient();
    private final String GITHUB_HOST = "https://raw.githubusercontent.com/MaximKa99/epam-microservices/main/APIs/";
    private String token;
    private String api;
    private String outputFile;

    @TaskAction
    public void download() {
        String url = GITHUB_HOST + api + ".yaml?token=" + token;

        Request request = new Request.Builder()
                .url(url)
                .build();

        LOGGER.info("Downloading " + "url");
        try {
            Response response = httpClient.newCall(request).execute();

            LOGGER.info("Writting to " + outputFile);

            ResponseBody responseBody = response.body();

            String content = responseBody.string();
            File file = new File(outputFile);

            setUpOutputDir(file);
            Files.write(Paths.get(outputFile), content.getBytes(StandardCharsets.UTF_8));
            responseBody.close();
        } catch (IOException exception) {
            LOGGER.error("Cannot load API " + api + ". The following error occured: " + exception.getMessage());
        }
    }

    private void setUpOutputDir(File file) {
        final File parentFile = file.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
        }
    }

    @Input
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Input
    public String getApi() {
        return api;
    }

    public void setApi(String api) {
        this.api = api;
    }

    @Input
    public String getOutputFile() {
        return outputFile;
    }

    public void setOutputFile(String outputFile) {
        this.outputFile = outputFile;
    }
}
