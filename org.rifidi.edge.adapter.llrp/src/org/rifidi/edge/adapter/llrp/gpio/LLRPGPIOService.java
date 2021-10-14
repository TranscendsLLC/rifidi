/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.adapter.llrp.gpio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.llrp.ltk.generated.enumerations.GetReaderConfigRequestedData;
import org.llrp.ltk.generated.messages.GET_READER_CONFIG;
import org.llrp.ltk.generated.messages.GET_READER_CONFIG_RESPONSE;
import org.llrp.ltk.generated.messages.SET_READER_CONFIG;
import org.llrp.ltk.generated.parameters.GPIPortCurrentState;
import org.llrp.ltk.generated.parameters.GPOWriteData;
import org.llrp.ltk.types.Bit;
import org.llrp.ltk.types.UnsignedShort;
import org.rifidi.edge.adapter.llrp.LLRPReaderSession;
import org.rifidi.edge.sensors.AbstractGPIOService;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.CannotExecuteException;
import org.rifidi.edge.sensors.SensorSession;

/**
 * @author matt
 * 
 */
public class LLRPGPIOService extends AbstractGPIOService<LLRPReaderSession> {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.AbstractGPIOService#flashGPO(java.lang.String,
	 * int, java.util.Set)
	 */
	@Override
	public void flashGPO(String readerID, int flashTime, Set<Integer> ports) throws CannotExecuteException {
		LLRPReaderSession session = super.getSession(readerID);
		LLRPGPOFlashThread flashrun = new LLRPGPOFlashThread(session, flashTime, ports);
		Thread flashthread = new Thread(flashrun);
		flashthread.start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.AbstractGPIOService#setGPO(java.lang.String,
	 * java.util.Collection)
	 */
	@Override
	public void setGPO(String readerID, Collection<Integer> ports) throws CannotExecuteException {
		LLRPReaderSession session = super.getSession(readerID);

		GET_READER_CONFIG grc = new GET_READER_CONFIG();
		grc.setGPOPortNum(new UnsignedShort(0));
		grc.setAntennaID(new UnsignedShort(0));
		GetReaderConfigRequestedData requestedData = new GetReaderConfigRequestedData();
		grc.setRequestedData(requestedData);
		grc.setGPIPortNum(new UnsignedShort(0));
		GET_READER_CONFIG_RESPONSE grcr = null;
		try {
			grcr = (GET_READER_CONFIG_RESPONSE) session.transact(grc);
		} catch (Exception e) {
			throw new CannotExecuteException("Exception during transact while trying to " + "obtain GPO information");
		}
		int num_ports = grcr.getGPOWriteDataList().size();

		List<GPOWriteData> writedata = new ArrayList<GPOWriteData>();
		for (int i = 1; i <= num_ports; i++) {
			Bit setbit;
			if (ports.contains(new Integer(i))) {
				setbit = new Bit(1);
			} else {
				setbit = new Bit(0);
			}
			GPOWriteData data = new GPOWriteData();
			data.setGPOData(setbit);
			data.setGPOPortNumber(new UnsignedShort(i));
			writedata.add(data);
		}

		SET_READER_CONFIG src = new SET_READER_CONFIG();
		src.setGPOWriteDataList(writedata);
		src.setResetToFactoryDefault(new Bit(0));

		session.send(src);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.AbstractGPIOService#testGPI(java.lang.String,
	 * int)
	 */
	@Override
	public boolean testGPI(String readerID, int port) throws CannotExecuteException {
		LLRPReaderSession session = super.getSession(readerID);

		GET_READER_CONFIG grc = new GET_READER_CONFIG();
		grc.setGPOPortNum(new UnsignedShort(0));
		grc.setAntennaID(new UnsignedShort(0));
		GetReaderConfigRequestedData requestedData = new GetReaderConfigRequestedData();
		grc.setRequestedData(requestedData);
		grc.setGPIPortNum(new UnsignedShort(0));
		GET_READER_CONFIG_RESPONSE grcr = null;
		try {
			grcr = (GET_READER_CONFIG_RESPONSE) session.transact(grc);
		} catch (Exception e) {
			throw new CannotExecuteException("Exception during transact while trying to " + "obtain GPI information");
		}
		List<GPIPortCurrentState> gpilist = grcr.getGPIPortCurrentStateList();

		for (GPIPortCurrentState gpi : gpilist) {
			if (gpi.getGPIPortNum().intValue() == port) {
				if (gpi.getState().intValue() == 1) {
					return true;
				} else {
					return false;
				}
			}
		}

		throw new CannotExecuteException("No such port found while searching for GPI value: " + port);
	}

	/*
	 * Thread class for running GPO flash commands
	 */
	private class LLRPGPOFlashThread implements Runnable {
		public LLRPReaderSession session = null;
		public Set<Integer> ports = null;
		public int flashTime;

		/**
		 * The LLRP session.
		 * 
		 * @param session
		 */
		public LLRPGPOFlashThread(LLRPReaderSession session, int flashTime, Set<Integer> ports) {
			this.session = session;
			this.flashTime = flashTime;
			this.ports = ports;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			SET_READER_CONFIG srchigh = new SET_READER_CONFIG();
			List<GPOWriteData> gpowritehigh = new ArrayList<GPOWriteData>();
			for (Integer port : ports) {
				GPOWriteData write = new GPOWriteData();
				write.setGPOData(new Bit(1));
				write.setGPOPortNumber(new UnsignedShort(port));
				gpowritehigh.add(write);
			}
			srchigh.setGPOWriteDataList(gpowritehigh);
			srchigh.setResetToFactoryDefault(new Bit(0));

			session.send(srchigh);

			// Sleeping for the time given for the flash.
			try {
				Thread.sleep(flashTime * 1000);
			} catch (InterruptedException e) {
			}

			SET_READER_CONFIG srclow = new SET_READER_CONFIG();
			List<GPOWriteData> gpowritelow = new ArrayList<GPOWriteData>();
			for (Integer port : ports) {
				GPOWriteData write = new GPOWriteData();
				write.setGPOData(new Bit(0));
				write.setGPOPortNumber(new UnsignedShort(port));
				gpowritelow.add(write);
			}
			srclow.setGPOWriteDataList(gpowritelow);
			srclow.setResetToFactoryDefault(new Bit(0));

			session.send(srclow);
		}

	}

	/*
	 * 
	 */
	@Override
	public boolean isReaderAvailable(String readerID) {
		AbstractSensor<?> sensor = this.readerDAO.getReaderByID(readerID);
		if(sensor == null) {
			return false;
		}
		SensorSession cursession = null;
		for(SensorSession session : sensor.getSensorSessions().values()) {
			cursession = session;
			break;
		}
		if (cursession instanceof LLRPReaderSession) {
			return true;
		}
		return false;
	}
}
