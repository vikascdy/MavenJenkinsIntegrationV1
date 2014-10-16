// -----------------------------------------------------------------------------
//  Copyright (c) Edifecs Inc. All Rights Reserved.
//
// This software is the confidential and proprietary information of Edifecs Inc.
// ("Confidential Information").  You shall not disclose such Confidential
// Information and shall use it only in accordance with the terms of the license
// agreement you entered into with Edifecs.
//
// EDIFECS MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
// SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
// WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
// NON-INFRINGEMENT. EDIFECS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
// LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR
// ITS DERIVATIVES.
// -----------------------------------------------------------------------------
package com.edifecs.epp.isc;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import com.edifecs.epp.isc.async.MessageFuture;
import com.edifecs.epp.isc.command.CommandSource;
import com.edifecs.epp.isc.exception.MessageException;
import com.edifecs.epp.isc.exception.RegistryUpdateException;
import com.edifecs.epp.security.remote.SecurityManager;
import com.edifecs.servicemanager.metric.api.IMetric;
import com.typesafe.config.Config;

/**
 * Interface representing the connection to a cluster.
 * <p/>
 * *This has been depreciated in exchange for the Isc API*
 * 
 * @author willclem
 */
public interface ICommandCommunicator {

    /**
     * Sends a synchronous command message, with no arguments, and returns the
     * response.
     * <p/>
     * fgetzz   * @param destination
     * The address of the Agent, Node, or Service to send the command
     * to.
     *
     * @param command The name of the command to send.
     * @return The value returned from the command.
     * @throws MessageException If an exception is thrown on the receiver's side, it will be
     *                          wrapped in a {@code MessageException}.
     * @throws Exception        If an exception is thrown on the sender's side.
     */
    Serializable sendSyncMessage(Address destination, String command);

    /**
     * Sends a synchronous command message, and returns the response.
     *
     * @param destination The address of the Agent, Node, or Service to send the command
     *                    to.
     * @param command     The name of the command to send.
     * @param args        A map from argument names to argument values.
     * @return The value returned from the command.
     * @throws MessageException If an exception is thrown on the receiver's side, it will be
     *                          wrapped in a {@code MessageException}.
     * @throws Exception        If an exception is thrown on the sender's side.
     */
    Serializable sendSyncMessage(Address destination, String command,
                                 Map<String, ? extends Serializable> args);

    /**
     * Sends a synchronous command message, and returns the response.
     *
     * @deprecated The varargs versions of {@code sendSyncMessage} and
     * {@code sendBroadcastMessage} create code that is difficult to
     * read and potentially ambiguous. Rather than adding arguments
     * directly to the end of the method call, wrap the arguments in
     * an {@link Args} object and use
     * {@link #sendSyncMessage(Address, String, Map)}.
     */
    @Deprecated
    Serializable sendSyncMessage(Address destination, String command,
                                 Serializable... args);

    /**
     * Sends a synchronous command message with streaming data, and returns the
     * response.
     *
     * @param destination   The address of the Agent, Node, or Service to send the command
     *                      to.
     * @param command       The name of the command to send.
     * @param streamArgName The name of the argument that {@code stream} should be passed
     *                      as.
     * @param stream        A stream of data to send as an argument to the command.
     * @param args          A map from argument names to argument values.
     * @return The value returned from the command.
     * @throws MessageException If an exception is thrown on the receiver's side, it will be
     *                          wrapped in a {@code MessageException}.
     * @throws Exception        If an exception is thrown on the sender's side.
     */
    Serializable sendSyncMessage(Address destination, String command,
                                 String streamArgName, InputStream stream,
                                 Map<String, ? extends Serializable> args);

