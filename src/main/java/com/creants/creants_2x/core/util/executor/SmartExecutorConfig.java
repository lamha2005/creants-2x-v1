package com.creants.creants_2x.core.util.executor;

import java.io.Serializable;

/**
 * @author LamHM
 *
 */
public class SmartExecutorConfig implements Serializable {
	private static final long serialVersionUID = 503536543992946473L;
	public String name;
	public int coreThreads;
	public int backupThreads;
	public int maxBackups;
	public int queueSizeTriggeringBackup;
	public int secondsTriggeringBackup;
	public int backupThreadsExpiry;
	public int queueSizeTriggeringBackupExpiry;
	public boolean logActivity;
	public int queueFullWarningInterval;


	public SmartExecutorConfig() {
		this.name = null;
		this.coreThreads = 16;
		this.backupThreads = 8;
		this.maxBackups = 2;
		this.queueSizeTriggeringBackup = 500;
		this.secondsTriggeringBackup = 60;
		this.backupThreadsExpiry = 3600;
		this.queueSizeTriggeringBackupExpiry = 300;
		this.logActivity = true;
		this.queueFullWarningInterval = 300;
	}
}
