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

package com.edifecs.helloexample.handler;

import com.edifecs.epp.isc.async.MessageFuture;
import com.edifecs.epp.isc.core.command.AbstractCommandHandler;
import com.edifecs.helloexample.api.HelloMessage;

public class HelloExampleHandler extends AbstractCommandHandler implements IHelloExampleHandler {
	
	private String greetee = "";
	
	public HelloExampleHandler(String greetee) {
		super();
		this.greetee = greetee;
	}
	
	@Override
	public HelloMessage greeting() {
		HelloMessage helloMessage = new HelloMessage();
		helloMessage.setMessage("Hello " + this.greetee + "!");
		return helloMessage;
	}

    @Override
    public MessageFuture<HelloMessage> greetingFromTheFuture() {
        return MessageFuture.of(greeting());
    }


}
