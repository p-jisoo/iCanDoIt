package org.kosta.icandoit.controller;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.kosta.icandoit.model.MemberVO;
import org.kosta.icandoit.model.PostDAO;
import org.kosta.icandoit.model.PostVO;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

public class RegisterHobbyPostController implements Controller {

	@Override
	public String handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		HttpSession session = request.getSession(false);

		String realFolder= request.getServletContext().getRealPath("/picture");
		MultipartRequest multi = new MultipartRequest(request,realFolder,10*1024*1024,"UTF-8", new DefaultFileRenamePolicy()); 
		///////////////////////////////////////////////////////////////
	    //getFile() - File객체를 얻어 올 수 있다. File 객체를 맘대로 제어 가능.
	    //getFileName() - 멀티 업로드가 기본적으로 가능하다.
	    Enumeration enu2 = multi.getFileNames();
		
	String title = multi.getParameter("title");
	String postContent = multi.getParameter("postContent");
	String gatheringPeriod = multi.getParameter("gatheringPeriod");
	String categoryType = multi.getParameter("categoryType");
		MemberVO memberVO = (MemberVO) session.getAttribute("memberVO");

		PostVO post = new PostVO();
		post.setTitle(title);
		post.setPostContent(postContent);
		String maxCount = multi.getParameter("maxCount");
		
		//시간 넣기
		Calendar cal = Calendar.getInstance()  ;
	      SimpleDateFormat dateFormat = new SimpleDateFormat("HHmmSS");
		 String time = dateFormat.format(cal.getTime());
		
		 //파일명 가져오기
	    while(enu2.hasMoreElements()){
	        String fileName = (String)enu2.nextElement();
	        System.out.println("fileName => " + fileName);
	        System.out.println("실제 파일명 => " + multi.getOriginalFileName(fileName) + "<br>");
	        System.out.println("저장될 파일명 => " + multi.getFilesystemName(fileName) + "<br>");
	        System.out.println("파일 타입 => " + multi.getContentType(fileName) + "<br>");
	        
	        //파일명 수정하기
	        String[] imgArray = multi.getOriginalFileName(fileName).split("[.]");
	        String imgArray2 =imgArray[0].concat("_"+time+"."+imgArray[1]);
	        System.out.println(imgArray2);
	        
	        //저장소의 파일명 수정하기
	        String realFileName = multi.getOriginalFileName(fileName);
	        String fullFileName = realFolder + "/" + realFileName;
	        java.io.File f1 = new java.io.File(fullFileName);
	        
	        java.io.File newFile = new java.io.File(realFolder + "/" + imgArray2);
	        f1.renameTo(newFile);
	        post.setImg(imgArray2);			
	    }
		
		post.setGatheringType("모집중");
		post.setGatheringPeriod(gatheringPeriod);
		post.setCategoryType(categoryType);
		post.setMaxCount(Integer.parseInt(maxCount));
		post.setMemberVO(memberVO);
		PostDAO.getInstance().posting(post);
		System.out.println("post :"+post);

		return "FindHobbyPostList.do";
	}

}
