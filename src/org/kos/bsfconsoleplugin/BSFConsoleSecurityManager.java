/*
 * SupportWizard 2
 *
 * Copyright (C) 2003 ISC corp. All Rights Reserved.
 *
 * $Id$
 * Created by Konstantin Sobolev (k_o_s@mail.ru) on 14.02.2004
 * Last modification $Date$
 */
package org.kos.bsfconsoleplugin;

import java.io.FileDescriptor;
import java.net.InetAddress;
import java.security.Permission;

/**
 * Security manager that disallows <code>System.exit()</code>.
 * 
 * @author <a href="mailto:k_o_s@mail.ru" title="">Konstantin Sobolev</a>
 * @version $Revision$
 */
public class BSFConsoleSecurityManager extends SecurityManager {
	private final SecurityManager securityManager;

	public BSFConsoleSecurityManager(final SecurityManager securityManager) {
		this.securityManager = securityManager;
	}

	@Override
	public void checkAwtEventQueueAccess() {
		if (securityManager != null) securityManager.checkAwtEventQueueAccess();
	}

	@Override
	public void checkCreateClassLoader() {
		if (securityManager != null) securityManager.checkCreateClassLoader();
	}

	@Override
	public void checkPrintJobAccess() {
		if (securityManager != null) securityManager.checkPrintJobAccess();
	}

	@Override
	public void checkPropertiesAccess() {
		if (securityManager != null) securityManager.checkPropertiesAccess();
	}

	@Override
	public void checkSetFactory() {
		if (securityManager != null) securityManager.checkSetFactory();
	}

	@Override
	public void checkSystemClipboardAccess() {
		if (securityManager != null) securityManager.checkSystemClipboardAccess();
	}

	@Override
	public void checkExit(final int status) {
		super.checkExit(status);
		if (Thread.currentThread().getName().startsWith("interpreter"))
			throw new SecurityException();
		if (securityManager != null) securityManager.checkExit(status);
	}

	@Override
	public void checkListen(final int port) {
		if (securityManager != null) securityManager.checkListen(port);
	}

	@Override
	public void checkRead(final FileDescriptor fd) {
		if (securityManager != null) securityManager.checkRead(fd);
	}

	@Override
	public void checkWrite(final FileDescriptor fd) {
		if (securityManager != null) securityManager.checkWrite(fd);
	}

	@Override
	public void checkMemberAccess(final Class clazz, final int which) {
		if (securityManager != null) securityManager.checkMemberAccess(clazz, which);
	}

	@Override
	public Object getSecurityContext() {
		if (securityManager != null) return securityManager.getSecurityContext();
		return super.getSecurityContext();
	}

	@Override
	public boolean checkTopLevelWindow(final Object window) {
		if (securityManager != null) return securityManager.checkTopLevelWindow(window);
		return super.checkTopLevelWindow(window);
	}

	@Override
	public void checkDelete(final String file) {
		if (securityManager != null) securityManager.checkDelete(file);
	}

	@Override
	public void checkExec(final String cmd) {
		if (securityManager != null) securityManager.checkExec(cmd);
	}

	@Override
	public void checkLink(final String lib) {
		if (securityManager != null) securityManager.checkLink(lib);
	}

	@Override
	public void checkPackageAccess(final String pkg) {
		if (securityManager != null) securityManager.checkPackageAccess(pkg);
	}

	@Override
	public void checkPackageDefinition(final String pkg) {
		if (securityManager != null) securityManager.checkPackageDefinition(pkg);
	}

	@Override
	public void checkPropertyAccess(final String key) {
		if (securityManager != null) securityManager.checkPropertyAccess(key);
	}

	@Override
	public void checkRead(final String file) {
		if (securityManager != null) securityManager.checkRead(file);
	}

	@Override
	public void checkSecurityAccess(final String target) {
		if (securityManager != null) securityManager.checkSecurityAccess(target);
	}

	@Override
	public void checkWrite(final String file) {
		if (securityManager != null) securityManager.checkWrite(file);
	}

	@Override
	public void checkAccept(final String host, final int port) {
		if (securityManager != null) securityManager.checkAccept(host, port);
	}

	@Override
	public void checkConnect(final String host, final int port) {
		if (securityManager != null) securityManager.checkConnect(host, port);
	}

	@Override
	public void checkAccess(final Thread t) {
		//it's still possible to kill us with Thread.currentThread().interrupt()
		//but I don't know how to restrict thread access to this kind of operations only
		if (securityManager != null) securityManager.checkAccess(t);
	}

	@Override
	public ThreadGroup getThreadGroup() {
		if (securityManager != null) return securityManager.getThreadGroup();
		return super.getThreadGroup();
	}

	@Override
	public void checkAccess(final ThreadGroup g) {
		if (securityManager != null) securityManager.checkAccess(g);
	}

	@Override
	public void checkMulticast(final InetAddress maddr) {
		if (securityManager != null) securityManager.checkMulticast(maddr);
	}

	@Override
	public void checkPermission(final Permission perm) {
		if (securityManager != null) securityManager.checkPermission(perm);
	}

	@Override
	public void checkConnect(final String host, final int port, final Object context) {
		if (securityManager != null) securityManager.checkConnect(host, port, context);
	}

	@Override
	public void checkRead(final String file, final Object context) {
		if (securityManager != null) securityManager.checkRead(file, context);
	}

	@Override
	public void checkPermission(final Permission perm, final Object context) {
		if (securityManager != null) securityManager.checkPermission(perm, context);
	}
}