    /**
     * Sends a synchronous command message with streaming data, and returns the
     * response.
     *
     * @deprecated The varargs versions of {@code sendSyncMessage} and
     * {@code sendBroadcastMessage} create code that is difficult to
     * read and potentially ambiguous. Rather than adding arguments
     * directly to the end of the method call, wrap the arguments in
     * an {@link Args} object and use
     * {@link #sendSyncMessage(Address, String, String, InputStream, Map)}
     * .
     */
    @Deprecated
    Serializable sendSyncMessage(Address destination, String command,
                                 String arg, InputStream inputStream, Serializable... args)
           ;

    /**
     * Sends a synchronous command message, with no arguments, to multiple
     * receivers, and returns an aggregation of the responses once all receivers
     * have responded or timed out.
     *
     * @param destinations The addresses of the Agents, Nodes, and/or Services to send
     *                     the command to.
     * @param command      The name of the command to send.
     * @return A {@link MessageResponse} containing the responses or exceptions
     * returned by each receiver.
     * @throws Exception If an exception is thrown on the sender's side. Receiver-side
     *                   {@link MessageException}s will not be thrown; they will be
     *                   stored in the {@link MessageResponse}.
     */
    MessageResponse sendSyncMessage(Collection<Address> destinations,
                                    String command);

    /**
     * Sends a synchronous command message to multiple receivers, and returns an
     * aggregation of the responses once all receivers have responded or timed
     * out.
     *
     * @param destinations The addresses of the Agents, Nodes, and/or Services to send
     *                     the command to.
     * @param command      The name of the command to send.
     * @param args         A map from argument names to argument values.
     * @return A {@link MessageResponse} containing the responses or exceptions
     * returned by each receiver.
     * @throws Exception If an exception is thrown on the sender's side. Receiver-side
     *                   {@link MessageException}s will not be thrown; they will be
     *                   stored in the {@link MessageResponse}.
     */
    MessageResponse sendSyncMessage(Collection<Address> destinations,
                                    String command, Map<String, ? extends Serializable> args);

    /**
     * Sends a synchronous command message to multiple receivers, and returns an
     * aggregation of the responses once all receivers have responded or timed
     * out.
     *
     * @deprecated The varargs versions of {@code sendSyncMessage} and
     * {@code sendBroadcastMessage} create code that is difficult to
     * read and potentially ambiguous. Rather than adding arguments
     * directly to the end of the method call, wrap the arguments in
     * an {@link Args} object and use
     * {@link #sendSyncMessage(Collection, String, Map)}.
     */
    @Deprecated
    MessageResponse sendSyncMessage(Collection<Address> destinations,
                                    String command, Serializable... args);

    MessageFuture<Serializable> send(
            Address destination,
            String command
    );

    MessageFuture<Serializable> send(
            Address destination,
            String command,
            java.util.Map<String, ? extends Serializable> arguments
    );

    MessageFuture<Serializable> send(
            Address destination,
            String command,
            scala.collection.Map<String, ? extends Serializable> arguments
    );

    MessageFuture<Serializable> send(
            Address destination,
            String command,
            java.util.Map<String, ? extends Serializable> arguments,
            CommandSource source
    );

    MessageFuture<Serializable> send(
            Address destination,
            String command,
            scala.collection.Map<String, ? extends Serializable> arguments,
            CommandSource source
    );

    MessageResponse sendBroadcastMessage(String command) throws Exception;

    MessageResponse sendBroadcastMessage(String command,
                                         Map<String, ? extends Serializable> args) throws Exception;

    @Deprecated
    MessageResponse sendBroadcastMessage(String command, Serializable... args) throws Exception;

    /**
     * The current connections Address.
     *
     * @return Address to the current connection.
     */
    Address getAddress();

    SecurityManager getSecurityManager();

    IMetric getMetric();

    Config getConfig();

    void requestServiceRegistryUpdate() throws RegistryUpdateException;

    <T> T getService(Class<T> serviceClass);

    /**
     * Checks to see if the instance of the CommandCommunicator is connected to the cluster or not.
     *
     * @return
     */
    boolean isConnected();

    void disconnect();

    IAddressRegistry getAddressRegistry();

}

