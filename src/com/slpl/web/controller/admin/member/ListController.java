package com.slpl.web.controller.admin.member;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.slpl.web.dao.jdbc.MemberContext;
import com.slpl.web.entity.member.Member;
import com.slpl.web.entity.member.MemberView;
import com.slpl.web.service.member.MemberService;

@WebServlet("/admin/member/list")
public class ListController extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// 로그인 되어 있는지 확인
//		Member loginMember = (Member) request.getSession().getAttribute("login");
//		if(loginMember == null) {
//			request.getRequestDispatcher("login.jsp").forward(request, response);
//			return;
//		}
		
		MemberService service = new MemberService();

		List<MemberView> list = new ArrayList<MemberView>();
		
		
		int pageCount = MemberContext.PAGE_SCOPE_COUNT;
		int itemCount = MemberContext.PAGE_ITEM_COUNT;
		
		int allCount = 0;   	// 전체 리스트 항목 수
		int allPageCount = 1;   // 전체 페이지 갯수
		int restCount = 0;      // 마지막 페이지의 아이템 항목 수
		int startPage = 1;		// 전환 가능한 페이지 범위 시작
		int endPage = 1;		// 전환 가능한 페이지 범위 끝  (2페이지일 경우 1~5의 페이지를 리스트로 전시. 이때 5를 말함)
		int currentPage = 1;    // 검색 일 경우 1페이지
		
		//  파라미터 가져오기
		String pageParam = request.getParameter("page");
		if(pageParam != null ) {	// list url로 바로 들어 왔을 경우(page parameter가 없음)
			currentPage = Integer.parseInt(pageParam);
		}
		String field = request.getParameter("field");
		String query = request.getParameter("query");
		
		if(field != null && query != null) {   //검색임
			allCount = service.getViewList(field, query).size();   //검색 결과에 대한 전체 count 갖고오기
			if(allCount > 0) {
				// 전체 페이지 수 계산
				allPageCount = allCount / itemCount;
		        restCount = allCount % itemCount;
		        if( restCount > 0 ) {
		        	allPageCount += 1;
		        }
		        
		        // 현재 페이지에 대한 페이지 범위 산출
		        startPage = ( pageCount * ((currentPage-1)/pageCount) ) + 1;
		        endPage = startPage + pageCount-1;
		        if(endPage > allPageCount) {   // 만약 전체 페이지 수 보다 전시할 끝 페이지 수가 크면
		        	endPage = allPageCount;
		        }
			}
			
			// 검색 결과에 맞는 정보 가져오기
			list = service.getViewList(currentPage, itemCount, field, query);
			boolean searchResult = false;
			if(list.size() > 0) {
				searchResult = true;
			}

			request.setAttribute("field", field);
			request.setAttribute("query", query);
			request.setAttribute("searchResult", searchResult);
			
			System.out.println("검색 - field : "+field+" / query : "+query +" / searchResult : "+searchResult);
			
		} else {    // 낫 검색
			allCount = service.getViewList().size();   //전체 멤버수 갖고오기
			
			// 페이지에 맞는 정보 가져오기
			list = service.getViewList(currentPage, itemCount);
			
	        // 전체 페이지 수 계산
	        allPageCount = allCount / itemCount;
	        restCount = allCount % itemCount;
	        if( restCount > 0 ) {
	        	allPageCount += 1;
	        }
	        
	        // 현재 페이지에 대한 페이지 범위 산출
	        startPage = ( pageCount * ((currentPage-1)/pageCount) ) + 1;
	        endPage = startPage + pageCount-1;
	        if(endPage > allPageCount) {   // 만약 전체 페이지 수 보다 전시할 끝 페이지 수가 크면
	        	endPage = allPageCount;
	        }
		}
		
		
    	System.out.println("ㅎ 현재 페이지 - "+ currentPage +" / size():"+list.size()+" / allCount:"+allCount
    					+" / allPageCount : "+allPageCount + " / 나머지 : "+restCount + " / 페이지 범위 : "+startPage+"~"+endPage
    					+ " / itemCount : "+itemCount+" / pageCount : "+pageCount);
    	
    	request.setAttribute("currentPage", currentPage);
		request.setAttribute("allPageCount", allPageCount);
		request.setAttribute("startPage", startPage);
		request.setAttribute("endPage", endPage);
		
		request.setAttribute("list", list);
		request.getRequestDispatcher("list.jsp").forward(request, response);
	}
	
}
