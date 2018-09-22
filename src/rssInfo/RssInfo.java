/*
 * 
 * 
 * 입력한 rss feed url 로부터 정보를 가져오는 프로그램 입니다. 
 * 1. 해당 프로그램을 실행하기 위해서는 jre가 설치되어 있어야 합니다. 
 * 2. jre 1.8.0_181 이 설치되어져 있는 환경에서 만들어진 프로그램입니다. 
 * 3. jre가 설치되어 있지 않은 환경이라면  
 *	http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html
 *	에서 다운받아주세요.
 * 4. jre를 다운받고도 실행 환경에 문제가 있다면 JAVA_HOME 디렉토리를 확인해주세요.
 * 5. 해당 파일이 위치하는 디렉토리에서 (맥, 리눅스)./RssInfo.sh <rss주소> 
 * 	(윈도우) ./RssInfo.bat <rss주소> 와 같이 입력하면 프로그램을 실행할 수 있습니다.
 * 6. 권한이 없다고 나오는 경우 chmod +x RssInfo.sh 를 입력 후 다시 실행해주세요 
 * 7. 혹은 java -jar RssInfo.jar <rss주소> 의 방식으로도 실행 가능합니다 
 *
 * 
 */

package rssInfo;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class RssInfo{
	String rssUrl;
	String title;
	String description;
	
	public void getRssContent(String rssUrl){
		//받아온 변수를 매개변수에 세팅 
		this.rssUrl = rssUrl;
		try {
			URL url = new URL(rssUrl); //url 객체생성 
			InputStream isUrl = url.openStream(); //url을 inputstream 화 
			
			//url을 DOM 형태로 바꿔주기 Static 메소드인 newInstance를 사용하여 메소드 구현없이 바로 객체 생성 
			//DocumentBuilderFactory.newDocumentBuilder() 형태로 DocumentBuilder 인스턴스 생성 가능 
			DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

			//파싱 에러 체크를 위한 try-catch 
			try {
				Document document = documentBuilder.parse(isUrl);
				document.getDocumentElement().normalize();
				
				//해당 RSS 최상단 타이틀  
				NodeList rssTitle = document.getElementsByTagName("title");
				this.title = rssTitle.item(0).getTextContent();
				//해당 RSS 최상단 description
				NodeList rssDescription = document.getElementsByTagName("description");
				this.description = rssDescription.item(0).getTextContent();

				System.out.println("\n\nrss title : "+title);
				System.out.println("rss description : "+description);
				
				//item태그 밑의 title들을 가져오기 위해 item 태그를 담은 NodeList 타입 rssItem 생성 
				NodeList rssItem = document.getElementsByTagName("item");
				//상세 내역이 있을 경우 출력 
				if(rssItem.getLength()>0) {
					int maxCount;
					//최대 10개 출력, 상세 리스트가 10개보다 작을 경우 리스트가 가지고 있는 만큼만 출력
					maxCount = rssItem.getLength() > 10 ? 10 : rssItem.getLength();
					//item 의 title 들 출력 
					System.out.println("\n\n*************** Latest post "+maxCount+" *********************\n\n");
					for(int i = 0; i < maxCount; i++) {
						Element itemElement = (Element)rssItem.item(i);
						System.out.println(i+1 +". "+ itemElement.getElementsByTagName("title").item(0).getTextContent());
					}
					System.out.println("\n\n*************** Latest post "+maxCount+" *********************\n\n");
				}else {
					System.out.println("\n\n*************** 하위 포스트가 존재하지 않습니다 ****************\n\n");
				}
			} catch (SAXException e ) {
				//파싱 과정 에러 시 프로그램 종료 
				//e.printStackTrace();
				System.out.println("\n\nNOT CONNECTED - PARSE ERROR\n\n");
				System.exit(0);
			}
		}catch(Exception e){
			//잘못된 형태의 URL 들어온 경우 프로그램 종료 
			//e.printStackTrace();
			System.out.println("\n\nNOT CONNECTED - malformed URL\n\n");
			System.exit(0);
		}
	}
	
	public static void main(String[] consoleInputURL) {
		//프로그램 실행 시 입력 주소값이 없으면 실행되지 않는다 
		if(Array.getLength(consoleInputURL)==0) {
			System.out.println("프로그램 실행시 주소값을 함께 입력해주세요");
			System.exit(0);
		}
		//RssInfo 객체 생성 
		RssInfo rssInfo = new RssInfo();
		//콘솔에서 받아온 값 매개변수로 세팅 후 getRssContent 메서드 실행 
		rssInfo.getRssContent(consoleInputURL[0]);
	}
}

