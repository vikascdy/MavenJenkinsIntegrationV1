package com.edifecs.agent.exception;

import org.junit.Test;

public class ExceptionTests {

	@Test( expected = CommandException.class)
	public void commandExceptionTest() throws CommandException {
		throw new CommandException("I want to throw this Error");
	}
	
	@Test( expected = CommandException.class)
	public void commandExceptionTest2() throws CommandException {
		throw new CommandException(new NoClassDefFoundError());
	}
	
	@Test( expected = CommandException.class)
	public void commandExceptionTest3() throws CommandException {
		throw new CommandException("I want to throw this Error", new NoClassDefFoundError());
	}
	
}
