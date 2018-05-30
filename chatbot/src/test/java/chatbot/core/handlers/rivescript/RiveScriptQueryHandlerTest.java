package chatbot.core.handlers.rivescript;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import com.rivescript.RiveScript;

import chatbot.io.incomingrequest.IncomingRequest;
import chatbot.io.incomingrequest.RequestContent;

public class RiveScriptQueryHandlerTest {

	@Autowired
	private  ResourceLoader resourceLoader;

	@PostConstruct
    public void postConstruct() {
		createInitialRequest();
    }
	
	
    private RiveScript createInitialRequest() {
        
    		RiveScript obj = null;
        try {
        		ClassLoader cl = this.getClass().getClassLoader(); 
        		obj = new RiveScript(); 

            // Create a directory to store the rive files which will be used by RiveScript loader
            File temp = new File("rivefiles/");
            temp.delete();
            temp.mkdir();
            
            Resource[] messageResources = new PathMatchingResourcePatternResolver(cl).getResources("classpath*:rivescript/rivefiles/*.rive");
            for (Resource resource: messageResources){
            	
            		File file = new File(temp + "/" + resource.getFilename() +"/");
            		InputStream inputStream = resource.getInputStream();
            		OutputStream outputStream = new FileOutputStream(file);
            		IOUtils.copy(inputStream, outputStream);
            		outputStream.close();

            }
            obj.loadDirectory(temp);
            obj.sortReplies();
            
            // deleting the temp directory post loading the data
            FileUtils.deleteDirectory(temp);
            
        } catch (Exception e) {
 			e.printStackTrace();
        }
        
        return obj;
	}
	
	public IncomingRequest createIncomingRequest(String incomingText) {	
		IncomingRequest request =new IncomingRequest() ;
		RequestContent addingContent = new RequestContent();	
		addingContent.setPayload("text");
		addingContent.setText(incomingText);
		List<RequestContent> messageData = 	new ArrayList<RequestContent>();
		messageData.add(addingContent);
		request.setRequestContent(messageData);
		return request;
	}	
	/*
	public RiveScript createInitialRequest() {
			RiveScript obj = new RiveScript();
			obj.loadDirectory("src/main/resources/rivescript/rivefiles");
			obj.sortReplies();
		return obj;
	}
	*/
	public RiveScriptQueryHandler createInitialObject() {
		RiveScriptQueryHandler queryHandlerObject = new RiveScriptQueryHandler();
		return queryHandlerObject;
	}
	
	/**
	 * Test for checking query pass and fail scenario
	 */
	@Test
	public void testSearchHi() throws IOException {
		IncomingRequest input = createIncomingRequest("Hi");
		RiveScriptQueryHandler queryObject = createInitialObject();
		String actualOutput = queryObject.search(input).getMessageData().get(0).getContent();
		List<String> expectedOutputs = Arrays.asList("Hi, how may I help you?", "Hello there", "Hi to you too!", "Hello, how may I help you?");
		assertNotNull(actualOutput);
		boolean result = expectedOutputs.contains(actualOutput);
		assertTrue(result);			
	}
	
	/**
	 * Testing Day Greetings with Unicode of o
	 */
	@Test
	public void testSearchDayGreetings() throws IOException {
		IncomingRequest input = createIncomingRequest("Go\u006Fd Morning");
		RiveScriptQueryHandler queryObject = createInitialObject();
		String actualOutput = queryObject.search(input).getMessageData().get(0).getContent();
		List<String> expectedOutputs = Arrays.asList("good morning to you too", "good evening to you too", "good night to you too");
		assertNotNull(actualOutput);
		boolean result = expectedOutputs.contains(actualOutput);
		assertTrue(result);			
	}
	
	@Test
	public void testSearch() throws IOException {
		IncomingRequest input = createIncomingRequest("how are you?");
		RiveScriptQueryHandler queryObject = createInitialObject();
		String actualOutput = queryObject.search(input).getMessageData().get(0).getContent();
		List<String> expectedOutputs = Arrays.asList("Never been better", "I'm good, you?", "I am fine thanks for asking", "I'm fine, thanks for asking!", "I'm great, how are you?", "Good :) you?", "Great! You?");
		assertNotNull(actualOutput);
		boolean result = expectedOutputs.contains(actualOutput);
		assertTrue(result);
		}

	@Test
	public void testSearchDbpedia() throws IOException {
		IncomingRequest input = createIncomingRequest("who are you?");
		RiveScriptQueryHandler queryObject = createInitialObject();
		String actualOutput = queryObject.search(input).getMessageData().get(0).getContent();
		String expectedHelloOutput = "I am the DBpedia Bot";
		assertNotNull(actualOutput);
		assertEquals(expectedHelloOutput, actualOutput);		
	}	
	
	@Test
	public void testSearchBye() throws IOException {
		IncomingRequest input = createIncomingRequest("Bye");
		RiveScriptQueryHandler queryObject = createInitialObject();
		String actualOutput = queryObject.search(input).getMessageData().get(0).getContent();
		List<String> expectedOutputs = Arrays.asList("See ya!", "Bye","Good Bye");
		assertNotNull(actualOutput);
		boolean result = expectedOutputs.contains(actualOutput);
		assertTrue(result);			
	}
}
