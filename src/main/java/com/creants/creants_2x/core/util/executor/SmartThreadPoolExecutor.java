package com.creants.creants_2x.core.util.executor;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.creants.creants_2x.core.util.QAntTracer;

/**
 * @author LamHM
 *
 */
public class SmartThreadPoolExecutor extends ThreadPoolExecutor {
	private static final long ALERT_INTERVAL = 30000L;
	private final SmartExecutorConfig cfg;
	private final int maxThreads;
	private final int backupThreadsExpirySeconds;
	private volatile long lastQueueCheckTime;
	private volatile long lastBackupTime;
	private volatile boolean threadShutDownNotified;
	private long lastAlertTime;


	public SmartThreadPoolExecutor(final SmartExecutorConfig config) {
		super(config.coreThreads, Integer.MAX_VALUE, 0L, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
				new QAntThreadFactory(config.name));
		threadShutDownNotified = false;
		cfg = config;
		maxThreads = cfg.coreThreads + cfg.backupThreads * cfg.maxBackups;
		backupThreadsExpirySeconds = cfg.backupThreadsExpiry * 1000;
		lastQueueCheckTime = -1L;
	}


	@Override
	public void execute(Runnable command) {
		if (getPoolSize() >= cfg.coreThreads) {
			boolean needsBackup = checkQueueWarningLevel();
			if (needsBackup) {
				if (getPoolSize() >= maxThreads) {
					alertLowThreads();
				} else {
					setCorePoolSize(getPoolSize() + cfg.backupThreads);
					long currentTimeMillis = System.currentTimeMillis();
					lastQueueCheckTime = currentTimeMillis;
					lastBackupTime = currentTimeMillis;
					threadShutDownNotified = false;
					QAntTracer.info(SmartThreadPoolExecutor.class, String
							.format("Added %s new threads, current size is: %s", cfg.backupThreads, getPoolSize()));
				}
			} else if (getPoolSize() > cfg.coreThreads) {
				boolean isTimeToShutDownBackupThreads = System.currentTimeMillis()
						- lastBackupTime > backupThreadsExpirySeconds;
				boolean isQueueSizeSmallEnough = getQueue().size() < cfg.queueSizeTriggeringBackupExpiry;
				if (isTimeToShutDownBackupThreads && isQueueSizeSmallEnough && !threadShutDownNotified) {
					setCorePoolSize(cfg.coreThreads);
					threadShutDownNotified = true;
					QAntTracer.info(this.getClass(), "Shutting down old backup threads");
				}
			}
		}
		super.execute(command);
	}


	private boolean checkQueueWarningLevel() {
		boolean needsBackup = false;
		boolean queueIsBusy = getQueue().size() >= cfg.queueSizeTriggeringBackup;
		long now = System.currentTimeMillis();
		if (lastQueueCheckTime < 0L) {
			lastQueueCheckTime = now;
		}

		if (queueIsBusy) {
			if (now - lastQueueCheckTime > cfg.secondsTriggeringBackup * 1000) {
				needsBackup = true;
			}
		} else {
			lastQueueCheckTime = now;
		}

		return needsBackup;
	}


	private void alertLowThreads() {
		long now = System.currentTimeMillis();
		if (now > lastAlertTime + ALERT_INTERVAL) {
			QAntTracer.warn(this.getClass(),
					String.format("%s :: Queue size is big: %s, but all backup thread are already active: %s", cfg.name,
							getQueue().size(), getPoolSize()));

			lastAlertTime = now;
		}
	}


	public int getCoreThreads() {
		return cfg.coreThreads;
	}


	public int getBackupThreads() {
		return cfg.backupThreads;
	}


	public int getMaxBackups() {
		return cfg.maxBackups;
	}


	public int getQueueSizeTriggeringBackup() {
		return cfg.queueSizeTriggeringBackup;
	}


	public int getSecondsTriggeringBackup() {
		return cfg.secondsTriggeringBackup;
	}


	public int getBackupThreadsExpiry() {
		return cfg.backupThreadsExpiry;
	}


	public int getQueueSizeTriggeringBackupExpiry() {
		return cfg.queueSizeTriggeringBackupExpiry;
	}

	private static final class QAntThreadFactory implements ThreadFactory {
		private final static AtomicInteger POOL_ID;
		private static final String THREAD_BASE_NAME = "QAntWorker:%s:%s";
		private final AtomicInteger threadId;
		private final String poolName;

		static {
			POOL_ID = new AtomicInteger(0);
		}


		public QAntThreadFactory(String poolName) {
			this.threadId = new AtomicInteger(1);
			this.poolName = poolName;
			QAntThreadFactory.POOL_ID.incrementAndGet();
		}


		@Override
		public Thread newThread(Runnable r) {
			final Thread t = new Thread(r,
					String.format(THREAD_BASE_NAME,
							(this.poolName != null) ? this.poolName : QAntThreadFactory.POOL_ID.get(),
							this.threadId.getAndIncrement()));

			if (t.isDaemon()) {
				t.setDaemon(false);
			}

			if (t.getPriority() != 5) {
				t.setPriority(5);
			}

			return t;
		}
	}
}
