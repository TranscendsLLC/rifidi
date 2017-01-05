package org.rifidi.edge.server.ale.infrastructure;

import java.io.CharArrayWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.NotImplementedException;
import org.rifidi.edge.epcglobal.ale.ECReports;
import org.rifidi.edge.epcglobal.ale.ECSpec;
import org.rifidi.edge.epcglobal.ale.ImplementationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.InvalidURIExceptionResponse;
import org.rifidi.edge.epcglobal.ale.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.ValidationExceptionResponse;
import org.rifidi.edge.utils.RifidiHelper;
import org.rifidi.edge.utils.SerializerUtil;

import rx.Observer;

public abstract class Notifier implements Observer<ECReports> {

	public abstract void init(String uri, RifidiHelper rifidiHelper) throws InvalidURIExceptionResponse;

	public abstract void notifySubscriber(ECReports reports) throws ImplementationExceptionResponse;

	// public abstract void notifyExceptionToSubscriber(ECReports t,
	// ImplementationExceptionResponse e);
	public abstract void notifyExceptionToSubscriber(ImplementationExceptionResponse e);

	public abstract String getURIfromFields();

	private String notifierId;

	private RifidiHelper rifidiHelper;

	public String getNotifierId() {
		return notifierId;
	}

	public void setNotifierId(String notifierId) {
		this.notifierId = notifierId.replace(':', '_').replace('/', '_');
	}

	public RifidiHelper getRifidiHelper() {
		return rifidiHelper;
	}

	public void setRifidiHelper(RifidiHelper rifidiHelper) {
		this.rifidiHelper = rifidiHelper;
	}

	/**
	 * This method serializes ec reports into a xml representation.
	 * 
	 * @param reports
	 *            the report to be serialized.
	 * @return xml representation of the ec reports
	 * @throws ImplementationException
	 *             if a implementation exception occurs
	 */
	public String getXml(ECReports reports) throws ImplementationExceptionResponse {

		CharArrayWriter writer = new CharArrayWriter();
		try {
			SerializerUtil.serializeECReports(reports, writer);
		} catch (Exception e) {
			// LOG.debug("could not serialize the reports", e);
			throw new ImplementationExceptionResponse("Unable to serialize reports.", e);
		}
		return writer.toString();

	}

	public String getXml(ImplementationExceptionResponse ex) throws ImplementationExceptionResponse {

		CharArrayWriter writer = new CharArrayWriter();
		try {
			SerializerUtil.serializeImplementationExceptionResponse(ex, writer);
		} catch (Exception e) {
			// LOG.debug("could not serialize the reports", e);
			throw new ImplementationExceptionResponse("Unable to serialize ImplementationExceptionResponse.", e);
		}
		return writer.toString();

	}

	@Override
	public void onCompleted() {
		// TODO Auto-generated method stub
		throw new NotImplementedException("");

	}

	@Override
	public void onError(Throwable e) {
		// TODO Auto-generated method stub
		throw new NotImplementedException("");

	}

	@Override
	public void onNext(ECReports t) {
		// TODO Auto-generated method stub
		try {
			
			notifySubscriber(t);
		} catch (ImplementationExceptionResponse e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
		
}
