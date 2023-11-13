package Training4;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.OmikujiDAO;

/**
 * Servlet implementation class inputBirthday
 */
@WebServlet("/inputBirthday")
public class inputBirthday extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */
	public inputBirthday() {
		super();
	}

	/**
	 * 入力フォームを出力しています
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		//エラーメッセージを取得
		String error = (String)request.getAttribute("errMsg");

		//HTMLを出力
		response.setContentType("text/html; charset = UTF-8");
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<head>");
		out.println("<title>占い実行結果</title>");
		out.println("</head>");
		out.println("<body>");
		if(error != null) {
		//nullでない場合は、赤字でエラーメッセージを出力
		out.println("<font color = \"red \">");
		out.println(error);
		}
		out.println("</font>");
		out.println("<form action = \"/Training4/checkServlet\" method = \"POST\">");
		out.print("誕生日を入力してください(例:20150809):");
		out.println("<input  type = \"text\"  name = \"birthday\">");
		out.println("<input type = \"submit\" value = \"送信\">");
		out.println("</form>");
		out.println("</body>");
		out.println("</html>");

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		//Get送信できるメソッド
		doGet(request, response);
	}

	/**
	 * 初期化処理をするメソッドです
	 * @exception ServletException サーブレット内部で例外が起こった場合
	 */
	public void init() throws ServletException {

		//OmikujiDAOのオブジェクト生成
		OmikujiDAO omikujiDao = new OmikujiDAO();

		//csvファイルを読み込んで、おみくじテーブルに値を入れる処理
		omikujiDao.insertOmikuji();
	}

}
