/*
	Copyright 2008-2014 ITACA-TSB, http://www.tsb.upv.es
	Instituto Tecnologico de Aplicaciones de Comunicacion
	Avanzadas - Grupo Tecnologias para la Salud y el
	Bienestar (TSB)

	See the NOTICE file distributed with this work for additional
	information regarding copyright ownership

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	  http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
 */
package org.universAAL.context.sysinfo;

import org.universAAL.middleware.container.ModuleContext;
import org.universAAL.middleware.container.SharedObjectListener;
import org.universAAL.middleware.container.utils.LogUtils;
import org.universAAL.middleware.context.ContextEvent;
import org.universAAL.middleware.interfaces.PeerCard;
import org.universAAL.middleware.interfaces.space.SpaceDescriptor;
import org.universAAL.middleware.interfaces.space.SpaceStatus;
import org.universAAL.middleware.managers.api.SpaceListener;
import org.universAAL.middleware.managers.api.SpaceManager;
import org.universAAL.middleware.util.Constants;
import org.universAAL.ontology.sysinfo.SystemInfo;

public class SystemEventsListener implements SharedObjectListener, SpaceListener {

	private ModuleContext context;
	private SpaceManager spaceManager;
	private boolean initialized;
	private SystemInfo sys = new SystemInfo(Constants.MIDDLEWARE_LOCAL_ID_PREFIX + "sysInfoPublisher");

	public SystemEventsListener(ModuleContext context) {
		this.context = context;
		init();

	}

	public boolean init() {
		if (!initialized) {
			logDebug("Initializing the System Info Provider...");
			Object[] managers = context.getContainer().fetchSharedObject(context,
					new Object[] { SpaceManager.class.getName().toString() }, this);
			if (managers != null) {
				spaceManager = (SpaceManager) managers[0];
				spaceManager.addSpaceListener(this);
			} else {
				logDebug("No SpaceManagers found");
				initialized = false;
				return initialized;
			}
			logDebug("System Info Provider initialized");
		} else {
			logDebug("System Info Provider already initialized");
		}
		return initialized;
	}

	public void sharedObjectAdded(Object sharedObj, Object arg1) {
		if (sharedObj instanceof SpaceManager) {
			logDebug("SpaceManager service added detected > updating");
			spaceManager = (SpaceManager) sharedObj;
			spaceManager.addSpaceListener(this);
		}
	}

	public void sharedObjectRemoved(Object sharedObj) {
		if (sharedObj instanceof SpaceManager) {
			logDebug("SpaceManager service removed detected > updating");
			spaceManager = null;
			initialized = false;
		}
	}

	private void logDebug(String msg) {
		if (context != null) {
			LogUtils.logDebug(context, SystemEventsListener.class, "SystemInfoProvider", new Object[] { msg }, null);
		} else {
			System.out.println(msg);
		}
	}

	private void logInfo(String msg) {
		if (context != null) {
			LogUtils.logInfo(context, SystemEventsListener.class, "SystemInfoProvider", new Object[] { msg }, null);
		} else {
			System.out.println(msg);
		}
	}

	public void spaceJoined(SpaceDescriptor spaceDescriptor) {
		logInfo("SPACEJOINED");
		org.universAAL.ontology.sysinfo.SpaceDescriptor des = new org.universAAL.ontology.sysinfo.SpaceDescriptor(
				Constants.MIDDLEWARE_LOCAL_ID_PREFIX + spaceDescriptor.getSpaceCard().getSpaceID());
		des.setSerializedValue(spaceDescriptor.toString());
		sys.setSpaceJoined(des);
		ContextEvent ev = new ContextEvent(sys, SystemInfo.PROP_SPACE_JOINED);
		if (Activator.cpublisher != null)
			Activator.cpublisher.publish(ev);
	}

	public void spaceLost(SpaceDescriptor spaceDescriptor) {
		logInfo("SPACELOST");
		org.universAAL.ontology.sysinfo.SpaceDescriptor des = new org.universAAL.ontology.sysinfo.SpaceDescriptor(
				Constants.MIDDLEWARE_LOCAL_ID_PREFIX + spaceDescriptor.getSpaceCard().getSpaceID());
		des.setSerializedValue(spaceDescriptor.toString());
		sys.setSpaceLost(des);
		ContextEvent ev = new ContextEvent(sys, SystemInfo.PROP_SPACE_LOST);
		if (Activator.cpublisher != null)
			Activator.cpublisher.publish(ev);
	}

	public void peerJoined(PeerCard peer) {
		logInfo("PEERJOINED");
		org.universAAL.ontology.sysinfo.PeerCardDescriptor des = new org.universAAL.ontology.sysinfo.PeerCardDescriptor(
				Constants.MIDDLEWARE_LOCAL_ID_PREFIX + peer.getPeerID());
		des.setSerializedValue(peer.toString());
		sys.setPeerJoined(des);
		ContextEvent ev = new ContextEvent(sys, SystemInfo.PROP_PEER_JOINED);
		if (Activator.cpublisher != null)
			Activator.cpublisher.publish(ev);
	}

	public void peerLost(PeerCard peer) {
		logInfo("PEERLOST");

		org.universAAL.ontology.sysinfo.PeerCardDescriptor des = new org.universAAL.ontology.sysinfo.PeerCardDescriptor(
				Constants.MIDDLEWARE_LOCAL_ID_PREFIX + peer.getPeerID());
		des.setSerializedValue(peer.toString());
		sys.setPeerLost(des);
		ContextEvent ev = new ContextEvent(sys, SystemInfo.PROP_PEER_LOST);
		if (Activator.cpublisher != null)
			Activator.cpublisher.publish(ev);
	}

	public void spaceStatusChanged(SpaceStatus status) {
		logInfo("SPACECHANGED");
		org.universAAL.ontology.sysinfo.SpaceStatusDescriptor des = new org.universAAL.ontology.sysinfo.SpaceStatusDescriptor(
				Constants.MIDDLEWARE_LOCAL_ID_PREFIX + status.toString());
		des.setSerializedValue(status.toString());
		sys.setSpaceChanged(des);
		ContextEvent ev = new ContextEvent(sys, SystemInfo.PROP_SPACE_CHANGED);
		if (Activator.cpublisher != null)
			Activator.cpublisher.publish(ev);
	}

}
