package hello;

import java.util.Arrays;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import storage.StorageProperties;
import storage.StorageService;

@SpringBootApplication(scanBasePackages = "hello, storage")
@EnableScheduling
@EnableAsync
@EnableConfigurationProperties(StorageProperties.class)
public class Application {
	private static final Logger log = LoggerFactory.getLogger(Application.class);

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * This will list the beans that are discovered by Spring.
	 * 
	 * @param ctx
	 * @return
	 */
	@Bean
	public CommandLineRunner initBeanInspector(ApplicationContext ctx) {
		return args -> {
			System.out.println("Let's inspect the beans provided by Spring Boot:");
			String[] beanNames = ctx.getBeanDefinitionNames();
			Arrays.sort(beanNames);
			for (String beanName : beanNames) {
				System.out.println(beanName);
			}
		};
	}

	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.build();
	}

	/**
	 * This will consume a restful service.
	 * 
	 * @param restTemplate
	 * @return
	 * @throws Exception
	 */
	@Bean
	public CommandLineRunner initQuoteService(RestTemplate restTemplate) throws Exception {
		return args -> {
			Quote quote = restTemplate.getForObject("http://gturnquist-quoters.cfapps.io/api/random", Quote.class);
			log.info(quote.toString());
		};
	}

	/**
	 * This will delete all uploaded files and then recreates the file upload
	 * directory.
	 * 
	 * @param storageService
	 * @return
	 */
	@Bean
	CommandLineRunner initFileStorage(StorageService storageService) {
		return args -> {
			storageService.deleteAll();
			storageService.init();
		};
	}

	/**
	 * For asynchronous methods this will limit the number of concurrent threads
	 * to 2 and limit the size of the queue to 500.
	 * 
	 * @return
	 */
	@Bean
	public Executor asyncExecutor() {
		ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(2);
		executor.setMaxPoolSize(2);
		executor.setQueueCapacity(500);
		executor.setThreadNamePrefix("GithubLookup-");
		executor.initialize();
		return executor;
	}
}
