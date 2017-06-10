/*
 The MIT License (MIT)

 Copyright (c) 2016 Martin Braun

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
package com.github.s4ke.flixbus.quickconnect;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author Martin Braun
 */
public class Main {

	private static final boolean MAC_FIX_PRESENT = false;

	private static final Logger LOGGER = Logger.getLogger( Main.class.getName() );

	public static void main(String[] args) throws InterruptedException {
		System.getProperties().put( "org.apache.commons.logging.simplelog.defaultlog", "fatal" );
		while ( true ) {
			//establish connection
			while ( true ) {
				LOGGER.info( "Establishing connection..." );
				if ( tryLogin() ) {
					LOGGER.info( "Login to FlixBus WLAN successful..." );
					break;
				}
				else {
					//FIXME: remove the break as soon as mac changing is added.
					if ( MAC_FIX_PRESENT ) {
						LOGGER.info( "Trying again soon(ish)..." );
						Thread.sleep( 10000 );
					}
					else {
						break;
					}
					LOGGER.info( "Login to FlixBus WLAN failed..." );
				}
			}
			//check if the connection is alive
			while ( true ) {
				LOGGER.info( "Checking connection..." );
				if ( isConnected() ) {
					LOGGER.info( "Connection is okay..." );
					//FIXME: remove the break as soon as mac changing is added.
					if ( MAC_FIX_PRESENT ) {
						Thread.sleep( 10000 );
					}
					else {
						break;
					}
				}
				else {
					LOGGER.info( "Lost connection..." );
					break;
				}
			}
			//TODO: add automatic mac changing
			if ( !MAC_FIX_PRESENT ) {
				break;
			}
		}
	}

	public static boolean isConnected() {
		try (final WebClient webClient = new WebClient(BrowserVersion.CHROME)) {
			HtmlPage page = webClient.getPage( "http://78.47.27.135/flixbus.html" );
			return page.getElementById( "1" ) != null;
		}
		catch (MalformedURLException e) {
			throw new RuntimeException( e );
		}
		catch (IOException e) {
			LOGGER.log( Level.SEVERE, "IOException in tryLogin...", e );
			return false;
		}
	}

	public static boolean tryLogin() throws InterruptedException {
		System.out.println( "Logging in to Flixbus WLAN..." );
		try (final WebClient webClient = new WebClient( BrowserVersion.CHROME)) {
			final HtmlPage page = webClient.getPage( "https://portal.moovmanage.com/flixbus-albus/connect.php" );
			HtmlPage page2 = page.getElementById( "aup_agree" ).click();
			//FCK Java Generics
			HtmlPage page3 = (HtmlPage) page2.getElementsByTagName( "input" )
					.stream()
					.filter( elem -> elem.getAttribute( "type" ).equals( "submit" ) )
					.findFirst()
					.map( wrapExceptionFunction( (CheckedFunction<? super DomElement, HtmlPage>) DomElement::click ) )
					.orElseThrow( () -> new RuntimeException(
							"clicking the submit button failed!" ) );
			if ( page3.getTitleText().trim().equals( "Willkommen im WLAN von FlixBus" ) ) {
				Thread.sleep(3000);
				System.out.println( "Successfully logged in to FlixBus WLAN!" );
				return true;
			}
		}
		catch (MalformedURLException e) {
			throw new RuntimeException( e );
		}
		catch (IOException e) {
			LOGGER.log( Level.SEVERE, "IOException in tryLogin...", e );
			return false;
		}
		return false;
	}

	@FunctionalInterface
	public interface CheckedFunction<T, R> {
		R apply(T t) throws Exception;
	}

	@FunctionalInterface
	public interface CheckedConsumer<T> {
		T apply(T t) throws Exception;
	}

	private static <T> CheckedFunction<? super T, ? super T> toFn(CheckedConsumer<? super T> consumer) {
		return (t) -> {
			consumer.apply( t );
			return t;
		};
	}

	private static <T, R> Function<? super T, ? super R> wrapExceptionFunction(CheckedFunction<? super T, R> consumer) {
		return (t) -> {
			try {
				return consumer.apply( t );
			}
			catch (Exception e) {
				throw new RuntimeException( e );
			}
		};
	}

	private static <T> Consumer<? super T> wrapExceptionConsumer(CheckedConsumer<? super T> consumer) {
		return (t) -> {
			try {
				consumer.apply( t );
			}
			catch (Exception e) {
				throw new RuntimeException( e );
			}
		};
	}

}
