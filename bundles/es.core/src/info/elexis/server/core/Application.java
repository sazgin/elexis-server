package info.elexis.server.core;

import java.io.File;
import java.util.Date;
import java.util.Set;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import info.elexis.server.core.common.util.CoreUtil;
import info.elexis.server.core.contrib.ApplicationShutdownRegistrar;
import info.elexis.server.core.contrib.IApplicationShutdownListener;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	private static Logger log = LoggerFactory.getLogger(Application.class);

	private static boolean restart;
	private static boolean shutdown;
	private static boolean force;

	private static final Date startTime = new Date();

	
	@Override
	public Object start(IApplicationContext context) throws Exception {
		log.info("Starting " + Application.class.getName() + "...");

		TimeZone tzone = TimeZone.getTimeZone("CET");
		TimeZone.setDefault(tzone);

		final File singleInstanceLockFile = new File(CoreUtil.getHomeDirectory().toString(), "elexis-server.lock");
		if (!singleInstanceLockFile.createNewFile()) {
			log.error("Found existing lock-file {}, shutting down.", singleInstanceLockFile.toString());
			return IApplication.EXIT_OK;
		}
		singleInstanceLockFile.deleteOnExit();
		
		/**
		 * In case the JVM shuts down, we want the lock file to be removed.
		 * This is e.g. the case in a docker environment
		 */
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				log.warn("Shutdown-Hook removing lock file [{}]", singleInstanceLockFile);
				boolean delete = singleInstanceLockFile.delete();
				if (!delete) {
					log.error("Delete failed.");
				}
			}
		});

		context.applicationRunning();
		while (!restart && !shutdown) {
			Thread.sleep(2000);
			if (restart || shutdown) {
				checkVeto();
			}
		}

		if (restart) {
			log.info("Restarting " + Application.class.getName() + "...");
			// give all services time to shutdown
			Thread.sleep(2000);
			return IApplication.EXIT_RESTART;
		}

		log.info("Stopping " + Application.class.getName() + "...");
		return IApplication.EXIT_OK;
	}

	private static String checkVeto() {
		Set<IApplicationShutdownListener> shutdownListeners = ApplicationShutdownRegistrar
				.getApplicationShutdownListeners();
		for (IApplicationShutdownListener ias : shutdownListeners) {
			if (force) {
				ias.performShutdown(true);
			} else {
				String reason = ias.performShutdown(false);
				if (reason != null) {
					shutdown = false;
					restart = false;
					log.info("[{}] shutdown/restart veto:  {}", ias.getClass().getName(), reason);
					return "[VETO " + ias.getClass().getName() + "] " + reason;
				}
			}
		}
		return null;
	}

	@Override
	public void stop() {
	}

	/**
	 * Restart the server
	 * 
	 * @param force
	 * @return <code>null</code> if restart initiated, or a veto reason denying
	 *         the request
	 */
	public static String restart(boolean force) {
		String veto = checkVeto();
		if (veto != null && !force) {
			return veto;
		}
		restart = true;
		Application.force = force;
		return null;
	}

	/**
	 * Shutdown the server
	 * 
	 * @param force
	 * @return <code>null</code> if shutdown initiated, or a veto reason denying
	 *         the request
	 */
	public static String shutdown(boolean force) {
		String veto = checkVeto();
		if (veto != null && !force) {
			return veto;
		}
		shutdown = true;
		Application.force = force;
		return null;
	}

	public static Date getStarttime() {
		return startTime;
	}

	public static String getStatus() {
		long millis = new Date().getTime() - Application.getStarttime().getTime();

		long days = TimeUnit.MILLISECONDS.toDays(millis);
		millis -= TimeUnit.DAYS.toMillis(days);
		long hours = TimeUnit.MILLISECONDS.toHours(millis);
		millis -= TimeUnit.HOURS.toMillis(hours);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
		millis -= TimeUnit.MINUTES.toMillis(minutes);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

		return "Uptime: " + String.format("%d days, %d hours, %d min, %d sec", days, hours, minutes, seconds);
	}
}
