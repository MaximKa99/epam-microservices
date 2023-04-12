package plugin;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.okhttp.ResponseBody;
import org.gradle.api.DefaultTask;
import org.gradle.api.GradleException;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.impldep.org.eclipse.jgit.api.Git;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class ApiDownloadTask extends DefaultTask {
    private final Logger LOGGER = Logging.getLogger(ApiDownloadTask.class);
    private final OkHttpClient httpClient = new OkHttpClient();
    private String api;
    private String outputFile;
    private String branch;

    @TaskAction
    public void download() {
        String url = String
                .format("https://raw.githubusercontent.com/MaximKa99/epam-microservices/%s/APIs/%s.yaml",
                        branch,
                        api
                );

        try {
            Request fileDownloadRequest = new Request.Builder()
                    .url(url)
                    .build();
            ResponseBody fileResponseBody = httpClient.newCall(fileDownloadRequest).execute().body();
            String content = fileResponseBody.string();

            File file = new File(outputFile);
            setUpOutputDir(file);
            Files.write(Paths.get(outputFile), content.getBytes(StandardCharsets.UTF_8));

            fileResponseBody.close();
        } catch (IOException exception) {
            throw new GradleException("Cannot load API "
                    + api
                    + ". The following error occured: "
                    + exception.getMessage()
            );
        }
    }

    private void setUpOutputDir(File file) {
        final File parentFile = file.getParentFile();
        if (parentFile != null) {
            parentFile.mkdirs();
        }
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

    @Input
    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }
}
