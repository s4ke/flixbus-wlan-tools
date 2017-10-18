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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

	private static final boolean MAC_FIX_PRESENT = true;

	private static final ExecutorService executorService = Executors.newSingleThreadExecutor();
	private static final Logger LOGGER = Logger.getLogger( Main.class.getName() );

	private static int failedWLANLogins = 0;
	private static final int MAX_WLAN_LOGIN_TRIES = 3;

	private static final String WLAN_ADAPTER_ID = "0001";
	private static final String WLAN_ADAPTER_NAME = "Intel(R) Wireless-N 7260";
	private static final String WLAN_SETTINGS_XML = "WLAN-FlixBus.xml";
	private static final String INTERFACE_NAME = "WLAN";
	private static int currentMac = 0x123456;
	private static final int MINIMUM_MAC_SUFFIX = 0x000000;
	private static final int MAXIMUM_MAC_SUFFIX = 0xFFFFFF;
	private static final Set<Integer> usedMacSuffixes = new HashSet<>();
	private static final Random random = new Random();

	public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {
		System.getProperties().put( "org.apache.commons.logging.simplelog.defaultlog", "fatal" );
		outer:
		while ( !Thread.currentThread().isInterrupted() ) {
			LOGGER.info( "Establishing connection to FlixBus WLAN..." );
			//add wlan profile
			runCmd( "netsh wlan add profile filename=\""+ WLAN_SETTINGS_XML +"\" interface=\"" + INTERFACE_NAME + "\"");
			runCmd( "netsh wlan connect name=FlixBus ssid=FlixBus" );
			//establish connection
			while ( !Thread.currentThread().isInterrupted() ) {
				if ( isConnected() ) {
					LOGGER.info(
							"Already authenticated to FlixBus WLAN and connection works \u2714 \n Skipping authentication to FlixBus WLAN" );
					break;
				}
				else {
					System.out.println( "Logging in to Flixbus WLAN..." );
					if ( tryLogin() ) {
						LOGGER.info( "Login to FlixBus WLAN successful \u2714" );
						break;
					}
					else {
						LOGGER.info( "Login to FlixBus WLAN failed..." );
						if ( ++failedWLANLogins > MAX_WLAN_LOGIN_TRIES ) {
							LOGGER.info( "Login to FlixBus WLAN failed too many times... getting a new mac address..." );
							Integer newMacSuffix = -1;
							while ( newMacSuffix == -1 ) {
								newMacSuffix = random.nextInt( (MAXIMUM_MAC_SUFFIX - MINIMUM_MAC_SUFFIX) + 1 ) + MINIMUM_MAC_SUFFIX;
								if ( usedMacSuffixes.contains( newMacSuffix ) ) {
									newMacSuffix = -1;
									continue;
								}
								else {
									usedMacSuffixes.add( newMacSuffix );
								}
							}
							String newMacSuffixStr = Integer.toHexString( newMacSuffix );
							while ( newMacSuffixStr.length() < 6 ) {
								newMacSuffixStr = "0" + newMacSuffixStr;
							}
							String newMac = "02608c" + newMacSuffixStr;
							runCmd( "MacMakeUp.exe set " + WLAN_ADAPTER_ID + " " + newMac );
							runCmd( "devmanview.exe /disable_enable \"" + WLAN_ADAPTER_NAME + "\"" );
							runCmd( "devmanview.exe /disable_enable \"" + WLAN_ADAPTER_NAME + "\"" );
							failedWLANLogins = 0;
							continue outer;
						}
						//FIXME: remove the break as soon as mac changing is added.
						if ( MAC_FIX_PRESENT ) {
							LOGGER.info( "Trying again soon(ish)..." );
							Thread.sleep( 5000 );
						}
						else {
							break;
						}
					}
				}
			}
			//check if the connection is alive
			while ( !Thread.currentThread().isInterrupted() ) {
				LOGGER.info( "Checking connection..." );
				if ( isConnected() ) {
					LOGGER.info( "Connection is okay \u2714" );
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
		}
	}

	public static void runCmd(String cmd) throws IOException, ExecutionException, InterruptedException {
		LOGGER.info( "running cmd: " + cmd );

		final Process p = Runtime.getRuntime().exec( cmd );
		executorService.submit( () -> {
			BufferedReader input = new BufferedReader( new InputStreamReader( p.getInputStream() ) );
			String line = null;

			try {
				while ( (line = input.readLine()) != null ) {
					LOGGER.info( line );
				}
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		} ).get();
	}

	public static boolean isConnected() {
		try (final WebClient webClient = new WebClient( BrowserVersion.CHROME )) {
			webClient.getOptions().setCssEnabled( false );
			webClient.getOptions().setUseInsecureSSL( true );
			webClient.getOptions().setThrowExceptionOnScriptError( false );
			HtmlPage page = webClient.getPage( "http://78.47.27.135/flixbus.html" );
			return page.getElementById( "1" ) != null;
		}
		catch (MalformedURLException e) {
			throw new AssertionError( e );
		}
		catch (IOException e) {
			LOGGER.log( Level.SEVERE, "IOException in isConnected...", e );
			return false;
		}
	}

	public static boolean tryLogin() throws InterruptedException {
		try (final WebClient webClient = new WebClient( BrowserVersion.CHROME )) {
			webClient.getOptions().setCssEnabled( false );
			webClient.getOptions().setUseInsecureSSL( true );
			webClient.getOptions().setThrowExceptionOnScriptError( false );
			final HtmlPage page = webClient.getPage( "https://go.microsoft.com/fwlink/" );
			HtmlPage page2 = page.getElementById( "aup_agree" ).click();
			//FCK Java Generics
			HtmlPage page3 = (HtmlPage) page2.getElementsByTagName( "input" )
					.stream()
					.filter( elem -> elem.getAttribute( "type" ).equals( "submit" ) )
					.findFirst()
					.map( wrapExceptionFunction( (CheckedFunction<? super DomElement, HtmlPage>) DomElement::click ) )
					.orElseThrow( () -> new RuntimeException(
							"clicking the submit button failed!" ) );
			if ( page3.getUrl().toString().equals( "https://www.flixbus.de/wlan-willkommen" ) ) {
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
