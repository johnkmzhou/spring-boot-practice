package hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.concurrent.CompletableFuture;

@Component
public class AppRunner implements CommandLineRunner {
	private static final Logger logger = LoggerFactory.getLogger(AppRunner.class);

	private final GitHubLookupService gitHubLookupService;
	private final BookingService bookingService;

	public AppRunner(GitHubLookupService gitHubLookupService, BookingService bookingService) {
		this.gitHubLookupService = gitHubLookupService;
		this.bookingService = bookingService;
	}

	@Override
	public void run(String... args) throws Exception {
		// Start the clock
		long start = System.currentTimeMillis();

		// Kick of multiple, asynchronous lookups
		CompletableFuture<User> page1 = gitHubLookupService.findUser("PivotalSoftware");
		CompletableFuture<User> page2 = gitHubLookupService.findUser("CloudFoundry");
		CompletableFuture<User> page3 = gitHubLookupService.findUser("Spring-Projects");

		// Wait until they are all done
		CompletableFuture.allOf(page1, page2, page3).join();

		// Print results, including elapsed time
		logger.info("Elapsed time: " + (System.currentTimeMillis() - start));
		logger.info("--> " + page1.get());
		logger.info("--> " + page2.get());
		logger.info("--> " + page3.get());

		// Starts the booking service
		// names cannot be longer than five characters nor null
		bookingService.book("Alice", "Bob", "Carol");
		Assert.isTrue(bookingService.findAllBookings().size() == 3, "First booking should work with no problem.");
		logger.info("Alice, Bob and Carol have been booked");
		try {
			bookingService.book("Chris", "Samuel");
		} catch (RuntimeException e) {
			logger.error("'Samuel' is too big for the DB", e);
		}
		Assert.isTrue(bookingService.findAllBookings().size() == 3, "'Samuel' should have triggered a rollback.");

		logBooked();

		try {
			bookingService.book("Buddy", null);
		} catch (RuntimeException e) {
			logger.error("Null is not valid for the name", e);
		}
		Assert.isTrue(bookingService.findAllBookings().size() == 3, "'null' should have triggered a rollback");

		logBooked();
	}

	private void logBooked() {
		String message = "";
		for (String person : bookingService.findAllBookings()) {
			message += person + ", ";
		}
		message = message.substring(0, message.length() - 2) + " have been booked";
		logger.info(message);
	}

}