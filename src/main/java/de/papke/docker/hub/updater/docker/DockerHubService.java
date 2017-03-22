package de.papke.docker.hub.updater.docker;

import de.papke.docker.hub.updater.model.LoginResponse;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileReader;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Class for communicating with the docker hub api for updating description texts.
 *
 * @author Christoph Papke (info@papke.it)
 */
@Service
public class DockerHubService {

    private static final Logger LOG = LoggerFactory.getLogger(DockerHubService.class);

    private static final JSONParser JSON_PARSER = new JSONParser();
    private static final String FILE_ENCODING = "UTF-8";
    private static final String TOKEN_PREFIX = "JWT ";
    private static final String USERNAME_PARAMETER = "username";
    private static final String PASSWORD_PARAMETER = "password"; // NOSONAR
    private static final String DESCRIPTION_PARAMETER = "description";
    private static final String FULL_DESCRIPTION_PARAMETER = "full_description";

    @Value("${url.prefix}")
    private String urlPrefix;

    @Value("${login.path}")
    private String loginPath;

    @Value("${image.name}")
    private String imageName;

    @Value("${short.description.file}")
    private String shortDescriptionFile;

    @Value("${full.description.file}")
    private String fullDescriptionFile;

    /**
     * Method for initializing the object.
     */
    @PostConstruct
    public void init() {

        // get credentials for docker hub from docker config file
        String[] credentialsArray = getCredentials();

        // if retrieval of credentials was successful
        if (credentialsArray.length == 2) {

            // get login token
            String loginToken = getLoginToken(credentialsArray);

            // update description texts
            if (!StringUtils.isEmpty(loginToken)) {
                updateDescriptionTexts(loginToken, credentialsArray[0]);
            }
        }
    }

    /**
     * Method for retrieving the docker hub credentials from the user home config file.
     *
     * @return credentials array for docker hub
     */
    private static String[] getCredentials() {

        String[] credentialsArray  = null;

        try {

            File dockerCredentialsFile = new File(System.getProperty("user.home") + "/.docker/config.json");

            if (dockerCredentialsFile.exists()) {

                // Logging
                LOG.info("Getting credentials from docker config file '{}'", dockerCredentialsFile.getAbsolutePath());

                // parse json from user home config file
                JSONObject jsonObject = (JSONObject) JSON_PARSER.parse(new FileReader(dockerCredentialsFile));

                // get auth entry for docker io
                JSONObject auths = (JSONObject) jsonObject.get("auths");
                JSONObject dockerIo = (JSONObject) auths.get("docker.io");
                String auth = (String) dockerIo.get("auth");

                // base64 decoding for auth string
                String credentials = new String(Base64.getDecoder().decode(auth));

                // get credentials array
                credentialsArray = credentials.split(":");
            }
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return credentialsArray;
    }

    /**
     * Method for getting the login token from docker hub.
     *
     * @param credentialsArray - The credentials array
     * @return login token from docker hub
     */
    private String getLoginToken(String[] credentialsArray) {

        // specify HTTP headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // specify parameters
        Map<String, Object> params = new HashMap<>();
        params.put(USERNAME_PARAMETER, credentialsArray[0]);
        params.put(PASSWORD_PARAMETER, credentialsArray[1]);

        // create HTTP entity
        HttpEntity entity =  new HttpEntity(params, headers);

        // build login url
        String loginUrl = urlPrefix + loginPath;

        // logging
        LOG.info("Getting login token for username '{}' and login url'{}'", credentialsArray[0], loginUrl);

        // post login credentials to docker hub
        LoginResponse response = new RestTemplate().postForObject(loginUrl, entity, LoginResponse.class);

        // return login token
        return response.getToken();
    }

    /**
     * Method for updating the description texts of a docker image.
     *
     * @param loginToken - The login token
     * @param username - The username
     */
    private void updateDescriptionTexts(String loginToken, String username) {

        try {

            // specify HTTP headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set(HttpHeaders.AUTHORIZATION, TOKEN_PREFIX + loginToken);

            // get description texts from files
            String shortDescription = FileUtils.readFileToString(new File(shortDescriptionFile), FILE_ENCODING);
            String fullDescription = FileUtils.readFileToString(new File(fullDescriptionFile), FILE_ENCODING);

            // specify parameters
            Map<String, Object> params = new HashMap<>();
            params.put(DESCRIPTION_PARAMETER, shortDescription);
            params.put(FULL_DESCRIPTION_PARAMETER, fullDescription);

            // create HTTP entity
            HttpEntity entity = new HttpEntity(params, headers);

            // build image url
            String imageUrl = urlPrefix + "/repositories/" + username + "/" + imageName + "/";

            // logging
            LOG.info("Updating description texts for image url '{}'", imageUrl);

            // create rest template with HttpComponentsClientHttpRequestFactory
            RestTemplate restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());

            // update description texts for docker image
            restTemplate.patchForObject(imageUrl, entity, String.class);
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}