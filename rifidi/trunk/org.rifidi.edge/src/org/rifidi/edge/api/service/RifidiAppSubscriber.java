/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
package org.rifidi.edge.api.service;

/**
 * This is the base interface for RifidiApplicationSubscribers to implement.
 * Concrete implementations should be careful when overiding the equals method
 * since this method is used to determine if a subscriber is already subscribed
 * or not.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface RifidiAppSubscriber {

}
