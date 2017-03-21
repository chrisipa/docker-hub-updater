package de.papke.docker.hub.updater;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * Main class of application.
 *
 * @author Christoph Papke (info@papke.it)
 */
@SpringBootApplication
public class Main {

    /**
     * Main method.
     *
     * @param args
     */
    public static void main(String[] args) throws Exception {
        SpringApplication.run(Main.class, args); //NOSONAR
    }
}